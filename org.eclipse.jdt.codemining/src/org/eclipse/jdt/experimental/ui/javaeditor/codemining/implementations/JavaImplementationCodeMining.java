/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - [CodeMining] Provide Java References/Implementation CodeMinings - Bug 529127
 */
package org.eclipse.jdt.experimental.ui.javaeditor.codemining.implementations;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.experimental.junit.codemining.AbstractJavaCodeMining;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;

/**
 * Java implementation code mining.
 * 
 * @since 3.14
 */
public class JavaImplementationCodeMining extends AbstractJavaCodeMining {

	private final boolean implementationsCodeMiningsAtLeastOne;

	public JavaImplementationCodeMining(IType element, IDocument document, ICodeMiningProvider provider,
			boolean implementationsCodeMiningsAtLeastOne) throws JavaModelException, BadLocationException {
		super(element, document, provider, null);
		this.implementationsCodeMiningsAtLeastOne = implementationsCodeMiningsAtLeastOne;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			try {
				long implCount = countImplementations((IType) getElement(), monitor);
				if (implCount == 0 && implementationsCodeMiningsAtLeastOne) {
					super.setLabel("");
				} else {
					super.setLabel(implCount + " " + (implCount > 1 ? "implementations" : "implementation")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			} catch (JavaModelException e) {
				// TODO: what should we done when there are some errors?
			}
		});
	}

	/**
	 * Return the count of implementation for the given java element type.
	 * 
	 * @param type    the java element type.
	 * @param monitor the monitor
	 * @return the count of implementation for the given java element type.
	 * @throws JavaModelException throws when Java error
	 */
	private static long countImplementations(IType type, IProgressMonitor monitor) throws JavaModelException {
		IType[] results = type.newTypeHierarchy(monitor).getAllSubtypes(type);
		return Stream.of(results).filter(t -> t.getAncestor(IJavaElement.COMPILATION_UNIT) != null).count();
	}

}
