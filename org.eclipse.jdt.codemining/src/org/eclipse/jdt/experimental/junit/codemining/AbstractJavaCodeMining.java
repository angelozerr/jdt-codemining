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
package org.eclipse.jdt.experimental.junit.codemining;

import java.util.function.Consumer;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.swt.events.MouseEvent;

/**
 * Abstract class for Java code mining.
 *
 */
public abstract class AbstractJavaCodeMining extends LineHeaderCodeMining {

	private final IJavaElement element;

	public AbstractJavaCodeMining(IJavaElement element, IDocument document, ICodeMiningProvider provider,
			Consumer<MouseEvent> action) throws JavaModelException, BadLocationException {
		super(getLineNumber(element, document), document, provider, action);
		this.element = element;
	}

	private static int getLineNumber(IJavaElement element, IDocument document)
			throws JavaModelException, BadLocationException {
		ISourceRange r = ((ISourceReference) element).getNameRange();
		int offset = r.getOffset();
		return document.getLineOfOffset(offset);
	}

	/**
	 * Returns the java element.
	 * 
	 * @return the java element.
	 */
	public IJavaElement getElement() {
		return element;
	}

}
