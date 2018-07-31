package org.eclipse.jdt.experimental.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.experimental.ui.javaeditor.codemining.methods.MethodFilterManager;
import org.eclipse.jdt.internal.ui.preferences.PreferencesMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class JavaMethodFiltersTable {

	private class TypeFilterAdapter implements IListAdapter<String>, IDialogFieldListener {

		private boolean canEdit(ListDialogField<String> field) {
			return field.getSelectedElements().size() == 1;
		}

		@Override
		public void customButtonPressed(ListDialogField<String> field, int index) {
			doButtonPressed(index);
		}

		@Override
		public void selectionChanged(ListDialogField<String> field) {
			fFilterListField.enableButton(IDX_EDIT, canEdit(field));
		}

		@Override
		public void dialogFieldChanged(DialogField field) {
		}

		@Override
		public void doubleClicked(ListDialogField<String> field) {
			if (canEdit(field)) {
				doButtonPressed(IDX_EDIT);
			}
		}
	}

	private static final int IDX_ADD = 0;
	//private static final int IDX_ADD_PACKAGE = 1;
	private static final int IDX_EDIT = 2;
	private static final int IDX_REMOVE = 3;
	private static final int IDX_SELECT = 5;
	private static final int IDX_DESELECT = 6;

	private CheckedListDialogField<String> fFilterListField;

	public JavaMethodFiltersTable() {

		String[] buttonLabels = new String[] { PreferencesMessages.TypeFilterPreferencePage_add_button,
				"TODO",
				PreferencesMessages.TypeFilterPreferencePage_edit_button,
				PreferencesMessages.TypeFilterPreferencePage_remove_button, /* 4 */ null,
				PreferencesMessages.TypeFilterPreferencePage_selectall_button,
				PreferencesMessages.TypeFilterPreferencePage_deselectall_button, };

		TypeFilterAdapter adapter = new TypeFilterAdapter();

		fFilterListField = new CheckedListDialogField<>(adapter, buttonLabels, new LabelProvider());
		fFilterListField.setDialogFieldListener(adapter);
		fFilterListField.setLabelText(
				MyPreferencesMessages.JavaMethodFiltersTable_description);
		fFilterListField.setCheckAllButtonIndex(IDX_SELECT);
		fFilterListField.setUncheckAllButtonIndex(IDX_DESELECT);
		fFilterListField.setRemoveButtonIndex(IDX_REMOVE);

		fFilterListField.enableButton(IDX_EDIT, false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		composite.setLayout(layout);

		fFilterListField.doFillIntoGrid(composite, 3);
		LayoutUtil.setHorizontalSpan(fFilterListField.getLabelControl(null), 2);
		// LayoutUtil.setWidthHint(fFilterListField.getLabelControl(null),
		// convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(fFilterListField.getListControl(null));

		fFilterListField.getTableViewer().setComparator(new ViewerComparator());

	}

	public String getEnabled() {
		List<String> checked = fFilterListField.getCheckedElements();
		return MethodFilterManager.packOrderList(checked);
	}

	public String getDisabled() {
		List<String> checked = fFilterListField.getCheckedElements();
		List<String> unchecked = fFilterListField.getElements();
		unchecked.removeAll(checked);
		return MethodFilterManager.packOrderList(unchecked);
	}

	public void refresh(String enabled, String disabled) {
		List<String> res = new ArrayList<>();

		String[] enabledEntries = MethodFilterManager.unpackOrderList(enabled);
		for (int i = 0; i < enabledEntries.length; i++) {
			res.add(enabledEntries[i]);
		}
		String[] disabledEntries = MethodFilterManager.unpackOrderList(disabled);
		for (int i = 0; i < disabledEntries.length; i++) {
			res.add(disabledEntries[i]);
		}

		fFilterListField.setElements(res);
		fFilterListField.setCheckedElements(Arrays.asList(enabledEntries));
	}

	public void setEnabled(boolean enabled) {
		fFilterListField.setEnabled(enabled);
	}
	
	private void doButtonPressed(int index) {
		if (index == IDX_ADD) { // add new
			List<String> existing= fFilterListField.getElements();
			JavaMethodFilterInputDialog dialog= new JavaMethodFilterInputDialog(getShell(), existing);
			if (dialog.open() == Window.OK) {
				String res= (String) dialog.getResult();
				fFilterListField.addElement(res);
				fFilterListField.setChecked(res, true);
			}
		/*} else if (index == IDX_ADD_PACKAGE) { // add packages
			String[] res= choosePackage();
			if (res != null) {
				fFilterListField.addElements(Arrays.asList(res));
				for (int i= 0; i < res.length; i++) {
					fFilterListField.setChecked(res[i], true);
				}
			}

		*/} else if (index == IDX_EDIT) { // edit
			List<String> selected= fFilterListField.getSelectedElements();
			if (selected.isEmpty()) {
				return;
			}
			String editedEntry= selected.get(0);

			List<String> existing= fFilterListField.getElements();
			existing.remove(editedEntry);

			JavaMethodFilterInputDialog dialog= new JavaMethodFilterInputDialog(getShell(), existing);
			dialog.setInitialString(editedEntry);
			if (dialog.open() == Window.OK) {
				fFilterListField.replaceElement(editedEntry, (String) dialog.getResult());
			}
		}
	}

	private Shell getShell() {
		return fFilterListField.getTableViewer().getControl().getShell();
	}

}
