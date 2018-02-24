package plugin_to_delete_when_m7;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JavaCodeMiningEditorPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "plugin_to_delete_when_m7"; //$NON-NLS-1$

	// The shared instance.
	private static JavaCodeMiningEditorPlugin plugin;

	/**
	 * The constructor.
	 */
	public JavaCodeMiningEditorPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JavaCodeMiningEditorPlugin getDefault() {
		return plugin;
	}

	public static void log(IStatus status) {
		JavaCodeMiningEditorPlugin plugin = getDefault();
		if (plugin != null) {
			plugin.getLog().log(status);
		} else {
			System.err.println(status.getPlugin() + ": " + status.getMessage()); //$NON-NLS-1$
		}
	}

	public static void log(Throwable e) {
		if (e instanceof CoreException) {
			log(new Status(IStatus.ERROR, PLUGIN_ID, ((CoreException) e).getStatus().getSeverity(), e.getMessage(),
					e.getCause()));
		} else {
			log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		}
	}
}