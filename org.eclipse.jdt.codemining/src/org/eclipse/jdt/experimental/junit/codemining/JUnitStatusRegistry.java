package org.eclipse.jdt.experimental.junit.codemining;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestSuiteElement;

public class JUnitStatusRegistry {

	private final TestTracker testTracker;
	private final Map<IJavaProject, Map<String, Map<String, ITestCaseElement>>> projects;

	public JUnitStatusRegistry() {
		projects = new HashMap<>();
		testTracker = new TestTracker();
		JUnitCore.addTestRunListener(testTracker);
	}
	
	public void dispose() {
		JUnitCore.removeTestRunListener(testTracker);
	}

	class TestTracker extends TestRunListener {

		@Override
		public void testCaseFinished(ITestCaseElement testCaseElement) {
			super.testCaseFinished(testCaseElement);
			IJavaProject project = testCaseElement.getTestRunSession().getLaunchedProject();
			String className = testCaseElement.getTestClassName();
			String methodName = testCaseElement.getTestMethodName();

			Map<String, Map<String, ITestCaseElement>> testClasses = projects.get(project);
			if (testClasses == null) {
				testClasses = new HashMap<>();
				projects.put(project, testClasses);
			}
			Map<String, ITestCaseElement> tests = testClasses.get(className);
			if (tests == null) {
				tests = new HashMap<>();
				testClasses.put(className, tests);
			}
			String key = methodName;
			tests.put(key, testCaseElement);
		}
	}

	public ITestCaseElement findTestCase(IMethod element) {
		IJavaProject project = element.getJavaProject();
		String className = element.getDeclaringType().getFullyQualifiedName();
		String methodName = element.getElementName();
		Map<String, Map<String, ITestCaseElement>> testClasses = projects.get(project);
		if (testClasses == null) {
			return null;
		}
		Map<String, ITestCaseElement> tests = testClasses.get(className);
		if (tests == null) {
			return null;
		}
		return tests.get(methodName);
	}

	public ITestSuiteElement findTestSuite(IType element) {
		IJavaProject project = element.getJavaProject();
		String className = element.getFullyQualifiedName();
		Map<String, Map<String, ITestCaseElement>> testClasses = projects.get(project);
		if (testClasses == null) {
			return null;
		}
		Map<String, ITestCaseElement> tests = testClasses.get(className);
		if (tests == null) {
			return null;
		}
		return (ITestSuiteElement) ((ITestCaseElement) tests.values().toArray()[0]).getParentContainer();
	}

}
