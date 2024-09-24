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
package com.xwiki.patch.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.xwiki.rest.XWikiRestException;

import com.xwiki.patch.rest.model.jaxb.Patch;

/**
 * Provides access to the patches published in the patch repository.
 *
 * @version $Id$
 */
@Path("/instances/{instanceId}/versions/{version}/patches")
public interface PatchResource
{
    /**
     * @param instanceId the id of the XWiki instance for which to retrieve the list of patches
     * @param version the XWiki version for which to retrieve the list of patches
     * @param since optional parameter to return only patches newer than the specified timestamp
     * @return the list of patches available for the specified XWiki version
     * @throws XWikiRestException if the list of patches cannot be retrieved
     */
    @GET
    List<Patch> getPatches(@PathParam("instanceId") String instanceId, @PathParam("version") String version,
        @QueryParam("since") long since) throws XWikiRestException;

    /**
     * @param instanceId the id of the XWiki instance for which to retrieve the patch
     * @param version the XWiki version for which to retrieve the patch
     * @param patchId the id of the patch to retrieve
     * @return a ZIP archive containing all the patched files
     * @throws XWikiRestException if the patch cannot be retrieved
     */
    @GET
    @Path("/{patchId}")
    Response getPatch(@PathParam("instanceId") String instanceId, @PathParam("version") String version,
        @PathParam("patchId") String patchId) throws XWikiRestException;
}
