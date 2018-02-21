package org.eclipse.jdt.junit.codemining;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public class JUnitLaunchCodeMining extends AbstractJavaCodeMining {

	public JUnitLaunchCodeMining(IJavaElement element, String label, String mode, IDocument document,
			ICodeMiningProvider provider) throws JavaModelException, BadLocationException {
		super(element, document, provider, e -> {
			JUnitLaunchShortcut shortcut = new JUnitLaunchShortcut();
			shortcut.launch(new StructuredSelection(element), mode);
		});
		super.setLabel(label);
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	// @Override
	// public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
	// Image image = getImage();
	// gc.drawImage(image, x, y + gc.getFontMetrics().getDescent());
	// Rectangle bounds = image.getBounds();
	// return new Point(bounds.width, bounds.height);
	// }
	//
	// private Image getImage() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
