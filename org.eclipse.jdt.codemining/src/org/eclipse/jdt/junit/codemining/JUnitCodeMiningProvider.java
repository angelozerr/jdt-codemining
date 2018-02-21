package org.eclipse.jdt.junit.codemining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.source.ISourceViewerExtension5;
import org.eclipse.ui.texteditor.ITextEditor;

public class JUnitCodeMiningProvider extends AbstractCodeMiningProvider {

	class CodeMiningTestRunListener extends TestRunListener {

		private Map<IJavaProject, Map<String, ITestCaseElement>> projects;

		public CodeMiningTestRunListener() {
			projects = new HashMap<>();
		}

		@Override
		public void testCaseFinished(ITestCaseElement testCaseElement) {
			super.testCaseFinished(testCaseElement);
			IJavaProject project = testCaseElement.getTestRunSession().getLaunchedProject();
			String className = testCaseElement.getTestClassName();
			String methodName = testCaseElement.getTestMethodName();

			Map<String, ITestCaseElement> tests = projects.get(project);
			if (tests == null) {
				tests = new HashMap<>();
				projects.put(project, tests);
			}
			String key = className + "#" + methodName;
			tests.put(key, testCaseElement);
		}

		@Override
		public void sessionFinished(ITestRunSession session) {
			super.sessionFinished(session);
			((ISourceViewerExtension5) viewer).updateCodeMinings();
		}

		public ITestCaseElement findTestCase(IMethod method) {
			IJavaProject project = method.getJavaProject();
			String className = method.getDeclaringType().getElementName();
			String methodName = method.getElementName();
			String key = className + "#" + methodName;
			Map<String, ITestCaseElement> tests = projects.get(project);
			if (tests == null) {
				return null;
			}
			return tests.get(key);
		}
	}

	private final CodeMiningTestRunListener junitListener;
	private ITextViewer viewer;

	public JUnitCodeMiningProvider() {
		junitListener = new CodeMiningTestRunListener();
		JUnitCore.addTestRunListener(junitListener);
	}


	private boolean isStatusCodeMiningsEnabled() {
		return true;
	}

	private boolean isRunCodeMiningsEnabled() {
		return true;
	}

	private boolean isDebugCodeMiningsEnabled() {
		return true;
	}

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		this.viewer = viewer;
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
			if (unit == null) {
				return null;
			}
			try {
				IJavaElement[] elements = unit.getChildren();
				List<ICodeMining> minings = new ArrayList<>(elements.length);
				collectCodeMinings(unit, elements, minings, viewer, monitor);
				monitor.isCanceled();
				return minings;
			} catch (JavaModelException e) {
				// TODO: what should we done when there are some errors?
			}
			return null;
		});
	}

	private void collectCodeMinings(ITypeRoot unit, IJavaElement[] elements, List<ICodeMining> minings,
			ITextViewer viewer, IProgressMonitor monitor) {
		for (IJavaElement element : elements) {
			if (monitor.isCanceled()) {
				return;
			}
			try {
				if (element.getElementType() == IJavaElement.TYPE) {
					collectCodeMinings(unit, ((IType) element).getChildren(), minings, viewer, monitor);
				} else if (element.getElementType() == IJavaElement.METHOD) {
					IMethod method = (IMethod) element;
					if (isTestMethod(method, "org.junit.Test") || isTestMethod(method, "Test")) {
						if (isStatusCodeMiningsEnabled())
							minings.add(new JUnitStatusCodeMining(method, junitListener, viewer.getDocument(), this));
						if (isRunCodeMiningsEnabled())
							minings.add(new JUnitLaunchCodeMining(method, "Run", "run", viewer.getDocument(), this));
						if (isDebugCodeMiningsEnabled())
							minings.add(
									new JUnitLaunchCodeMining(method, "Debug", "debug", viewer.getDocument(), this));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isTestMethod(IMethod method, String annotation) {
		int flags;
		try {
			flags = method.getFlags();
			// 'V' is void signature
			return !(method.isConstructor() || !Flags.isPublic(flags) || Flags.isAbstract(flags)
					|| Flags.isStatic(flags) || !"V".equals(method.getReturnType()))
					&& method.getAnnotation(annotation).exists();
		} catch (JavaModelException e) {
			// ignore
			return false;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		JUnitCore.removeTestRunListener(junitListener);
	}

}
