package org.eclipse.jface.text.revisions.provisional.codemining;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.revisions.provisional.IRevisionRangeExtension;
import org.eclipse.jface.text.revisions.provisional.IRevisionRangeProvider;
import org.eclipse.jface.text.revisions.provisional.avatar.Avatar;
import org.eclipse.jface.text.revisions.provisional.avatar.AvatarRepository;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class RevisionRecentChangeCodeMining extends LineHeaderCodeMining {

	private final ILineRange lineRange;
	private final IRevisionRangeProvider rangeProvider;
	private final boolean showAvatar;
	private final boolean showDate;
	private Avatar avatar;

	public RevisionRecentChangeCodeMining(int beforeLineNumber, ILineRange lineRange, IDocument document,
			boolean showAvatar, boolean showDate, ICodeMiningProvider provider, IRevisionRangeProvider rangeProvider)
			throws JavaModelException, BadLocationException {
		super(beforeLineNumber, document, provider);
		this.rangeProvider = rangeProvider;
		this.lineRange = lineRange;
		this.showAvatar = showAvatar;
		this.showDate = showDate;
		if (rangeProvider.isInitialized()) {
			updateLabel();
		}
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		if (getLabel() != null) {
			return super.doResolve(viewer, monitor);
		}
		return CompletableFuture.runAsync(() -> {
			updateLabel();
		});
	}

	private void updateLabel() {
		try {
			List<RevisionRange> ranges = rangeProvider.getRanges(lineRange);
			if (ranges != null && ranges.size() > 0) {
				Revision revision = ranges.stream().map(r -> r.getRevision())
						.max(Comparator.comparing(Revision::getDate)).get();
				if (showDate && (revision instanceof IRevisionRangeExtension)) {
					super.setLabel(
							revision.getAuthor() + ", " + ((IRevisionRangeExtension) revision).getFormattedTime());
				} else {
					super.setLabel(revision.getAuthor());
				}
				if (showAvatar) {
					if (revision instanceof IRevisionRangeExtension) {
						String email = ((IRevisionRangeExtension) revision).getAuthorEmail();
						if (email != null) {
							avatar = AvatarRepository.getInstance().getAvatarByEmail(email);
						}
					}
				}
			}
		} catch (Exception e) {
			super.setLabel(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
		if (avatar != null) {
			Display display = textWidget.getDisplay();
			Image image = new Image(display, avatar.getData());
			
			int textHeight = gc.stringExtent(getLabel()).y;
			int imageYOffset = (textHeight - image.getBounds().height) / 2;
			if (imageYOffset < 0 ) {
				imageYOffset = 0;
			}
			
			gc.drawImage(image, x, y + imageYOffset);
			image.dispose();
			x += 18;

			Point title = super.draw(gc, textWidget, color, x, y);
			title.x += 18;
			return title;
		} else {
			return super.draw(gc, textWidget, color, x, y);
		}

	}

}
