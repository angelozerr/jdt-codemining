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
package org.eclipse.debug.ui.codemining.provisional;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.views.launch.DebugElementHelper;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * Abstract class to display debug variable value as mining at the line end.
 *
 * @param <T> the stack frame
 */
public abstract class AbstractDebugElementCodeMining<T extends IStackFrame> extends LineContentCodeMining {

	private final String fVariableName;
	private final T fStackFrame;
	private RGB rgb;

	/**
	 * Debug element mining constructor
	 *
	 * * @param position the position where the mining must be drawn.
	 *
	 * @param variableName the variable name to search in the given debug stack
	 *            frame
	 * @param stackFrame the current debug stack frame
	 * @param provider the owner codemining provider which creates this mining.
	 */
	protected AbstractDebugElementCodeMining(Position position, String variableName, T stackFrame, AbstractDebugElementCodeMiningProvider provider) {
		super(position, provider, null);
		this.fVariableName = variableName;
		this.fStackFrame = stackFrame;
	}

	@Override
	public String getLabel() {
		String label = super.getLabel();
		if (label == null) {
			updateLabel(fVariableName);
		}
		return super.getLabel();
	}

	/**
	 * Update the debug mining label with the debug variable value.
	 *
	 * @param variableName the variable name
	 */
	private void updateLabel(String variableName) {
		try {
			IVariable variable = findVariable(variableName);
			if (variable != null) {
				rgb = getRGB(variable);
				super.setLabel(DebugElementHelper.getLabel(variable));
			} else {
				super.setLabel(""); //$NON-NLS-1$
			}
		} catch (DebugException e) {
			super.setLabel(""); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the rgb value of the given variable and null otherwise.
	 *
	 * @param variable the debug variable.
	 * @return the rgb value of the given variable and null otherwise.
	 * @throws DebugException
	 */
	protected RGB getRGB(IVariable variable) throws DebugException {
		return null;
	}

	/**
	 * Returns the debug stack frame.
	 *
	 * @return the debug stack frame.
	 */
	protected T getStackFrame() {
		return fStackFrame;
	}

	@Override
	public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
		// increment x with 3 spaces width
		x += 3 * (int) gc.getFontMetrics().getAverageCharacterWidth();
		Point p = super.draw(gc, textWidget, color, x, y);
		if (rgb != null) {
			int width = drawSquare(rgb, gc, textWidget, x + p.x, y);
			p.x += width;
		}
		return p;
	}

	/**
	 * Draw square.
	 *
	 * @param rgb the rgb color
	 * @param gc the graphic context
	 * @param textWidget the text widget
	 * @param x the location y
	 * @param y the location y
	 * @return the square width.
	 */
	private int drawSquare(RGB rgb, GC gc, StyledText textWidget, int x, int y) {
		FontMetrics fontMetrics = gc.getFontMetrics();
		int size = getSquareSize(fontMetrics);
		x += fontMetrics.getLeading();
		y += fontMetrics.getDescent();

		Rectangle rect = new Rectangle(x, y, size, size);

		// Fill square
		gc.setBackground(getColor(rgb, textWidget.getDisplay()));
		gc.fillRectangle(rect);

		// Draw square box
		gc.setForeground(textWidget.getForeground());
		gc.drawRectangle(rect);
		return getSquareWidth(gc.getFontMetrics());
	}

	/**
	 * Returns the color from the gigen rgb.
	 *
	 * @param rgb the rgb
	 * @param display the display
	 * @return the color from the gigen rgb.
	 */
	private Color getColor(RGB rgb, Display display) {
		return ((AbstractDebugElementCodeMiningProvider) getProvider()).getColor(rgb, display);
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

	/**
	 * Returns the debug variable from the given variable name and null
	 * otherwise.
	 *
	 * @param variableName the variable name.
	 * @return the debug variable from the given variable name and null
	 *         otherwise.
	 * @throws DebugException
	 */
	protected abstract IVariable findVariable(String variableName) throws DebugException;

}
