package org.eclipse.jface.text.revisions.provisional;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.source.ILineDiffer;
import org.eclipse.ui.texteditor.ITextEditor;

public interface IRevisionInformationProvider {

	boolean canApply(IResource resource);
	
	RevisionInformation getRevisionInformation(IResource resource);
	
	ILineDiffer getDocumentLineDiffer(ITextViewer viewer, ITextEditor textEditor);
}
