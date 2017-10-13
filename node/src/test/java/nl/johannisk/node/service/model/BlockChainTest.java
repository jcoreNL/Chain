package nl.johannisk.node.service.model;

import static nl.johannisk.node.service.model.Block.ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.TreeSet;

public class BlockChainTest {
    private BlockChain impl;

    @Before
    public void setup() {
        impl = new BlockChain();
    }

    @Test
    public void testContainsBlock() {
        /* Arrange */
        final String hash = "foo";
        final String parentHash = ZERO.getHash();
        final Block addBlock = new Block(hash, parentHash, new TreeSet<>(), null);
        final Block checkBlock = new Block(hash, parentHash, new TreeSet<>(), null);
        final Block checkBlockNotInChain = new Block("bar", parentHash, new TreeSet<>(), null);

        /* Act & Assert */
        impl.addBlock(addBlock);
        assertTrue(impl.containsBlock(checkBlock));
        assertFalse(impl.containsBlock(checkBlockNotInChain));
    }

    @Test
    public void testThatChainHasCorrectHead() {
         /* Arrange */
        final String hash = "foo";
        final String parentHash = ZERO.getHash();
        final Block addBlock = new Block(hash, parentHash, new TreeSet<>(), null);
        final Block addBlockNotInChain = new Block("bar", parentHash, new TreeSet<>(), null);
        /* Act & Assert */
        impl.addBlock(addBlock);
        impl.addBlock(addBlockNotInChain);

        assertEquals(addBlock, impl.getEndBlock().getData());
    }

    @Test
    public void testGetEndAndOrphanedBlock() {
        /* Arrange */
        final Block a = new Block("a", ZERO.getHash(), new TreeSet<>(), null);
        final Block bWithParentA = new Block("b", "a", new TreeSet<>(), null);
        final Block cWithParentB = new Block("c", "b", new TreeSet<>(), null);
        final Block dWithParentA = new Block("d", "a", new TreeSet<>(), null);

        /* Act & Assert */
        impl.addBlock(a);
        impl.addBlock(bWithParentA);
        assertEquals(bWithParentA, impl.getEndBlock().getData());
        impl.addBlock(cWithParentB);
        assertEquals(cWithParentB, impl.getEndBlock().getData());
        impl.addBlock(dWithParentA);
        assertEquals(cWithParentB, impl.getEndBlock().getData());
        assertEquals(Arrays.asList(dWithParentA), impl.getOrphanedBlocks());
    }
}
