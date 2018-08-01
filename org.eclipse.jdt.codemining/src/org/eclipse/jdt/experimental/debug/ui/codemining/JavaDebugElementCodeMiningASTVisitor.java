package org.eclipse.jdt.experimental.debug.ui.codemining;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.codemining.provisional.AbstractDebugVariableCodeMining;
import org.eclipse.debug.ui.codemining.provisional.AbstractDebugVariableCodeMiningProvider;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMining;

public class JavaDebugElementCodeMiningASTVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final AbstractDebugVariableCodeMiningProvider provider;

	private final ITextViewer viewer;

	private IJavaStackFrame fFrame;

	private boolean inFrame;

	public JavaDebugElementCodeMiningASTVisitor(IJavaStackFrame frame, CompilationUnit cu, ITextViewer viewer,
			List<ICodeMining> minings, AbstractDebugVariableCodeMiningProvider provider) {
		this.cu = cu;
		this.minings = minings;
		this.provider = provider;
		this.viewer = viewer;
		this.fFrame = frame;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		if (inFrame) {
			List arguments = node.arguments();
			if (arguments.size() > 0) {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = (Expression) arguments.get(i);
					if (exp instanceof SimpleName) {
						AbstractDebugVariableCodeMining<IJavaStackFrame> m = new JavaDebugElementCodeMining(
								(SimpleName) exp, fFrame, viewer, provider);
						minings.add(m);
					}
				}
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		if (inFrame) {
			List arguments = node.arguments();
			if (arguments.size() > 0) {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = (Expression) arguments.get(i);
					if (exp instanceof SimpleName) {
						AbstractDebugVariableCodeMining<IJavaStackFrame> m = new JavaDebugElementCodeMining(
								(SimpleName) exp, fFrame, viewer, provider);
						minings.add(m);
					}
				}
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		try {
			// TODO: improve the comparison of the method which is visited and the debug
			// frame
			inFrame = (node.getName().toString().equals(fFrame.getMethodName()));
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclaration node) {
		if (inFrame) {
			AbstractDebugVariableCodeMining<IJavaStackFrame> m = new JavaDebugElementCodeMining(node.getName(), fFrame,
					viewer, provider);
			minings.add(m);
		}
		return super.visit(node);
	}
}
