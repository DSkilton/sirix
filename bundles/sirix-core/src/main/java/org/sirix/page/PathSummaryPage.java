package org.sirix.page;

import com.google.common.base.MoreObjects;
import org.sirix.access.DatabaseType;
import org.sirix.api.PageReadOnlyTrx;
import org.sirix.cache.TransactionIntentLog;
import org.sirix.index.IndexType;
import org.sirix.page.delegates.BitmapReferencesPage;
import org.sirix.page.delegates.ReferencesPage4;
import org.sirix.page.interfaces.Page;
import org.sirix.settings.Constants;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Page to hold references to a path summary.
 *
 * @author Johannes Lichtenberger, University of Konstanz
 *
 */
public final class PathSummaryPage extends AbstractForwardingPage {

    /**
     * The references page instance.
     */
    private Page delegate;

    /**
     * Maximum node keys.
     */
    private final Map<Integer, Long> maxNodeKeys;

    /**
     * Current maximum levels of indirect pages in the tree.
     */
    private final Map<Integer, Integer> currentMaxLevelsOfIndirectPages;

    /**
     * Constructor.
     */
    public PathSummaryPage() {
        delegate = new ReferencesPage4();
        maxNodeKeys = new HashMap<>();
        currentMaxLevelsOfIndirectPages = new HashMap<>();
    }

    /**
     * Get indirect page reference.
     *
     * @param index the offset of the indirect page, that is the index number
     * @return indirect page reference
     */
    public PageReference getIndirectPageReference(int index) {
        return getOrCreateReference(index);
    }

    /**
     * Read meta page.
     *
     * @param in input bytes to read from
     */
    protected PathSummaryPage(final DataInput in, final SerializationType type) throws IOException {
        delegate = PageUtils.createDelegate(in, type);
        final int size = in.readInt();
        maxNodeKeys = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            maxNodeKeys.put(i, in.readLong());
        }
        final int currentMaxLevelOfIndirectPages = in.readInt();
        currentMaxLevelsOfIndirectPages = new HashMap<>(currentMaxLevelOfIndirectPages);
        for (int i = 0; i < currentMaxLevelOfIndirectPages; i++) {
            currentMaxLevelsOfIndirectPages.put(i, in.readByte() & 0xFF);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("mDelegate", delegate).toString();
    }

    @Override
    protected Page delegate() {
        return delegate;
    }

    /**
     * Initialize path summary tree.
     *
     * @param databaseType The type of database.
     * @param pageReadTrx {@link PageReadOnlyTrx} instance
     * @param index the index number
     * @param log the transaction intent log
     */
    public void createPathSummaryTree(final DatabaseType databaseType,
            final PageReadOnlyTrx pageReadTrx,
            final int index,
            final TransactionIntentLog log) {
        PageReference reference = getOrCreateReference(index);
        if (reference == null) {
            delegate = new BitmapReferencesPage(Constants.INP_REFERENCE_COUNT, (ReferencesPage4) delegate());
            reference = delegate.getOrCreateReference(index);
        }
        if (reference.getPage() == null && reference.getKey() == Constants.NULL_ID_LONG
                && reference.getLogKey() == Constants.NULL_ID_INT
                && reference.getPersistentLogKey() == Constants.NULL_ID_LONG) {
            PageUtils.createTree(databaseType, reference, IndexType.PATH_SUMMARY, pageReadTrx, log);
            if (maxNodeKeys.get(index) == null) {
                maxNodeKeys.put(index, 0L);
            } else {
                maxNodeKeys.put(index, maxNodeKeys.get(index) + 1);
            }
            currentMaxLevelsOfIndirectPages.merge(index, 1, Integer::sum);
        }
    }

    @Override
    public void serialize(final DataOutput out, final SerializationType type) throws IOException {
        out.writeByte(0);
        super.serialize(out, type);
        final int size = maxNodeKeys.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            out.writeLong(maxNodeKeys.get(i));
        }
        final int currentMaxLevelOfIndirectPages = maxNodeKeys.size();
        out.writeInt(currentMaxLevelOfIndirectPages);
        for (int i = 0; i < currentMaxLevelOfIndirectPages; i++) {
            out.writeByte(currentMaxLevelsOfIndirectPages.get(i));
        }
    }

    public int getCurrentMaxLevelOfIndirectPages(int index) {
        return currentMaxLevelsOfIndirectPages.getOrDefault(index, 1);
    }

    public int incrementAndGetCurrentMaxLevelOfIndirectPages(int index) {
        return currentMaxLevelsOfIndirectPages.merge(
                index, 1, Integer::sum);
    }

    /**
     * Get the maximum node key of the specified index by its index number. The
     * index number of the PathSummary is 0.
     *
     * @param indexNo the index number
     * @return the maximum node key stored
     */
    public long getMaxNodeKey(final int indexNo) {
        return maxNodeKeys.get(indexNo);
    }

    public long incrementAndGetMaxNodeKey(final int indexNo) {
        final long newMaxNodeKey = maxNodeKeys.get(indexNo) + 1;
        maxNodeKeys.put(indexNo, newMaxNodeKey);
        return newMaxNodeKey;
    }

}
