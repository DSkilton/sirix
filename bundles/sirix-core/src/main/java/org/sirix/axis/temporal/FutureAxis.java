package org.sirix.axis.temporal;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import org.sirix.api.NodeCursor;
import org.sirix.api.NodeReadOnlyTrx;
import org.sirix.api.NodeTrx;
import org.sirix.api.ResourceManager;
import org.sirix.api.xml.XmlNodeReadOnlyTrx;
import org.sirix.axis.AbstractTemporalAxis;
import org.sirix.axis.IncludeSelf;

/**
 * Retrieve a node by node key in all future revisions. In each revision a
 * {@link XmlNodeReadOnlyTrx} is opened which is moved to the node with the
 * given node key if it exists. Otherwise the iterator has no more elements (the
 * {@link XmlNodeReadOnlyTrx} moved to the node by it's node key).
 *
 * @author Johannes Lichtenberger
 *
 */
public final class FutureAxis<R extends NodeReadOnlyTrx & NodeCursor, W extends NodeTrx & NodeCursor>
        extends AbstractTemporalAxis<R, W> {

    /**
     * The revision number.
     */
    private int mRevision;

    /**
     * Sirix {@link ResourceManager}.
     */
    private final ResourceManager<R, W> mResourceManager;

    /**
     * Node key to lookup and retrieve.
     */
    private final long mNodeKey;

    /**
     * Constructor.
     *
     * @param rtx Sirix {@link XmlNodeReadOnlyTrx}
     */
    public FutureAxis(final ResourceManager<R, W> resourceManager, final R rtx) {
        // Using telescope pattern instead of builder (only one optional parameter).
        this(resourceManager, rtx, IncludeSelf.NO);
    }

    /**
     * Constructor.
     *
     * @param resourceManager the resource manager
     * @param rtx the transactional read only cursor
     * @param includeSelf determines if current revision must be included or not
     */
    public FutureAxis(final ResourceManager<R, W> resourceManager, final R rtx, final IncludeSelf includeSelf) {
        mResourceManager = checkNotNull(resourceManager);
        mNodeKey = rtx.getNodeKey();
        mRevision = checkNotNull(includeSelf) == IncludeSelf.YES
                ? rtx.getRevisionNumber()
                : rtx.getRevisionNumber() + 1;
    }

    @Override
    protected R computeNext() {
        // != a little bit faster?
        if (mRevision <= mResourceManager.getMostRecentRevisionNumber()) {
            final Optional<R> optionalRtx = mResourceManager.getNodeReadTrxByRevisionNumber(mRevision);

            final R rtx;
            if (optionalRtx.isPresent()) {
                rtx = optionalRtx.get();
            } else {
                rtx = mResourceManager.beginNodeReadOnlyTrx(mRevision);
            }

            mRevision++;

            if (rtx.moveTo(mNodeKey).hasMoved()) {
                return rtx;
            } else {
                rtx.close();
                return endOfData();
            }
        } else {
            return endOfData();
        }
    }

    @Override
    public ResourceManager<R, W> getResourceManager() {
        return mResourceManager;
    }
}
