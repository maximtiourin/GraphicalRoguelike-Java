package com.fizzikgames.roguelike.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Offers string utility functions for manipulating or encrypting strings
 * @author Maxim Tiourin
 */
public class StringUtil {	
	/**
	 * Returns the string between starting position and rest of string
	 * @param s string to take substring from
	 * @param start position of this string exclusive
	 * @return substring
	 */
	public static String substring(String s, String start, boolean optimized) {
		String newString;
		if (optimized) {
			newString = new String(s.substring(s.indexOf(start) + start.length()));
		}
		else {
			newString = s.substring(s.indexOf(start) + start.length());
		}
		return newString;
	}
	
	/**
	 * Returns the string between two strings
	 * @param s string to take substring from
	 * @param start position of this string exclusive
	 * @param end position of this string exclusive
	 * @return substring
	 */
	public static String substring(String s, String start, String end, boolean optimized) {
		String newString;
		if (optimized) {
			newString = new String(s.substring(s.indexOf(start) + start.length(), s.indexOf(end)));
		}
		else {
			newString = s.substring(s.indexOf(start) + start.length(), s.indexOf(end));
		}
		return newString;
	}
	
	/**
	 * Returns the string between two strings
	 * @param s string to take substring from
	 * @param start position of this string inclusive
	 * @param end position of this string exclusive
	 * @return substring
	 */
	public static String substring(String s, int start, int end, boolean optimized) {
		String newString;
		if (optimized) {
			newString = new String(s.substring(start, end));
		}
		else {
			newString = s.substring(start, end);
		}
		return newString;
	}
	
	/**
	 * Returns a String array with index 0 is the String before the parameter String,
	 * and index 1 is the rest of the String after the parameter String. So if a String was 
	 * "All work and no play", and the parameter String was " ", then this function would return
	 * String[0] = "All", and String[1] = "work and no play".
	 * @param s string to take substring from
	 * @param pivot the string to trim from the two sides of the string
	 * @return substring
	 */
	public static String[] trimSubstring(String s, String pivot, boolean optimized) {
		String stra[] = new String[2];
		int pos = s.indexOf(pivot);
		
		if (pos == -1) return null;
		
		stra[0] = substring(s, 0, pos, optimized);
		stra[1] = substring(s, pos + pivot.length(), s.length(), optimized);
		return stra;
	}
	
	/**
	 * Finds and returns the position of the first occurence of 
	 * the one character string before the given position inclusively.
	 * @param src the source string
	 * @param c the character to search for
	 * @param pos the position to backtrack from
	 * @return int
	 */
	public static int firstOccurenceBeforePos(String src, String c, int pos) {
		int i = pos;
		
		while ((i < src.length()) && (i > 0) && (src.charAt(i) != c.charAt(0))) {
			i--;
		}
		
		return i;
	}
	
	/**
	 * Returns the md5 hash of a source string's bytes using UTF-8 character set
	 * @param src the source string
	 * @return the md5 has of the source string
	 */
	public static String md5(String src) {
		try {
			MessageDigest crypto = MessageDigest.getInstance("MD5");
			byte[] bytes = src.getBytes("UTF-8");
			byte[] digest = crypto.digest(bytes);
			BigInteger bigint = new BigInteger(1, digest);
			String result = bigint.toString(16);
			crypto.reset();
			
			return result;
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * Returns whether or not the given string has numeric characters only.
	 * Numeric characters are 0-9. If Negative is allowed, then will allow
	 * the '-' character only if it is at the beginning of the string and the
	 * size of the string is greater than 1. Returns false if the string is empty.
	 * @param src the string to check
	 * @param allowNegative whether or not to check and allow negative numbers
	 * @return boolean whether or not the string is numeric
	 */
	public static boolean isNumeric(String src, boolean allowNegative) {
		if (src.length() <= 0) return false;
		
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			
			if ((c < '0') || (c > '9')) {
				if (!((allowNegative) && (c == '-') && (i == 0) && (src.length() > 1))) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Tokenizes the source string around the split character, while ignoring
	 * any split occurences between an opening and closing ignore character.
	 * <br> Example: Split = ' ', Ignore = '"', Source = 'Hello there "mary had a lambovitch" how are you?'
	 * <br> Example Returns:
	 * <br> [0] = 'Hello'
	 * <br> [1] = 'there'
	 * <br> [2] = 'mary had a lambovitch'
	 * <br> [3] = 'how'
	 * <br> [4] = 'are'
	 * <br> [5] = 'you?'
	 * @param source the source string
	 * @param split the character to tokenize around
	 * @param ignore the character to search for opening and closing matches of
	 * @return String[] string array containing the tokens from the source string.
	 */
	public static String[] tokenize(String source, char split, char ignore) {
		ArrayList<String> tokens = new ArrayList<String>();
		
		int cursor = 0;
		int iterator = 0;
		boolean ignoreOpen = false;
		while (iterator < source.length()) {
			if (source.charAt(iterator) == split) {
				if (!ignoreOpen) {
					//Perform split
					tokens.add(substring(source, cursor, iterator, true)); //Add the token between cursor inclusive, and iterator exclusive
					cursor = ++iterator; //Assign iterator and cursor to just after the split character
				}
			}
			if (source.charAt(iterator) == ignore) {
				if (!ignoreOpen) {
					ignoreOpen = true;
				}
				else {
					ignoreOpen = false;
				}
			}
			
			iterator++;
		}
		
		tokens.add(substring(source, cursor, iterator, true)); //Add the final cursor to iterator subsection
		
		//Strip all ignore characters from the final tokens
		for (int i = 0; i < tokens.size(); i++) {
			tokens.set(i, tokens.get(i).replaceAll(Character.toString(ignore), ""));
		}
		
		//Strip all empty tokens from the final tokens
		Iterator<String> i = tokens.iterator();
		while (i.hasNext()) {
			String next = i.next();
			if (next.length() <= 0) tokens.remove(next);
		}
		
		return tokens.toArray(new String[tokens.size()]);
	}
	
	/**
	 * Returns a String that has commas inserted to make a proper US number string.
	 * Example: 1000000000 becomes 1,000,000,000
	 */
	public static String addUSNumberCommas(String string) {
		final int seperator = 3;
		int length = string.length();
		int remain = length;
		
		String str = string;
		int line = str.length() - seperator;
		while (remain > 3) {
			String lhs = StringUtil.substring(str, 0, line, true);
			String middle = ",";
			String rhs = StringUtil.substring(str, line, str.length(), true);
			str = lhs + middle + rhs;
			
			remain -= seperator;
			line -= seperator;
		}
		
		return str;
	}
	
	public static String convertToRomanNumerals(int num) {
		final int[] numbers = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
		final String[] letters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
		
		String out = "";
		int n = num;
		for (int i = 0; i < numbers.length; i++) {
			while (n >= numbers[i]) {
				out += letters[i];
				n -= numbers[i];
			}
		}
		return out;
	}
}
