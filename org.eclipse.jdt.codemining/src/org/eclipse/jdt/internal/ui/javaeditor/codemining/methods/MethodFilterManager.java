package org.eclipse.jdt.internal.ui.javaeditor.codemining.methods;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodFilterManager {

	private final static MethodFilterManager INSTANCE = new MethodFilterManager();

	public static MethodFilterManager getInstance() {
		return INSTANCE;
	}

	private boolean initialized;

	private final List<MethodFilter> filters;
	private final List<MethodFilter> filtersWithBinding;

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

	private synchronized void initialize() {
		if (initialized) {
			return;
		}
		try (InputStream resource = MethodFilterManager.class.getResourceAsStream("DefaultMethodFilters.txt")) {
			new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
					.filter(line -> !line.trim().isEmpty()).collect(Collectors.toList())
					.forEach(pattern -> addFilter(pattern));
		} catch (Exception e) {
			e.printStackTrace();
		}
		initialized = true;
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

}
