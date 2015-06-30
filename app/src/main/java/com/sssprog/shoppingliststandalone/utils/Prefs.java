package com.sssprog.shoppingliststandalone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.sssprog.shoppingliststandalone.R;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Prefs {
	
    private static Context context;
	private static SharedPreferences settings = null;
	private static SharedPreferences.Editor editor = null;

	public static void init(Context context) {
        Prefs.context = context;
		settings = PreferenceManager.getDefaultSharedPreferences(context);
		editor = settings.edit();
		setDefaults();
	}

	private static void setDefaults() {
		if (!contains(R.string.prefs_currency)) {
			String symbol = DecimalFormatSymbols.getInstance(Locale.getDefault()).getCurrencySymbol();
			putString(R.string.prefs_currency, CurrencyHelper.toString(
					new CurrencyHelper.Currency(symbol, CurrencyHelper.CurrencyPosition.RIGHT)));
		}
		if (!contains(R.string.prefs_list_row_size)) {
			putString(R.string.prefs_list_row_size, "0");
		}
	}

	public static void putString(int key, String value) {
		editor.putString(getKey(key), value).apply();
	}

	public static String getString(int key) {
		return settings.getString(getKey(key), null);
	}
	
	public static void putInt(int key, Integer value) {
		editor.putInt(getKey(key), value).apply();
	}

	public static int getInt(int key) {
		return settings.getInt(getKey(key), 0);
	}
	
	public static void putBoolean(int key, Boolean value) {
		editor.putBoolean(getKey(key), value).apply();
	}

	public static boolean getBoolean(int key) {
		return settings.getBoolean(getKey(key), false);
	}
	
	public static void putFloat(int key, Float value) {
		editor.putFloat(getKey(key), value).apply();
	}

	public static float getFloat(int key) {
		return settings.getFloat(getKey(key), 0);
	}
	
	public static void putLong(int key, long value) {
		editor.putLong(getKey(key), value).apply();
	}

	public static long getLong(int key) {
		return settings.getLong(getKey(key), 0);
	}
	
	public static boolean contains(int key) {
		return settings.contains(getKey(key));
	}
	
	public static void remove(int key) {
		editor.remove(getKey(key)).apply();
	}

    public static void clear() {
        editor.clear().apply();
    }

    public static String getKey(int key) {
        return context.getString(key);
    }

    public static SharedPreferences getSharedPreferences() {
        return settings;
    }

	/**
	 * @return tax percent, or 0 if tax is not specified
	 */
	public static BigDecimal getTaxPercent() {
		String str = getString(R.string.prefs_tax_percent);
		if (TextUtils.isEmpty(str)) {
			return BigDecimal.ZERO;
		}
		try {
			BigDecimal tax = new BigDecimal(str);
			if (NumberUtils.numberLess(tax, 0)) {
				tax = BigDecimal.ZERO;
			}
			return tax;
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	private static int getListOptionValue(int key, int maxValue) {
		int result;
		try {
			result = Integer.parseInt(getString(key));
		} catch (NumberFormatException e) {
			result = 0;
		}
		if (result < 0 || result > maxValue) {
			result = 0;
		}
		return result;
	}

	public static boolean isListHeightAlwaysLarge() {
		return getListOptionValue(R.string.prefs_list_row_size, 1) == 1;
	}

}
