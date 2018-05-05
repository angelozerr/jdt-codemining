package org.eclipse.egit.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.internal.blame.BlameRevision;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.internal.text.revisions.Hunk;
import org.eclipse.jface.internal.text.revisions.HunkComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IChangeRulerColumn;
import org.eclipse.jface.text.source.ILineDiffer;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaGitCodeMiningProvider extends AbstractCodeMiningProvider {

	private RevisionInformation fRevisionInfo;

	private List<RevisionRange> fRevisionRanges;

	private ILineDiffer fLineDiffer = null;

	private ITextViewer fViewer;

	private static Set<String> SILENCED_CODEGENS = Collections.singleton("lombok"); //$NON-NLS-1$

	private boolean isReferencesCodeMiningsEnabled() {
		return true;
	}

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		fViewer = viewer;
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			setModel(((ISourceViewer) viewer).getAnnotationModel());
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
			if (unit == null) {
				return null;
			}
			IResource resource = unit.getResource();
			if (resource == null) {
				return null;
			}
			RepositoryMapping mapping = RepositoryMapping.getMapping(resource);
			if (mapping == null) {
				return new ArrayList<>();
			}
			String repoRelativePath = mapping.getRepoRelativePath(resource);
			try {
				if (fRevisionInfo == null) {
					fRevisionInfo = execute(mapping.getRepository(), repoRelativePath, null, monitor);
				}
			} catch (CoreException e2) {
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

	private void collectLineHeaderCodeMinings(ITypeRoot unit,ITextEditor textEditor,
			IJavaElement[] elements, List<ICodeMining> minings, ITextViewer viewer, IProgressMonitor monitor)
			throws JavaModelException {
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
					minings.add(
							new GitAuthorMining(element, (JavaEditor) textEditor, viewer.getDocument(), this));
					minings.add(
							new GitChangesMining(element, (JavaEditor) textEditor, viewer.getDocument(), this));					
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

	/**
	 * Sets the annotation model.
	 *
	 * @param model the annotation model, possibly <code>null</code>
	 * @see IVerticalRulerColumn#setModel(IAnnotationModel)
	 */
	public void setModel(IAnnotationModel model) {
		IAnnotationModel diffModel;
		if (model instanceof IAnnotationModelExtension)
			diffModel = ((IAnnotationModelExtension) model).getAnnotationModel(IChangeRulerColumn.QUICK_DIFF_MODEL_ID);
		else
			diffModel = model;

		setDiffer(diffModel);
		// setAnnotationModel(model);
	}

	private void setDiffer(IAnnotationModel differ) {
		if (differ instanceof ILineDiffer || differ == null) {
			if (fLineDiffer != differ) {
				// if (fLineDiffer != null)
				// ((IAnnotationModel)
				// fLineDiffer).removeAnnotationModelListener(fAnnotationListener);
				fLineDiffer = (ILineDiffer) differ;
				// if (fLineDiffer != null)
				// ((IAnnotationModel)
				// fLineDiffer).addAnnotationModelListener(fAnnotationListener);
			}
		}
	}

	/**
	 * Returns the revision range that contains the given line, or <code>null</code>
	 * if there is none.
	 *
	 * @param line the line of interest
	 * @return the corresponding <code>RevisionRange</code> or <code>null</code>
	 */
	public RevisionRange getRange(int line) {
		List<RevisionRange> ranges = getRangeCache();

		if (ranges.isEmpty() || line == -1)
			return null;

		for (RevisionRange range : ranges) {
			if (contains(range, line))
				return range;
		}

		// line may be right after the last region
		RevisionRange lastRegion = ranges.get(ranges.size() - 1);
		if (line == end(lastRegion))
			return lastRegion;
		return null;
	}

	/**
	 * Returns <code>true</code> if <code>range</code> contains <code>line</code>. A
	 * line is not contained in a range if it is the range's exclusive end line.
	 *
	 * @param range the range to check whether it contains <code>line</code>
	 * @param line  the line the line
	 * @return <code>true</code> if <code>range</code> contains <code>line</code>,
	 *         <code>false</code> if not
	 */
	private static boolean contains(ILineRange range, int line) {
		return range.getStartLine() <= line && end(range) > line;
	}

	/**
	 * Computes the end index of a line range.
	 *
	 * @param range a line range
	 * @return the last line (exclusive) of <code>range</code>
	 */
	private static int end(ILineRange range) {
		return range.getStartLine() + range.getNumberOfLines();
	}

	/**
	 * Gets all change ranges of the revisions in the revision model and adapts them
	 * to the current quick diff information. The list is cached.
	 *
	 * @return the list of all change regions, with diff information applied
	 */
	private synchronized List<RevisionRange> getRangeCache() {
		if (fRevisionRanges == null) {
			if (fRevisionInfo == null) {
				fRevisionRanges = Collections.emptyList();
			} else {
				Hunk[] hunks = HunkComputer.computeHunks(fLineDiffer, fViewer.getDocument().getNumberOfLines());
				fRevisionInfo.applyDiff(hunks);
				fRevisionRanges = fRevisionInfo.getRanges();
				// updateOverviewAnnotations();
				// informListeners();
			}
		}

		return fRevisionRanges;
	}

	private RevisionInformation execute(Repository repository, String path, RevCommit startCommit,
			IProgressMonitor monitor) throws CoreException {
		// SubMonitor progress = SubMonitor.convert(monitor, 3);
		final RevisionInformation info = new RevisionInformation();

		final BlameCommand command = new BlameCommand(repository).setFollowFileRenames(true).setFilePath(path);
		if (startCommit != null)
			command.setStartCommit(startCommit);
		else {
			try {
				command.setStartCommit(repository.resolve(Constants.HEAD));
			} catch (IOException e) {
				// Activator
				// .error("Error resolving HEAD for showing annotations in repository: " +
				// repository, e); //$NON-NLS-1$
				return null;
			}
		}
		// if (Activator.getDefault().getPreferenceStore()
		// .getBoolean(UIPreferences.BLAME_IGNORE_WHITESPACE))
		command.setTextComparator(RawTextComparator.WS_IGNORE_ALL);

		BlameResult result;
		try {
			result = command.call();
		} catch (Exception e1) {
			// Activator.error(e1.getMessage(), e1);
			return null;
		}
		// progress.worked(1);
		if (result == null)
			return null;

		Map<RevCommit, BlameRevision> revisions = new HashMap<>();
		int lineCount = result.getResultContents().size();
		BlameRevision previous = null;
		for (int i = 0; i < lineCount; i++) {
			RevCommit commit = result.getSourceCommit(i);
			String sourcePath = result.getSourcePath(i);
			if (commit == null) {
				// Unregister the current revision
				if (previous != null) {
					previous.register();
					previous = null;
				}
				continue;
			}
			BlameRevision revision = revisions.get(commit);
			if (revision == null) {
				revision = new BlameRevision();
				revision.setRepository(repository);
				revision.setCommit(commit);
				revision.setSourcePath(sourcePath);
				revisions.put(commit, revision);
				info.addRevision(revision);
			}
			revision.addSourceLine(i, result.getSourceLine(i));
			if (previous != null)
				if (previous == revision)
					previous.addLine();
				else {
					previous.register();
					previous = revision.reset(i);
				}
			else
				previous = revision.reset(i);
		}
		if (previous != null)
			previous.register();

//		progress.worked(1);
//		if (shell.isDisposed()) {
//			return;
//		}

		/*
		 * if (fileRevision != null) { storage =
		 * fileRevision.getStorage(progress.newChild(1)); } else { //progress.worked(1);
		 * }
		 */
//		shell.getDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				openEditor(info);
//			}
//		});
		return info;
	}

}
