/*
 * Copyright (c) 2018, Sirix
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sirix.page;

import org.magicwerk.brownies.collections.GapList;
import org.sirix.exception.SirixIOException;
import org.sirix.page.interfaces.PageFragmentKey;
import org.sirix.settings.Constants;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Defines the serialization/deserialization type.
 *
 * @author Johannes Lichtenberger
 * <a href="mailto:lichtenberger.johannes@gmail.com">mail</a>
 */
public enum SerializationType {
    /**
     * The transaction intent log.
     */
    TRANSACTION_INTENT_LOG {
        @Override
        public void serializeBitmapReferencesPage(DataOutput out, List<PageReference> pageReferences, BitSet bitmap) {
            assert out != null;
            assert pageReferences != null;

            try {
                serializeBitSet(out, bitmap);

                for (final PageReference pageReference : pageReferences) {
                    out.writeInt(pageReference.getLogKey());
                }
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public void serializeReferencesPage4(DataOutput out, List<PageReference> pageReferences, List<Short> offsets) {
            assert out != null;
            assert pageReferences != null;
            try {
                out.writeByte(pageReferences.size());
                for (int i = 0, size = pageReferences.size(); i < size; i++) {
                    final PageReference pageReference = pageReferences.get(i);
                    final short offset = offsets.get(i);
                    out.writeInt(pageReference.getLogKey());
                    out.writeShort(offset);
                }
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public DeserializedBitmapReferencesPageTuple deserializeBitmapReferencesPage(@Nonnegative int referenceCount,
                DataInput in) {
            assert in != null;

            try {
                final BitSet bitmap = deserializeBitSet(in);

                final int length = bitmap.cardinality();

                final List<PageReference> references = new GapList<>(length);

                for (int offset = 0; offset < length; offset++) {
                    final int key = in.readInt();
                    final PageReference reference = new PageReference();
                    reference.setLogKey(key);
                    references.add(offset, reference);
                }

                return new DeserializedBitmapReferencesPageTuple(references, bitmap);
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public DeserializedReferencesPage4Tuple deserializeReferencesPage4(DataInput in) {
            try {
                final byte size = in.readByte();
                final List<PageReference> pageReferences = new ArrayList<>(4);
                final List<Short> offsets = new ArrayList<>(4);
                for (int i = 0; i < size; i++) {
                    final int key = in.readInt();
                    final var pageReference = new PageReference().setLogKey(key);
                    pageReferences.add(pageReference);
                    offsets.add(in.readShort());
                }
                return new DeserializedReferencesPage4Tuple(pageReferences, offsets);
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public void serializeFullReferencesPage(DataOutput out, PageReference[] pageReferences) {
            try {
                final BitSet bitSet = new BitSet(Constants.INP_REFERENCE_COUNT);
                for (int i = 0, size = pageReferences.length; i < size; i++) {
                    if (pageReferences[i] != null) {
                        bitSet.set(i, true);
                    }
                }
                serializeBitSet(out, bitSet);

                for (final PageReference pageReference : pageReferences) {
                    if (pageReference != null) {
                        out.writeLong(pageReference.getPersistentLogKey());
                        writePageFragments(out, pageReference);
                    }
                }
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public PageReference[] deserializeFullReferencesPage(DataInput in) {
            try {
                final PageReference[] references = new PageReference[Constants.INP_REFERENCE_COUNT];
                final BitSet bitSet = deserializeBitSet(in);

                for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
                    final var pageReference = new PageReference();
                    pageReference.setPersistentLogKey(in.readLong());
                    readPageFragments(in, pageReference);
                    references[i] = pageReference;
                }

                return references;
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }
    },
    /**
     * The actual data.
     */
    DATA {
        @Override
        public void serializeBitmapReferencesPage(DataOutput out, List<PageReference> pageReferences, BitSet bitmap) {
            assert out != null;
            assert pageReferences != null;

            try {
                serializeBitSet(out, bitmap);

                for (final PageReference pageReference : pageReferences) {
                    writePageFragments(out, pageReference);
                    writeHash(out, pageReference);
                }
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public void serializeReferencesPage4(DataOutput out, List<PageReference> pageReferences, List<Short> offsets) {
            try {
                out.writeByte(pageReferences.size());
                for (final PageReference pageReference : pageReferences) {
                    writePageFragments(out, pageReference);
                    writeHash(out, pageReference);
                }
                for (final short offset : offsets) {
                    out.writeShort(offset);
                }
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public DeserializedBitmapReferencesPageTuple deserializeBitmapReferencesPage(@Nonnegative int referenceCount,
                DataInput in) {
            assert in != null;

            try {
                final BitSet bitmap = deserializeBitSet(in);
                final int length = bitmap.cardinality();
                final GapList<PageReference> references = new GapList<>(length);

                for (int offset = 0; offset < length; offset++) {
                    final PageReference reference = new PageReference();
                    readPageFragments(in, reference);
                    readHash(in, reference);
                    references.add(offset, reference);
                }

                return new DeserializedBitmapReferencesPageTuple(references, bitmap);
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public DeserializedReferencesPage4Tuple deserializeReferencesPage4(DataInput in) {
            try {
                final byte size = in.readByte();
                final List<PageReference> pageReferences = new ArrayList<>(4);
                final List<Short> offsets = new ArrayList<>(4);
                for (int i = 0; i < size; i++) {
                    final var reference = new PageReference();
                    readPageFragments(in, reference);
                    readHash(in, reference);
                    pageReferences.add(reference);
                }
                for (int i = 0; i < size; i++) {
                    offsets.add(in.readShort());
                }
                return new DeserializedReferencesPage4Tuple(pageReferences, offsets);
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public void serializeFullReferencesPage(DataOutput out, PageReference[] pageReferences) {
            try {
                final BitSet bitSet = new BitSet(Constants.INP_REFERENCE_COUNT);
                for (int i = 0, size = pageReferences.length; i < size; i++) {
                    if (pageReferences[i] != null) {
                        bitSet.set(i, true);
                    }
                }
                serializeBitSet(out, bitSet);

                for (final PageReference pageReference : pageReferences) {
                    if (pageReference != null) {
                        out.writeLong(pageReference.getKey());
                        writePageFragments(out, pageReference);
                        writeHash(out, pageReference);
                    }
                }
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }

        @Override
        public PageReference[] deserializeFullReferencesPage(DataInput in) {
            try {
                final PageReference[] references = new PageReference[Constants.INP_REFERENCE_COUNT];
                final BitSet bitSet = deserializeBitSet(in);

                for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
                    final var pageReference = new PageReference();
                    pageReference.setKey(in.readLong());
                    readPageFragments(in, pageReference);
                    readHash(in, pageReference);
                    references[i] = pageReference;
                }

                return references;
            } catch (final IOException e) {
                throw new SirixIOException(e);
            }
        }
    };

    private static void writeHash(DataOutput out, PageReference pageReference) throws IOException {
        if (pageReference.getHash() == null) {
            out.writeInt(-1);
        } else {
            final byte[] hash = pageReference.getHash();
            out.writeInt(hash.length);
            out.write(pageReference.getHash());
        }
    }

    private static void readHash(DataInput in, PageReference reference) throws IOException {
        final int hashLength = in.readInt();
        if (hashLength != -1) {
            final byte[] hash = new byte[hashLength];
            in.readFully(hash);

            reference.setHash(hash);
        }
    }

    private static void readPageFragments(DataInput in, PageReference reference) throws IOException {
        final int keysSize = in.readByte() & 0xff;
        if (keysSize > 0) {
            for (int i = 0; i < keysSize; i++) {
                final var revision = in.readInt();
                final var key = in.readLong();
                reference.addPageFragment(new PageFragmentKeyImpl(revision, key));
            }
        }
        final long key = in.readLong();
        reference.setKey(key);
    }

    private static void writePageFragments(DataOutput out, PageReference pageReference) throws IOException {
        final var keys = pageReference.getPageFragments();
        out.writeByte(keys.size());
        for (final PageFragmentKey key : keys) {
            out.writeInt(key.getRevision());
            out.writeLong(key.getKey());
        }
        out.writeLong(pageReference.getKey());
    }

    public static void serializeBitSet(DataOutput out, @Nonnull final BitSet bitmap) throws IOException {
        final var bytes = bitmap.toByteArray();
        final int len = bytes.length;
        out.writeShort(len);
        out.write(bytes);
    }

    public static BitSet deserializeBitSet(DataInput in) throws IOException {
        final int len = in.readShort();
        final var bytes = new byte[len];
        in.readFully(bytes);
        return BitSet.valueOf(bytes);
    }

    /**
     * Serialize all page references.
     *
     * @param out the output
     * @param pageReferences the page references
     * @param bitmap the bitmap
     * @throws SirixIOException if an I/O error occurs.
     */
    public abstract void serializeBitmapReferencesPage(DataOutput out, List<PageReference> pageReferences, BitSet bitmap);

    /**
     * Serialize all page references.
     *
     * @param out the output
     * @param pageReferences the page references
     * @param offsets the offset indexes
     * @throws SirixIOException if an I/O error occurs.
     */
    public abstract void serializeReferencesPage4(DataOutput out, List<PageReference> pageReferences,
            List<Short> offsets);

    /**
     * Deserialize all page references.
     *
     * @param referenceCount the number of references
     * @param in the input
     * @return the in-memory instances
     */
    public abstract DeserializedBitmapReferencesPageTuple deserializeBitmapReferencesPage(@Nonnegative int referenceCount,
            DataInput in);

    /**
     * Deserialize all page references.
     *
     * @param in the input
     * @return the in-memory instances
     */
    public abstract DeserializedReferencesPage4Tuple deserializeReferencesPage4(DataInput in);

    /**
     * Serialize all page references.
     *
     * @param out the output
     * @param pageReferences the page references
     * @throws SirixIOException if an I/O error occurs.
     */
    public abstract void serializeFullReferencesPage(DataOutput out, PageReference[] pageReferences);

    /**
     * Deserialize all page references.
     *
     * @param in the input
     * @return the in-memory instances
     */
    public abstract PageReference[] deserializeFullReferencesPage(DataInput in);
}
