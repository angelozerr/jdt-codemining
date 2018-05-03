package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jdt.internal.ui.javaeditor.codemining.debug.InlinedDebugCodeMining;
import org.eclipse.jdt.internal.ui.javaeditor.codemining.debug.SimpleNameDebugCodeMining;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesPropertyTester;
import org.eclipse.jdt.internal.ui.preferences.MyPreferenceConstants;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaCodeMiningASTVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final ICodeMiningProvider provider;

	private final boolean showName;

	private final boolean showType;

	private final ITextEditor textEditor;

	private final ITextViewer viewer;

	private IJavaStackFrame frame;

	private int endStatementMinLineNumber;

	public JavaCodeMiningASTVisitor(CompilationUnit cu, ITextEditor textEditor, ITextViewer viewer,
			List<ICodeMining> minings, ICodeMiningProvider provider) {
		this.cu = cu;
		this.minings = minings;
		this.provider = provider;
		this.showName = isShowName();
		this.showType = isShowType();
		this.endStatementMinLineNumber = getEndStatementMinLineNumber();
		this.textEditor = textEditor;
		this.viewer = viewer;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		if (showName || showType) {
			List arguments = node.arguments();
			if (arguments.size() > 0) {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = (Expression) arguments.get(i);
					minings.add(new JavaMethodParameterCodeMining(node, exp, i, cu, provider, showName, showType));
				}
			}
		}
		if (isShowVariableValueWhileDebugging() && frame != null) {
			List arguments = node.arguments();
			if (arguments.size() > 0) {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = (Expression) arguments.get(i);
					if (exp instanceof SimpleName) {
						InlinedDebugCodeMining m = new SimpleNameDebugCodeMining((SimpleName) exp, frame, viewer,
								provider);
						minings.add(m);
					}
				}
			}
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(Statement node) {
		super.endVisit(node);
		if (node.getNodeType() == ASTNode.IF_STATEMENT || node.getNodeType() == ASTNode.WHILE_STATEMENT
				|| node.getNodeType() == ASTNode.FOR_STATEMENT || node.getNodeType() == ASTNode.DO_STATEMENT
				|| node.getNodeType() == ASTNode.SWITCH_STATEMENT) {
			if (isShowEndStatement()) {
				minings.add(new EndStatementCodeMining(node, textEditor, viewer, endStatementMinLineNumber, provider));
			}
		}
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (isShowVariableValueWhileDebugging()) {
			IJavaStackFrame frame = getFrame();
			if (frame != null) {
				try {
					// TODO: improve the comparison of the methode which is visted and the debug
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
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclaration node) {
		if (isShowVariableValueWhileDebugging() && frame != null) {
			InlinedDebugCodeMining m = new SimpleNameDebugCodeMining(node.getName(), frame, viewer, provider);
			minings.add(m);
		}
		return super.visit(node);
	}

	private boolean isShowName() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_NAMES);
	}

	private boolean isShowType() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_TYPES);
	}

	private boolean isShowEndStatement() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT);
	}

	private int getEndStatementMinLineNumber() {
		return MyPreferenceConstants.getPreferenceStore()
				.getInt(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT_MIN_LINE_NUMBER);
	}

	private boolean isShowVariableValueWhileDebugging() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING);
	}

	/**
	 * Returns the stack frame in which to search for variables, or
	 * <code>null</code> if none.
	 *
	 * @return the stack frame in which to search for variables, or
	 *         <code>null</code> if none
	 */
	protected IJavaStackFrame getFrame() {
		IAdaptable adaptable = DebugUITools.getPartDebugContext(textEditor.getSite());
		if (adaptable != null) {
			return adaptable.getAdapter(IJavaStackFrame.class);
		}
		return null;
	}
}
