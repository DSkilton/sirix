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
package org.sirix.axis.filter.json;

import org.brackit.xquery.atomic.QNm;
import org.sirix.api.json.JsonNodeReadOnlyTrx;
import org.sirix.api.xml.XmlNodeReadOnlyTrx;
import org.sirix.axis.filter.AbstractFilter;

/**
 * <p>
 * Match QNames of json object records.
 * </p>
 */
public final class JsonNameFilter extends AbstractFilter<JsonNodeReadOnlyTrx> {

    /**
     * Key of local name to test.
     */
    private final QNm mName;

    /**
     * Default constructor.
     *
     * @param rtx {@link XmlNodeReadOnlyTrx} this filter is bound to
     * @param name name to check
     */
    public JsonNameFilter(final JsonNodeReadOnlyTrx rtx, final QNm name) {
        super(rtx);

        mName = name;
    }

    /**
     * Default constructor.
     *
     * @param rtx {@link XmlNodeReadOnlyTrx} this filter is bound to
     * @param name name to check
     */
    public JsonNameFilter(final JsonNodeReadOnlyTrx rtx, final String name) {
        super(rtx);

        mName = new QNm(name);
    }

    @Override
    public boolean filter() {
        final JsonNodeReadOnlyTrx rtx = getTrx();

        return rtx.isObjectKey() && mName.equals(rtx.getName());
    }
}
