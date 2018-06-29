package org.eclipse.egit.codemining.ui.internal.blame;

import org.eclipse.egit.ui.internal.blame.BlameRevision;
import org.eclipse.jface.text.revisions.provisional.IRevisionRangeExtension;
import org.eclipse.jgit.util.RelativeDateFormatter;

public class ExtendedBlameRevision extends BlameRevision implements IRevisionRangeExtension {

	@Override
	public String getAuthorEmail() {
		return getCommit().getAuthorIdent().getEmailAddress();
	}

	@Override
	public String getFormattedTime() {
		// To use preferences from Preferences->Team->Git->Date Format
		// return PreferenceBasedDateFormatter.create().formatDate(getDate());
		// but IMHO I think it's better to use "time ago"
		return RelativeDateFormatter.format(getDate());
	}
}
