package nl.johannisk.node.hasher;

import com.google.common.collect.ImmutableSet;
import nl.johannisk.node.service.ParameterService;
import org.apache.commons.codec.binary.Base64;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class JChainHasherTest {
    private JChainHasher jChainHasher;

    @Before
    public void setup() {
        jChainHasher = new JChainHasher(new ParameterService());
    }

    @Test
    public void testIsValidHashWithInvalidHashes() {
        assertFalse(jChainHasher.isValidHash(null));
        assertFalse(jChainHasher.isValidHash(""));
        assertFalse(jChainHasher.isValidHash("foo"));
    }

    @Test
    public void testIsValidHashWithInvalidlyCasedHashes() {
        assertFalse(jChainHasher.isValidHash("JCoer"));
        assertFalse(jChainHasher.isValidHash("jcore"));
        assertFalse(jChainHasher.isValidHash("JCoRe"));
    }

    @Test
    public void testIsValidHashWithInvalidlyOrderedCharacterHashes() {
        assertFalse(jChainHasher.isValidHash("JCoe JCore"));
    }

    @Test
    public void testIsValidHashWithValidHashes() {
        assertTrue(jChainHasher.isValidHash("JCore"));
        assertTrue(jChainHasher.isValidHash("JCor Core"));
        assertTrue(jChainHasher.isValidHash("JCor Core"));
        assertTrue(jChainHasher.isValidHash("f456J7yhgtC567uhogfdr4567e8ui"));
    }

    @Test
    public void testHashProducesNonEmptyString() {
        final String hash = jChainHasher.hash(null, ImmutableSet.of(), null);
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    public void testHashProducesBase64() {
        final String hash = jChainHasher.hash(null, ImmutableSet.of(), null);
        assertNotNull(hash);
            assertTrue(Base64.isBase64(hash));
    }
}
