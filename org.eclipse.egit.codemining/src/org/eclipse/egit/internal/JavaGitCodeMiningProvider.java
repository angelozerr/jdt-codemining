package org.eclipse.egit.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.ui.internal.blame.GitRevisionInformationProvider;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.revisions.RevisionInformationSupport;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.revisions.codemining.IRevisionRangeProvider;
import org.eclipse.jface.text.revisions.codemining.RevisionRecentChangeCodeMining;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaGitCodeMiningProvider extends AbstractCodeMiningProvider implements IRevisionRangeProvider {

	private RevisionInformationSupport fRevisionInfoSupport;

	private static Set<String> SILENCED_CODEGENS = Collections.singleton("lombok"); //$NON-NLS-1$

	private boolean isReferencesCodeMiningsEnabled() {
		return true;
	}

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
			if (unit == null) {
				return null;
			}
			IResource resource = unit.getResource();
			if (resource == null) {
				return null;
			}
			if (fRevisionInfoSupport == null) {
				RevisionInformation info = new GitRevisionInformationProvider().getRevisionInformation(resource);
				if (info != null) {
					fRevisionInfoSupport = new RevisionInformationSupport();
					fRevisionInfoSupport.install((ISourceViewer) viewer, info);
				}
			}
			if (fRevisionInfoSupport == null) {
				return new ArrayList<>();
			}
			try {
				IJavaElement[] elements = unit.getChildren();
				List<ICodeMining> minings = new ArrayList<>(elements.length);
				collectLineHeaderCodeMinings(unit, textEditor, unit.getChildren(), minings, viewer, monitor);
				monitor.isCanceled();
				return minings;
			} catch (JavaModelException e) {
				// TODO: what should we done when there are some errors?
			}
			return new ArrayList<>();
		});
	}

	private void collectLineHeaderCodeMinings(ITypeRoot unit, ITextEditor textEditor, IJavaElement[] elements,
			List<ICodeMining> minings, ITextViewer viewer, IProgressMonitor monitor) throws JavaModelException {
		for (IJavaElement element : elements) {
			if (monitor.isCanceled()) {
				return;
			}
			if (element.getElementType() == IJavaElement.TYPE) {
				collectLineHeaderCodeMinings(unit, textEditor, ((IType) element).getChildren(), minings, viewer,
						monitor);
			} else if (element.getElementType() != IJavaElement.METHOD || isHiddenGeneratedElement(element)) {
				continue;
			}
			if (isReferencesCodeMiningsEnabled()) {
				try {
					minings.add(new RevisionRecentChangeCodeMining(getLineNumber(element, viewer.getDocument()),
							viewer.getDocument(), this, this));
					// minings.add(
					// new RevisionAuthorsCodeMining(getLineNumber(element, viewer.getDocument()),
					// viewer.getDocument(), this, this));
					// minings.add(
					// new GitChangesMining(element, (JavaEditor) textEditor, viewer.getDocument(),
					// this));
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

	public static int getLineNumber(IJavaElement element, IDocument document)
			throws JavaModelException, BadLocationException {
		ISourceRange r = ((ISourceReference) element).getNameRange();
		int offset = r.getOffset();
		return document.getLineOfOffset(offset);
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

	/**
	 * Returns the revision range that contains the given line, or <code>null</code>
	 * if there is none.
	 *
	 * @param line the line of interest
	 * @return the corresponding <code>RevisionRange</code> or <code>null</code>
	 */
	public RevisionRange getRange(int line) {
		if (fRevisionInfoSupport != null) {
			return fRevisionInfoSupport.getRange(line);
		}
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (fRevisionInfoSupport != null) {
			fRevisionInfoSupport.uninstall();
		}
	}

}
