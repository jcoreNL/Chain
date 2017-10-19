package nl.johannisk.node.service;

import io.reactivex.Maybe;
import nl.johannisk.node.hasher.JChainHasher;
import nl.johannisk.node.service.model.Block;
import nl.johannisk.node.service.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;


@Service
public class BlockCreatorService {

    private final ParameterService parameterService;
    private final JChainHasher jChainHasher;

    public enum State {
        READY,
        RUNNING,
        CANCELLED
    }

    private final Random random;
    private State state;

    @Autowired
    public BlockCreatorService(final ParameterService parameterService, final JChainHasher jChainHasher) {
        this.parameterService = parameterService;
        this.jChainHasher = jChainHasher;
        this.random = new Random();
        this.state = State.READY;
    }

    Maybe<Block> createBlock(Block parentBlock, Set<Message> messages) {
        state = State.RUNNING;

        return Maybe.create(maybeEmitter -> {
            String hash;
            String parentHash = parentBlock.getHash();
            long nonce = random.nextLong();
            do {
                hash = jChainHasher.hash(parentHash, messages, Long.toString(++nonce));
                try {
                    Thread.sleep(parameterService.getDifficulty());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } while (!jChainHasher.isValidHash(hash) && state.equals(State.RUNNING));

            if (state.equals(State.RUNNING)) {
                maybeEmitter.onSuccess(new Block(hash, parentHash, messages, Long.toString(nonce)));
            } else {
                maybeEmitter.onComplete();
            }

            state = State.READY;
        });
    }

    public State getState() {
        return state;
    }

    public void cancelRun() {
        this.state = State.CANCELLED;
    }

}
