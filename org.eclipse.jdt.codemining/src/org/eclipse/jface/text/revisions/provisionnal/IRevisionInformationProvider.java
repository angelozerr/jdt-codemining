package org.eclipse.jface.text.revisions.provisionnal;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.revisions.RevisionInformation;

public interface IRevisionInformationProvider {

	RevisionInformation getRevisionInformation(IResource resource);
}
