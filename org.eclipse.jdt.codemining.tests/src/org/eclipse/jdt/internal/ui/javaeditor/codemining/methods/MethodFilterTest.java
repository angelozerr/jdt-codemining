package org.eclipse.jdt.internal.ui.javaeditor.codemining.methods;

import org.eclipse.jdt.experimental.ui.javaeditor.codemining.methods.MethodFilter;
import org.junit.Assert;
import org.junit.Test;

public class MethodFilterTest {

	@Test
	public void testMath() {
		MethodFilter filter = new MethodFilter("java.lang.Math.*");
		MockMethod langMathAbsMethod = new MockMethod("java.lang", "Math", "abs", "abs");
		Assert.assertTrue(filter.match(langMathAbsMethod));
		MockMethod myMathAbsMethod = new MockMethod("my", "Math", "abs", "abs");
		Assert.assertFalse(filter.match(myMathAbsMethod));
		MockMethod langMyClassAbsMethod = new MockMethod("java.lang", "MyClass", "abs", "abs");
		Assert.assertFalse(filter.match(langMyClassAbsMethod));
		
	}

}
