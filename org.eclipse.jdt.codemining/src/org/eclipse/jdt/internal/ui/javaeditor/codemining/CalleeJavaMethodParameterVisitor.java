package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.List;

import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.preferences.CodeMiningPreferencePage;

public class CalleeJavaMethodParameterVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final ICodeMiningProvider provider;

	private final boolean showName;

	private final boolean showType;

	public CalleeJavaMethodParameterVisitor(CompilationUnit cu, List<ICodeMining> minings, ICodeMiningProvider provider) {
		this.cu= cu;
		this.minings= minings;
		this.provider= provider;
		this.showName = isShowName();
		this.showType = isShowType();
	}

	public boolean visit(MethodInvocation node) {
		List arguments= node.arguments();
		if (arguments.size() > 0) {

			for (int i= 0; i < arguments.size(); i++) {
				Expression exp= (Expression)arguments.get(i);
				minings.add(new JavaMethodParameterCodeMining(node, exp, i, cu, provider, showName, showType));
			}
		}
		return super.visit(node);
	}
	
	private boolean isShowName() {
		return JavaPlugin.getDefault().getPreferenceStore().getBoolean(CodeMiningPreferencePage.PREF_SHOW_METHOD_PARAMETER_NAMES);
	}
	
	private boolean isShowType() {
		return JavaPlugin.getDefault().getPreferenceStore().getBoolean(CodeMiningPreferencePage.PREF_SHOW_METHOD_PARAMETER_TYPES);
	}

}
