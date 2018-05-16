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
package org.eclipse.jdt.internal.ui.javaeditor.codemining.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.views.launch.DebugElementHelper;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.ui.javaeditor.codemining.JavaMethodParameterCodeMiningProvider;
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
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Show start statement text in the end statement as mining.
 *
 */
public abstract class InlinedDebugCodeMining extends LineContentCodeMining {

	private final IJavaStackFrame frame;
	private RGB rgb;

	protected InlinedDebugCodeMining(ASTNode node, IJavaStackFrame frame, ITextViewer viewer,
			ICodeMiningProvider provider) {
		super(getPosition(node, viewer.getDocument()), provider, null);
		this.frame = frame;
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

	protected void updateLabel(String variableName) {
		try {
			IJavaVariable variable = frame.findVariable(variableName);
			if (variable != null) {
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
						rgb = new RGB(red, green, blue);
					}
				}
				String s = " " + DebugElementHelper.getLabel(variable);
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
		gc.setBackground(textWidget.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		Point space = gc.stringExtent(" ");
		gc.fillRectangle(x + space.x, y, p.x, p.y);
		gc.drawString(title, x, y, true);
		if (rgb != null) {
			int width = drawSquare(rgb, gc, textWidget, x + p.x, y);
			p.x += width;
		}
		return p;
	}

	private int drawSquare(RGB rgb, GC gc, StyledText textWidget, int x, int y) {
		FontMetrics fontMetrics = gc.getFontMetrics();
		int size = getSquareSize(fontMetrics);
		x += fontMetrics.getLeading();
		y += fontMetrics.getDescent();

		Rectangle rect = new Rectangle(x, y, size, size);

		// Fill square
		gc.setBackground(
				((JavaMethodParameterCodeMiningProvider) getProvider()).getColor(rgb, textWidget.getDisplay()));
		gc.fillRectangle(rect);

		// Draw square box
		gc.setForeground(textWidget.getForeground());
		gc.drawRectangle(rect);
		return getSquareWidth(gc.getFontMetrics());
	}

	/**
	 * Returns the colorized square size.
	 *
	 * @param fontMetrics
	 * @return the colorized square size.
	 */
	public static int getSquareSize(FontMetrics fontMetrics) {
		return fontMetrics.getHeight() - 2 * fontMetrics.getDescent();
	}

	/**
	 * Compute width of square
	 *
	 * @param styledText
	 * @return the width of square
	 */
	private static int getSquareWidth(FontMetrics fontMetrics) {
		// width = 2 spaces + size width of square
		int width = 2 * fontMetrics.getAverageCharWidth() + getSquareSize(fontMetrics);
		return width;
	}
}
