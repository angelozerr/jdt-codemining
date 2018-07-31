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
package org.eclipse.jdt.experimental.ui.javaeditor.codemining.references;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.experimental.junit.codemining.AbstractJavaCodeMining;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.actions.FindReferencesAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;

/**
 * Java reference code mining.
 * 
 * @since 3.14
 */
public class JavaReferenceCodeMining extends AbstractJavaCodeMining {

	private final boolean referencesCodeMiningsAtLeastOne;

	public JavaReferenceCodeMining(IJavaElement element, JavaEditor editor, IDocument document,
			ICodeMiningProvider provider, boolean referencesCodeMiningsAtLeastOne)
			throws JavaModelException, BadLocationException {
		super(element, document, provider, e -> new FindReferencesAction(editor).run(element));
		this.referencesCodeMiningsAtLeastOne = referencesCodeMiningsAtLeastOne;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			try {
				monitor.isCanceled();
				long refCount = countReferences(getElement(), monitor);
				monitor.isCanceled();
				if (refCount == 0 && referencesCodeMiningsAtLeastOne) {
					super.setLabel("");
				} else {
					super.setLabel(refCount + " " + (refCount > 1 ? "references" : "reference")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			} catch (JavaModelException e) {
				// TODO: what should we done when there are some errors?
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO: what should we done when there are some errors?
				e.printStackTrace();
			}
		});
	}

	/**
	 * Return the number of references for the given java element.
	 * 
	 * @param element the java element.
	 * @param monitor the monitor
	 * @return he number of references for the given java element.
	 * @throws JavaModelException throws when java error.
	 * @throws CoreException      throws when java error.
	 */
	private static long countReferences(IJavaElement element, IProgressMonitor monitor)
			throws JavaModelException, CoreException {
		if (element == null) {
			return 0;
		}
		final AtomicLong count = new AtomicLong(0);
		SearchPattern pattern = SearchPattern.createPattern(element, IJavaSearchConstants.REFERENCES);
		SearchEngine engine = new SearchEngine();
		engine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
				createSearchScope(), new SearchRequestor() {

					@Override
					public void acceptSearchMatch(SearchMatch match) throws CoreException {
						Object o = match.getElement();
						if (o instanceof IJavaElement
								&& ((IJavaElement) o).getAncestor(IJavaElement.COMPILATION_UNIT) != null) {
							count.incrementAndGet();
						}
					}
				}, monitor);

		return count.get();
	}

	/**
	 * Create Java workspace scope.
	 * 
	 * @return the Java workspace scope.
	 * @throws JavaModelException when java error.
	 */
	private static IJavaSearchScope createSearchScope() throws JavaModelException {
		IJavaProject[] projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
		return SearchEngine.createJavaSearchScope(projects, IJavaSearchScope.SOURCES);
	}
}
