package nl.johannisk.node.service.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    private final T data;
    private final List<TreeNode<T>> children = new ArrayList<>();
    private final TreeNode<T> parent;
    private final int depth;

    public TreeNode(final TreeNode<T> parent, final T data, final int depth) {
        this.parent = parent;
        this.data = data;
        this.depth = depth;
    }

    public TreeNode<T> addChild(final T data) {
        TreeNode<T> newChild = new TreeNode<>(this, data, depth + 1);
        this.children.add(newChild);
        return newChild;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getData() {
        return data;
    }

    public int getDepth() {
        return depth;
    }
}
