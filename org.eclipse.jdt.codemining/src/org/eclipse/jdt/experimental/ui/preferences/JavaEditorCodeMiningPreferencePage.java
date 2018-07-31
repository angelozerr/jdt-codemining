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
package org.eclipse.jdt.experimental.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * Code mining preference page.
 * <p>
 * Note: Must be public since it is referenced from plugin.xml
 * </p>
 *
 * @since 3.15
 */
public class JavaEditorCodeMiningPreferencePage extends PropertyAndPreferencePage {

	private JavaEditorCodeMiningConfigurationBlock fConfigurationBlock;

	public JavaEditorCodeMiningPreferencePage() {
		setPreferenceStore(MyPreferenceConstants.getPreferenceStore());
	}

	@Override
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container= (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock= new JavaEditorCodeMiningConfigurationBlock(getNewStatusChangedListener(), container);

		super.createControl(parent);
	}
	
	@Override
	protected Control createPreferenceContent(Composite composite) {
		return fConfigurationBlock.createContents(composite);
	}

	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return false;
	}

	@Override
	protected String getPreferencePageID() {
		return "org.eclipse.jdt.experimental.ui.preferences.JavaEditorCodeMiningPreferencePage"; //$NON-NLS-1$
	}

	@Override
	protected String getPropertyPageID() {
		return null;
	}

	@Override
	public void dispose() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.dispose();
		}
		super.dispose();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		super.performDefaults();
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
			return false;
		}
		return super.performOk();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performApply()
	 */
	@Override
	public void performApply() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performApply();
		}
	}
}
