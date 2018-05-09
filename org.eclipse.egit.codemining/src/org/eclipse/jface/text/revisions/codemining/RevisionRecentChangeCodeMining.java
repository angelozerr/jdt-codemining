package org.eclipse.jface.text.revisions.codemining;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.internal.avatar.Avatar;
import org.eclipse.egit.internal.avatar.AvatarRepository;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.revisions.IRevisionRangeExtension;
import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.github.marlonlom.utilities.timeago.TimeAgo;

public class RevisionRecentChangeCodeMining extends LineHeaderCodeMining {

	private Revision revision;
	private Avatar avatar;
	private final IRevisionRangeProvider rangeProvider;
	private int beforeLineNumber;

	public RevisionRecentChangeCodeMining(int beforeLineNumber, IDocument document, ICodeMiningProvider provider,
			IRevisionRangeProvider rangeProvider) throws JavaModelException, BadLocationException {
		super(beforeLineNumber, document, provider);
		this.rangeProvider = rangeProvider;
		this.beforeLineNumber = beforeLineNumber;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			try {
				RevisionRange range = rangeProvider.getRange(beforeLineNumber);
				if (range != null) {
					revision = range.getRevision();
					super.setLabel(range.getRevision().getAuthor() + ", "
							+ TimeAgo.using(range.getRevision().getDate().getTime()));
					if (revision instanceof IRevisionRangeExtension) {
						String email = ((IRevisionRangeExtension) revision).getAuthorEmail();
						if (email != null) {
							avatar = AvatarRepository.getInstance().getAvatarByEmail(email);
						}
					}
				}
			} catch (Exception e) {
				super.setLabel(e.getMessage());
				e.printStackTrace();
			}
		});
	}

	@Override
	public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
		if (avatar != null) {
			Display display = textWidget.getDisplay();
			Image image = new Image(display, avatar.getData());
			gc.drawImage(image, x, y + 2);
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
