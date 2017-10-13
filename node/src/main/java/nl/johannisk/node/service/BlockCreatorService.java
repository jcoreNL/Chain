package nl.johannisk.node.service;

import nl.johannisk.node.hasher.JChainHasher;
import nl.johannisk.node.service.model.Block;
import nl.johannisk.node.service.model.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class BlockCreatorService {

    public enum State {
        READY,
        RUNNING,
        CANCELLED
    }

    private final Random random;
    private State state;

    public BlockCreatorService() {
        this.random = new Random();
        this.state = State.READY;
    }

    @Async
    void createBlock(Block parentBlock, Set<Message> messages, Consumer<Block> consumer) {
        state = State.RUNNING;
        String hash;
        String parentHash = parentBlock.getHash();
        long nonce = random.nextLong();
        do {
            hash = JChainHasher.hash(parentHash, messages, Long.toString(++nonce));
            try {
                Thread.sleep(6);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } while (!JChainHasher.isValidHash(hash) && state.equals(State.RUNNING));

        if (state.equals(State.RUNNING)) {
            consumer.accept(new Block(hash, parentHash, messages, Long.toString(nonce)));
        }
        state = State.READY;
    }

    public State getState() {
        return state;
    }

    public void cancelRun() {
        this.state = State.CANCELLED;
    }

}
