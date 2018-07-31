package org.eclipse.jdt.experimental.ui.javaeditor.codemining;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.LineRange;

public class Utils {

	/*private static final Field isGeneratedByLombokField;

	static {
		isGeneratedByLombokField = isGeneratedByLombokField();
	}

	static Field isGeneratedByLombokField() {
		try {
			return ASTNode.class.getDeclaredField("$isGenerated");
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isGeneratedByLombok(ASTNode node) {
		if (isGeneratedByLombokField == null) {
			return false;
		}
		try {
			return (boolean) isGeneratedByLombokField.get(node);
		} catch (Exception e) {
			return false;
		}
	}*/

	public static int getLineNumber(IJavaElement element, IDocument document)
			throws JavaModelException, BadLocationException {
		ISourceRange r = ((ISourceReference) element).getNameRange();
		int offset = r.getOffset();
		return document.getLineOfOffset(offset);
	}

	public static ILineRange getLineRange(IJavaElement element, IDocument document)
			throws JavaModelException, BadLocationException {
		ISourceRange r = ((ISourceReference) element).getSourceRange();
		int offset = r.getOffset();
		int startLine = document.getLineOfOffset(offset);
		int endLine = document.getLineOfOffset(offset + r.getLength());
		return new LineRange(startLine, endLine - startLine);
	}
}
