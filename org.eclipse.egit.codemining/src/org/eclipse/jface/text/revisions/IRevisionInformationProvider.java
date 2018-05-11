package org.eclipse.jface.text.revisions;

import org.eclipse.core.resources.IResource;

public interface IRevisionInformationProvider {

	RevisionInformation getRevisionInformation(IResource resource);
}
