package org.eclipse.jdt.internal.ui.preferences;


import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * The page for setting the Java codemining.
 */
public final class CodeMiningPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String PREF_SHOW_REFERENCES= "java.codemining.references.enabled"; //$NON-NLS-1$

	public static final String PREF_SHOW_IMPLEMENTATIONS= "java.codemining.implementations.enabled"; //$NON-NLS-1$

	private static final String PREF_SHOW_METHOD_PARAMETERS= "java.codemining.methodParameters.enabled"; //$NON-NLS-1$

	public static final String PREF_SHOW_JUNIT_STATUS = "java.codemining.junit.status.enabled"; //$NON-NLS-1$
	
	public static final String PREF_SHOW_JUNIT_RUN = "java.codemining.junit.run.enabled"; //$NON-NLS-1$
	
	public static final String PREF_SHOW_JUNIT_DEBUG = "java.codemining.junit.debug.enabled"; //$NON-NLS-1$

	private SelectionButtonDialogField fShowReferences;

	private SelectionButtonDialogField fShowImplementations;

	private SelectionButtonDialogField fShowMethodParameters;

	private SelectionButtonDialogField fShowJUnitStatus;
	
	private SelectionButtonDialogField fShowJUnitRun;
	
	private SelectionButtonDialogField fShowJUnitDebug;
	
	public CodeMiningPreferencePage() {
		setPreferenceStore(JavaPlugin.getDefault().getPreferenceStore());
		setDescription(MyPreferencesMessages.CodeMiningPreferencePage_description);

		IDialogFieldListener listener= new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				doDialogFieldChanged(field);
			}
		};

		fShowReferences= new SelectionButtonDialogField(SWT.CHECK);
		fShowReferences.setDialogFieldListener(listener);
		fShowReferences.setLabelText(MyPreferencesMessages.CodeMiningPreferencePage_showReferences_label);

		fShowImplementations= new SelectionButtonDialogField(SWT.CHECK);
		fShowImplementations.setDialogFieldListener(listener);
		fShowImplementations.setLabelText(MyPreferencesMessages.CodeMiningPreferencePage_showImplementations_label);

		fShowMethodParameters= new SelectionButtonDialogField(SWT.CHECK);
		fShowMethodParameters.setDialogFieldListener(listener);
		fShowMethodParameters.setLabelText(MyPreferencesMessages.CodeMiningPreferencePage_showMethodParameters_label);

		fShowJUnitStatus= new SelectionButtonDialogField(SWT.CHECK);
		fShowJUnitStatus.setDialogFieldListener(listener);
		fShowJUnitStatus.setLabelText(MyPreferencesMessages.CodeMiningPreferencePage_showJUnitStatus_label);

		fShowJUnitRun= new SelectionButtonDialogField(SWT.CHECK);
		fShowJUnitRun.setDialogFieldListener(listener);
		fShowJUnitRun.setLabelText(MyPreferencesMessages.CodeMiningPreferencePage_showJUnitRun_label);
		
		fShowJUnitDebug= new SelectionButtonDialogField(SWT.CHECK);
		fShowJUnitDebug.setDialogFieldListener(listener);
		fShowJUnitDebug.setLabelText(MyPreferencesMessages.CodeMiningPreferencePage_showJUnitDebug_label);

	}

	private void initFields() {
		IPreferenceStore prefs= getPreferenceStore();
		fShowReferences.setSelection(prefs.getBoolean(PREF_SHOW_REFERENCES));
		fShowImplementations.setSelection(prefs.getBoolean(PREF_SHOW_IMPLEMENTATIONS));
		fShowMethodParameters.setSelection(prefs.getBoolean(PREF_SHOW_METHOD_PARAMETERS));
		fShowJUnitStatus.setSelection(prefs.getBoolean(PREF_SHOW_JUNIT_STATUS));
		fShowJUnitRun.setSelection(prefs.getBoolean(PREF_SHOW_JUNIT_RUN));
		fShowJUnitDebug.setSelection(prefs.getBoolean(PREF_SHOW_JUNIT_DEBUG));
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaHelpContextIds.APPEARANCE_PREFERENCE_PAGE);
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		int nColumns= 1;

		Composite result= new Composite(parent, SWT.NONE);
		result.setFont(parent.getFont());

		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= 0;
		layout.numColumns= nColumns;
		result.setLayout(layout);

		fShowReferences.doFillIntoGrid(result, nColumns);
		fShowImplementations.doFillIntoGrid(result, nColumns);
		fShowMethodParameters.doFillIntoGrid(result, nColumns);
		fShowJUnitStatus.doFillIntoGrid(result, nColumns);
		fShowJUnitRun.doFillIntoGrid(result, nColumns);
		fShowJUnitDebug.doFillIntoGrid(result, nColumns);
		
		initFields();

		Dialog.applyDialogFont(result);
		return result;
	}

	private void doDialogFieldChanged(DialogField field) {
//		if (field == fCompressPackageNames)
//			fPackageNamePattern.setEnabled(fCompressPackageNames.isSelected());
//
//		if (field == fAbbreviatePackageNames)
//			fAbbreviatePackageNamePattern.setEnabled(fAbbreviatePackageNames.isSelected());
//
//		updateStatus(getValidationStatus());
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

	/*
	 * @see IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		IPreferenceStore prefs= getPreferenceStore();
		prefs.setValue(PREF_SHOW_REFERENCES, fShowReferences.isSelected());
		prefs.setValue(PREF_SHOW_IMPLEMENTATIONS, fShowImplementations.isSelected());
		prefs.setValue(PREF_SHOW_METHOD_PARAMETERS, fShowMethodParameters.isSelected());
		prefs.setValue(PREF_SHOW_JUNIT_STATUS, fShowJUnitStatus.isSelected());
		prefs.setValue(PREF_SHOW_JUNIT_RUN, fShowJUnitRun.isSelected());
		prefs.setValue(PREF_SHOW_JUNIT_DEBUG, fShowJUnitDebug.isSelected());
		JavaPlugin.flushInstanceScope();
		return super.performOk();
	}

	/*
	 * @see PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		IPreferenceStore prefs= getPreferenceStore();
		fShowReferences.setSelection(prefs.getDefaultBoolean(PREF_SHOW_REFERENCES));
		fShowImplementations.setSelection(prefs.getDefaultBoolean(PREF_SHOW_IMPLEMENTATIONS));
		fShowMethodParameters.setSelection(prefs.getDefaultBoolean(PREF_SHOW_METHOD_PARAMETERS));
		fShowJUnitStatus.setSelection(prefs.getDefaultBoolean(PREF_SHOW_JUNIT_STATUS));
		fShowJUnitRun.setSelection(prefs.getDefaultBoolean(PREF_SHOW_JUNIT_RUN));
		fShowJUnitDebug.setSelection(prefs.getDefaultBoolean(PREF_SHOW_JUNIT_DEBUG));
		super.performDefaults();
	}
}
