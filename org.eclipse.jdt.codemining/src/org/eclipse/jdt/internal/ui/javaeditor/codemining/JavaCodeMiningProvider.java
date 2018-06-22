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
package org.eclipse.jdt.internal.ui.javaeditor.codemining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.codemining.implementations.JavaImplementationCodeMining;
import org.eclipse.jdt.internal.ui.javaeditor.codemining.references.JavaReferenceCodeMining;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesPropertyTester;
import org.eclipse.jdt.internal.ui.preferences.MyPreferenceConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.revisions.provisionnal.IRevisionRangeProvider;
import org.eclipse.jface.text.revisions.provisionnal.RevisionInformationProviderManager;
import org.eclipse.jface.text.revisions.provisionnal.RevisionInformationSupport;
import org.eclipse.jface.text.revisions.provisionnal.codemining.RevisionAuthorsCodeMining;
import org.eclipse.jface.text.revisions.provisionnal.codemining.RevisionRecentChangeCodeMining;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Java code mining provider to show Java references and implementations code
 * minings.
 * 
 * @since 3.14
 *
 */
public class JavaCodeMiningProvider extends AbstractCodeMiningProvider implements IRevisionRangeProvider {

	private static Set<String> SILENCED_CODEGENS = Collections.singleton("lombok"); //$NON-NLS-1$

	private RevisionInformationSupport fRevisionInfoSupport;

	private ITextViewer fViewer;

	private ITypeRoot fUnit;

