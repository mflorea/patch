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

import java.util.List;
import java.util.Optional;

import org.xwiki.component.annotation.Role;

/**
 * Provides access to the patches published in the patch repository.
 *
 * @version $Id$
 */
@Role
public interface PatchRepository
{
    /**
     * @param instanceId the id of the XWiki instance for which to retrieve the list of patches
     * @param version the XWiki version for which to retrieve the list of patches
     * @param since return only patches newer than the specified timestamp
     * @return the list of patches available for the specified XWiki version
     */
    List<Patch> getPatches(String instanceId, String version, long since);

    /**
     * @param patchId the id of the patch to retrieve
     * @return the patch with the specified id
     */
    Optional<Patch> getPatch(String patchId);
}
