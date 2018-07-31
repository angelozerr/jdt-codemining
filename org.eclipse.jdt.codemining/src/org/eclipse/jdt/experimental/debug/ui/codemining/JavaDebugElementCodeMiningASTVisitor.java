package org.eclipse.jdt.experimental.debug.ui.codemining;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.codemining.AbstractDebugElementCodeMining;
import org.eclipse.debug.ui.codemining.AbstractDebugElementCodeMiningProvider;
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
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaDebugElementCodeMiningASTVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final AbstractDebugElementCodeMiningProvider provider;

	private final ITextEditor textEditor;

	private final ITextViewer viewer;

	private IJavaStackFrame frame;

	public JavaDebugElementCodeMiningASTVisitor(CompilationUnit cu, ITextEditor textEditor, ITextViewer viewer,
			List<ICodeMining> minings, AbstractDebugElementCodeMiningProvider provider) {
		this.cu = cu;
		this.minings = minings;
		this.provider = provider;
		this.textEditor = textEditor;
		this.viewer = viewer;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		if (frame != null) {
			List arguments = node.arguments();
			if (arguments.size() > 0) {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = (Expression) arguments.get(i);
					if (exp instanceof SimpleName) {
						AbstractDebugElementCodeMining<IJavaStackFrame> m = new JavaDebugElementCodeMining((SimpleName) exp,
								frame, viewer, provider);
						minings.add(m);
					}
				}
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		List arguments = node.arguments();
		if (arguments.size() > 0) {
			for (int i = 0; i < arguments.size(); i++) {
				Expression exp = (Expression) arguments.get(i);
				if (exp instanceof SimpleName) {
					AbstractDebugElementCodeMining<IJavaStackFrame> m = new JavaDebugElementCodeMining((SimpleName) exp, frame,
							viewer, provider);
					minings.add(m);
				}
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		IJavaStackFrame frame = getFrame();
		if (frame != null) {
			try {
				// TODO: improve the comparison of the method which is visited and the debug
				// frame
				if (node.getName().toString().equals(frame.getMethodName())) {
					this.frame = frame;
				} else {
					this.frame = null;
				}
			} catch (DebugException e) {
				e.printStackTrace();
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclaration node) {
		if (frame != null) {
			AbstractDebugElementCodeMining<IJavaStackFrame> m = new JavaDebugElementCodeMining(node.getName(), frame, viewer,
					provider);
			minings.add(m);
		}
		return super.visit(node);
	}

	/**
	 * Returns the stack frame in which to search for variables, or
	 * <code>null</code> if none.
	 *
	 * @return the stack frame in which to search for variables, or
	 *         <code>null</code> if none
	 */
	private IJavaStackFrame getFrame() {
		IAdaptable adaptable = DebugUITools.getPartDebugContext(textEditor.getSite());
		if (adaptable != null) {
			return adaptable.getAdapter(IJavaStackFrame.class);
		}
		return null;
	}
}
