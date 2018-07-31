package org.eclipse.jdt.experimental.ui.javaeditor.codemining.methods;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.core.search.matching.JavaSearchPattern;
import org.eclipse.jdt.internal.core.search.matching.MethodPattern;

public class MethodFilter {

	private MethodPattern pattern;
	private boolean isCaseSensitive;
	private int matchMode;
	private boolean mayBeGeneric;
	private boolean requireResolveBinding;

	public MethodFilter(String methodPattern) {
		if (methodPattern.startsWith("(")) {
			methodPattern = "*" + methodPattern;
		}
		this.pattern = (MethodPattern) SearchPattern.createPattern(methodPattern, IJavaSearchConstants.METHOD, 0,
				SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE);
		if (pattern != null) {
			int matchRule = pattern.getMatchRule();
			this.matchMode = matchRule & JavaSearchPattern.MATCH_MODE_MASK;
			requireResolveBinding = requireResolveBinding(pattern);
		}
		isCaseSensitive = true;
	}

	boolean isRequireResolveBinding() {
		return requireResolveBinding;
	}

	private static boolean requireResolveBinding(MethodPattern pattern) {
		if (pattern.declaringQualification != null) {
			return true;
		}
		if (pattern.parameterCount <= 0) {
			return false;
		}
		for (char[] c : pattern.parameterSimpleNames) {
			if ((c != null)) {
				return true;
			}
		}
		return false;
	}

	boolean isValid() {
		return pattern != null;
	}

	public boolean match(MethodInvocation node) {
		return match(node.getName().getIdentifier(), node.arguments());
	}

	public boolean match(ClassInstanceCreation node) {
		return match(node.getType().toString(), node.arguments());
	}

	private boolean match(String name, List<ASTNode> args) {
		int argsLength = args == null ? 0 : args.size();
		return match(name, argsLength);
	}

	private boolean match(String name, int argsLength) {

		// Verify method name
		if (!matchesName(this.pattern.selector, name.toCharArray()))
			return false; // IMPOSSIBLE_MATCH;

		// Verify parameters types
		boolean resolve = this.pattern.mustResolve;
		if (this.pattern.parameterSimpleNames != null) {
			int length = this.pattern.parameterSimpleNames.length;
			if (length != argsLength)
				return false; // IMPOSSIBLE_MATCH;
			/*
			 * for (int i = 0; i < argsLength; i++) { if (args != null &&
			 * !matchesTypeReference(this.pattern.parameterSimpleNames[i], args.get(i).res))
			 * { // Do not return as impossible when source level is at least 1.5 if
			 * (this.mayBeGeneric) { /*if (!this.pattern.mustResolve) { // Set resolution
			 * flag on node set in case of types was inferred in parameterized types from
			 * generic ones... // (see bugs
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=79990, 96761, 96763)
			 * nodeSet.mustResolve = true; resolve = true; }
			 */
			// this.methodDeclarationsWithInvalidParam.put(node, null);
			/*
			 * } else { return false; //IMPOSSIBLE_MATCH; } } }
			 */
		}

		// Verify type arguments (do not reject if pattern has no argument as it can be
		// an erasure match)
		/*
		 * if (this.pattern.hasMethodArguments()) { if (node.typeParameters == null ||
		 * node.typeParameters.length != this.pattern.methodArguments.length) return
		 * IMPOSSIBLE_MATCH; }
		 */

		// Method declaration may match pattern
		return true; // nodeSet.addMatch(node, resolve ? POSSIBLE_MATCH : ACCURATE_MATCH);
	}

	private boolean matchesTypeReference(char[] pattern, TypeReference type) {
		if (pattern == null)
			return true; // null is as if it was "*"
		if (type == null)
			return true; // treat as an inexact match

		char[][] compoundName = type.getTypeName();
		char[] simpleName = compoundName[compoundName.length - 1];
		int dimensions = type.dimensions() * 2;
		if (dimensions > 0) {
			int length = simpleName.length;
			char[] result = new char[length + dimensions];
			System.arraycopy(simpleName, 0, result, 0, length);
			for (int i = length, l = result.length; i < l;) {
				result[i++] = '[';
				result[i++] = ']';
			}
			simpleName = result;
		}

		return matchesName(pattern, simpleName);
	}

	/**
	 * Returns whether the given name matches the given pattern.
	 */
	protected boolean matchesName(char[] pattern, char[] name) {
		if (pattern == null)
			return true; // null is as if it was "*"
		if (name == null)
			return false; // cannot match null name
		return matchNameValue(pattern, name);
	}

