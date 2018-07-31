package org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.debug.InlinedDebugCodeMining;
import org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.debug.SimpleNameDebugCodeMining;
import org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.endstatement.EndStatementCodeMining;
import org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.methods.JavaMethodParameterCodeMining;
import org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.methods.MethodFilterManager;
import org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.var.JavaVarTypeCodeMining;
import org.eclipse.jdt.experimental.internal.ui.preferences.JavaPreferencesPropertyTester;
import org.eclipse.jdt.experimental.internal.ui.preferences.MyPreferenceConstants;
import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaCodeMiningASTVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final ICodeMiningProvider provider;

	private final boolean showParameterName;

	private final boolean showParameterType;

	private final boolean showParameterOnlyForLiteral;

	private final boolean showParameterByUsingFilters;

	private final ITextEditor textEditor;

	private final ITextViewer viewer;

	private final boolean showEndStatement;

	private int endStatementMinLineNumber;

	private IJavaStackFrame frame;

	private boolean showVariableValueWhileDebugging;

	private final boolean showJava10VarType;

	public JavaCodeMiningASTVisitor(CompilationUnit cu, ITextEditor textEditor, ITextViewer viewer,
			List<ICodeMining> minings, ICodeMiningProvider provider) {
		this.cu = cu;
		this.minings = minings;
		this.provider = provider;
		this.showParameterName = isShowParameterName();
		this.showParameterType = isShowParameterType();
		this.showParameterOnlyForLiteral = isShowParameterOnlyForLiteral();
		this.showParameterByUsingFilters = isShowParameterByUsingFilters();
		this.showVariableValueWhileDebugging = isShowVariableValueWhileDebugging();
		this.showEndStatement = isShowEndStatement();
		this.endStatementMinLineNumber = getEndStatementMinLineNumber();
		this.showJava10VarType = isShowJava10VarType();
		this.textEditor = textEditor;
		this.viewer = viewer;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		/*
		 * if (Utils.isGeneratedByLombok(node)) { return super.visit(node); }
		 */
		if (showParameterName || showParameterType) {
			List arguments = node.arguments();
			if (arguments.size() > 0 && acceptMethod(node)) {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = (Expression) arguments.get(i);
					if (showParameterOnlyForLiteral && !isLiteral(exp)) {
						continue;
					}
					minings.add(new JavaMethodParameterCodeMining(node, exp, i, cu, provider, showParameterName,
							showParameterType, showParameterByUsingFilters));
				}
			}
		}
		if (showVariableValueWhileDebugging && frame != null) {
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

	private boolean acceptMethod(ClassInstanceCreation node) {
		if (showParameterByUsingFilters) {
			return !MethodFilterManager.getInstance().match(node);
		}
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		/*
		 * if (Utils.isGeneratedByLombok(node)) { return super.visit(node); }
		 */
		if (showParameterName || showParameterType) {
			List arguments = node.arguments();
			if (arguments.size() > 0 && acceptMethod(node)) {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = (Expression) arguments.get(i);
					if (showParameterOnlyForLiteral && !isLiteral(exp)) {
						continue;
					}
					// Ignore empty parameter
					if (exp instanceof SimpleName) {
						if ("$missing$".equals(((SimpleName) exp).getIdentifier())) {
							continue;
						}
					}
					minings.add(new JavaMethodParameterCodeMining(node, exp, i, cu, provider, showParameterName,
							showParameterType, showParameterByUsingFilters));
				}
			}
		}
		if (showVariableValueWhileDebugging && frame != null) {
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

	private boolean acceptMethod(MethodInvocation node) {
		if (showParameterByUsingFilters) {
			return !MethodFilterManager.getInstance().match(node);
		}
		return true;
	}

	@Override
	public void endVisit(Statement node) {
		super.endVisit(node);
		if (node.getNodeType() == ASTNode.IF_STATEMENT || node.getNodeType() == ASTNode.WHILE_STATEMENT
				|| node.getNodeType() == ASTNode.FOR_STATEMENT || node.getNodeType() == ASTNode.DO_STATEMENT
				|| node.getNodeType() == ASTNode.SWITCH_STATEMENT) {
			if (showEndStatement) {
				minings.add(new EndStatementCodeMining(node, textEditor, viewer, endStatementMinLineNumber, provider));
			}
		}
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (showVariableValueWhileDebugging) {
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
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclaration node) {
		if (showVariableValueWhileDebugging && frame != null) {
			InlinedDebugCodeMining m = new SimpleNameDebugCodeMining(node.getName(), frame, viewer, provider);
			minings.add(m);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleType node) {
		if (node.isVar() && showJava10VarType) {
			JavaVarTypeCodeMining m = new JavaVarTypeCodeMining(node, viewer, provider);
			minings.add(m);
		}
		return super.visit(node);
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

	private boolean isShowVariableValueWhileDebugging() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING);
	}

	private boolean isShowJava10VarType() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JAVA10_VAR_TYPE);
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

	private static boolean isLiteral(Expression expression) {
		switch (expression.getNodeType()) {
		case ASTNode.BOOLEAN_LITERAL:
		case ASTNode.CHARACTER_LITERAL:
		case ASTNode.NULL_LITERAL:
		case ASTNode.NUMBER_LITERAL:
		case ASTNode.STRING_LITERAL:
			return true;

		default:
			return false;
		}
	}
}
