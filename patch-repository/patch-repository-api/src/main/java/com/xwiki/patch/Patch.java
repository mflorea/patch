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
package com.xwiki.patch;

import java.io.InputStream;
import java.util.List;

/**
 * Represents a patch that can be applied to a XWiki instance.
 *
 * @version $Id$
 */
public interface Patch
{
    /**
     * @return the id of the patch
     */
    String getId();

    /**
     * @return the versions of XWiki that this patch can be applied to
     */
    List<String> getVersions();

    /**
     * @return {@code true} if the patch is ready to be applied, {@code false} otherwise
     */
    boolean isPublished();

    /**
     * @return the patch content type
     */
    String getContentType();

    /**
     * @return the content of the patch
     */
    InputStream getContent();
}
