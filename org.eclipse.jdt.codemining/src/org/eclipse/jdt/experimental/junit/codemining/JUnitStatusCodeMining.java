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

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.model.TestCaseElement;
import org.eclipse.jdt.internal.junit.model.TestElement.Status;
import org.eclipse.jdt.internal.junit.model.TestSuiteElement;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Java implementation code mining.
 * 
 * @since 3.14
 */
public class JUnitStatusCodeMining extends AbstractJavaCodeMining {

	final static Image fTestIcon;
	final static Image fTestOkIcon;
	final static Image fTestErrorIcon;
	final static Image fTestFailIcon;
	final static Image fTestRunningIcon;
	final static Image fTestIgnoredIcon;
	final static Image fTestAssumptionFailureIcon;

	static {
		fTestIcon = createManagedImage("obj16/test.png"); //$NON-NLS-1$
		fTestOkIcon = createManagedImage("obj16/testok.png"); //$NON-NLS-1$
		fTestErrorIcon = createManagedImage("obj16/testerr.png"); //$NON-NLS-1$
		fTestFailIcon = createManagedImage("obj16/testfail.png"); //$NON-NLS-1$
		fTestRunningIcon = createManagedImage("obj16/testrun.png"); //$NON-NLS-1$
		fTestIgnoredIcon = createManagedImage("obj16/testignored.png"); //$NON-NLS-1$
		fTestAssumptionFailureIcon = createManagedImage("obj16/testassumptionfailed.png"); //$NON-NLS-1$

	}

	private final JUnitStatusRegistry testRegistry;
	private TestCaseElement testCaseElement;
	private TestSuiteElement testSuiteElement;

	public JUnitStatusCodeMining(IJavaElement element, JUnitStatusRegistry testRegistry, IDocument document,
			ICodeMiningProvider provider) throws JavaModelException, BadLocationException {
		super(element, document, provider, null);
		this.testRegistry = testRegistry;
	}

	private static Image createManagedImage(String path) {
		return JUnitPlugin.getImageDescriptor(path).createImage();
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			if (getElement().getElementType() == IJavaElement.TYPE) {
				this.testSuiteElement = (TestSuiteElement) testRegistry.findTestSuite((IType) getElement());
			} else {
				this.testCaseElement = (TestCaseElement) testRegistry.findTestCase((IMethod) getElement());
			}
			super.setLabel(" "); // set label with space to mark the mining as resolved.
		});
	}

	@Override
	public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
		Image image = getImage();
		gc.drawImage(image, x, y + gc.getFontMetrics().getDescent());
		Rectangle bounds = image.getBounds();
		return new Point(bounds.width, bounds.height);
	}

	private Image getImage() {
		if (testCaseElement == null && testSuiteElement == null) {
			return fTestIcon;
		}
		Status status = testCaseElement != null ? testCaseElement.getStatus() : testSuiteElement.getStatus();
		if (status.isNotRun())
			return fTestIcon;
		else if (status.isRunning())
			return fTestRunningIcon;
		else if (status.isError())
			return fTestErrorIcon;
		else if (status.isFailure())
			return fTestFailIcon;
		else if (status.isOK())
			return fTestOkIcon;
		return null;
	}
}
