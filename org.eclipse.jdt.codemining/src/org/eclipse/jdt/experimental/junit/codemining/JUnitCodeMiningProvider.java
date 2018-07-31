package org.eclipse.jdt.experimental.junit.codemining;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.experimental.internal.ui.preferences.JavaPreferencesPropertyTester;
import org.eclipse.jdt.experimental.internal.ui.preferences.MyPreferenceConstants;
import org.eclipse.jdt.experimental.junit.codemining.tester.JUnit3MethodTester;
import org.eclipse.jdt.experimental.junit.codemining.tester.JUnit4MethodTester;
import org.eclipse.jdt.experimental.junit.codemining.tester.JUnit5MethodTester;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.source.ISourceViewerExtension5;
import org.eclipse.ui.texteditor.ITextEditor;

public class JUnitCodeMiningProvider extends AbstractCodeMiningProvider {

	class CodeMiningTestRunListener extends TestRunListener {

		@Override
		public void sessionFinished(ITestRunSession session) {
			super.sessionFinished(session);
			if (viewer != null) {
				((ISourceViewerExtension5) viewer).updateCodeMinings();
			}
		}
	}

	private final JUnitStatusRegistry testRegistry;
	private final CodeMiningTestRunListener junitListener;
	private ITextViewer viewer;

	public JUnitCodeMiningProvider() {
		// TODO: use JUnitStatusRegistry as singleton once it will track remove of Java file/
		// Using JUnitStatusRegistry as singleton will give the capability to show JUnit status when user open the editor.
		testRegistry = new JUnitStatusRegistry();
		junitListener = new CodeMiningTestRunListener();
		JUnitCore.addTestRunListener(junitListener);
	}

	private boolean isStatusCodeMiningsEnabled() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_STATUS);
	}

	private boolean isRunCodeMiningsEnabled() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_RUN);
	}

	private boolean isDebugCodeMiningsEnabled() {
		return JavaPreferencesPropertyTester.isEnabled(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_DEBUG);
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
					if (!minings.isEmpty()) {
						try {
							// There is one or more JUNit test, display Run, Debug icon for class
							if (isStatusCodeMiningsEnabled())
								minings.add(
										new JUnitStatusCodeMining(element, testRegistry, viewer.getDocument(), this));
							if (isRunCodeMiningsEnabled())
								minings.add(new JUnitLaunchCodeMining(element, "Run All", "run", viewer.getDocument(),
										this));
							if (isDebugCodeMiningsEnabled())
								minings.add(new JUnitLaunchCodeMining(element, "Debug All", "debug",
										viewer.getDocument(), this));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (element.getElementType() == IJavaElement.METHOD) {
					IMethod method = (IMethod) element;
					if (isTestMethod(method)) {
						if (isStatusCodeMiningsEnabled())
							minings.add(new JUnitStatusCodeMining(method, testRegistry, viewer.getDocument(), this));
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

	private static boolean isTestMethod(IMethod method) {
		return JUnit3MethodTester.INSTANCE.isTestMethod(method) || JUnit4MethodTester.INSTANCE.isTestMethod(method)
				|| JUnit5MethodTester.INSTANCE.isTestMethod(method);
	}

	@Override
	public void dispose() {
		super.dispose();
		testRegistry.dispose();
		JUnitCore.removeTestRunListener(junitListener);
	}

}
