package com.sssprog.shoppingliststandalone.utils;

import android.text.TextUtils;

public class CurrencyHelper {

    public static class Currency {
        public String symbol;
        public CurrencyPosition position;

        public Currency(String symbol, CurrencyPosition position) {
            this.symbol = symbol;
            this.position = position;
        }

        public Currency copy() {
            return new Currency(symbol, position);
        }
    }

    public static Currency parseCurrency(String currency) {
        if (TextUtils.isEmpty(currency)) {
            return new Currency("", CurrencyPosition.RIGHT);
        } else {
            CurrencyPosition position = currency.charAt(0) == '0' ? CurrencyPosition.LEFT : CurrencyPosition.RIGHT;
            return new Currency(currency.substring(1), position);
        }
    }

    public static String toString(Currency currency) {
        String result = currency.position == CurrencyPosition.LEFT ? "0" : "1";
        result += currency.symbol;
        return result;
    }

    public enum CurrencyPosition {
        LEFT, RIGHT;
    }

}
