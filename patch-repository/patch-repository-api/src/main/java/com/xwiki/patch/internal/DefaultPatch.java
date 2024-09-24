/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.patch.internal;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.ObjectPropertyReference;
import org.xwiki.model.reference.ObjectReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xwiki.patch.Patch;

/**
 * Default implementation of the {@link Patch} interface.
 *
 * @version $Id$
 */
public class DefaultPatch implements Patch
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPatch.class);

    private DocumentReference documentReference;

    private XWikiContext xcontext;

    /**
     * Create a new {@link DefaultPatch} instance.
     *
     * @param documentReference the reference of the document holding the patch
     * @param xcontext the XWiki context used to access the patch
     */
    public DefaultPatch(DocumentReference documentReference, XWikiContext xcontext)
    {
        this.documentReference = documentReference;
        this.xcontext = xcontext;
    }

    @Override
    public String getId()
    {
        return this.documentReference.getName();
    }

    private XWikiDocument getDocument() throws XWikiException
    {
        return this.xcontext.getWiki().getDocument(this.documentReference, this.xcontext);
    }

    @SuppressWarnings("unchecked")
    private <T> T getPropertyValue(String propertyName, T defaultValue)
    {
        try {
            ObjectPropertyReference propertyReference = new ObjectPropertyReference(propertyName,
                new ObjectReference("PatchRepository.Code.PatchClass[0]", this.documentReference));
            return (T) getDocument().getXObjectProperty(propertyReference).getValue();
        } catch (Exception e) {
            LOGGER.warn("Failed to read property [{}] from patch [{}]. Root cause is [{}].", propertyName,
                this.documentReference, ExceptionUtils.getRootCauseMessage(e));
            return defaultValue;
        }
    }

    @Override
    public List<String> getVersions()
    {
        return getPropertyValue("versions", List.of());
    }

    @Override
    public boolean isPublished()
    {
        return Integer.valueOf(1).equals(getPropertyValue("published", Integer.valueOf(0)));
    }

    @Override
    public String getContentType()
    {
        return getAttachment().getMimeType();
    }

    @Override
    public InputStream getContent()
    {
        try {
            return getAttachment().getContentInputStream(this.xcontext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get patch content", e);
        }
    }

    private XWikiAttachment getAttachment()
    {
        try {
            return getDocument().getAttachmentList().stream()
                .filter(attachment -> attachment.getFilename().startsWith(getId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Patch attachment not found."));
        } catch (Exception e) {
            throw new RuntimeException("Failed to access patch document.", e);
        }
    }
}
