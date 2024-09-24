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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryFilter;
import org.xwiki.query.QueryManager;

import com.xpn.xwiki.XWikiContext;
import com.xwiki.patch.Patch;
import com.xwiki.patch.PatchRepository;

/**
 * Default implementation of the {@link PatchRepository} interface.
 * 
 * @version $Id$
 */
@Component
@Singleton
public class DefaultPatchRepository implements PatchRepository
{
    @Inject
    private Logger logger;

    @Inject
    private Provider<XWikiContext> xcontextProvider;

    @Inject
    private QueryManager queryManager;

    @Inject
    @Named("document")
    private QueryFilter documentQueryFilter;

    @Override
    public List<Patch> getPatches(String instanceId, String version, long since)
    {
        String statement = "from doc.object(PatchRepository.Code.PatchClass) as patch"
            + " where doc.creationDate > :since and :version member of patch.versions";

        try {
            Query query = this.queryManager.createQuery(statement, Query.XWQL);
            query.bindValue("version", version);
            query.bindValue("since", new Date(since));
            query.addFilter(this.documentQueryFilter);
            return query.execute().stream().map(DocumentReference.class::cast).map(this::getPatch)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        } catch (QueryException e) {
            this.logger.warn("Failed to get patches. Root cause is [{}].", ExceptionUtils.getRootCauseMessage(e));
            return List.of();
        }
    }

    @Override
    public Optional<Patch> getPatch(String patchId)
    {
        return getPatch(getDocumentReference(patchId));
    }

    private Optional<Patch> getPatch(DocumentReference documentReference)
    {
        try {
            XWikiContext xcontext = this.xcontextProvider.get();
            if (xcontext.getWiki().exists(documentReference, xcontext)) {
                return Optional.of(new DefaultPatch(documentReference, xcontext));
            }
        } catch (Exception e) {
            this.logger.warn("Failed to get patch [{}]. Root cause is [{}].", documentReference,
                ExceptionUtils.getRootCauseMessage(e));
        }
        return Optional.empty();
    }

    private DocumentReference getDocumentReference(String patchId)
    {
        return new DocumentReference("xwiki", "PatchRepository", patchId);
    }
}
