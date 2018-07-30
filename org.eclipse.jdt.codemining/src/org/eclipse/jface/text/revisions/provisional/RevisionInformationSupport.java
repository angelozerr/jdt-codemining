package org.eclipse.jface.text.revisions.provisional;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.internal.text.revisions.Hunk;
import org.eclipse.jface.internal.text.revisions.HunkComputer;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.ILineDiffer;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;

public class RevisionInformationSupport {

	public static final String MY_QUICK_DIFF_MODEL_ID = "myQuickDiff";

	private RevisionInformation fRevisionInfo;

	private List<RevisionRange> fRevisionRanges;

	private ILineDiffer fLineDiffer;

	private ITextViewer fViewer;

	private class AnnotationListener implements IAnnotationModelListener {
		@Override
		public void modelChanged(IAnnotationModel model) {
			clearRangeCache();
		}
	}

	private final AnnotationListener fAnnotationListener = new AnnotationListener();

	public void install(ISourceViewer viewer, RevisionInformation information) {
		fViewer = viewer;
		fRevisionInfo = information;		
		setModel(viewer.getAnnotationModel());
	}

	public void uninstall() {
		if (fLineDiffer != null) {
			((IAnnotationModel) fLineDiffer).removeAnnotationModelListener(fAnnotationListener);
			fLineDiffer = null;
		}
	}

	/**
	 * Sets the annotation model.
	 *
	 * @param model the annotation model, possibly <code>null</code>
	 * @see IVerticalRulerColumn#setModel(IAnnotationModel)
	 */
	private void setModel(IAnnotationModel model) {
		IAnnotationModel diffModel;
		if (model instanceof IAnnotationModelExtension)
			diffModel = ((IAnnotationModelExtension) model).getAnnotationModel(MY_QUICK_DIFF_MODEL_ID);
		else
			diffModel = model;
		if (diffModel == null) {
			//diffModel = new DocumentLineDiffer();
		}
		setDiffer(diffModel);
		// setAnnotationModel(model);
	}

	private void setDiffer(IAnnotationModel differ) {
		if (differ instanceof ILineDiffer || differ == null) {
			if (fLineDiffer != differ) {
				if (fLineDiffer != null)
					((IAnnotationModel) fLineDiffer).removeAnnotationModelListener(fAnnotationListener);
				fLineDiffer = (ILineDiffer) differ;
				if (fLineDiffer != null)
					((IAnnotationModel) fLineDiffer).addAnnotationModelListener(fAnnotationListener);
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
	 * Returns the sublist of all <code>RevisionRange</code>s that intersect with the given lines.
	 *
	 * @param lines the model based lines of interest
	 * @return elementType: RevisionRange
	 */
	public List<RevisionRange> getRanges(ILineRange lines) {
		List<RevisionRange> ranges= getRangeCache();

		// return the interesting subset
		int end= end(lines);
		int first= -1, last= -1;
		for (int i= 0; i < ranges.size(); i++) {
			RevisionRange range= ranges.get(i);
			int rangeStart = range.getStartLine();
			int rangeEnd= end(range);
			if (first == -1 && (rangeEnd > lines.getStartLine() && rangeStart <= lines.getStartLine()))
				first= i;
			if (first != -1 && rangeEnd > end) {
				last= i;
				break;
			}
		}
		if (first == -1)
			return Collections.emptyList();
		if (last == -1)
			last= ranges.size() - 1; // bottom index may be one too much
			//return Collections.emptyList();

		return ranges.subList(first, last + 1);
	}
	
	/**
	 * Gets all change ranges of the revisions in the revision model and adapts them
	 * to the current quick diff information. The list is cached.
	 *
	 * @return the list of all change regions, with diff information applied
	 */
	private synchronized List<RevisionRange> getRangeCache() {
		if (fRevisionRanges == null) {
			if (fRevisionInfo == null || fLineDiffer == null) {
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

	/**
	 * Clears the range cache.
	 *
	 */
	private synchronized void clearRangeCache() {
		fRevisionRanges = null;
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

}
