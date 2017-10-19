package nl.johannisk.node.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import nl.johannisk.node.hasher.JChainHasher;
import nl.johannisk.node.service.model.Block;
import nl.johannisk.node.service.model.BlockChain;
import nl.johannisk.node.service.model.Message;
import nl.johannisk.node.service.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlockChainService {

    @Value("${eureka.instance.instanceId}")
    private String instanceId;

    private final Set<Message> unhandledMessages;
    private final Set<Message> handledMessages;
    private final BlockChain chain;
    private final BlockCreatorService blockCreatorService;
    private final JChainHasher jChainHasher;
    private final EurekaClient eurekaClient;
    private final Random random;

    @Autowired
    public BlockChainService(final BlockCreatorService blockCreatorService, final JChainHasher jChainHasher, final EurekaClient eurekaClient) {
        this.blockCreatorService = blockCreatorService;
        this.jChainHasher = jChainHasher;
        this.eurekaClient = eurekaClient;
        this.unhandledMessages = new HashSet<>();
        this.handledMessages = new HashSet<>();
        this.chain = new BlockChain();
        this.random = new SecureRandom();
    }

    public BlockChain getChain() {
        return chain;
    }

    public Set<Message> getUnprocessedMessages() {
        return unhandledMessages;
    }

    public void addMessage(final Message m) {
        if (!handledMessages.contains(m) && !unhandledMessages.contains(m)) {
            unhandledMessages.add(m);
            if (isReadyForNewBlock()) {
                createNewBlock();
            }
        }
    }

    public void addBlock(final Block block) {
        final String hash = jChainHasher.hash(block.getParentHash(), block.getContent(), block.getNonce());
        if (jChainHasher.isValidHash(hash) && block.getHash().equals(hash) && !chain.containsBlock(block)) {
            final String lastBlockHash = chain.getEndBlock().getData().getHash();
            chain.addBlock(block);
            if (!chain.getEndBlock().getData().getHash().equals(lastBlockHash)) {
                if (blockCreatorService.getState() == BlockCreatorService.State.RUNNING) {
                    blockCreatorService.cancelRun();
                }
                resetMessagesAccordingToChain();
                if (isReadyForNewBlock()) {
                    createNewBlock();
                }
            }
        }
    }

    private void addCreatedBlock(final Block block) {
        if (chain.getEndBlock().getData().getHash().equals(block.getParentHash())) {
            chain.addBlock(block);
            final Application application = eurekaClient.getApplication("jchain-node");
            final List<InstanceInfo> instanceInfo = application.getInstances();
            for (InstanceInfo info : instanceInfo) {
                if (info.getInstanceId().equals(instanceId)) {
                    continue;
                }
                informNodeOfNewBlock(Integer.toString(info.getPort()), block);
            }
        }
    }

    private void resetMessagesAccordingToChain() {
        unhandledMessages.addAll(handledMessages);
        handledMessages.clear();
        TreeNode<Block> block = chain.getEndBlock();
        do {
            for (Message m : block.getData().getContent()) {
                unhandledMessages.remove(m);
                handledMessages.add(m);
            }
        } while ((block = block.getParent()) != null);
    }

    private Set<Message> pickMessagesForPotentialBlock() {
        final Set<Message> messageForNextBlock = unhandledMessages.stream()
                .limit(5)
                .collect(Collectors.toSet());
        unhandledMessages.removeAll(messageForNextBlock);
        handledMessages.addAll(messageForNextBlock);
        return messageForNextBlock;
    }

    private boolean isReadyForNewBlock() {
        return unhandledMessages.size() >= 5 && blockCreatorService.getState() == BlockCreatorService.State.READY;
    }

    private void createNewBlock() {
        final Set<Message> blockContent = pickMessagesForPotentialBlock();
        blockCreatorService.createBlock(chain.getEndBlock().getData(), blockContent).subscribe(this::addCreatedBlock);
    }

    @Async
    void informNodeOfNewBlock(final String host, final Block block) {
        final int delay = random.nextInt(10000) + 3000;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://localhost:" + host + "/node/block", block, Block.class);
    }
}
