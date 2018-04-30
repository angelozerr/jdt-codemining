package org.eclipse.jdt.internal.ui.javaeditor.codemining.debug;

import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;

public class SimpleNameDebugCodeMining extends InlinedDebugCodeMining {

	public SimpleNameDebugCodeMining(SimpleName node, IJavaStackFrame frame, ITextViewer viewer,
			ICodeMiningProvider provider) {
		super(node, frame, viewer, provider);
		super.updateLabel(node.getIdentifier());
	}

}
