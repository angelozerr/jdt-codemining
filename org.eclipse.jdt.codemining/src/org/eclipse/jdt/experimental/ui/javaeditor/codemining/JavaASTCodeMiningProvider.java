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
package org.eclipse.jdt.experimental.ui.javaeditor.codemining;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.experimental.ui.preferences.JavaPreferencesPropertyTester;
import org.eclipse.jdt.experimental.ui.preferences.MyPreferenceConstants;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Java code mining provider to show method parameters code minings.
 * 
 * @since 3.15
 *
 */
public class JavaASTCodeMiningProvider extends AbstractCodeMiningProvider {

	private final boolean showParameterName;

	private final boolean showParameterType;

	private final boolean showParameterOnlyForLiteral;

	private final boolean showParameterByUsingFilters;

	private final boolean showEndStatement;

	private final int endStatementMinLineNumber;

	private final boolean showJava10VarType;

	public JavaASTCodeMiningProvider() {
		this.showParameterName = isShowParameterName();
		this.showParameterType = isShowParameterType();
		this.showParameterOnlyForLiteral = isShowParameterOnlyForLiteral();
		this.showParameterByUsingFilters = isShowParameterByUsingFilters();
		this.showEndStatement = isShowEndStatement();
		this.endStatementMinLineNumber = getEndStatementMinLineNumber();
		this.showJava10VarType = isShowJava10VarType();
	}

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
			if (unit == null) {
				return null;
			}
			List<ICodeMining> minings = new ArrayList<>();
			collectCodeMinings(unit, textEditor, viewer, minings);
			monitor.isCanceled();
			return minings;
		});
	}

	private void collectCodeMinings(ITypeRoot unit, ITextEditor textEditor, ITextViewer viewer,
			List<ICodeMining> minings) {
		CompilationUnit cu = SharedASTProvider.getAST(unit, SharedASTProvider.WAIT_YES, null);
		JavaCodeMiningASTVisitor visitor = new JavaCodeMiningASTVisitor(cu, textEditor, viewer, minings, this);
		visitor.setShowParameterName(showParameterName);
		visitor.setShowParameterType(showParameterType);
		visitor.setShowParameterOnlyForLiteral(showParameterOnlyForLiteral);
		visitor.setShowParameterByUsingFilters(showParameterByUsingFilters);
		visitor.setShowEndStatement(showEndStatement);
		visitor.setEndStatementMinLineNumber(endStatementMinLineNumber);
		visitor.setShowJava10VarType(showJava10VarType);
		cu.accept(visitor);
	}

	private boolean isShowParameterName() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_NAMES);
	}

	private boolean isShowParameterType() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_TYPES);
	}

	private boolean isShowParameterOnlyForLiteral() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_ONLY_FOR_LITERAL);
	}

	private boolean isShowParameterByUsingFilters() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_BY_USING_FILTERS);
	}

	private boolean isShowEndStatement() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT);
	}

	private int getEndStatementMinLineNumber() {
		return MyPreferenceConstants.getPreferenceStore()
				.getInt(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT_MIN_LINE_NUMBER);
	}

	private boolean isShowJava10VarType() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JAVA10_VAR_TYPE);
	}

}
