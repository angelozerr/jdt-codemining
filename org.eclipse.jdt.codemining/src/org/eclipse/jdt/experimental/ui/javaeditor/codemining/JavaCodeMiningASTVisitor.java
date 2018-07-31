package org.eclipse.jdt.experimental.ui.javaeditor.codemining;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.experimental.ui.javaeditor.codemining.endstatement.EndStatementCodeMining;
import org.eclipse.jdt.experimental.ui.javaeditor.codemining.methods.JavaMethodParameterCodeMining;
import org.eclipse.jdt.experimental.ui.javaeditor.codemining.methods.MethodFilterManager;
import org.eclipse.jdt.experimental.ui.javaeditor.codemining.var.JavaVarTypeCodeMining;
import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaCodeMiningASTVisitor extends HierarchicalASTVisitor {

	private final CompilationUnit cu;

	private final List<ICodeMining> minings;

	private final ICodeMiningProvider provider;

	private final ITextEditor textEditor;

	private final ITextViewer viewer;

	private boolean showParameterName;

	private boolean showParameterType;

	private boolean showParameterOnlyForLiteral;

	private boolean showParameterByUsingFilters;

	private boolean showEndStatement;

	private int endStatementMinLineNumber;

	private boolean showJava10VarType;

	public JavaCodeMiningASTVisitor(CompilationUnit cu, ITextEditor textEditor, ITextViewer viewer,
			List<ICodeMining> minings, ICodeMiningProvider provider) {
		this.cu = cu;
		this.minings = minings;
		this.provider = provider;
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
	public boolean visit(SimpleType node) {
		if (node.isVar() && showJava10VarType) {
			JavaVarTypeCodeMining m = new JavaVarTypeCodeMining(node, viewer, provider);
			minings.add(m);
		}
		return super.visit(node);
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

	public void setShowParameterName(boolean showParameterName) {
		this.showParameterName = showParameterName;
	}

	public void setShowParameterType(boolean showParameterType) {
		this.showParameterType = showParameterType;
	}

	public void setShowParameterOnlyForLiteral(boolean showParameterOnlyForLiteral) {
		this.showParameterOnlyForLiteral = showParameterOnlyForLiteral;
	}

	public void setShowParameterByUsingFilters(boolean showParameterByUsingFilters) {
		this.showParameterByUsingFilters = showParameterByUsingFilters;
	}

	public void setShowEndStatement(boolean showEndStatement) {
		this.showEndStatement = showEndStatement;
	}

	public void setEndStatementMinLineNumber(int endStatementMinLineNumber) {
		this.endStatementMinLineNumber = endStatementMinLineNumber;
	}

	public void setShowJava10VarType(boolean showJava10VarType) {
		this.showJava10VarType = showJava10VarType;
	}

}
