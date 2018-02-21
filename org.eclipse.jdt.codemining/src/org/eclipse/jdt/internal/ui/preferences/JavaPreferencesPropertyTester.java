package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.internal.ui.JavaPlugin;

public class JavaPreferencesPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		return JavaPlugin.getDefault().getPreferenceStore().getBoolean(expectedValue.toString());
	}

}
