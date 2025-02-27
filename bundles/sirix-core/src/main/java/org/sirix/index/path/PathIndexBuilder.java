package org.sirix.index.path;

import java.util.Optional;
import java.util.Set;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.util.path.Path;
import org.brackit.xquery.util.path.PathException;
import org.sirix.api.visitor.VisitResult;
import org.sirix.api.visitor.VisitResultType;
import org.sirix.exception.SirixIOException;
import org.sirix.index.SearchMode;
import org.sirix.index.redblacktree.RBTreeReader.MoveCursor;
import org.sirix.index.redblacktree.RBTreeWriter;
import org.sirix.index.redblacktree.keyvalue.NodeReferences;
import org.sirix.index.path.summary.PathSummaryReader;
import org.sirix.node.interfaces.immutable.ImmutableNode;
import org.sirix.utils.LogWrapper;
import org.slf4j.LoggerFactory;

public final class PathIndexBuilder {

    private static final LogWrapper LOGGER = new LogWrapper(LoggerFactory.getLogger(PathIndexBuilder.class));

    private final Set<Path<QNm>> paths;

    private final PathSummaryReader pathSummaryReader;

    private final RBTreeWriter<Long, NodeReferences> avlTreeWriter;

    public PathIndexBuilder(final RBTreeWriter<Long, NodeReferences> avlTreeWriter,
            final PathSummaryReader pathSummaryReader, final Set<Path<QNm>> paths) {
        this.pathSummaryReader = pathSummaryReader;
        this.paths = paths;
        this.avlTreeWriter = avlTreeWriter;
    }

    public VisitResult process(final ImmutableNode node, final long pathNodeKey) {
        try {
            final long PCR = pathNodeKey;
            if (pathSummaryReader.getPCRsForPaths(paths, true).contains(PCR) || paths.isEmpty()) {
                final Optional<NodeReferences> textReferences = avlTreeWriter.get(PCR, SearchMode.EQUAL);
                if (textReferences.isPresent()) {
                    setNodeReferences(node, textReferences.get(), PCR);
                } else {
                    setNodeReferences(node, new NodeReferences(), PCR);
                }
            }
        } catch (final PathException | SirixIOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return VisitResultType.CONTINUE;
    }

    private void setNodeReferences(final ImmutableNode node, final NodeReferences references, final long pathNodeKey)
            throws SirixIOException {
        avlTreeWriter.index(pathNodeKey, references.addNodeKey(node.getNodeKey()), MoveCursor.NO_MOVE);
    }

}
