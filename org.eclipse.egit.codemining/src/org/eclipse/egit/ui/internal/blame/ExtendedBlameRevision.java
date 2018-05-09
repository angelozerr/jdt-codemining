package org.eclipse.egit.ui.internal.blame;

import org.eclipse.jface.text.revisions.IRevisionRangeExtension;

public class ExtendedBlameRevision extends BlameRevision implements IRevisionRangeExtension {

	@Override
	public String getAuthorEmail() {
		return getCommit().getAuthorIdent().getEmailAddress();
	}
}
