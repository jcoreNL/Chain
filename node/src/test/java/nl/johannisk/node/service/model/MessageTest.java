package nl.johannisk.node.service.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {
    private Message subject;

    @Before
    public void setup() {
        subject = new Message(1, "test");
    }

    @Test
    public void testThatMessageReturnsCorrectValues() {
        assertEquals(1, subject.getIndex());
        assertEquals("test", subject.getText());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier
                .forClass(Message.class)
                .withIgnoredFields("text")
                .verify();
    }

    @Test
    public void comparableContract() {
        assertEquals(0, subject.compareTo(new Object()));
        assertEquals(0, subject.compareTo(new Message(1, "test2")));
        assertTrue(subject.compareTo(new Message(0,"smallerMessage")) > 0);
        assertTrue(subject.compareTo(new Message(2,"biggerMessage")) < 0);
    }

    @Test
    public void testThatToStringIsCorrect() {
        assertEquals("Message{index=1, text=test}", subject.toString());
    }
}