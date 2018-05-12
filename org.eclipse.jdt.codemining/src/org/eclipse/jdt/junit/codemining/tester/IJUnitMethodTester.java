package org.eclipse.jdt.junit.codemining.tester;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public interface IJUnitMethodTester {

	public static boolean isTestMethod(IMethod method, boolean onlyPublicMethod, String[] annotations) {
		if (isMethod(method, onlyPublicMethod)) {
			for (String annotation : annotations) {
				if (method.getAnnotation(annotation).exists()) {
					return true;
				}
			}
		}
		return false;

	}

	public static boolean isMethod(IMethod method, boolean onlyPublicMethod) {
		try {
			int flags = method.getFlags();
			if (onlyPublicMethod && !Flags.isPublic(flags)) {
				return false;
			}
			// 'V' is void signature
			return !(method.isConstructor() || Flags.isAbstract(flags) || Flags.isStatic(flags)
					|| !"V".equals(method.getReturnType()));
		} catch (JavaModelException e) {
			// ignore
			return false;
		}
	}

	boolean isTestMethod(IMethod method);

}
