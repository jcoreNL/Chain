package nl.johannisk.node.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.LinkedHashSet;
import java.util.Set;

public final class Block {
    public static final Block ZERO;

    static {
        ZERO = new Block("0", "0", new LinkedHashSet<>(), "0");
    }

    private final String hash;
    private final String parentHash;
    private final Set<Message> contents;
    private final String nonce;

    public Block(@JsonProperty("hash") final String hash,
                 @JsonProperty("parentHash") final String parentHash,
                 @JsonProperty("content") final Set<Message> content,
                 @JsonProperty("nonce") final String nonce) {
        this.hash = hash;
        this.parentHash = parentHash;
        this.contents = new LinkedHashSet<>(content);
        this.nonce = nonce;
    }

    public String getHash() {
        return hash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public Set<Message> getContent() {
        return new LinkedHashSet<>(contents);
    }

    public String getNonce() {
        return nonce;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hash", hash)
                .add("parentHash", parentHash)
                .add("contents", contents)
                .add("nonce", nonce)
                .toString();
    }
}
