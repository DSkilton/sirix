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
package org.sirix.node.json;

import org.sirix.api.visitor.JsonNodeVisitor;
import org.sirix.api.visitor.VisitResult;
import org.sirix.node.NodeKind;
import org.sirix.node.delegates.StructNodeDelegate;
import org.sirix.node.immutable.json.ImmutableBooleanNode;
import org.sirix.node.immutable.json.ImmutableObjectBooleanNode;
import org.sirix.node.interfaces.StructNode;

import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * <p>
 * Node representing a JSON boolean.
 * </p>
 */
public final class ObjectBooleanNode extends AbstractBooleanNode {

    /**
     * Constructor.
     *
     * @param boolValue the boolean value
     * @param structNodeDelegate delegate for {@link StructNode} implementation
     */
    public ObjectBooleanNode(final boolean boolValue, final StructNodeDelegate structNodeDelegate) {
        super(structNodeDelegate, boolValue);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.OBJECT_BOOLEAN_VALUE;
    }

    @Override
    public VisitResult acceptVisitor(final JsonNodeVisitor visitor) {
        return visitor.visit(ImmutableObjectBooleanNode.of(this));
    }

}
