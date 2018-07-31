/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - [CodeMining] Provide Java References/Implementation CodeMinings - Bug 529127
 */
package org.eclipse.jdt.experimental.debug.ui.codemining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.experimental.ui.preferences.JavaPreferencesPropertyTester;
import org.eclipse.jdt.experimental.ui.preferences.MyPreferenceConstants;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaLaunchingCodeMiningProvider extends AbstractCodeMiningProvider {

	private final boolean showMainRun;

	private final boolean showMainDebug;

	public JavaLaunchingCodeMiningProvider() {
		showMainRun = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_RUN);
		showMainDebug = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_DEBUG);
	}

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
			if (unit == null) {
				return Collections.emptyList();
			}
			try {
				IJavaElement[] elements = unit.getChildren();
				List<ICodeMining> minings = new ArrayList<>(elements.length);
				collectMinings(unit, textEditor, unit.getChildren(), minings, viewer, monitor);
				monitor.isCanceled();
				return minings;
			} catch (JavaModelException e) {
				// Should never occur
			}
			return Collections.emptyList();
		});
	}

	/**
	 * Collect java code minings.
	 * 
	 * @param unit       the compilation unit
	 * @param textEditor the Java editor
	 * @param elements   the java elements to track
	 * @param minings    the current list of minings to update
	 * @param viewer     the viewer
	 * @param monitor    the monitor
	 * @throws JavaModelException thrown when java model error
	 */
	private void collectMinings(ITypeRoot unit, ITextEditor textEditor, IJavaElement[] elements,
			List<ICodeMining> minings, ITextViewer viewer, IProgressMonitor monitor) throws JavaModelException {
		for (IJavaElement element : elements) {
			if (monitor.isCanceled()) {
				return;
			}
			if (element.getElementType() == IJavaElement.TYPE) {
				collectMinings(unit, textEditor, ((IType) element).getChildren(), minings, viewer, monitor);
			} else if (element.getElementType() != IJavaElement.METHOD) {
				continue;
			}
			if (showMainRun) {
				if (element instanceof IMethod && ((IMethod) (element)).isMainMethod()) {
					try {
						minings.add(new JavaLaunchCodeMining(element, "Run", "run", viewer.getDocument(), this));
					} catch (BadLocationException e) {
						// Should never occur
					}
				}
			}
			if (showMainDebug) {
				if (element instanceof IMethod && ((IMethod) (element)).isMainMethod()) {
					try {
						minings.add(new JavaLaunchCodeMining(element, "Debug", "debug", viewer.getDocument(), this));
					} catch (BadLocationException e) {
						// Should never occur
					}
				}
			}
		}
	}
}
