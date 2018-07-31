package org.eclipse.jdt.experimental.ui.preferences;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.preferences.FilteredPreferenceTree.PreferenceTreeNode;
import org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.jdt.internal.ui.preferences.ScrolledPageContent;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class JavaEditorCodeMiningConfigurationBlock extends OptionsConfigurationBlock {

	// Preference store keys, see JavaCore.getOptions

	// --------------------- General

	private static final Key PREF_SHOW_REFERENCES = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES);

	private static final Key PREF_SHOW_REFERENCES_AT_LEAST_ONE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE);

	private static final Key PREF_SHOW_IMPLEMENTATIONS = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS);

	private static final Key PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE);

	public static final Key PREF_SHOW_END_STATEMENT = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT);

	public static final Key PREF_SHOW_END_STATEMENT_MIN_LINE_NUMBER = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT_MIN_LINE_NUMBER);

	private static final Key PREF_SHOW_MAIN_RUN = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_RUN);

	private static final Key PREF_SHOW_MAIN_DEBUG = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_DEBUG);

	private static final Key PREF_SHOW_JAVA10_VAR_TYPE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JAVA10_VAR_TYPE);

	// --------------------- Method parameter

	public static final Key PREF_SHOW_METHOD_PARAMETER_NAMES = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_NAMES);

	public static final Key PREF_SHOW_METHOD_PARAMETER_TYPES = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_TYPES);

	public static final Key PREF_SHOW_METHOD_PARAMETER_ONLY_FOR_LITERAL = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_ONLY_FOR_LITERAL);

	public static final Key PREF_SHOW_METHOD_PARAMETER_BY_USING_FILTERS = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_BY_USING_FILTERS);

	public static final Key PREF_SHOW_METHOD_PARAMETER_FILTERS_ENABLED = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_FILTERS_ENABLED);

	public static final Key PREF_SHOW_METHOD_PARAMETER_FILTERS_DISABLED = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_FILTERS_DISABLED);

	// --------------------- JUnit

	public static final Key PREF_SHOW_JUNIT_STATUS = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_STATUS);

	public static final Key PREF_SHOW_JUNIT_RUN = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_RUN);

	public static final Key PREF_SHOW_JUNIT_DEBUG = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_DEBUG);

	public static final Key PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING);

	public static final Key PREF_SHOW_REVISION_RECENT_CHANGE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE);

	public static final Key PREF_SHOW_REVISION_RECENT_CHANGE_WITH_AVATAR = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_AVATAR);

	public static final Key PREF_SHOW_REVISION_RECENT_CHANGE_WITH_DATE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_DATE); // $NON-NLS-1$

	public static final Key PREF_SHOW_REVISION_AUTHORS = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_AUTHORS); // $NON-NLS-1$

	private static final String SETTINGS_SECTION_NAME = "JavaEditorCodeMiningConfigurationBlock"; // $NON-NLS-1

	private static final String[] TRUE_FALSE = new String[] { "true", "false" }; //$NON-NLS-1$ //$NON-NLS-2$

	private PixelConverter fPixelConverter;

	private PreferenceTree fFilteredPrefTree;

	private JavaMethodFiltersTable fJavaMethodFiltersTable;

	public JavaEditorCodeMiningConfigurationBlock(IStatusChangeListener context,
			IWorkbenchPreferenceContainer container) {
		super(context, null, getAllKeys(), container);
	}

	public static Key[] getAllKeys() {
		return new Key[] { PREF_SHOW_REFERENCES, PREF_SHOW_REFERENCES_AT_LEAST_ONE, PREF_SHOW_IMPLEMENTATIONS,
				PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE, PREF_SHOW_END_STATEMENT,
				PREF_SHOW_END_STATEMENT_MIN_LINE_NUMBER, PREF_SHOW_MAIN_RUN, PREF_SHOW_MAIN_DEBUG,
				PREF_SHOW_JAVA10_VAR_TYPE, PREF_SHOW_METHOD_PARAMETER_NAMES, PREF_SHOW_METHOD_PARAMETER_TYPES,
				PREF_SHOW_METHOD_PARAMETER_ONLY_FOR_LITERAL, PREF_SHOW_METHOD_PARAMETER_BY_USING_FILTERS,
				PREF_SHOW_METHOD_PARAMETER_FILTERS_ENABLED, PREF_SHOW_METHOD_PARAMETER_FILTERS_DISABLED,
				PREF_SHOW_JUNIT_STATUS, PREF_SHOW_JUNIT_RUN, PREF_SHOW_JUNIT_DEBUG,
				PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING, PREF_SHOW_REVISION_RECENT_CHANGE,
				PREF_SHOW_REVISION_RECENT_CHANGE_WITH_AVATAR, PREF_SHOW_REVISION_RECENT_CHANGE_WITH_DATE,
				PREF_SHOW_REVISION_AUTHORS };
	}

	/*
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		fPixelConverter = new PixelConverter(parent);
		setShell(parent.getShell());

		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainComp.setLayout(layout);

		createIgnoreOptionalProblemsLink(mainComp);

		Composite spacer = new Composite(mainComp, SWT.NONE);
		spacer.setLayoutData(new GridData(0, 0));

		Composite commonComposite = createStyleTabContent(mainComp);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = fPixelConverter.convertHeightInCharsToPixels(30);
		commonComposite.setLayoutData(gridData);

		validateSettings(null, null, null);

		return mainComp;
	}

	private Composite createStyleTabContent(Composite folder) {
		fFilteredPrefTree = new PreferenceTree(this, folder,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_common_description);
		final ScrolledPageContent sc1 = fFilteredPrefTree.getScrolledPageContent();

		int nColumns = 3;
		Composite composite = sc1.getBody();
		GridLayout layout = new GridLayout(nColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		// --- General
		createGeneralSection(nColumns, composite);
		// --- Method parameter
		createMethodParameterSection(nColumns, composite);
		// --- JUnit
		createJUnitSection(nColumns, composite);
		// --- Debugging
		createDebuggingSection(nColumns, composite);
		// --- Git
		createGitSection(nColumns, composite);

		IDialogSettings settingsSection = JavaPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
		restoreSectionExpansionStates(settingsSection);

		return sc1;
	}

	private void createGeneralSection(int nColumns, Composite parent) {
		int defaultIndent = 0;
		int extraIndent = LayoutUtil.getIndent();
		String label = MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_section_general;
		Key twistieKey = OptionsConfigurationBlock.getLocalKey("JavaEditorCodeMiningPreferencePage_section_general"); //$NON-NLS-1$
		PreferenceTreeNode<?> section = fFilteredPrefTree.addExpandableComposite(parent, label, nColumns, twistieKey,
				null, false);
		ExpandableComposite excomposite = getExpandableComposite(twistieKey);

		Composite inner = createInnerComposite(excomposite, nColumns, parent.getFont());

		// - Show references
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showReferences_label, PREF_SHOW_REFERENCES,
				TRUE_FALSE, defaultIndent, section);
		// - Show references (Only if there is at least one reference)
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showReferences_atLeastOne_label,
				PREF_SHOW_REFERENCES_AT_LEAST_ONE, TRUE_FALSE, extraIndent, section);

		// - Show implementations
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showImplementations_label,
				PREF_SHOW_IMPLEMENTATIONS, TRUE_FALSE, defaultIndent, section);
		// - Show implementations (Only if there is at least one implementation)
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showImplementations_atLeastOne_label,
				PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE, TRUE_FALSE, extraIndent, section);

		// - Show end statement
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showEndStatement_label,
				PREF_SHOW_END_STATEMENT, TRUE_FALSE, defaultIndent, section);
		// - Show end statement min line number
		fFilteredPrefTree.addTextField(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showEndStatement_minLineNumber_label,
				PREF_SHOW_END_STATEMENT_MIN_LINE_NUMBER, extraIndent, 0, section);

		// - Show type of Java9 'var' declaration
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showJava10VarType_label,
				PREF_SHOW_JAVA10_VAR_TYPE, TRUE_FALSE, defaultIndent, section);
	}

	private void createMethodParameterSection(int nColumns, Composite parent) {
		final int defaultIndent = 0;
		String label = MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_section_methodParameter;
		Key twistieKey = OptionsConfigurationBlock
				.getLocalKey("JavaEditorCodeMiningPreferencePage_section_methodParameter"); //$NON-NLS-1$
		PreferenceTreeNode<?> section = fFilteredPrefTree.addExpandableComposite(parent, label, nColumns, twistieKey,
				null, false);
		ExpandableComposite excomposite = getExpandableComposite(twistieKey);

		Composite inner = createInnerComposite(excomposite, nColumns, parent.getFont());

		// - Show method parameter names
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showMethodParameterNames_label,
				PREF_SHOW_METHOD_PARAMETER_NAMES, TRUE_FALSE, defaultIndent, section);
		// - Show method parameter types
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showMethodParameterTypes_label,
				PREF_SHOW_METHOD_PARAMETER_TYPES, TRUE_FALSE, defaultIndent, section);
		// - Show parameter only for literal
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showMethodParameterOnlyForLiteral_label,
				PREF_SHOW_METHOD_PARAMETER_ONLY_FOR_LITERAL, TRUE_FALSE, defaultIndent, section);
		// - Show parameter by using filters
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showMethodParameterByUsingFilters_label,
				PREF_SHOW_METHOD_PARAMETER_BY_USING_FILTERS, TRUE_FALSE, defaultIndent, section);

		fJavaMethodFiltersTable = new JavaMethodFiltersTable();
		fJavaMethodFiltersTable.createControl(inner);
		fJavaMethodFiltersTable.refresh(getValue(PREF_SHOW_METHOD_PARAMETER_FILTERS_ENABLED),
				getValue(PREF_SHOW_METHOD_PARAMETER_FILTERS_DISABLED));

		Button useFiltersCheckbox = getCheckBox(PREF_SHOW_METHOD_PARAMETER_BY_USING_FILTERS);
		fJavaMethodFiltersTable.setEnabled(useFiltersCheckbox.getSelection());
		useFiltersCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fJavaMethodFiltersTable.setEnabled(useFiltersCheckbox.getSelection());
			}
		});
	}

	private void createJUnitSection(int nColumns, Composite parent) {
		final int defaultIndent = 0;
		String label = MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_section_junit;
		Key twistieKey = OptionsConfigurationBlock.getLocalKey("JavaEditorCodeMiningPreferencePage_section_junit"); //$NON-NLS-1$
		PreferenceTreeNode<?> section = fFilteredPrefTree.addExpandableComposite(parent, label, nColumns, twistieKey,
				null, false);
		ExpandableComposite excomposite = getExpandableComposite(twistieKey);

		Composite inner = createInnerComposite(excomposite, nColumns, parent.getFont());

		// - Show JUnit status
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showJUnitStatus_label,
				PREF_SHOW_JUNIT_STATUS, TRUE_FALSE, defaultIndent, section);
		// - Show JUnit run
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showJUnitRun_label, PREF_SHOW_JUNIT_RUN,
				TRUE_FALSE, defaultIndent, section);
		// - Show JUnit debug
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showJUnitDebug_label,
				PREF_SHOW_JUNIT_DEBUG, TRUE_FALSE, defaultIndent, section);
	}

	private void createDebuggingSection(int nColumns, Composite parent) {
		final int defaultIndent = 0;
		String label = MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_section_debugging;
		Key twistieKey = OptionsConfigurationBlock.getLocalKey("JavaEditorCodeMiningPreferencePage_section_debugging"); //$NON-NLS-1$
		PreferenceTreeNode<?> section = fFilteredPrefTree.addExpandableComposite(parent, label, nColumns, twistieKey,
				null, false);
		ExpandableComposite excomposite = getExpandableComposite(twistieKey);

		Composite inner = createInnerComposite(excomposite, nColumns, parent.getFont());

		// - Show main run/debug
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showMainRun_label, PREF_SHOW_MAIN_RUN,
				TRUE_FALSE, defaultIndent, section);
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showMainDebug_label, PREF_SHOW_MAIN_DEBUG,
				TRUE_FALSE, defaultIndent, section);

		// - Show variable value while debugging
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showVariableValueWhileDebugging_label,
				PREF_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING, TRUE_FALSE, defaultIndent, section);
	}

	private void createGitSection(int nColumns, Composite parent) {
		final int defaultIndent = 0;
		int extraIndent = LayoutUtil.getIndent();
		String label = MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_section_scsm;
		Key twistieKey = OptionsConfigurationBlock.getLocalKey("JavaEditorCodeMiningPreferencePage_section_sccm"); //$NON-NLS-1$
		PreferenceTreeNode<?> section = fFilteredPrefTree.addExpandableComposite(parent, label, nColumns, twistieKey,
				null, false);
		ExpandableComposite excomposite = getExpandableComposite(twistieKey);

		Composite inner = createInnerComposite(excomposite, nColumns, parent.getFont());

		// - Show git recent change
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showRevisionRecentChange,
				PREF_SHOW_REVISION_RECENT_CHANGE, TRUE_FALSE, defaultIndent, section);
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showRevisionRecentChangeWithAvatar,
				PREF_SHOW_REVISION_RECENT_CHANGE_WITH_AVATAR, TRUE_FALSE, extraIndent, section);
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showRevisionRecentChangeWithDate,
				PREF_SHOW_REVISION_RECENT_CHANGE_WITH_DATE, TRUE_FALSE, extraIndent, section);
		// Show authors
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showRevisionAuthors,
				PREF_SHOW_REVISION_AUTHORS, TRUE_FALSE, defaultIndent, section);

		// - Show git changes
//		fFilteredPrefTree.addCheckBox(inner,
//				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showGitChanges_label,
//				PREF_SHOW_GIT_CHANGES, enabledDisabled, defaultIndent, section);
	}

	private Composite createInnerComposite(ExpandableComposite excomposite, int nColumns, Font font) {
		Composite inner = new Composite(excomposite, SWT.NONE);
		inner.setFont(font);
		inner.setLayout(new GridLayout(nColumns, false));
		excomposite.setClient(inner);
		return inner;
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		if (!areSettingsEnabled()) {
			return;
		}
		fContext.statusChanged(new StatusInfo());
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	@Override
	public void dispose() {
		IDialogSettings section = JavaPlugin.getDefault().getDialogSettings().addNewSection(SETTINGS_SECTION_NAME);
		storeSectionExpansionStates(section);
		super.dispose();
	}

	@Override
	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		String enabled = fJavaMethodFiltersTable.getEnabled();
		setValue(PREF_SHOW_METHOD_PARAMETER_FILTERS_ENABLED, enabled);
		String disabled = fJavaMethodFiltersTable.getDisabled();
		setValue(PREF_SHOW_METHOD_PARAMETER_FILTERS_DISABLED, disabled);
		return super.processChanges(container);
	}

	@Override
	protected void updateControls() {
		fJavaMethodFiltersTable.refresh(getValue(PREF_SHOW_METHOD_PARAMETER_FILTERS_ENABLED),
				getValue(PREF_SHOW_METHOD_PARAMETER_FILTERS_DISABLED));
		super.updateControls();
	}
}
