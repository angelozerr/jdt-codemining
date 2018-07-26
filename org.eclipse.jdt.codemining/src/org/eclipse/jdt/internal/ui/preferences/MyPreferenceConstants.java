package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class MyPreferenceConstants {

	/**
	 * A named preference that stores the value for "Show references" codemining.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REFERENCES = "java.codemining.references"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show references" only if there
	 * is at least one reference.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE = "java.codemining.references.atLeastOne"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show implementations"
	 * codemining.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS = "java.codemining.implementations"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show implementations" only if
	 * there is at least one implementation.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE = "java.codemining.implementations.atLeastOne"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show method parameter names"
	 * codemining.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_NAMES = "java.codemining.methodParameter.names"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show method parameter types".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_TYPES = "java.codemining.methodParameter.types"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show method parameter only for literal"
	 * codemining.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_ONLY_FOR_LITERAL = "java.codemining.methodParameter.onlyForLiteral"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show method parameter by using filters"
	 * codemining.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_BY_USING_FILTERS = "java.codemining.methodParameter.byUsingFilters"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show end statement".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT = "java.codemining.endStatement"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show end statement min line
	 * number".
	 * <p>
	 * Value is of type <code>int</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT_MIN_LINE_NUMBER = "java.codemining.endStatement.minLineNumber"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show run for main method"
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_MAIN_RUN = "java.codemining.main.run"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show debug for main method"
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_MAIN_DEBUG = "java.codemining.main.debug"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show type of Java9 'var' declaration"
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_JAVA10_VAR_TYPE = "java.codemining.java10.var.type"; //$NON-NLS-1$
		
	/**
	 * A named preference that stores the value for "Show JUnit status".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_JUNIT_STATUS = "java.codemining.junit.status"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show JUnit run".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_JUNIT_RUN = "java.codemining.junit.run"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show JUnit debug".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_JUNIT_DEBUG = "java.codemining.junit.debug"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show variable values inline
	 * while debugging".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING = "java.codemining.debug.variable"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show revision recent change".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE = "java.codemining.sccm.revison.recent.change"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show revision recent change
	 * with avatar".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_AVATAR = "java.codemining.sccm.revison.recent.change.withAvatar"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show revision recent change
	 * with date".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_DATE = "java.codemining.sccm.revison.recent.change.withDate"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show authors".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REVISION_AUTHORS = "java.codemining.sccm.revision.authors"; //$NON-NLS-1$

	private static boolean initialized;

	/**
	 * Initializes the given preference store with the default values.
	 *
	 * @param store the preference store to be initialized
	 *
	 */
	public static void initializeDefaultValues(IPreferenceStore store) {
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE,
				JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_NAMES, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_TYPES, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_ONLY_FOR_LITERAL, JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_BY_USING_FILTERS, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_END_STATEMENT_MIN_LINE_NUMBER, 4);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_RUN, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_MAIN_DEBUG, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JAVA10_VAR_TYPE, JavaCore.DISABLED);		
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_STATUS, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_RUN, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_JUNIT_DEBUG, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_VARIABLE_VALUE_WHILE_DEBUGGING,
				JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE, JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_AVATAR,
				JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_DATE,
				JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_AUTHORS, JavaCore.DISABLED);
	}

	/**
	 * Returns the JDT-UI preference store.
	 *
	 * @return the JDT-UI preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		if (!initialized) {
			MyPreferenceConstants.initializeDefaultValues(store);
			initialized = true;
		}
		return store;
	}
}
