package org.eclipse.jdt.internal.ui.javaeditor.codemining.methods;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

public class MockMethod implements IMethod {

	private final IType declaringType;

	private final String methodName;

	private final String[] parameterNames;

	public MockMethod(String packageName, String className, String methodName, String... parameterNames) {
		this.declaringType = new DeclaringType(packageName, className);
		this.methodName = methodName;
		this.parameterNames = parameterNames == null ? new String[0] : parameterNames;
	}

	@Override
	public String[] getCategories() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassFile getClassFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICompilationUnit getCompilationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getDeclaringType() {
		return declaringType;
	}

	@Override
	public int getFlags() throws JavaModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ISourceRange getJavadocRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOccurrenceCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ITypeRoot getTypeRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getType(String name, int occurrenceCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBinary() {
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
	public String getSource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISourceRange getSourceRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISourceRange getNameRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
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
	public IAnnotation getAnnotation(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAnnotation[] getAnnotations() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMemberValuePair getDefaultValue() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementName() {
		return methodName;
	}

	@Override
	public String[] getExceptionTypes() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getTypeParameterSignatures() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfParameters() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ILocalVariable[] getParameters() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterNames() throws JavaModelException {
		return parameterNames;
	}

	@Override
	public String[] getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getRawParameterNames() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReturnType() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSignature() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeParameter getTypeParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConstructor() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMainMethod() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLambdaMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSimilar(IMethod method) {
		// TODO Auto-generated method stub
		return false;
	}

}
