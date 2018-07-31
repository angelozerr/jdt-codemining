package org.eclipse.jdt.experimental.debug.ui.codemining;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.experimental.junit.codemining.AbstractJavaCodeMining;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public class JavaLaunchCodeMining extends AbstractJavaCodeMining {

	public JavaLaunchCodeMining(IJavaElement element, String label, String mode, IDocument document,
			ICodeMiningProvider provider) throws JavaModelException, BadLocationException {
		super(element, document, provider, e -> {
			JavaApplicationLaunchShortcut shortcut = new JavaApplicationLaunchShortcut();
			shortcut.launch(new StructuredSelection(element), mode);
		});
		super.setLabel(label);
	}

	@Override
	public boolean isResolved() {
		return true;
	}

}
