package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.preferences.JavaEditorCodeMiningPreferencePage;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class CalleeJavaMethodParameterVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final ICodeMiningProvider provider;

	private final boolean showName;

	private final boolean showType;

	private final ITextEditor textEditor;

	public CalleeJavaMethodParameterVisitor(CompilationUnit cu, ITextEditor textEditor, List<ICodeMining> minings,
			ICodeMiningProvider provider) {
		this.cu = cu;
		this.minings = minings;
		this.provider = provider;
		this.showName = isShowName();
		this.showType = isShowType();
		this.textEditor = textEditor;
	}

	public boolean visit(MethodInvocation node) {
		List arguments = node.arguments();
		if (arguments.size() > 0) {
			for (int i = 0; i < arguments.size(); i++) {
				Expression exp = (Expression) arguments.get(i);
				minings.add(new JavaMethodParameterCodeMining(node, exp, i, cu, provider, showName, showType));
			}
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(IfStatement node) {
		super.endVisit(node);
		if (isShowEndStatement()) {
			minings.add(new EndStatementCodeMining(node, textEditor, provider));
		}
	}

	private boolean isShowName() {
		return JavaPlugin.getDefault().getPreferenceStore()
				.getBoolean(JavaEditorCodeMiningPreferencePage.PREF_SHOW_METHOD_PARAMETER_NAMES);
	}

	private boolean isShowType() {
		return JavaPlugin.getDefault().getPreferenceStore()
				.getBoolean(JavaEditorCodeMiningPreferencePage.PREF_SHOW_METHOD_PARAMETER_TYPES);
	}

	private boolean isShowEndStatement() {
		return JavaPlugin.getDefault().getPreferenceStore()
				.getBoolean(JavaEditorCodeMiningPreferencePage.PREF_SHOW_END_STATEMENT);
	}
}
