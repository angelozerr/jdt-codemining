package org.eclipse.egit.codemining.internal;

import org.eclipse.egit.codemining.ui.internal.blame.GitRevisionInformationProvider;
import org.eclipse.jface.text.revisions.provisional.RevisionInformationProviderManager;
import org.eclipse.ui.IStartup;

public class EGitCodeMiningStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// Just to register GitRevisionInformationProvider
		// FIXME: use extension point to register GitRevisionInformationProvider
		RevisionInformationProviderManager.getInstance().addProvider(new GitRevisionInformationProvider());
	}

}
