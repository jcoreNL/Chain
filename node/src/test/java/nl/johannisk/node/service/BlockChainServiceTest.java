package nl.johannisk.node.service;

import com.netflix.discovery.EurekaClient;
import nl.johannisk.node.hasher.JChainHasher;
import nl.johannisk.node.service.model.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class BlockChainServiceTest {

    private BlockChainService subject;

    @Mock
    private BlockCreatorService blockCreatorService;

    @Mock
    private EurekaClient eurekaClient;

    @Mock
    private JChainHasher jChainHasher;

    @Before
    public void setUp() throws Exception {
        subject = new BlockChainService(blockCreatorService, jChainHasher, eurekaClient);
    }

    @Test
    public void testThatAddMessageAddsMessageToUnProcessedMessages() {
        final Message addMessage = new Message(1, "test");
        assertTrue(subject.getUnprocessedMessages().isEmpty());
        subject.addMessage(addMessage);
        assertTrue(subject.getUnprocessedMessages().contains(addMessage));
    }

    @Test
    public void testThatMessageWithSameIdIsNotAdded() {
        final Message addMessage = new Message(1, "test");
        final Message addMessage2 = new Message(1, "test2");
        assertTrue(subject.getUnprocessedMessages().isEmpty());
        subject.addMessage(addMessage);
        subject.addMessage(addMessage2);
        assertTrue(subject.getUnprocessedMessages().contains(addMessage));
        assertTrue(subject.getUnprocessedMessages().size() == 1);
    }

}