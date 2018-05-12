package org.eclipse.jdt.junit.codemining.tester;

import org.eclipse.jdt.core.IMethod;

public class JUnit4MethodTester implements IJUnitMethodTester {

	public static final IJUnitMethodTester INSTANCE = new JUnit4MethodTester();

	private final String[] JUNIT_TEST_ANNOTATIONS = new String[] { "Test", "org.junit.Test" };

	@Override
	public boolean isTestMethod(IMethod method) {
		return IJUnitMethodTester.isTestMethod(method, true, JUNIT_TEST_ANNOTATIONS);
	}

}
