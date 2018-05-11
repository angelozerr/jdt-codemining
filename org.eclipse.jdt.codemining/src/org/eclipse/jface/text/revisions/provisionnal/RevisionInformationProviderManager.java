package org.eclipse.jface.text.revisions.provisionnal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.revisions.RevisionInformation;

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

	public RevisionInformation getRevisionInformation(IResource resource) {
		for (IRevisionInformationProvider provider : providers) {
			RevisionInformation info = provider.getRevisionInformation(resource);
			if (info != null) {
				return info;
			}
		}
		return null;
	}

}
