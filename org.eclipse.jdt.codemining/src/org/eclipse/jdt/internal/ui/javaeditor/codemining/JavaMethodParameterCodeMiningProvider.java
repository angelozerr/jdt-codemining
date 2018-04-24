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
package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.debug.ui.contexts.IDebugContextService;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.source.ISourceViewerExtension5;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Java code mining provider to show method parameters code minings.
 * 
 * @since 3.14
 *
 */
public class JavaMethodParameterCodeMiningProvider extends AbstractCodeMiningProvider {

	private IDebugContextListener contextListener;

	private IDebugContextService service;

	public JavaMethodParameterCodeMiningProvider() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		service = DebugUITools.getDebugContextManager().getContextService(window);
	}

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			addDebugListener(viewer);
			monitor.isCanceled();
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
			if (unit == null) {
				return null;
			}
			try {
				IJavaElement[] elements = unit.getChildren();
				List<ICodeMining> minings = new ArrayList<>(elements.length);
				collectLineContentCodeMinings(unit, textEditor, viewer, minings);
				monitor.isCanceled();
				return minings;
			} catch (JavaModelException e) {
				// TODO: what should we done when there are some errors?
			}
			return null;
		});
	}

	private void addDebugListener(ITextViewer viewer) {
		if (contextListener == null) {
			contextListener = event -> {
				if (viewer != null) {
					((ISourceViewerExtension5) viewer).updateCodeMinings();
				}
			};
			service.addDebugContextListener(contextListener);
		}
	}

	private void collectLineContentCodeMinings(ITypeRoot unit, ITextEditor textEditor, ITextViewer viewer,
			List<ICodeMining> minings) {
		CompilationUnit cu = SharedASTProvider.getAST(unit, SharedASTProvider.WAIT_YES, null);
		JavaCodeMiningASTVisitor visitor = new JavaCodeMiningASTVisitor(cu, textEditor, viewer, minings, this);
		cu.accept(visitor);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (contextListener != null) {
			service.removeDebugContextListener(contextListener);
		}
	}
}
