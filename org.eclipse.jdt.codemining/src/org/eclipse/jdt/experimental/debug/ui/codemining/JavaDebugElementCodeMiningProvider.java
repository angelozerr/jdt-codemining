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
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.codemining.provisional.AbstractDebugVariableCodeMiningProvider;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Java code mining provider to show method parameters code minings.
 * 
 * @since 3.15
 *
 */
public class JavaDebugElementCodeMiningProvider extends AbstractDebugVariableCodeMiningProvider<IJavaStackFrame> {

	@Override
	protected List provideCodeMinings(ITextViewer viewer, IJavaStackFrame frame, IProgressMonitor monitor) {
		List<ICodeMining> minings = new ArrayList<>();
		ITextEditor textEditor = super.getAdapter(ITextEditor.class);
		ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
		if (unit == null) {
			return minings;
		}
		CompilationUnit cu = SharedASTProvider.getAST(unit, SharedASTProvider.WAIT_YES, null);
		JavaDebugElementCodeMiningASTVisitor visitor = new JavaDebugElementCodeMiningASTVisitor(frame, cu, viewer,
				minings, this);
		cu.accept(visitor);
		return minings;
	}

	@Override
	protected IJavaStackFrame getStackFrame(ITextViewer viewer, ITextEditor textEditor) {
		IAdaptable adaptable = DebugUITools.getPartDebugContext(textEditor.getSite());
		if (adaptable != null) {
			return adaptable.getAdapter(IJavaStackFrame.class);
		}
		return null;
	}

}
