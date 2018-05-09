package org.eclipse.jface.text.revisions.codemining;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.revisions.RevisionRange;

public class RevisionAuthorsCodeMining extends LineHeaderCodeMining {

	public RevisionAuthorsCodeMining(int beforeLineNumber, IDocument document, ICodeMiningProvider provider,
			IRevisionRangeProvider rangeProvider) throws JavaModelException, BadLocationException {
		super(beforeLineNumber, document, provider);
		try {
			RevisionRange range = rangeProvider.getRange(beforeLineNumber);
			if (range != null) {
				List<String> authors = range.getRevision().getRegions().stream().map(r -> r.getRevision().getAuthor())
						.distinct().collect(Collectors.toList());
				long count = authors.size();
				StringBuilder label = new StringBuilder();
				label.append(count);
				label.append(" ");
				if (count == 1) {
					label.append("author");
				} else {
					label.append("authors");
				}
				label.append(" ");
				label.append(authors.get(0));
				if (count > 1) {
					label.append(" and others");
				}
				super.setLabel(label.toString());
			}
		} catch (Exception e) {
			super.setLabel(e.getMessage());
			e.printStackTrace();
		}
	}

}
