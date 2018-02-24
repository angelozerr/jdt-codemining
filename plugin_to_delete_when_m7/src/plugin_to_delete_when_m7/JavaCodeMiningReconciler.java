/*******************************************************************************
 * Copyright (c) 2018 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - [CodeMining] Update CodeMinings with IJavaReconcilingListener - Bug 530825
 *******************************************************************************/
package plugin_to_delete_when_m7;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension5;

/**
 * Java codemining reconciler.
 *
 * @since 3.14
 */
public class JavaCodeMiningReconciler implements IJavaReconcilingListener {

	/** The Java editor this Java codemining reconciler is installed on */
	private JavaEditor fEditor;

	/** The source viewer this Java codemining reconciler is installed on */
	private ISourceViewer fSourceViewer;

	/*
	 * @see
	 * org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener#reconciled(
	 * CompilationUnit, boolean, IProgressMonitor)
	 */
	@Override
	public void reconciled(CompilationUnit ast, boolean forced, IProgressMonitor progressMonitor) {
		updateCodeMinings();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener#
	 * aboutToBeReconciled()
	 */
	@Override
	public void aboutToBeReconciled() {
		// Do nothing
	}

	/**
	 * Install this reconciler on the given editor, codeminings.
	 * 
	 * @param editor
	 *            the editor
	 * @param sourceViewer
	 *            the source viewer
	 */
	public void install(JavaEditor editor, ISourceViewer sourceViewer) {
		fEditor = editor;
		fSourceViewer = sourceViewer;

		if (fEditor instanceof CompilationUnitEditor) {
			addReconcileListener((CompilationUnitEditor) fEditor);
			updateCodeMinings();
		}
	}

	/**
	 * Uninstall this reconciler from the editor
	 */
	public void uninstall() {
		if (fEditor != null) {
			if (fEditor instanceof CompilationUnitEditor)
				removeReconcileListener((CompilationUnitEditor) fEditor);
			fEditor = null;
		}
		fSourceViewer = null;
	}

	/**
	 * Update Java codeminings in the Java editor.
	 */
	private void updateCodeMinings() {
		((ISourceViewerExtension5) fSourceViewer).updateCodeMinings();
	}

	private void addReconcileListener(CompilationUnitEditor textEditor) {
		try {
			Method m = CompilationUnitEditor.class.getDeclaredMethod("addReconcileListener",
					IJavaReconcilingListener.class);
			m.setAccessible(true);
			m.invoke(textEditor, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void removeReconcileListener(CompilationUnitEditor textEditor) {
		try {
			Method m = CompilationUnitEditor.class.getDeclaredMethod("removeReconcileListener",
					IJavaReconcilingListener.class);
			m.setAccessible(true);
			m.invoke(textEditor, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
