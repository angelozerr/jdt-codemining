package org.eclipse.egit.codemining.ui.internal.blame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.internal.blame.BlameRevision;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.revisions.provisional.IRevisionInformationProvider;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitRevisionInformationProvider implements IRevisionInformationProvider {

	@Override
	public RevisionInformation getRevisionInformation(IResource resource) {
		RepositoryMapping mapping = RepositoryMapping.getMapping(resource);
		if (mapping == null) {
			return null;
		}
		Repository repository = mapping.getRepository();
		if (repository == null) {
			return null;
		}
		String path = mapping.getRepoRelativePath(resource);
		if (path == null) {
			return null;
		}
		final BlameCommand command = new BlameCommand(repository).setFollowFileRenames(true).setFilePath(path);
		try {
			command.setStartCommit(repository.resolve(Constants.HEAD));
		} catch (IOException e) {
			return null;
		}

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

		RevisionInformation info = new RevisionInformation();
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
				revision = new ExtendedBlameRevision();
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

		return info;
	}

}
