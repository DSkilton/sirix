package org.sirix.access.trx.node;

import javax.xml.namespace.QName;
import org.brackit.xquery.atomic.QNm;
import org.sirix.exception.SirixIOException;
import org.sirix.index.path.summary.PathNode;
import org.sirix.node.NodeKind;

public interface NodeFactory {

    /**
     * Create a {@link PathNode}.
     *
     * @param parentKey parent node key
     * @param leftSibKey left sibling key
     * @param rightSibKey right sibling key
     * @param name {@link QName} of the node
     * @param level level of this node
     * @return the created node
     * @throws SirixIOException if an I/O error occurs
     */
    PathNode createPathNode(long parentKey, long leftSibKey, long rightSibKey, QNm name, NodeKind kind, int level);
}
