package org.eclipse.jface.text.revisions.provisional;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ILineDiffer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class RevisionInformationProviderManager {

	private final List<IRevisionInformationProvider> providers;

	private final static RevisionInformationProviderManager INSTANCE = new RevisionInformationProviderManager();

	public static RevisionInformationProviderManager getInstance() {
		return INSTANCE;
	}

	private RevisionInformationProviderManager() {
		providers = new ArrayList<>();
	}

	public void addProvider(IRevisionInformationProvider provider) {
		providers.add(provider);
	}

	public RevisionInformation getRevisionInformation(IResource resource, ITextViewer viewer, ITextEditor textEditor) {
		for (IRevisionInformationProvider provider : providers) {
			if (provider.canApply(resource)) {
				RevisionInformation info = provider.getRevisionInformation(resource);
				if (info != null) {
					ILineDiffer differ = provider.getDocumentLineDiffer(viewer, textEditor);
					// Connect line differ
					IAnnotationModelExtension ext = (IAnnotationModelExtension) ((ISourceViewer) viewer).getAnnotationModel();
					ext.addAnnotationModel(RevisionInformationSupport.MY_QUICK_DIFF_MODEL_ID, (IAnnotationModel) differ);
					return info;
				}
			}
		}
		return null;
	}

}
