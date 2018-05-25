/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com>
 */
package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Show start statement text in the end statement as mining.
 *
 */
public class EndStatementCodeMining extends LineContentCodeMining {

	protected EndStatementCodeMining(Statement node, ITextEditor textEditor, ITextViewer viewer, int minLineNumber,
			ICodeMiningProvider provider) {
		super(new Position(node.getStartPosition() + node.getLength(), 1), provider, e -> {
			textEditor.selectAndReveal(node.getStartPosition(), 0);
		});
		String label = getLabel(node, viewer.getDocument(), minLineNumber);
		super.setLabel(label);
	}

	private static String getLabel(Statement node, IDocument document, int minLineNumber) {
		try {
			int offset = node.getStartPosition();
			if (minLineNumber > 0) {
				int startLine = document.getLineOfOffset(offset);
				int endLine = document.getLineOfOffset(offset + node.getLength());
				if (endLine - startLine <= minLineNumber) {
					return "";
				}
			}
			IRegion first = document.getLineInformationOfOffset(offset);
			return "  // --> " + document.get(first.getOffset(), first.getLength()).trim();
		} catch (BadLocationException e1) {
			return "";
		}
	}
}
