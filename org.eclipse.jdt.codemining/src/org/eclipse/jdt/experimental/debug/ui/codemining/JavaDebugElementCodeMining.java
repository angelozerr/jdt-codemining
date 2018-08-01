package org.eclipse.jdt.experimental.debug.ui.codemining;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.codemining.provisional.AbstractDebugVariableCodeMining;
import org.eclipse.debug.ui.codemining.provisional.AbstractDebugVariableCodeMiningProvider;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.RGB;

public class JavaDebugElementCodeMining extends AbstractDebugVariableCodeMining<IJavaStackFrame> {

	public JavaDebugElementCodeMining(SimpleName node, IJavaStackFrame frame, ITextViewer viewer,
			AbstractDebugVariableCodeMiningProvider provider) {
		super(getPosition(node, viewer.getDocument()), node.getIdentifier(), frame, provider);
	}

	private static Position getPosition(ASTNode node, IDocument document) {
		int offset = node.getStartPosition();
		try {
			IRegion region = document.getLineInformationOfOffset(offset);
			return new Position(region.getOffset() + region.getLength(), 1);
		} catch (BadLocationException e) {
			return new Position(offset, 1);
		}
	}

	@Override
	protected IVariable findVariable(String variableName) throws DebugException {
		return getStackFrame().findVariable(variableName);
	}

	@Override
	protected RGB getRGB(IVariable variable) throws DebugException {
		IJavaValue v = (IJavaValue) variable.getValue();
		if ("org.eclipse.swt.graphics.RGB".equals(v.getJavaType().toString())) {
			Integer red = null, blue = null, green = null;
			for (IVariable field : v.getVariables()) {
				if ("red".equals(field.getName().toString())) {
					red = Integer.parseInt(field.getValue().getValueString());
				} else if ("green".equals(field.getName().toString())) {
					green = Integer.parseInt(field.getValue().getValueString());
				} else if ("blue".equals(field.getName().toString())) {
					blue = Integer.parseInt(field.getValue().getValueString());
				}
			}
			if (red != null && green != null && blue != null) {
				return new RGB(red, green, blue);
			}
		}
		return null;
	}

}
