package org.eclipse.jface.text.revisions.provisional.codemining;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.revisions.provisional.IRevisionRangeProvider;
import org.eclipse.jface.text.source.ILineRange;

public class RevisionAuthorsCodeMining extends LineHeaderCodeMining {

	private final ILineRange lineRange;
	private final IRevisionRangeProvider rangeProvider;

	public RevisionAuthorsCodeMining(int beforeLineNumber, ILineRange lineRange, IDocument document,
			ICodeMiningProvider provider, IRevisionRangeProvider rangeProvider)
			throws JavaModelException, BadLocationException {
		super(beforeLineNumber, document, provider);
		this.lineRange = lineRange;
		this.rangeProvider = rangeProvider;
		if (rangeProvider.isInitialized()) {
			updateLabel();
		}
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		if (getLabel() != null) {
			return super.doResolve(viewer, monitor);
		}
		return CompletableFuture.runAsync(() -> {
			updateLabel();
		});
	}

	private void updateLabel() {
		List<RevisionRange> ranges = rangeProvider.getRanges(lineRange);
		if (ranges != null && ranges.size() > 0) {
			long count = ranges.stream().map(r -> r.getRevision().getAuthor()).distinct().count();
			StringBuilder label = new StringBuilder();
			label.append(count);
			label.append(" ");
			if (count == 1) {
				label.append("author");
			} else {
				label.append("authors");
			}
			label.append(" (");
			label.append(ranges.get(0).getRevision().getAuthor());
			if (count > 1) {
				label.append(" and others");
			}
			label.append(")");
			super.setLabel(label.toString());
		} else {
			super.setLabel("");
		}
	}

}
