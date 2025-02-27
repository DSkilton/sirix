package org.sirix.access.trx.node;

import org.sirix.api.PageReadOnlyTrx;
import org.sirix.node.interfaces.StructNode;
import org.sirix.node.interfaces.immutable.ImmutableNode;

public interface InternalNodeReadOnlyTrx<N extends ImmutableNode> {

    N getCurrentNode();

    void setCurrentNode(N node);

    StructNode getStructuralNode();

    void assertNotClosed();

    void setPageReadTransaction(PageReadOnlyTrx trx);
}
