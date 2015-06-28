package com.sssprog.shoppingliststandalone.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

public class NumberUtils {

    public static final MathContext QUANTITY_CONTEXT = new MathContext(3, RoundingMode.HALF_EVEN);
    public static final MathContext PRICE_CONTEXT = new MathContext(2, RoundingMode.HALF_EVEN);

    private static final int QUANTITY_SCALE = 3;
    private static final int PRICE_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static BigDecimal roundQuantity(BigDecimal quantity) {
        return quantity.scale() == QUANTITY_SCALE ? quantity : quantity.setScale(QUANTITY_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal roundPrice(BigDecimal price) {
        return price.scale() == PRICE_SCALE ? price : price.setScale(PRICE_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal stringToQuantity(String quantity) {
        quantity = quantity.replace(',', '.');
        try {
            return roundQuantity(new BigDecimal(quantity));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal stringToPrice(String price) {
        price = price.replace(',', '.');
        try {
            return roundPrice(new BigDecimal(price));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static String priceToString(BigDecimal price) {
        return String.format(Locale.getDefault(), "%.2f", price);
    }

    public static String quantityToString(BigDecimal quantity) {
        return new DecimalFormat("#.###").format(quantity);
    }

    public static String priceWithCurrency(BigDecimal price) {
        return priceToString(price);
    }

    public static boolean numberGreater(BigDecimal number, int value) {
        return number.compareTo(new BigDecimal(value)) > 0;
    }

    public static boolean numberGreaterOrEquals(BigDecimal number, int value) {
        return number.compareTo(new BigDecimal(value)) >= 0;
    }

    public static boolean numberLess(BigDecimal number, int value) {
        return number.compareTo(new BigDecimal(value)) < 0;
    }

    public static boolean numberLessOrEquals(BigDecimal number, int value) {
        return number.compareTo(new BigDecimal(value)) <= 0;
    }

    public static boolean numberEquals(BigDecimal number, int value) {
        return number.compareTo(new BigDecimal(value)) == 0;
    }

}
