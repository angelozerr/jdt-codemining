package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class Utils {

	public static int getLineNumber(IJavaElement element, IDocument document) throws JavaModelException, BadLocationException {
		ISourceRange r= ((ISourceReference) element).getNameRange();
		int offset= r.getOffset();
		return document.getLineOfOffset(offset);
	}
}
