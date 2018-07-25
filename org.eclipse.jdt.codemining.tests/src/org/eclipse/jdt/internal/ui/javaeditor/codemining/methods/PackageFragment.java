package org.eclipse.jdt.internal.ui.javaeditor.codemining.methods;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IModularClassFile;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

public class PackageFragment implements IPackageFragment{

	private final String packageName;

	public PackageFragment(String packageName) {
		this.packageName = packageName;
	}
	@Override
	public IJavaElement[] getChildren() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IJavaElement getAncestor(int ancestorType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getCorrespondingResource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getElementType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHandleIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaModel getJavaModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaProject getJavaProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOpenable getOpenable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement getPrimaryElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getUnderlyingResource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStructureKnown() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String findRecommendedLineSeparator() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBuffer getBuffer() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasUnsavedChanges() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConsistent() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void makeConsistent(IProgressMonitor progress) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open(IProgressMonitor progress) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(IProgressMonitor progress, boolean force) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copy(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(boolean force, IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
			IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rename(String name, boolean replace, IProgressMonitor monitor) throws JavaModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsJavaResources() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ICompilationUnit createCompilationUnit(String name, String contents, boolean force, IProgressMonitor monitor)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassFile getClassFile(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOrdinaryClassFile getOrdinaryClassFile(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModularClassFile getModularClassFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassFile[] getAllClassFiles() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassFile[] getClassFiles() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOrdinaryClassFile[] getOrdinaryClassFiles() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICompilationUnit getCompilationUnit(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICompilationUnit[] getCompilationUnits() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICompilationUnit[] getCompilationUnits(WorkingCopyOwner owner) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementName() {
		return packageName;
	}

	@Override
	public int getKind() throws JavaModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getNonJavaResources() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSubpackages() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefaultPackage() {
		// TODO Auto-generated method stub
		return false;
	}

}
