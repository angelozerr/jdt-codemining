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

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The page for setting the Java codemining.
 */
public final class JavaEditorCodeMiningPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String PREF_SHOW_REFERENCES = MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES;

	private static final String PREF_SHOW_REFERENCES_AT_LEAST_ONE = MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE;

	private static final String PREF_SHOW_IMPLEMENTATIONS = MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS;

	private static final String PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE = MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE;

	public static final String PREF_SHOW_METHOD_PARAMETER_NAMES = "java.codemining.methodParameter.names.enabled"; //$NON-NLS-1$

	public static final String PREF_SHOW_METHOD_PARAMETER_TYPES = "java.codemining.methodParameter.types.enabled"; //$NON-NLS-1$

	public static final String PREF_SHOW_END_STATEMENT = "java.codemining.endStatement.enabled"; //$NON-NLS-1$

	public static final String PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING = "java.codemining.debug.variable"; //$NON-NLS-1$

	public static final String PREF_SHOW_JUNIT_STATUS = "java.codemining.junit.status.enabled"; //$NON-NLS-1$

	public static final String PREF_SHOW_JUNIT_RUN = "java.codemining.junit.run.enabled"; //$NON-NLS-1$

	public static final String PREF_SHOW_JUNIT_DEBUG = "java.codemining.junit.debug.enabled"; //$NON-NLS-1$

	private SelectionButtonDialogField fShowReferences;

	private SelectionButtonDialogField fShowReferencesAtLeastOne;

	private SelectionButtonDialogField fShowImplementations;

	private SelectionButtonDialogField fShowImplementationsAtLeastOne;

	private SelectionButtonDialogField fShowMethodParameterNames;

	private SelectionButtonDialogField fShowMethodParameterTypes;

	private SelectionButtonDialogField fShowEndStatement;

	private SelectionButtonDialogField fShowVariableValueWhileDebugging;

	private SelectionButtonDialogField fShowJUnitStatus;

	private SelectionButtonDialogField fShowJUnitRun;

	private SelectionButtonDialogField fShowJUnitDebug;

	public JavaEditorCodeMiningPreferencePage() {
		setPreferenceStore(JavaPlugin.getDefault().getPreferenceStore());
		setDescription(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_description);

		IDialogFieldListener listener = new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				doDialogFieldChanged(field);
			}
		};

		fShowReferences = new SelectionButtonDialogField(SWT.CHECK);
		fShowReferences.setDialogFieldListener(field -> {
			listener.dialogFieldChanged(field);
			fShowReferencesAtLeastOne.setEnabled(fShowReferences.isSelected());
		});
		fShowReferences.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showReferences_label);
		fShowReferencesAtLeastOne = new SelectionButtonDialogField(SWT.CHECK);
		fShowReferencesAtLeastOne.setDialogFieldListener(listener);
		fShowReferencesAtLeastOne
				.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showReferences_atLeastOne_label);

		fShowImplementations = new SelectionButtonDialogField(SWT.CHECK);
		fShowImplementations.setDialogFieldListener(field -> {
			listener.dialogFieldChanged(field);
			fShowImplementationsAtLeastOne.setEnabled(fShowImplementations.isSelected());
		});
		fShowImplementations
				.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showImplementations_label);
		fShowImplementationsAtLeastOne = new SelectionButtonDialogField(SWT.CHECK);
		fShowImplementationsAtLeastOne.setDialogFieldListener(listener);
		fShowImplementationsAtLeastOne.setLabelText(
				MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showImplementations_atLeastOne_label);

		fShowMethodParameterNames = new SelectionButtonDialogField(SWT.CHECK);
		fShowMethodParameterNames.setDialogFieldListener(listener);
		fShowMethodParameterNames
				.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showMethodParameterNames_label);

		fShowMethodParameterTypes = new SelectionButtonDialogField(SWT.CHECK);
		fShowMethodParameterTypes.setDialogFieldListener(listener);
		fShowMethodParameterTypes
				.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showMethodParameterTypes_label);

		fShowEndStatement = new SelectionButtonDialogField(SWT.CHECK);
		fShowEndStatement.setDialogFieldListener(listener);
		fShowEndStatement.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showEndStatement_label);

		fShowVariableValueWhileDebugging = new SelectionButtonDialogField(SWT.CHECK);
		fShowVariableValueWhileDebugging.setDialogFieldListener(listener);
		fShowVariableValueWhileDebugging.setLabelText(
				MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showVariableValueWhileDebugging_label);

		fShowJUnitStatus = new SelectionButtonDialogField(SWT.CHECK);
		fShowJUnitStatus.setDialogFieldListener(listener);
		fShowJUnitStatus.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showJUnitStatus_label);

		fShowJUnitRun = new SelectionButtonDialogField(SWT.CHECK);
		fShowJUnitRun.setDialogFieldListener(listener);
		fShowJUnitRun.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showJUnitRun_label);

		fShowJUnitDebug = new SelectionButtonDialogField(SWT.CHECK);
		fShowJUnitDebug.setDialogFieldListener(listener);
		fShowJUnitDebug.setLabelText(MyPreferencesMessages.JavaEditorCodeMiningPreferencePage_showJUnitDebug_label);

	}

	private void initFields() {
		IPreferenceStore prefs = getPreferenceStore();
		fShowReferences.setSelection(prefs.getBoolean(PREF_SHOW_REFERENCES));
		fShowReferencesAtLeastOne.setSelection(prefs.getBoolean(PREF_SHOW_REFERENCES_AT_LEAST_ONE));
		fShowReferencesAtLeastOne.setEnabled(fShowReferences.isSelected());
		fShowImplementations.setSelection(prefs.getBoolean(PREF_SHOW_IMPLEMENTATIONS));
		fShowImplementationsAtLeastOne.setSelection(prefs.getBoolean(PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE));
		fShowImplementationsAtLeastOne.setEnabled(fShowImplementations.isSelected());
		fShowMethodParameterNames.setSelection(prefs.getBoolean(PREF_SHOW_METHOD_PARAMETER_NAMES));
		fShowMethodParameterTypes.setSelection(prefs.getBoolean(PREF_SHOW_METHOD_PARAMETER_TYPES));
		fShowEndStatement.setSelection(prefs.getBoolean(PREF_SHOW_END_STATEMENT));
		fShowVariableValueWhileDebugging.setSelection(prefs.getBoolean(PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING));
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
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IJavaHelpContextIds.APPEARANCE_PREFERENCE_PAGE);
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		int nColumns = 1;

		Composite result = new Composite(parent, SWT.NONE);
		result.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = 0;
		layout.numColumns = nColumns;
		result.setLayout(layout);

		createShowReferences(nColumns, result);
		createShowImplementations(nColumns, result);

		fShowMethodParameterNames.doFillIntoGrid(result, nColumns);
		fShowMethodParameterTypes.doFillIntoGrid(result, nColumns);
		fShowEndStatement.doFillIntoGrid(result, nColumns);
		fShowVariableValueWhileDebugging.doFillIntoGrid(result, nColumns);
		fShowJUnitStatus.doFillIntoGrid(result, nColumns);
		fShowJUnitRun.doFillIntoGrid(result, nColumns);
		fShowJUnitDebug.doFillIntoGrid(result, nColumns);

		initFields();

		Dialog.applyDialogFont(result);
		return result;
	}

	private void createShowReferences(int nColumns, Composite result) {
		GridLayout layout;
		fShowReferences.doFillIntoGrid(result, nColumns);
		Composite fillComposite = new Composite(result, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalIndent = LayoutUtil.getIndent();
		fillComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		fillComposite.setLayout(layout);
		fShowReferencesAtLeastOne.doFillIntoGrid(fillComposite, 1);
	}

	private void createShowImplementations(int nColumns, Composite result) {
		GridLayout layout;
		fShowImplementations.doFillIntoGrid(result, nColumns);
		Composite fillComposite = new Composite(result, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalIndent = LayoutUtil.getIndent();
		fillComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		fillComposite.setLayout(layout);
		fShowImplementationsAtLeastOne.doFillIntoGrid(fillComposite, 1);
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
		IPreferenceStore prefs = getPreferenceStore();
		prefs.setValue(PREF_SHOW_REFERENCES, fShowReferences.isSelected());
		prefs.setValue(PREF_SHOW_REFERENCES_AT_LEAST_ONE, fShowReferencesAtLeastOne.isSelected());
		prefs.setValue(PREF_SHOW_IMPLEMENTATIONS, fShowImplementations.isSelected());
		prefs.setValue(PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE, fShowImplementationsAtLeastOne.isSelected());
		prefs.setValue(PREF_SHOW_METHOD_PARAMETER_NAMES, fShowMethodParameterNames.isSelected());
		prefs.setValue(PREF_SHOW_METHOD_PARAMETER_TYPES, fShowMethodParameterTypes.isSelected());
		prefs.setValue(PREF_SHOW_END_STATEMENT, fShowEndStatement.isSelected());
		prefs.setValue(PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING, fShowVariableValueWhileDebugging.isSelected());
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
		IPreferenceStore prefs = getPreferenceStore();
		fShowReferences.setSelection(prefs.getDefaultBoolean(PREF_SHOW_REFERENCES));
		fShowReferencesAtLeastOne.setSelection(prefs.getDefaultBoolean(PREF_SHOW_REFERENCES_AT_LEAST_ONE));
		fShowImplementations.setSelection(prefs.getDefaultBoolean(PREF_SHOW_IMPLEMENTATIONS));
		fShowImplementationsAtLeastOne.setSelection(prefs.getDefaultBoolean(PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE));
		fShowMethodParameterNames.setSelection(prefs.getDefaultBoolean(PREF_SHOW_METHOD_PARAMETER_NAMES));
		fShowMethodParameterTypes.setSelection(prefs.getDefaultBoolean(PREF_SHOW_METHOD_PARAMETER_TYPES));
		fShowEndStatement.setSelection(prefs.getDefaultBoolean(PREF_SHOW_END_STATEMENT));
		fShowVariableValueWhileDebugging
				.setSelection(prefs.getDefaultBoolean(PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING));
		fShowJUnitStatus.setSelection(prefs.getDefaultBoolean(PREF_SHOW_JUNIT_STATUS));
		fShowJUnitRun.setSelection(prefs.getDefaultBoolean(PREF_SHOW_JUNIT_RUN));
		fShowJUnitDebug.setSelection(prefs.getDefaultBoolean(PREF_SHOW_JUNIT_DEBUG));
		super.performDefaults();
	}
}