	private boolean isReferencesCodeMiningsEnabled() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES);
	}

	private boolean isReferencesCodeMiningsAtLeastOne() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE);
	}

	private boolean isImplementationsCodeMiningsEnabled() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS);
	}

	private boolean isImplementationsCodeMiningsAtLeastOne() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE);
	}

	private boolean isRunMainCodeMiningsEnabled() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_RUN);
	}

	private boolean isDebugMainCodeMiningsEnabled() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_DEBUG);
	}

	private boolean isRevisionRecentChangeEnabled() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE);
	}

	private boolean isRevisionRecentChangeWithAvatarEnabled() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_AVATAR);
	}

	private boolean isRevisionRecentChangeWithDateEnabled() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_DATE);
	}

	private boolean isRevisionAuthorsEnabled() {
		return JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_AUTHORS);
	}

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
			if (unit == null) {
				return Collections.emptyList();
			}
			fViewer = viewer;
			fUnit = unit;
			try {
				IJavaElement[] elements = unit.getChildren();
				List<ICodeMining> minings = new ArrayList<>(elements.length);
				collectMinings(unit, textEditor, unit.getChildren(), minings, viewer, monitor);
				monitor.isCanceled();
				return minings;
			} catch (JavaModelException e) {
				// TODO: what should we done when there are some errors?
			}
			return Collections.emptyList();
		});
	}

	/**
	 * Collect java code minings
	 * 
	 * @param unit       the compilation unit
	 * @param textEditor the Java editor
	 * @param elements   the java elements to track
	 * @param minings    the current list of minings to update
	 * @param viewer     the viewer
	 * @param monitor    the monitor
	 * @throws JavaModelException
	 */
	private void collectMinings(ITypeRoot unit, ITextEditor textEditor, IJavaElement[] elements,
			List<ICodeMining> minings, ITextViewer viewer, IProgressMonitor monitor) throws JavaModelException {
		for (IJavaElement element : elements) {
			if (monitor.isCanceled()) {
				return;
			}
			if (element.getElementType() == IJavaElement.TYPE) {
				collectMinings(unit, textEditor, ((IType) element).getChildren(), minings, viewer, monitor);
			} else if (element.getElementType() != IJavaElement.METHOD || isHiddenGeneratedElement(element)) {
				continue;
			}
			if (isReferencesCodeMiningsEnabled()) {
				try {
					minings.add(new JavaReferenceCodeMining(element, (JavaEditor) textEditor, viewer.getDocument(),
							this, isReferencesCodeMiningsAtLeastOne()));
				} catch (BadLocationException e) {
					// TODO: what should we done when there are some errors?
				}
			}
			if (isImplementationsCodeMiningsEnabled()) {
				if (element instanceof IType) {
					IType type = (IType) element;
					if (type.isInterface() || Flags.isAbstract(type.getFlags())) {
						try {
							minings.add(new JavaImplementationCodeMining(type, viewer.getDocument(), this,
									isImplementationsCodeMiningsAtLeastOne()));
						} catch (BadLocationException e) {
							// TODO: what should we done when there are some errors?
						}
					}
				}
			}
			if (isRunMainCodeMiningsEnabled()) {
				if (element instanceof IMethod && ((IMethod) (element)).isMainMethod()) {
					try {
						minings.add(new JavaLaunchCodeMining(element, "Run", "run", viewer.getDocument(), this));
					} catch (BadLocationException e) {
						// TODO: what should we done when there are some errors?
					}
				}
			}
			if (isDebugMainCodeMiningsEnabled()) {
				if (element instanceof IMethod && ((IMethod) (element)).isMainMethod()) {
					try {
						minings.add(new JavaLaunchCodeMining(element, "Debug", "debug", viewer.getDocument(), this));
					} catch (BadLocationException e) {
						// TODO: what should we done when there are some errors?
					}
				}
			}
			boolean revisionRecentChangeEnabled = isRevisionRecentChangeEnabled();
			boolean revisionAuthorsEnabled = isRevisionAuthorsEnabled();
			if (revisionRecentChangeEnabled || revisionAuthorsEnabled) {
				try {
					int lineNumber = Utils.getLineNumber(element, viewer.getDocument());
					ILineRange lineRange = Utils.getLineRange(element, viewer.getDocument());
					if (revisionRecentChangeEnabled) {
						minings.add(new RevisionRecentChangeCodeMining(lineNumber, lineRange, viewer.getDocument(),
								isRevisionRecentChangeWithAvatarEnabled(), isRevisionRecentChangeWithDateEnabled(),
								this, this));
					}
					if (revisionAuthorsEnabled) {
						minings.add(
								new RevisionAuthorsCodeMining(lineNumber, lineRange, viewer.getDocument(), this, this));

					}
				} catch (BadLocationException e) {
					// TODO: what should we done when there are some errors?
				}
			}
		}
	}

	/**
	 * This code is a copy/paste of
	 * https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/JDTUtils.java#L669
	 * 
	 * @param element
	 * @return
	 */
	private static boolean isHiddenGeneratedElement(IJavaElement element) {
		// generated elements are tagged with javax.annotation.Generated and
		// they need to be filtered out
		if (element instanceof IAnnotatable) {
			try {
				IAnnotation[] annotations = ((IAnnotatable) element).getAnnotations();
				if (annotations.length != 0) {
					for (IAnnotation annotation : annotations) {
						if (isSilencedGeneratedAnnotation(annotation)) {
							return true;
						}
					}
				}
			} catch (JavaModelException e) {
				// ignore
			}
		}
		return false;
	}

	/**
	 * This code is a copy/paste of
	 * https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/JDTUtils.java#L689
	 * 
	 * @param annotation
	 * @return
	 * @throws JavaModelException
	 */
	private static boolean isSilencedGeneratedAnnotation(IAnnotation annotation) throws JavaModelException {
		if ("javax.annotation.Generated".equals(annotation.getElementName())) { //$NON-NLS-1$
			IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
			for (IMemberValuePair m : memberValuePairs) {
				if ("value".equals(m.getMemberName()) && IMemberValuePair.K_STRING == m.getValueKind()) { //$NON-NLS-1$
					if (m.getValue() instanceof String) {
						return SILENCED_CODEGENS.contains(m.getValue());
					} else if (m.getValue() instanceof Object[]) {
						for (Object val : (Object[]) m.getValue()) {
							if (SILENCED_CODEGENS.contains(val)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (fRevisionInfoSupport != null) {
			fRevisionInfoSupport.uninstall();
			fRevisionInfoSupport = null;
		}
	}

	@Override
	public RevisionRange getRange(int line) {
		if (fRevisionInfoSupport == null) {
			initRevisionSupport(fViewer, fUnit);
		}
		return fRevisionInfoSupport != null ? fRevisionInfoSupport.getRange(line) : null;
	}

	@Override
	public List<RevisionRange> getRanges(ILineRange lines) {
		if (fRevisionInfoSupport == null) {
			initRevisionSupport(fViewer, fUnit);
		}
		return fRevisionInfoSupport != null ? fRevisionInfoSupport.getRanges(lines) : null;
	}

	private void initRevisionSupport(ITextViewer viewer, ITypeRoot unit) {
		IResource resource = unit.getResource();
		if (resource == null) {
			return;
		}
		if (fRevisionInfoSupport == null) {
			RevisionInformation info = RevisionInformationProviderManager.getInstance()
					.getRevisionInformation(resource);
			if (info != null) {
				fRevisionInfoSupport = new RevisionInformationSupport();
				fRevisionInfoSupport.install((ISourceViewer) viewer, info);
			}
		}
	}

	@Override
	public boolean isInitialized() {
		return fRevisionInfoSupport != null;
	}
}
