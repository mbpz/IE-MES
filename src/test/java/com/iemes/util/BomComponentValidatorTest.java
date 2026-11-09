package com.iemes.util;

import junit.framework.TestCase;

public class BomComponentValidatorTest extends TestCase {
	public void testFractionalThresholdsAreAccepted() {
		BomComponentValidator.validateRow("ITEM-1", "1.25", "10.500", "2.125");
		BomComponentValidator.validate(null, null, null, null);
	}

	public void testInvalidTextAndReversedThresholdsAreRejected() {
		expectInvalid("ITEM-1", "1", "not-a-number", "2");
		expectInvalid("ITEM-1", "0", "10", "2");
		expectInvalid("ITEM-1", "1", "2", "3");
		expectInvalid("ITEM-1", "1", "2.1234", "1");
	}

	public void testIncompleteComponentRowIsRejected() {
		try {
			BomComponentValidator.validate(new String[] { "ITEM-1" }, new String[] { "1" }, null,
					new String[] { "1" });
			fail("incomplete BOM component must fail");
		} catch (IllegalArgumentException expected) {
			assertNotNull(expected.getMessage());
		}
	}

	private void expectInvalid(String item, String quantity, String upper, String lower) {
		try {
			BomComponentValidator.validateRow(item, quantity, upper, lower);
			fail("invalid BOM component must fail");
		} catch (IllegalArgumentException expected) {
			assertNotNull(expected.getMessage());
		}
	}
}
