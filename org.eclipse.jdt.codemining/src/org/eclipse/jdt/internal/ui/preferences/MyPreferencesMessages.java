package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class MyPreferencesMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.ui.preferences.MyPreferencesMessages";//$NON-NLS-1$

	public static String CodeMiningPreferencePage_description;
	public static String CodeMiningPreferencePage_showReferences_label;
	public static String CodeMiningPreferencePage_showImplementations_label;
	public static String CodeMiningPreferencePage_showMethodParameterNames_label;
	public static String CodeMiningPreferencePage_showMethodParameterTypes_label;
	public static String CodeMiningPreferencePage_showJUnitStatus_label;
	public static String CodeMiningPreferencePage_showJUnitRun_label;
	public static String CodeMiningPreferencePage_showJUnitDebug_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, MyPreferencesMessages.class);
	}
}
