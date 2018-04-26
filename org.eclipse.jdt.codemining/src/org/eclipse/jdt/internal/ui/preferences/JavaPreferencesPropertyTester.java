/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - [CodeMining] Provide Java References/Implementation CodeMinings - Bug 529127
 */
package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;

/**
 * Property tester which checks that a given preference is true inside the Java preference store.
 *
 * @since 3.14
 */
public class JavaPreferencesPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		return isEnabled(expectedValue.toString());
	}
	
	public static boolean isEnabled(String preferenceName) {
		return JavaCore.ENABLED.equals(PreferenceConstants.getPreferenceStore()
				.getString(preferenceName));
	}

}