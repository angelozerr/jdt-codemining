package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.List;

import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;

public class CalleeJavaMethodParameterVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final ICodeMiningProvider provider;

	public CalleeJavaMethodParameterVisitor(CompilationUnit cu, List<ICodeMining> minings, ICodeMiningProvider provider) {
		this.cu= cu;
		this.minings= minings;
		this.provider= provider;
	}

	public boolean visit(MethodInvocation node) {
		List arguments= node.arguments();
		if (arguments.size() > 0) {

			for (int i= 0; i < arguments.size(); i++) {
				Expression exp= (Expression)arguments.get(i);
				minings.add(new JavaMethodParameterCodeMining(node, exp, i, cu, provider));
			}
		}
		return super.visit(node);
	}

}
