/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - [code mining] Provide Debug codemining classes - Bug 537546
 */
package org.eclipse.debug.ui.codemining.provisional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.source.ISourceViewerExtension5;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Abstract class mining provider to display debug variable value in a given
 * position.
 *
 * @since 3.15
 *
 */
public abstract class AbstractDebugVariableCodeMiningProvider<T extends IStackFrame>
		extends AbstractCodeMiningProvider {

	private IDebugContextListener fContextListener;

	private final Map<RGB, Color> fColorTable;

	public AbstractDebugVariableCodeMiningProvider() {
		fColorTable = new HashMap<>();
	}

	@Override
	public final CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			addDebugListener(viewer);
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			T stackFrame = getStackFrame(viewer, textEditor);
			if (stackFrame == null) {
				return Collections.emptyList();
			}
			return provideCodeMinings(viewer, stackFrame, monitor);
		});
	}

	private void addDebugListener(ITextViewer viewer) {
		if (fContextListener == null) {
			addSynchronizedDebugListener(viewer);
		}
	}

	private synchronized void addSynchronizedDebugListener(ITextViewer viewer) {
		if (fContextListener != null) {
			return;
		}
		// When debug context changed, debug variable minings of the current stack frame
		// must be updated.
		fContextListener = event -> {
			if ((event.getFlags() & DebugContextEvent.ACTIVATED) > 0 && viewer != null) {
				((ISourceViewerExtension5) viewer).updateCodeMinings();
			}
		};
		DebugUITools.addPartDebugContextListener(getSite(), fContextListener);
	}

	private void removeDebugListener() {
		if (fContextListener != null) {
			DebugUITools.removePartDebugContextListener(getSite(), fContextListener);
		}
	}

	private IWorkbenchPartSite getSite() {
		ITextEditor textEditor = super.getAdapter(ITextEditor.class);
		return textEditor.getSite();
	}

	@Override
	public void dispose() {
		removeDebugListener();
		super.dispose();
		fColorTable.values().forEach(Color::dispose);
	}

	/**
	 * Returns the color from the given rgb.
	 *
	 * @param rgb     the rgb values
	 * @param display the display.
	 * @return the color from the given rgb.
	 */
	public Color getColor(RGB rgb, Display display) {
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(display, rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

	/**
	 * Returns the stack frame in which to search for variables, or
	 * <code>null</code> if none.
	 *
	 * @param viewer
	 * @param stackFrame
	 * @param monitor
	 * @return
	 */
	protected abstract T getStackFrame(ITextViewer viewer, ITextEditor textEditor);

	/**
	 * Collection minings included inside variables of the given stack frame.
	 *
	 * @param viewer
	 * @param stackFrame
	 * @param monitor
	 * @return
	 */
	protected abstract List<? extends ICodeMining> provideCodeMinings(ITextViewer viewer, T stackFrame,
			IProgressMonitor monitor);
}
