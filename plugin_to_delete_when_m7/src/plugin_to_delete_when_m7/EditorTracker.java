package plugin_to_delete_when_m7;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorTracker implements IWindowListener, IPageListener, IPartListener {

	private static EditorTracker INSTANCE;

	private Map<CompilationUnitEditor, JavaCodeMiningReconciler> reconcilers = new HashMap<>();

	private EditorTracker() {
		init();
	}

	public static EditorTracker getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EditorTracker();
		}
		return INSTANCE;
	}

	private void init() {
		if (PlatformUI.isWorkbenchRunning()) {
			IWorkbench workbench = JavaCodeMiningEditorPlugin.getDefault().getWorkbench();
			if (workbench != null) {
				IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
				for (IWorkbenchWindow window : windows) {
					windowOpened(window);
				}
				JavaCodeMiningEditorPlugin.getDefault().getWorkbench().addWindowListener(this);
			}
		}
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		IWorkbenchPage[] pages = window.getPages();
		for (IWorkbenchPage page : pages) {
			pageClosed(page);
		}
		window.removePageListener(this);
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		if (window.getShell() != null) {
			IWorkbenchPage[] pages = window.getPages();
			for (IWorkbenchPage page : pages) {
				pageOpened(page);
			}
			window.addPageListener(this);
		}
	}

	@Override
	public void pageActivated(IWorkbenchPage page) {
	}

	@Override
	public void pageClosed(IWorkbenchPage page) {
		IEditorReference[] rs = page.getEditorReferences();
		for (IEditorReference r : rs) {
			IEditorPart part = r.getEditor(false);
			if (part != null) {
				editorClosed(part);
			}
		}
		page.removePartListener(this);
	}

	@Override
	public void pageOpened(IWorkbenchPage page) {
		IEditorReference[] rs = page.getEditorReferences();
		for (IEditorReference r : rs) {
			IEditorPart part = r.getEditor(false);
			if (part != null) {
				editorOpened(part);
			}
		}
		page.addPartListener(this);
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextViewer textViewer = (ITextViewer) part.getAdapter(ITextOperationTarget.class);
			if (textViewer != null) {
				JavaCodeMiningReconciler reconciler = reconcilers.get(part);
				// if (reconciler != null) {
				// reconciler.refresh();
				// }
			}
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			editorClosed((IEditorPart) part);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			editorOpened((IEditorPart) part);
		}
	}

	private void editorOpened(IEditorPart part) {
		if (part instanceof CompilationUnitEditor) {
			CompilationUnitEditor javaEditor = (CompilationUnitEditor) part;
			JavaCodeMiningReconciler reconciler = reconcilers.get(part);
			if (reconciler == null) {
				ISourceViewer sourceViewer = (ISourceViewer) part.getAdapter(ITextOperationTarget.class);
				reconciler = new JavaCodeMiningReconciler();
				reconciler.install(javaEditor, sourceViewer);
				reconcilers.put(javaEditor, reconciler);
			}
		}
	}

	private void editorClosed(IEditorPart part) {
		if (part instanceof CompilationUnitEditor) {
			CompilationUnitEditor javaEditor = (CompilationUnitEditor) part;
			JavaCodeMiningReconciler reconciler = reconcilers.remove(part);
			if (reconciler != null) {
				reconciler.uninstall();
				Assert.isTrue(null == reconcilers.get(part),
						"An old ICodeLensController is not un-installed on Text Editor instance");
			}
		}
	}

}