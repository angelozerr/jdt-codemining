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
package org.eclipse.jdt.experimental.ui.javaeditor.codemining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.experimental.ui.javaeditor.codemining.implementations.JavaImplementationCodeMining;
import org.eclipse.jdt.experimental.ui.javaeditor.codemining.references.JavaReferenceCodeMining;
import org.eclipse.jdt.experimental.ui.preferences.JavaPreferencesPropertyTester;
import org.eclipse.jdt.experimental.ui.preferences.MyPreferenceConstants;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.revisions.provisional.IRevisionRangeProvider;
import org.eclipse.jface.text.revisions.provisional.RevisionInformationProviderManager;
import org.eclipse.jface.text.revisions.provisional.RevisionInformationSupport;
import org.eclipse.jface.text.revisions.provisional.codemining.RevisionAuthorsCodeMining;
import org.eclipse.jface.text.revisions.provisional.codemining.RevisionRecentChangeCodeMining;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Java code mining provider to show code minings by using {@link IJavaElement}:
 * 
 * <ul>
 * <li>Show references</li>
 * <li>Show implementations</li>
 * </ul>
 * 
 * @since 3.15
 *
 */
public class JavaElementCodeMiningProvider extends AbstractCodeMiningProvider implements IRevisionRangeProvider {

	private RevisionInformationSupport fRevisionInfoSupport;

	private ITextViewer fViewer;

	private ITypeRoot fUnit;

	private final boolean showReferences;

	private final boolean showReferencesOnClass;

	private final boolean showReferencesOnMethod;

	private final boolean showReferencesAtLeastOne;

	private final boolean showImplementations;

	private final boolean showImplementationsAtLeastOne;

	public JavaElementCodeMiningProvider() {
		showReferences = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES);
		showReferencesOnClass = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_CLASS_REFERENCES);
		showReferencesOnMethod = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_REFERENCES);
		showReferencesAtLeastOne = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE);
		showImplementations = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS);
		showImplementationsAtLeastOne = JavaPreferencesPropertyTester
				.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE);
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
				List<ICodeMining> minings = new ArrayList<>();
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
			} else if (element.getElementType() != IJavaElement.METHOD) {
				continue;
			}
			if (showReferences) {
				try {
					if ((showReferencesOnClass && (element.getElementType() == IJavaElement.TYPE))
							|| (showReferencesOnMethod && (element.getElementType() == IJavaElement.METHOD))) {
						minings.add(new JavaReferenceCodeMining(element, (JavaEditor) textEditor, viewer.getDocument(),
								this, showReferencesAtLeastOne));
					}
				} catch (BadLocationException e) {
					// Should never occur
				}
			}
			if (showImplementations) {
				if (element instanceof IType) {
					IType type = (IType) element;
					if (type.isInterface() || Flags.isAbstract(type.getFlags())) {
						try {
							minings.add(new JavaImplementationCodeMining(type, viewer.getDocument(), this,
									showImplementationsAtLeastOne));
						} catch (BadLocationException e) {
							// Should never occur
						}
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

	private synchronized void initRevisionSupport(ITextViewer viewer, ITypeRoot unit) {
		if (fRevisionInfoSupport != null) {
			return;
		}
		IResource resource = unit.getResource();
		if (resource == null) {
			return;
		}
		if (fRevisionInfoSupport == null) {
			RevisionInformation info = RevisionInformationProviderManager.getInstance().getRevisionInformation(resource,
					viewer, super.getAdapter(ITextEditor.class));
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

}
