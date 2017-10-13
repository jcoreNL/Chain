package nl.johannisk.node.service.model;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class BlockTest {

    private Block subject;
    private Set<Message> messageSet;

    @Before
    public void setup() {
        messageSet = new HashSet<>();
        messageSet.add(new Message(1, "message"));
        subject = new Block("hash", "parentHash", messageSet, "nonce");
    }

    @Test
    public void testThatBlockReturnsCorrectValues() {
        assertEquals("hash", subject.getHash());
        assertEquals("parentHash", subject.getParentHash());
        assertEquals(messageSet, subject.getContent());
        assertEquals("nonce", subject.getNonce());
    }

    @Test
    public void testThatBlockIsImmutable() {
        assertNotSame(messageSet, subject.getContent());
    }

    @Test
    public void testThatToStringIsCorrect() {
        assertEquals("Block{hash=hash, parentHash=parentHash, contents=[Message{index=1, text=message}], nonce=nonce}", subject.toString());
    }
}