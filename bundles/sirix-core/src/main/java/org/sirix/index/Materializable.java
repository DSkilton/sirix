package org.sirix.index;

import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.node.Node;

/**
 * Materializable structure.
 *
 * @author Sebastian Baechle
 *
 */
public interface Materializable {

    /**
     * Materialize the object as a {@link Node} tree
     *
     * @return the root of the materialized tree
     * @throws DocumentException
     */
    Node<?> materialize() throws DocumentException;

    /**
     * Initializes the materialized locator facet
     *
     * @param root root of the materialized facet subtree
     * @throws DocumentException
     */
    void init(Node<?> root) throws DocumentException;
}
