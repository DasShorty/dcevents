package de.dasshorty.pridebot.api;

import java.io.Serializable;

public record Pair<K,V>(K key, V value) {
    @Override
    public String toString() {
        return "key: " + key + " value: " + value;
    }
}
