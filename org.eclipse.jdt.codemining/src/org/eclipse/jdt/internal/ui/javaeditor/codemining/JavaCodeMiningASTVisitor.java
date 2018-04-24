package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.views.launch.DebugElementHelper;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.preferences.JavaEditorCodeMiningPreferencePage;
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
	
	public JavaCodeMiningASTVisitor(CompilationUnit cu, ITextEditor textEditor, ITextViewer viewer, List<ICodeMining> minings,
			ICodeMiningProvider provider) {
		this.cu = cu;
		this.minings = minings;
		this.provider = provider;
		this.showName = isShowName();
		this.showType = isShowType();
		this.textEditor = textEditor;
		this.viewer = viewer;
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
	public void endVisit(Statement node) {
		super.endVisit(node);
		if (node.getNodeType() == ASTNode.IF_STATEMENT || node.getNodeType() == ASTNode.WHILE_STATEMENT
				|| node.getNodeType() == ASTNode.FOR_STATEMENT || node.getNodeType() == ASTNode.DO_STATEMENT) {
			if (isShowEndStatement()) {
				minings.add(new EndStatementCodeMining(node, textEditor, provider));
			}
		}
	}

	@Override
	public boolean visit(VariableDeclaration node) {
		if (isShowVariableValueWhileDebugging()) {
			IJavaStackFrame frame = getFrame();
			if (frame != null) {
				InlinedDebugCodeMining m = new InlinedDebugCodeMining(node, frame, viewer, provider);
				minings.add(m);				
			}
		}
		return super.visit(node);
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

	private boolean isShowVariableValueWhileDebugging() {
		return JavaPlugin.getDefault().getPreferenceStore()
				.getBoolean(JavaEditorCodeMiningPreferencePage.PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING);
	}

	/**
	 * Returns the stack frame in which to search for variables, or
	 * <code>null</code> if none.
	 *
	 * @return the stack frame in which to search for variables, or
	 *         <code>null</code> if none
	 */
	protected IJavaStackFrame getFrame() {
		IAdaptable adaptable = DebugUITools.getDebugContext();
		if (adaptable != null) {
			return adaptable.getAdapter(IJavaStackFrame.class);
		}
		return null;
	}
}
