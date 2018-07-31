/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - [CodeMining] Provide Java References/Implementation CodeMinings - Bug 529127
 */
package org.eclipse.debug.ui.codemining.provisional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
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
 * Java code mining provider to show method parameters code minings.
 * 
 * @since 3.15
 *
 */
public abstract class AbstractDebugElementCodeMiningProvider extends AbstractCodeMiningProvider {

	private IDebugContextListener contextListener;

	private final Map<RGB, Color> colorTable;

	public AbstractDebugElementCodeMiningProvider() {
		colorTable = new HashMap<>();
	}

	@Override
	public final CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			addDebugListener(viewer);
			return doProvideCodeMinings(viewer, monitor);
		});
	}

	private void addDebugListener(ITextViewer viewer) {
		if (contextListener == null) {
			addSynchronizedDebugListener(viewer);
		}
	}

	private synchronized void addSynchronizedDebugListener(ITextViewer viewer) {
		if (contextListener != null) {
			return;
		}
		contextListener = event -> {
			if ((event.getFlags() & DebugContextEvent.ACTIVATED) > 0 && viewer != null) {				
				((ISourceViewerExtension5) viewer).updateCodeMinings();
			}
		};
		DebugUITools.addPartDebugContextListener(getSite(), contextListener);
	}

	private void removeDebugListener() {
		if (contextListener != null) {
			DebugUITools.removePartDebugContextListener(getSite(), contextListener);
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
		colorTable.values().forEach(Color::dispose);
	}

	public Color getColor(RGB rgb, Display display) {
		Color color = colorTable.get(rgb);
		if (color == null) {
			color = new Color(display, rgb);
			colorTable.put(rgb, color);
		}
		return color;
	}
	
	protected abstract List<? extends ICodeMining> doProvideCodeMinings(ITextViewer viewer, IProgressMonitor monitor);

}
