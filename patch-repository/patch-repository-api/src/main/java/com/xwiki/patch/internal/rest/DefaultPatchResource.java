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
package com.xwiki.patch.internal.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.xwiki.component.annotation.Component;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.XWikiRestException;

import com.xwiki.patch.InstanceIdValidator;
import com.xwiki.patch.PatchRepository;
import com.xwiki.patch.rest.PatchResource;
import com.xwiki.patch.rest.model.jaxb.Patch;

/**
 * Default implementation of the {@link PatchResource} interface.
 * 
 * @version $Id$
 */
@Component
@Named("com.xwiki.patch.internal.rest.DefaultPatchResource")
@Singleton
public class DefaultPatchResource extends XWikiResource implements PatchResource
{
    @Inject
    private InstanceIdValidator instanceIdValidator;

    @Inject
    private PatchRepository patchRepository;

    @Override
    public List<Patch> getPatches(String instanceId, String version, long since) throws XWikiRestException
    {
        if (this.instanceIdValidator.isValid(instanceId)) {
            return this.patchRepository.getPatches(instanceId, version, since).stream()
                .filter(com.xwiki.patch.Patch::isPublished)
                .map(patch -> new Patch().withId(patch.getId()).withVersions(patch.getVersions()))
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Response getPatch(String instanceId, String version, String patchId) throws XWikiRestException
    {
        if (this.instanceIdValidator.isValid(instanceId)) {
            Optional<com.xwiki.patch.Patch> patch = this.patchRepository.getPatch(patchId);
            if (patch.isPresent() && patch.get().isFor(instanceId, version)) {
                return Response.ok().type(MediaType.valueOf(patch.get().getContentType()))
                    .entity(patch.get().getContent()).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
