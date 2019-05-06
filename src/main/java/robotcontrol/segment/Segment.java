package robotcontrol.segment;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Segment extends HT16K33 {

	public static final HashMap<Character, Byte> DIGIT_VALUES = new HashMap<>();
	public static final HashMap<Character, Byte> IDIGIT_VALUES = new HashMap<>();

	static {
		initValue(' ', 0x00, 0x00);
		initValue('-', 0x40, 0x40);
		initValue('0', 0x3F, 0x3F);
		initValue('1', 0x06, 0x30);
		initValue('2', 0x5B, 0x5B);
		initValue('3', 0x4F, 0x79);
		initValue('4', 0x66, 0x74);
		initValue('5', 0x6D, 0x6D);
		initValue('6', 0x7D, 0x6F);
		initValue('7', 0x07, 0x38);
		initValue('8', 0x7F, 0x7F);
		initValue('9', 0x6F, 0x7D);
		initValue('A', 0x77, 0x7E);
		initValue('B', 0x7C, 0x67);
		initValue('C', 0x39, 0x0F);
		initValue('D', 0x5E, 0x73);
		initValue('E', 0x79, 0x4F);
		initValue('F', 0x71, 0x4E);
	}

	private static void initValue(char key, int digitValue, int indigitValue) {
		DIGIT_VALUES.put(key, (byte) digitValue);
		IDIGIT_VALUES.put(key, (byte) indigitValue);
	}

	private boolean invert;
	private static final int MAX_POS = 4;
	private static final int GLOBAL_OFFSET = 1;

	public Segment() {
		this(HT16K33.DEFAULT_ADDRESS, false);
	}

	public Segment(boolean invert) {
		this(HT16K33.DEFAULT_ADDRESS, invert);
	}

	public Segment(int address) {
		this(address, false);
	}

	/**
	 * Initialize display. All arguments will be passed to the HT16K33 class
	 * initializer, including optional I2C address and bus number parameters.
	 * 
	 * @param invert
	 * @param objects
	 */
	public Segment(int address, boolean invert) {
		super(address, null);
		this.invert = invert;
	}

	public boolean isInvert() {
		return invert;
	}

	/**
	 * Inverts the display of the 7-segment. This method has no effect, if the
	 * {@value invert} is set to the same value as it was before
	 * 
	 * @param invert
	 */
	public void setInvert(boolean invert) {
		if (invert == this.invert) {
			return;
		}
		Character key;
		StringBuilder builder = new StringBuilder();
		HashMap<Character, Byte> keyMap = this.invert ? IDIGIT_VALUES : DIGIT_VALUES;
		for (int i = 0; i < buffer.length; i++) {
			key = getKeyOfValue(keyMap, buffer[i]);
			if (key != null && key != ' ') {
				builder.append(key);
			}
		}
		this.invert = invert;
		printNumber(builder.reverse().toString());
		writeDisplay();
	}

	/**
	 * @param map
	 * @param value
	 * @return the char-key for the given byte-value of the given map if it exists
	 *         otherwise null
	 */
	private Character getKeyOfValue(HashMap<Character, Byte> map, byte value) {
		try {
			return map.keySet().stream().filter(key -> value == map.get(key)).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * pos is the pos on the 7-segment from 0 to 3
	 * 
	 * @param pos
	 * @param bitmask
	 */
	public void setDigitRaw(int pos, int bitmask) {
		buffer[calcPos(pos)] = (byte) (bitmask & 0xFF);
		writeDisplay();
	}

	/**
	 * set dec point from 0 to 3
	 * 
	 * @param pos
	 * @param decimal
	 */
	public void setDecimal(int pos, boolean decimal) {
		if (decimal) {
			buffer[calcPos(pos)] |= (1 << 7);
		} else {
			buffer[calcPos(pos)] &= ~(1 << 7);
		}
		writeDisplay();
	}

	public void setDigit(int pos, char digit) {
		setDigit(pos, digit, false);
	}

	/**
	 * Set digit at position to provided value. Position should be a value of 0 to 3
	 * with 0 being the left most digit on the display. Digit should be a number
	 * 0-9, character A-F, space (all LEDs off), or dash (-).
	 * 
	 * @param pos
	 * @param digit
	 * @param decimal
	 */
	public void setDigit(int pos, char digit, boolean decimal) {
		if (invert) {
			setDigitRaw(pos, IDIGIT_VALUES.get(charToUpperCase(digit)));
		} else {
			setDigitRaw(pos, DIGIT_VALUES.get(charToUpperCase(digit)));
		}

		if (decimal) {
			setDecimal(pos, true);
		}
	}

	/**
	 * Turn the colon on with show colon True, or off with show colon False.
	 * 
	 * @param showColon
	 */
	public void setColon(boolean showColon) {
		if (showColon) {
			buffer[MAX_POS + GLOBAL_OFFSET] |= 0x02;
		} else {
			buffer[MAX_POS + GLOBAL_OFFSET] &= (~0x02) & 0xFF;
		}
		writeDisplay();
	}

	/**
	 * Turn the left colon on with show color True, or off with show colon False.
	 * Only the large 1.2" 7-segment display has a left colon.
	 * 
	 * @param showColon
	 */
	public void setLeftColon(boolean showColon) {
		if (showColon) {
			buffer[MAX_POS + GLOBAL_OFFSET] |= 0x04;
			buffer[MAX_POS + GLOBAL_OFFSET] |= 0x08;
		} else {
			buffer[MAX_POS + GLOBAL_OFFSET] &= (~0x04) & 0xFF;
			buffer[MAX_POS + GLOBAL_OFFSET] &= (~0x08) & 0xFF;
		}
		writeDisplay();
	}

	/**
	 * Turn on/off the single fixed decimal point on the large 1.2" 7-segment
	 * display. Set show_decimal to True to turn on and False to turn off. Only the
	 * large 1.2" 7-segment display has this decimal point (in the upper right in
	 * the normal orientation of the display).
	 * 
	 * @param showDecimal
	 */
	public void setFixedDecimal(boolean showDecimal) {
		if (showDecimal) {
			buffer[MAX_POS + GLOBAL_OFFSET] |= 0x10;
		} else {
			buffer[MAX_POS + GLOBAL_OFFSET] &= (~0x10) & 0xFF;
		}
		writeDisplay();
	}

	public void printNumber(String value) {
		printNumber(value, true);
	}

	/**
	 * Print a 4 character long string of numeric values to the display. Characters
	 * in the string should be any supported character by set_digit, or a decimal
	 * point. Decimal point characters will be associated with the previous
	 * character.
	 * 
	 * @param value
	 * @param justifyRight
	 */
	public void printNumber(String value, boolean justifyRight) {
		int length = value.replace(".", "").length();
		if (length > 4) {
			printNumber("----");
			return;
		}
		int pos = justifyRight ? MAX_POS - length : 0;
		for (int ch = 0; ch < value.length(); ch++) {
			if (value.charAt(ch) == '.') {
				setDecimal(pos - 1, true);
			} else {
				setDigit(pos, value.charAt(ch));
				pos += 1;
			}
		}
	}

	public void printFloat(String value) {
		printFloat(value, 0, true);
	}

	public void printFloat(String value, int decimalDigits) {
		printFloat(value, decimalDigits, true);
	}

	public void printFloat(String value, boolean justifyRight) {
		printFloat(value, 0, justifyRight);
	}

	/**
	 * Print a numeric value to the display. If value is negative it will be printed
	 * with a leading minus sign. Decimal digits is the desired number of digits
	 * after the decimal point.
	 * 
	 * @param value
	 * @param decimalDigits
	 * @param justifyRight
	 */
	public void printFloat(String value, int decimalDigits, boolean justifyRight) {
		String formatString = MessageFormat.format("{{0:0.{0}F}}", decimalDigits);
		printNumber(MessageFormat.format(formatString, value), justifyRight);
	}

	public void printHex(int value) {
		printHex(value, true);
	}

	/**
	 * Print a numeric value in hexadecimal. Value should be from 0 to FFFF.
	 * 
	 * @param value
	 * @param justifyRight
	 */
	public void printHex(int value, boolean justifyRight) {
		if (value < 0 || value > 0xFFFF) {
			return;
		}
		printNumber(MessageFormat.format("{0:X}", value), justifyRight);
	}

	/**
	 * corrects the position of the given position in order to match the segment
	 * 
	 * @param pos
	 */
	private int calcPos(int pos) {
		if (pos < 0 || pos > 3) {
			throw new IndexOutOfBoundsException();
		}
		int offset = pos < 2 ? 0 : 1;
		if (invert) {
			pos = MAX_POS - (pos + offset);
		} else {
			pos += offset;
		}
		return pos * 2 + GLOBAL_OFFSET;
	}

	/**
	 * returns the given char in upper case
	 * 
	 * @param c
	 * @return
	 */
	private char charToUpperCase(char c) {
		return String.valueOf(c).toUpperCase().charAt(0);
	}
}
