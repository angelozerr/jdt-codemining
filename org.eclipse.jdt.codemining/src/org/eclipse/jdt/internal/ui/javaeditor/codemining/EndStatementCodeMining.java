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
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Show start statement text in the end statement as mining.
 *
 */
public class EndStatementCodeMining extends LineContentCodeMining {

	protected EndStatementCodeMining(Statement node, ITextEditor textEditor, ICodeMiningProvider provider) {
		super(new Position(node.getStartPosition() + node.getLength(), 1), provider, e -> {
			textEditor.selectAndReveal(node.getStartPosition(), 0);
		});
		String s = node.toString();
		int index = s.indexOf("\n");
		if (index != -1) {
			// Get first line of statement
			s = s.substring(0, index);
		}
		super.setLabel("  // --> " + s);
	}
}
