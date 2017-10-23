package nl.johannisk.node.controller;

import nl.johannisk.node.service.BlockChainService;
import nl.johannisk.node.service.ParameterService;
import nl.johannisk.node.service.model.Block;
import nl.johannisk.node.service.model.BlockChain;
import nl.johannisk.node.service.model.Message;
import nl.johannisk.node.service.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
public class ApiController {

    private final BlockChainService blockChainService;
    private final ParameterService parameterService;

    @Autowired
    public ApiController(final BlockChainService blockChainService, final ParameterService parameterService) {
        this.blockChainService = blockChainService;
        this.parameterService = parameterService;
    }


    @RequestMapping(path = "/chain")
    public List<Block> chain() {
        final BlockChain blockChain = blockChainService.getChain();
        final List<Block> mainChain = new LinkedList<>();
        TreeNode<Block> b = blockChain.getEndBlock();
        do {
            mainChain.add(b.getData());
            b = b.getParent();
        } while (b != null);
        Collections.reverse(mainChain);
        return mainChain;
    }

    @RequestMapping(path = "/orphaned")
    public List<Block> orphaned() {
        return blockChainService.getChain().getOrphanedBlocks();
    }

    @RequestMapping(path = "/messages")
    public Set<Message> messages() {
        return blockChainService.getUnprocessedMessages();
    }

    @RequestMapping(path = "/algorithm")
    public String getAlgorithm() {
        return "\"" + parameterService.getEncryptionAlgorithm() + "\"";
    }

    @RequestMapping(path = "/difficulty")
    public int getDifficulty() {
        return parameterService.getDifficulty();
    }

    @RequestMapping(path = "/difficulty/{number}")
    public void setDifficulty(@PathVariable("number") int difficulty) {
        parameterService.setDifficulty(difficulty);
    }

    @RequestMapping(path = "/change")
    public void increaseEncryption() {
        parameterService.increaseEncryption();
    }

}
