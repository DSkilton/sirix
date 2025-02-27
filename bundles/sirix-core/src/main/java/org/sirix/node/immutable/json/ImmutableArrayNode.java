package org.sirix.node.immutable.json;

import org.sirix.api.visitor.JsonNodeVisitor;
import org.sirix.api.visitor.VisitResult;
import org.sirix.node.NodeKind;
import org.sirix.node.interfaces.StructNode;
import org.sirix.node.json.ArrayNode;
import org.sirix.node.xml.ElementNode;

import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable array node wrapper.
 *
 * @author Johannes Lichtenberger
 *
 */
public final class ImmutableArrayNode extends AbstractImmutableJsonStructuralNode {

    /**
     * Mutable {@link ArrayNode}.
     */
    private final ArrayNode node;

    /**
     * Private constructor.
     *
     * @param node mutable {@link ElementNode}
     */
    private ImmutableArrayNode(final ArrayNode node) {
        this.node = checkNotNull(node);
    }

    /**
     * Get a path node key.
     *
     * @return path node key
     */
    public long getPathNodeKey() {
        return node.getPathNodeKey();
    }

    @Override
    public VisitResult acceptVisitor(final JsonNodeVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Get an immutable JSON-array node instance.
     *
     * @param node the mutable {@link ImmutableArrayNode} to wrap
     * @return immutable JSON-array node instance
     */
    public static ImmutableArrayNode of(final ArrayNode node) {
        return new ImmutableArrayNode(node);
    }

    @Override
    public StructNode structDelegate() {
        return node;
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.ARRAY;
    }

    @Override
    public BigInteger computeHash() {
        return node.computeHash();
    }
}
