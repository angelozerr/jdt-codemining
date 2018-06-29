package org.eclipse.jface.text.revisions.provisional;

import java.util.List;

import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.source.ILineRange;

public interface IRevisionRangeProvider {

	boolean isInitialized();

	/**
	 * Returns the revision range that contains the given line, or <code>null</code>
	 * if there is none.
	 *
	 * @param line the line of interest
	 * @return the corresponding <code>RevisionRange</code> or <code>null</code>
	 */
	RevisionRange getRange(int line);

	/**
	 * Returns the sublist of all <code>RevisionRange</code>s that intersect with
	 * the given lines.
	 *
	 * @param lines the model based lines of interest
	 * @return elementType: RevisionRange
	 */
	List<RevisionRange> getRanges(ILineRange lines);
}
