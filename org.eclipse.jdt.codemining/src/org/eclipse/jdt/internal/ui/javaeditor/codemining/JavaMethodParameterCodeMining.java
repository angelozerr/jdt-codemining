package org.eclipse.jdt.internal.ui.javaeditor.codemining;

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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;

public class JavaMethodParameterCodeMining extends LineContentCodeMining {

	private final ASTNode node;

	private final CompilationUnit cu;

	private int parameterIndex;

	private final boolean showName;

	private final boolean showType;

	public JavaMethodParameterCodeMining(MethodInvocation method, Expression parameter, int parameterIndex,
			CompilationUnit cu, ICodeMiningProvider provider, boolean showName, boolean showType) {
		this((ASTNode) method, parameter, parameterIndex, cu, provider, showName, showType);
	}

	public JavaMethodParameterCodeMining(ClassInstanceCreation constructor, Expression parameter, int parameterIndex,
			CompilationUnit cu, ICodeMiningProvider provider, boolean showName, boolean showType) {
		this((ASTNode) constructor, parameter, parameterIndex, cu, provider, showName, showType);
	}

	private JavaMethodParameterCodeMining(ASTNode node, Expression parameter, int parameterIndex, CompilationUnit cu,
			ICodeMiningProvider provider, boolean showName, boolean showType) {
		super(new Position(parameter.getStartPosition(), 1), provider, null);
		this.cu = cu;
		this.node = node;
		this.parameterIndex = parameterIndex;
		this.showName = showName;
		this.showType = showType;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			IMethodBinding calledMethodBinding = ((node instanceof MethodInvocation)
					? ((MethodInvocation) node).resolveMethodBinding()
					: ((ClassInstanceCreation) node).resolveConstructorBinding());
			if (calledMethodBinding != null) {
				MethodDeclaration decl = (MethodDeclaration) cu.findDeclaringNode(calledMethodBinding.getKey());
				if (decl != null) {
					SingleVariableDeclaration elem = (SingleVariableDeclaration) decl.parameters().get(parameterIndex);
					String paramName = elem.getName().getIdentifier();
					super.setLabel(" " + paramName + ": "); //$NON-NLS-1$
				} else {
					ITypeBinding calledTypeBinding = calledMethodBinding.getDeclaringClass();
					IType calledType = (IType) calledTypeBinding.getJavaElement();
					try {
						IMethod method = Bindings.findMethod(calledMethodBinding, calledType);
						if (method != null) {
							StringBuilder label = new StringBuilder(" ");
							if (showType) {
								String paramType = method.getParameterTypes()[parameterIndex];
								paramType = Signature
										.getSimpleName(Signature.toString(Signature.getTypeErasure(paramType)));
								// replace [] with ... when varArgs
								if (parameterIndex == method.getParameterTypes().length - 1 && Flags.isVarargs(method.getFlags())) {
									paramType = paramType.substring(0,  paramType.length() - 2) + "...";
								}
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
						} else {
							super.setLabel("");
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
