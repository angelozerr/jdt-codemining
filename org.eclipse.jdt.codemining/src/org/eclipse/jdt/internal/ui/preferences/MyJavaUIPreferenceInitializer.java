package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class MyJavaUIPreferenceInitializer extends AbstractPreferenceInitializer {

	public MyJavaUIPreferenceInitializer() {
		
	}
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = MyPreferenceConstants.getPreferenceStore();
		MyPreferenceConstants.initializeDefaultValues(store);
	}

}
