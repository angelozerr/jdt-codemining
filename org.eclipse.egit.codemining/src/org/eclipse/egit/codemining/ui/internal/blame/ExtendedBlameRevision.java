package org.eclipse.egit.codemining.ui.internal.blame;

import org.eclipse.egit.ui.internal.blame.BlameRevision;
import org.eclipse.jface.text.revisions.provisionnal.IRevisionRangeExtension;

public class ExtendedBlameRevision extends BlameRevision implements IRevisionRangeExtension {

	@Override
	public String getAuthorEmail() {
		return getCommit().getAuthorIdent().getEmailAddress();
	}
}
