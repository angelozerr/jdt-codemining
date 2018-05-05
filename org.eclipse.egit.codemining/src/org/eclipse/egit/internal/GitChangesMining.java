package org.eclipse.egit.internal;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.revisions.RevisionRange;

public class GitChangesMining extends AbstractJavaCodeMining {

	public GitChangesMining(IJavaElement element, JavaEditor textEditor, IDocument document,
			JavaGitCodeMiningProvider provider) throws JavaModelException, BadLocationException {
		super(element, document, provider, null);
		int lineNumber = getLineNumber(element, document);

		try {
			RevisionRange range = provider.getRange(lineNumber);
			if (range != null) {
				super.setLabel(range.getRevision().getRegions().size() + " changes");
			}
		} catch (Exception e) {
			super.setLabel(e.getMessage());
			e.printStackTrace();
		}
	}

}
