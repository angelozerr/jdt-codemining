package org.eclipse.jdt.experimental.ui.javaeditor.codemining.methods;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;

public class JavaMethodParameterCodeMining extends LineContentCodeMining {

	private final ASTNode node;

	private final Expression parameter;

	private final CompilationUnit cu;

	private final int parameterIndex;

	private final boolean showName;

	private final boolean showType;

	private boolean showParameterByUsingFilters;

	private class ParameterMiningLabelBuilder {

		private final StringBuilder label;

		private boolean done;

		public ParameterMiningLabelBuilder() {
			label = new StringBuilder();
			done = false;
		}

		public void addParameterInfo(String paramInfo) {
			if (label.length() > 0) {
				label.append(" |"); //$NON-NLS-1$
			}
			label.append(" ");
			label.append(paramInfo);
		}

		@Override
		public String toString() {
			if (!done && label.length() > 0) {
				label.append(": ");
				done = true;
			}
			return label.toString();
		}
	}

	public JavaMethodParameterCodeMining(MethodInvocation method, Expression parameter, int parameterIndex,
			CompilationUnit cu, ICodeMiningProvider provider, boolean showName, boolean showType,
			boolean showParameterByUsingFilters) {
		this((ASTNode) method, parameter, parameterIndex, cu, provider, showName, showType,
				showParameterByUsingFilters);
	}

	public JavaMethodParameterCodeMining(ClassInstanceCreation constructor, Expression parameter, int parameterIndex,
			CompilationUnit cu, ICodeMiningProvider provider, boolean showName, boolean showType,
			boolean showParameterByUsingFilters) {
		this((ASTNode) constructor, parameter, parameterIndex, cu, provider, showName, showType,
				showParameterByUsingFilters);
	}

	private JavaMethodParameterCodeMining(ASTNode node, Expression parameter, int parameterIndex, CompilationUnit cu,
			ICodeMiningProvider provider, boolean showName, boolean showType, boolean showParameterByUsingFilters) {
		super(new Position(parameter.getStartPosition(), 1), provider, null);
		this.cu = cu;
		this.node = node;
		this.parameter = parameter;
		this.parameterIndex = parameterIndex;
		this.showName = showName;
		this.showType = showType;
		this.showParameterByUsingFilters = showParameterByUsingFilters;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			updateLabel();
		});
	}

	private void updateLabel() {
		IMethodBinding calledMethodBinding = ((node instanceof MethodInvocation)
				? ((MethodInvocation) node).resolveMethodBinding()
				: ((ClassInstanceCreation) node).resolveConstructorBinding());
		try {
			IMethod method = getMethod(calledMethodBinding);
			if (method == null || !acceptMethod(method)) {
				super.setLabel("");
			} else {
				String label = calledMethodBinding != null
						? getParameterLabel(method, calledMethodBinding.getDeclaringClass())
						: null;
				super.setLabel(label != null ? label : "");
			}
		} catch (JavaModelException e) {
			super.setLabel("");
		}
	}

	private boolean acceptMethod(IMethod method) {
		if (showParameterByUsingFilters) {
			return !MethodFilterManager.getInstance().match(method);
		}
		return true;
	}

	private IMethod getMethod(IMethodBinding calledMethodBinding) throws JavaModelException {
		if (calledMethodBinding == null) {
			return null;
		}
		ITypeBinding calledTypeBinding = calledMethodBinding.getDeclaringClass();
		IType calledType = (IType) calledTypeBinding.getJavaElement();
		return Bindings.findMethod(calledMethodBinding, calledType);
	}

	private String getParameterLabel(IMethod method, ITypeBinding calledTypeBinding) throws JavaModelException {
		ParameterMiningLabelBuilder label = new ParameterMiningLabelBuilder();
		if (showType) {
			String paramType = "";
			if (calledTypeBinding.isParameterizedType()) {
				// ex : List<String>
				ITypeBinding typeArgument = calledTypeBinding.getTypeArguments()[parameterIndex];
				paramType = typeArgument.getName();
			} else {
				paramType = method.getParameterTypes()[parameterIndex];
				paramType = Signature.getSimpleName(Signature.toString(Signature.getTypeErasure(paramType)));
				// replace [] with ... when varArgs
				if (parameterIndex == method.getParameterTypes().length - 1 && Flags.isVarargs(method.getFlags())) {
					paramType = paramType.substring(0, paramType.length() - 2) + "...";
				}
			}
			label.addParameterInfo(paramType);
		}
		if (showName) {
			String paramName = method.getParameterNames()[parameterIndex];
			if (!isArgNumber(paramName, method)) {
				label.addParameterInfo(paramName);
			}
		}
		return label.toString();

	}

	private static boolean isArgNumber(String paramName, IMethod method) {
		if (method.isBinary()) {
			// check param name is not arg0, arg1, etc
			if (paramName.length() > 3 && paramName.startsWith("arg")) {
				try {
					Integer.parseInt(paramName.substring(3, paramName.length()));
					return true;
				} catch (Exception e) {

				}
			}
		}
		return false;
	}
}
