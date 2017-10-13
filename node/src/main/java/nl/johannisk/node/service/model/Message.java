package nl.johannisk.node.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public final class Message implements Comparable {
    private final int index;
    private final String text;

    public Message(@JsonProperty("index") final int index, @JsonProperty("text") final String text) {
        this.index = index;
        this.text = text;
    }

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }

        final Message message = (Message) o;

        return index == message.index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("index", index)
                .add("text", text)
                .toString();
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof Message) {
            Message m = (Message) o;
            return this.getIndex() - m.getIndex();
        }
        return 0;
    }
}
