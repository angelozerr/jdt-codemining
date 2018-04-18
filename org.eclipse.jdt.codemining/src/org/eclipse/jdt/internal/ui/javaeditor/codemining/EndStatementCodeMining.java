package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;

public class EndStatementCodeMining extends LineContentCodeMining {

	protected EndStatementCodeMining(Statement node, ICodeMiningProvider provider) {
		super(new Position(node.getStartPosition() + node.getLength(), 1), provider, null);
		String s = node.toString();
		int index = s.indexOf("\n");
		if (index != -1) {
			// Get first line of statement
			s = s.substring(0, index);
		}
		super.setLabel("  // --> " + s);
	}
}
