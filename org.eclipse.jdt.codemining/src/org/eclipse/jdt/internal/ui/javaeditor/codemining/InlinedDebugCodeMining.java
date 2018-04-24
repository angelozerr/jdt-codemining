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

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.views.launch.DebugElementHelper;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Show start statement text in the end statement as mining.
 *
 */
public class InlinedDebugCodeMining extends LineContentCodeMining {

	private final VariableDeclaration node;
	private final IJavaStackFrame frame;

	protected InlinedDebugCodeMining(VariableDeclaration node, IJavaStackFrame frame, ITextViewer viewer,
			ICodeMiningProvider provider) {
		super(getPosition(node, viewer.getDocument()), provider, null);
		this.node = node;
		this.frame = frame;
		updateLabel();
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

	private void updateLabel() {
		try {
			IJavaVariable variable = frame.findVariable(node.getName().getIdentifier());
			if (variable != null) {
				String s = "  " + DebugElementHelper.getLabel(variable);
				super.setLabel(s);
			} else {
				super.setLabel("");
			}
		} catch (DebugException e) {
			super.setLabel("");
		}
	}

	@Override
	public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
		String title = getLabel() != null ? getLabel() : "no command"; //$NON-NLS-1$
		Point p = gc.stringExtent(title);
		gc.setBackground(textWidget.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		Point space = gc.stringExtent(" ");
		gc.fillRectangle(x + space.x, y, p.x, p.y);
		gc.drawString(title, x, y, true);
		return p;
	}
}
