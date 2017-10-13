package nl.johannisk.node.service.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockChain {

    private final TreeNode<Block> root;
    private final Map<String, TreeNode<Block>> blocks;
    private TreeNode<Block> endBlock;
    private int maxDepth = 0;

    public BlockChain() {
        root = new TreeNode<>(null, Block.ZERO, 0);
        blocks = new HashMap<>();
        blocks.put("0", root);
        endBlock = root;
        maxDepth = 0;
    }

    public void addBlock(final Block block) {
        if(blocks.containsKey(block.getParentHash())) {
            final TreeNode<Block> parentBlock = blocks.get(block.getParentHash());
            final TreeNode<Block> newNode = parentBlock.addChild(block);
            if (newNode.getDepth() > maxDepth) {
                maxDepth = newNode.getDepth();
                endBlock = newNode;
            }
            blocks.put(block.getHash(), newNode);
        }
    }

    public boolean containsBlock(final Block b) {
        return blocks.containsKey(b.getHash());
    }

    public TreeNode<Block> getEndBlock() {
        return endBlock;
    }

    public List<Block> getOrphanedBlocks() {
        final List<Block> blocksInChain = new ArrayList<>();
        TreeNode<Block> b = endBlock;
        do {
            blocksInChain.add(b.getData());
            b = b.getParent();
        } while (b != null);
        return blocks.values().stream()
                .map(TreeNode::getData)
                .filter(block -> !blocksInChain.contains(block))
                .collect(Collectors.toList());
    }
}
