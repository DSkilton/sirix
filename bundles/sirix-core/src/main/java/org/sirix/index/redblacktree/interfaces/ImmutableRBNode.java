package org.sirix.index.redblacktree.interfaces;

import org.sirix.node.interfaces.Node;

/**
 * Immutable RBNode.
 */
public interface ImmutableRBNode<K extends Comparable<? super K>, V> extends Node {

    /**
     * Key to be indexed.
     *
     * @return key reference
     */
    K getKey();

    /**
     * Value to be indexed.
     *
     * @return key reference
     */
    V getValue();

    /**
     * Flag which determines if node is changed.
     *
     * @return {@code true} if it has been changed in memory, {@code false}
     * otherwise
     */
    boolean isChanged();

    /**
     * Determines node has a left child.
     *
     * @return {@code true}, if it has a left child, {@code false} otherwise
     */
    boolean hasLeftChild();

    /**
     * Determines node has a right child.
     *
     * @return {@code true}, if it has a left child, {@code false} otherwise
     */
    boolean hasRightChild();

    /**
     * Get left child.
     *
     * @return left child pointer
     */
    long getLeftChildKey();

    /**
     * Get right child.
     *
     * @return right child pointer
     */
    long getRightChildKey();
}