	/**
	 * Return how the given name matches the given pattern.
	 * 
	 * @see "https://bugs.eclipse.org/bugs/show_bug.cgi?id=79866"
	 *
	 * @param pattern
	 * @param name
	 * @return Possible values are:
	 *         <ul>
	 *         <li>{@link #ACCURATE_MATCH}</li>
	 *         <li>{@link #IMPOSSIBLE_MATCH}</li>
	 *         <li>{@link #POSSIBLE_MATCH} which may be flavored with following
	 *         values:
	 *         <ul>
	 *         <li>{@link #EXACT_FLAVOR}: Given name is equals to pattern</li>
	 *         <li>{@link #PREFIX_FLAVOR}: Given name prefix equals to pattern</li>
	 *         <li>{@link #CAMELCASE_FLAVOR}: Given name matches pattern as Camel
	 *         Case</li>
	 *         <li>{@link #PATTERN_FLAVOR}: Given name matches pattern as Pattern
	 *         (i.e. using '*' and '?' characters)</li>
	 *         </ul>
	 *         </li>
	 *         </ul>
	 */
	protected boolean matchNameValue(char[] pattern, char[] name) {
		if (pattern == null)
			return true; // ACCURATE_MATCH; // null is as if it was "*"
		if (name == null)
			return false; // IMPOSSIBLE_MATCH; // cannot match null name
		if (name.length == 0) { // empty name
			if (pattern.length == 0) { // can only matches empty pattern
				return true; // ACCURATE_MATCH;
			}
			return false; // IMPOSSIBLE_MATCH;
		} else if (pattern.length == 0) {
			return false; // IMPOSSIBLE_MATCH; // need to have both name and pattern length==0 to be
							// accurate
		}
		boolean matchFirstChar = !this.isCaseSensitive || pattern[0] == name[0];
		boolean sameLength = pattern.length == name.length;
		boolean canBePrefix = name.length >= pattern.length;
		switch (this.matchMode) {
		case SearchPattern.R_EXACT_MATCH:
			if (sameLength && matchFirstChar && CharOperation.equals(pattern, name, this.isCaseSensitive)) {
				return true; // POSSIBLE_MATCH | EXACT_FLAVOR;
			}
			break;

		case SearchPattern.R_PREFIX_MATCH:
			if (canBePrefix && matchFirstChar && CharOperation.prefixEquals(pattern, name, this.isCaseSensitive)) {
				return true; // POSSIBLE_MATCH;
			}
			break;

		case SearchPattern.R_PATTERN_MATCH:
			// TODO_PERFS (frederic) Not sure this lowercase is necessary
			if (!this.isCaseSensitive) {
				pattern = CharOperation.toLowerCase(pattern);
			}
			if (CharOperation.match(pattern, name, this.isCaseSensitive)) {
				return true; // POSSIBLE_MATCH;
			}
			break;

		case SearchPattern.R_REGEXP_MATCH:
			if (Pattern.matches(new String(pattern), new String(name))) {
				return true; // POSSIBLE_MATCH;
			}
			break;

		case SearchPattern.R_CAMELCASE_MATCH:
			if (CharOperation.camelCaseMatch(pattern, name, false)) {
				return true; // POSSIBLE_MATCH;
			}
			// only test case insensitive as CamelCase same part count already verified
			// prefix case sensitive
			if (!this.isCaseSensitive && CharOperation.prefixEquals(pattern, name, false)) {
				return true; // POSSIBLE_MATCH;
			}
			break;

		case SearchPattern.R_CAMELCASE_SAME_PART_COUNT_MATCH:
			if (CharOperation.camelCaseMatch(pattern, name, true)) {
				return true; // POSSIBLE_MATCH;
			}
			break;
		}
		return false; // IMPOSSIBLE_MATCH;
	}

	public boolean match(IMethod method) {
		try {
			if (pattern.declaringQualification != null) {
				if (!matchesName(this.pattern.declaringQualification,
						method.getDeclaringType().getPackageFragment().getElementName().toCharArray()))
					return false;
			}

			if (pattern.declaringSimpleName != null) {
				if (!matchesName(this.pattern.declaringSimpleName,
						method.getDeclaringType().getTypeQualifiedName().toCharArray()))
					return false;
			}

			int argsLength = method.getParameterNames().length;
			if (!match(method.getElementName(), argsLength)) {
				return false;
			}
			if (this.pattern.parameterSimpleNames != null) {
				// Check parameters names
				String[] parameterNames = method.getParameterNames();
				for (String parameterName : parameterNames) {
					if (!matchParam(argsLength, parameterName)) {
						return false;
					}
				}
			}
			return true;
		} catch (JavaModelException e) {
			return false;
		}
	}

	private boolean matchParam(int argsLength, String parameterName) {
		for (int i = 0; i < argsLength; i++) {
			if (matchesName(this.pattern.parameterSimpleNames[i], parameterName.toCharArray())) {
				return true;
			}
		}
		return false;
	}

}
