package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;

public class JavaMethodParameterCodeMining extends LineContentCodeMining {

	private final MethodInvocation method;

	private final CompilationUnit cu;

	private int parameterIndex;

	private final boolean showName;

	private final boolean showType;

	public JavaMethodParameterCodeMining(MethodInvocation method, Expression parameter, int parameterIndex,
			CompilationUnit cu, ICodeMiningProvider provider, boolean showName, boolean showType) {
		super(new Position(parameter.getStartPosition()), provider, null);
		this.cu = cu;
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.showName = showName;
		this.showType = showType;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			IMethodBinding calledMethodBinding = method.resolveMethodBinding();
			if (calledMethodBinding != null) {
				MethodDeclaration decl = (MethodDeclaration) cu.findDeclaringNode(calledMethodBinding.getKey());
				if (decl != null) {
					SingleVariableDeclaration elem = (SingleVariableDeclaration) decl.parameters().get(parameterIndex);
					String paramName = elem.getName().getIdentifier();
					super.setLabel(paramName + ": "); //$NON-NLS-1$
				} else {
					ITypeBinding calledTypeBinding = calledMethodBinding.getDeclaringClass();
					IType calledType = (IType) calledTypeBinding.getJavaElement();
					try {
						IMethod method = Bindings.findMethod(calledMethodBinding, calledType);
						if (method != null) {
							StringBuilder label = new StringBuilder();
							if (showType) {
								String paramType = method.getParameterTypes()[parameterIndex];
								paramType = Signature
										.getSimpleName(Signature.toString(Signature.getTypeErasure(paramType)));
								label.append(paramType);
							}
							if (showName) {
								String paramName = method.getParameterNames()[parameterIndex];
								if (showType) {
									label.append(" | ");
								}
								label.append(paramName);
							}

							super.setLabel(label.toString() + ": "); //$NON-NLS-1$
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			} else {
				super.setLabel("");
			}

		});
	}
}
