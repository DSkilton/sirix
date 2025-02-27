/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sirix.node.xml;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.hash.HashCode;
import org.brackit.xquery.atomic.QNm;
import org.sirix.api.visitor.VisitResult;
import org.sirix.api.visitor.XmlNodeVisitor;
import org.sirix.node.AbstractForwardingNode;
import org.sirix.node.NodeKind;
import org.sirix.node.SirixDeweyID;
import org.sirix.node.delegates.NameNodeDelegate;
import org.sirix.node.delegates.NodeDelegate;
import org.sirix.node.delegates.StructNodeDelegate;
import org.sirix.node.delegates.ValueNodeDelegate;
import org.sirix.node.immutable.xml.ImmutableAttributeNode;
import org.sirix.node.interfaces.NameNode;
import org.sirix.node.interfaces.Node;
import org.sirix.node.interfaces.ValueNode;
import org.sirix.node.interfaces.immutable.ImmutableXmlNode;
import org.sirix.settings.Constants;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.math.BigInteger;

/**
 * <p>
 * Node representing an attribute.
 * </p>
 */
public final class AttributeNode extends AbstractForwardingNode implements ValueNode, NameNode, ImmutableXmlNode {

    /**
     * Delegate for name node information.
     */
    private final NameNodeDelegate mNameDel;

    /**
     * Delegate for val node information.
     */
    private final ValueNodeDelegate mValDel;

    /**
     * Node delegate.
     */
    private final NodeDelegate mNodeDel;

    /**
     * The qualified name.
     */
    private final QNm mQNm;

    private BigInteger mHash;

    /**
     * Creating an attribute.
     *
     * @param nodeDel {@link NodeDelegate} to be set
     * @param nodeDel {@link StructNodeDelegate} to be set
     * @param valDel {@link ValueNodeDelegate} to be set
     */
    public AttributeNode(final NodeDelegate nodeDel, final NameNodeDelegate nameDel, final ValueNodeDelegate valDel,
            final QNm qNm) {
        assert nodeDel != null : "nodeDel must not be null!";
        mNodeDel = nodeDel;
        assert nameDel != null : "nameDel must not be null!";
        mNameDel = nameDel;
        assert valDel != null : "valDel must not be null!";
        mValDel = valDel;
        assert qNm != null : "qNm must not be null!";
        mQNm = qNm;
    }

    /**
     * Creating an attribute.
     *
     * @param nodeDel {@link NodeDelegate} to be set
     * @param nodeDel {@link StructNodeDelegate} to be set
     * @param valDel {@link ValueNodeDelegate} to be set
     */
    public AttributeNode(final BigInteger hashCode, final NodeDelegate nodeDel, final NameNodeDelegate nameDel,
            final ValueNodeDelegate valDel, final QNm qNm) {
        mHash = hashCode;
        assert nodeDel != null : "nodeDel must not be null!";
        mNodeDel = nodeDel;
        assert nameDel != null : "nameDel must not be null!";
        mNameDel = nameDel;
        assert valDel != null : "valDel must not be null!";
        mValDel = valDel;
        assert qNm != null : "qNm must not be null!";
        mQNm = qNm;
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.ATTRIBUTE;
    }

    @Override
    public BigInteger computeHash() {
        final HashCode valueHashCode = mNodeDel.getHashFunction().hashBytes(getRawValue());

        final BigInteger valueBigInteger = new BigInteger(1, valueHashCode.asBytes());

        BigInteger result = BigInteger.ONE;

        result = BigInteger.valueOf(31).multiply(result).add(mNodeDel.computeHash());
        result = BigInteger.valueOf(31).multiply(result).add(mNameDel.computeHash());
        result = BigInteger.valueOf(31).multiply(result).add(valueBigInteger);

        return Node.to128BitsAtMaximumBigInteger(result);
    }

    @Override
    public void setHash(BigInteger hash) {
        mHash = Node.to128BitsAtMaximumBigInteger(hash);
    }

    @Override
    public BigInteger getHash() {
        return mHash;
    }

    @Override
    public VisitResult acceptVisitor(final XmlNodeVisitor visitor) {
        return visitor.visit(ImmutableAttributeNode.of(this));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("nameDel", mNameDel).add("valDel", mValDel).toString();
    }

    @Override
    public int getPrefixKey() {
        return mNameDel.getPrefixKey();
    }

    @Override
    public int getLocalNameKey() {
        return mNameDel.getLocalNameKey();
    }

    @Override
    public int getURIKey() {
        return mNameDel.getURIKey();
    }

    @Override
    public void setPrefixKey(final int prefixKey) {
        mNameDel.setPrefixKey(prefixKey);
    }

    @Override
    public void setLocalNameKey(final int localNameKey) {
        mNameDel.setLocalNameKey(localNameKey);
    }

    @Override
    public void setURIKey(final int uriKey) {
        mNameDel.setURIKey(uriKey);
    }

    @Override
    public byte[] getRawValue() {
        return mValDel.getRawValue();
    }

    @Override
    public void setValue(final byte[] value) {
        mValDel.setValue(value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mNameDel, mValDel);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj instanceof AttributeNode) {
            final AttributeNode other = (AttributeNode) obj;
            return Objects.equal(mNameDel, other.mNameDel) && Objects.equal(mValDel, other.mValDel);
        }
        return false;
    }

    @Override
    public void setPathNodeKey(final @Nonnegative long pathNodeKey) {
        mNameDel.setPathNodeKey(pathNodeKey);
    }

    @Override
    public long getPathNodeKey() {
        return mNameDel.getPathNodeKey();
    }

    /**
     * Getting the inlying {@link NameNodeDelegate}.
     *
     * @return the {@link NameNodeDelegate} instance
     */
    public NameNodeDelegate getNameNodeDelegate() {
        return mNameDel;
    }

    /**
     * Getting the inlying {@link ValueNodeDelegate}.
     *
     * @return the {@link ValueNodeDelegate} instance
     */
    public ValueNodeDelegate getValNodeDelegate() {
        return mValDel;
    }

    @Override
    protected NodeDelegate delegate() {
        return mNodeDel;
    }

    @Override
    public QNm getName() {
        return mQNm;
    }

    @Override
    public String getValue() {
        return new String(mValDel.getRawValue(), Constants.DEFAULT_ENCODING);
    }

    @Override
    public SirixDeweyID getDeweyID() {
        return mNodeDel.getDeweyID();
    }

    @Override
    public int getTypeKey() {
        return mNodeDel.getTypeKey();
    }
}
