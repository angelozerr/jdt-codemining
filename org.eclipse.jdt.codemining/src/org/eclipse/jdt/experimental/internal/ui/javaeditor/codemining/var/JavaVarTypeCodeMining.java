package org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.var;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;

public class JavaVarTypeCodeMining extends LineContentCodeMining {

	private final SimpleType node;

	public JavaVarTypeCodeMining(SimpleType node, ITextViewer viewer, ICodeMiningProvider provider) {
		super(new Position(node.getStartPosition() + node.getLength(), 1), provider, null);
		this.node = node;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			ITypeBinding typeBinding = node.resolveBinding();
			String type = typeBinding.getName();
			super.setLabel(" " + type + " ");
		});
	}

}
