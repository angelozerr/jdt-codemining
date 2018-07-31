package org.eclipse.jdt.experimental.internal.ui.javaeditor.codemining.methods;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.experimental.internal.ui.preferences.MyPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class MethodFilterManager {

	private final static MethodFilterManager INSTANCE = new MethodFilterManager();

	public static MethodFilterManager getInstance() {
		return INSTANCE;
	}

	private boolean initialized;

	private final List<MethodFilter> filters;
	private final List<MethodFilter> filtersWithBinding;

	private IPreferenceStore fPreferenceStore;

	private MethodFilterManager() {
		this.filters = new ArrayList<>();
		this.filtersWithBinding = new ArrayList<>();
	}

	private void initializeIfNeeded() {
		if (initialized) {
			return;
		}
		initialize();
	}

	public static String getDefaultMethodFilters() {
		try (InputStream resource = MethodFilterManager.class.getResourceAsStream("DefaultMethodFilters.txt")) {
			return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
					.filter(line -> !line.trim().isEmpty()).collect(Collectors.joining(";"));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private synchronized void initialize() {
		if (initialized) {
			return;
		}
		try {
			fPreferenceStore = MyPreferenceConstants.getPreferenceStore();
			loadFromPreference();
			fPreferenceStore.addPropertyChangeListener(e -> {
				if (MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_FILTERS_ENABLED
						.equals(e.getProperty())
						|| MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_FILTERS_DISABLED
								.equals(e.getProperty())) {
					loadFromPreference();
				}
			});
		} catch (Exception e) {
			try (InputStream resource = MethodFilterManager.class.getResourceAsStream("DefaultMethodFilters.txt")) {
				new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
						.filter(line -> !line.trim().isEmpty()).collect(Collectors.toList())
						.forEach(pattern -> addFilter(pattern));
			} catch (Exception ex) {
				e.printStackTrace();
			}
		}
		initialized = true;
	}

	private void loadFromPreference() {
		String enabled = fPreferenceStore
				.getString(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_METHOD_PARAMETER_FILTERS_ENABLED);
		

		List<String> res = new ArrayList<>();
		String[] enabledEntries = MethodFilterManager.unpackOrderList(enabled);
		for (int i = 0; i < enabledEntries.length; i++) {
			res.add(enabledEntries[i]);
		}
		
		filters.clear();
		filtersWithBinding.clear();
		res.forEach(this::addFilter);
	}

	private void addFilter(String methodPattern) {
		try {
			MethodFilter filter = new MethodFilter(methodPattern);
			if (filter.isValid()) {
				if (filter.isRequireResolveBinding()) {
					filtersWithBinding.add(filter);
				} else {
					filters.add(filter);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean match(MethodInvocation node) {
		initializeIfNeeded();
		for (MethodFilter methodFilter : filters) {
			if (methodFilter.match(node)) {
				return true;
			}
		}
		return false;
	}

	public boolean match(ClassInstanceCreation node) {
		initializeIfNeeded();
		for (MethodFilter methodFilter : filters) {
			if (methodFilter.match(node)) {
				return true;
			}
		}
		return false;
	}

	public boolean match(IMethod method) {
		initializeIfNeeded();
		for (MethodFilter methodFilter : filtersWithBinding) {
			if (methodFilter.match(method)) {
				return true;
			}
		}
		return false;
	}

	public static String[] unpackOrderList(String str) {
		StringTokenizer tok = new StringTokenizer(str, ";"); //$NON-NLS-1$
		int nTokens = tok.countTokens();
		String[] res = new String[nTokens];
		for (int i = 0; i < nTokens; i++) {
			res[i] = tok.nextToken();
		}
		return res;
	}

	public static String packOrderList(List<String> orderList) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < orderList.size(); i++) {
			buf.append(orderList.get(i));
			buf.append(';');
		}
		return buf.toString();
	}

}
