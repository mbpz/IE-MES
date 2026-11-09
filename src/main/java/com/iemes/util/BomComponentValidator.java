package com.iemes.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Validates quantities stored as text in the legacy BOM relation table. */
public final class BomComponentValidator {
	private static final BigDecimal MAX = new BigDecimal("999999999999999.999");

	private BomComponentValidator() {
	}

	public static void validate(String[] items, String[] quantities, String[] upper, String[] lower) {
		if (items == null) {
			if (quantities != null || upper != null || lower != null) throw new IllegalArgumentException("BOM子项参数不完整");
			return;
		}
		if (quantities == null || upper == null || lower == null
				|| items.length != quantities.length || items.length != upper.length || items.length != lower.length) {
			throw new IllegalArgumentException("BOM子项参数不完整");
		}
		for (int index = 0; index < items.length; index++) {
			validateRow(items[index], quantities[index], upper[index], lower[index]);
		}
	}

	public static void validateRow(String item, String quantity, String upper, String lower) {
		if (item == null || item.trim().isEmpty()) throw new IllegalArgumentException("BOM物料编号不能为空");
		BigDecimal used = number(quantity);
		BigDecimal ceiling = number(upper);
		BigDecimal floor = number(lower);
		if (used.signum() <= 0) throw new IllegalArgumentException("BOM组件用量必须大于零");
		if (ceiling.signum() < 0 || floor.signum() < 0 || ceiling.compareTo(floor) < 0) {
			throw new IllegalArgumentException("BOM结存阈值必须非负且上限不小于下限");
		}
	}

	private static BigDecimal number(String text) {
		try {
			BigDecimal value = new BigDecimal(text == null ? "" : text.trim())
					.setScale(3, RoundingMode.UNNECESSARY);
			if (value.abs().compareTo(MAX) > 0) throw new IllegalArgumentException("BOM数值过大");
			return value;
		} catch (NumberFormatException invalid) {
			throw new IllegalArgumentException("BOM数量和结存阈值必须是数字");
		} catch (ArithmeticException tooPrecise) {
			throw new IllegalArgumentException("BOM数量和结存阈值最多保留三位小数");
		}
	}
}
