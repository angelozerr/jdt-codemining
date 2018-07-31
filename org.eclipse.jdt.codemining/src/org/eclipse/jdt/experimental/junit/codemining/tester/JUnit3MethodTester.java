package org.eclipse.jdt.experimental.junit.codemining.tester;

import org.eclipse.jdt.core.IMethod;

public class JUnit3MethodTester implements IJUnitMethodTester {

	public static final IJUnitMethodTester INSTANCE = new JUnit3MethodTester();

	@Override
	public boolean isTestMethod(IMethod method) {
		return IJUnitMethodTester.isMethod(method, true) && method.getElementName().startsWith("test");
	}

}
