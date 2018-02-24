package plugin_to_delete_when_m7;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class JavaCodeMiningEditorStartup implements IStartup {

	@Override
	public void earlyStartup() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				// Editor tracker
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					EditorTracker.getInstance();
				}
			}
		});
	}
}