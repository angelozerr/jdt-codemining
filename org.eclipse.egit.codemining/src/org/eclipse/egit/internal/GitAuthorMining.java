package org.eclipse.egit.internal;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jgit.blame.BlameResult;

import com.github.marlonlom.utilities.timeago.TimeAgo;

public class GitAuthorMining extends AbstractJavaCodeMining {

	public GitAuthorMining(IJavaElement element, BlameResult result, JavaEditor textEditor, IDocument document,
			JavaGitCodeMiningProvider provider) throws JavaModelException, BadLocationException {
		super(element, document, provider, null);
		int lineNumber = getLineNumber(element, document);

		try {
			RevisionRange range = provider.getRange(lineNumber);
			if (range != null) {
				super.setLabel(range.getRevision().getAuthor() + ", "
						+ TimeAgo.using(range.getRevision().getDate().getTime()) + " | "
						+ range.getRevision().getRegions().size() + " changes");
			}
		} catch (Exception e) {
			super.setLabel(e.getMessage());
			e.printStackTrace();
		}
	}

}
