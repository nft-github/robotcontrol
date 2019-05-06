package robotcontrol.segment;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class HT16K33 {

	public static final int DEFAULT_ADDRESS = 0x70;
	private static final int HT16K33_BLINK_CMD = 0x80;
	private static final int HT16K33_BLINK_DISPLAYON = 0x01;

	// refresh rates of display
	public static final int HT16K33_BLINK_OFF = 0x00;
	public static final int HT16K33_BLINK_2HZ = 0x02;
	public static final int HT16K33_BLINK_1HZ = 0x04;
	public static final int HT16K33_BLINK_HALFHZ = 0x06;

	// brightness of display
	private static final int HT16K33_CMD_BRIGHTNESS = 0xE0;

	byte[] buffer;
	private I2CDevice device;

	/**
	 * Create an HT16K33 driver for device on the specified I2C address (defaults to
	 * 0x70) and I2C bus (defaults to platform specific bus).
	 * 
	 * @param address
	 * @param i2c
	 * @param objects
	 */
	public HT16K33(Integer address, I2CBus i2c) {
		if (address == null) {
			address = DEFAULT_ADDRESS;
		}
		if (i2c == null) {
			try {
				i2c = I2CFactory.getInstance(I2CBus.BUS_1);
			} catch (UnsupportedBusNumberException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			device = i2c.getDevice(DEFAULT_ADDRESS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer = new byte[16];
		begin();
	}

	/**
	 * Initialize driver with LEDs enabled and all turned off.
	 */
	private void begin() {
		setBlink(HT16K33_BLINK_OFF);
		setBrightness(15);
		// clears display for fresh start
		clear();
	}

	/**
	 * Blink display at specified frequency. Note that frequency must be a value
	 * allowed by the HT16K33, specifically one of: HT16K33_BLINK_OFF,
	 * HT16K33_BLINK_2HZ, HT16K33_BLINK_1HZ, or HT16K33_BLINK_HALFHZ.
	 * 
	 * @param frequency
	 */
	public void setBlink(int frequency) {
		if (frequency != HT16K33_BLINK_OFF && frequency != HT16K33_BLINK_2HZ && frequency != HT16K33_BLINK_1HZ
				&& frequency != HT16K33_BLINK_HALFHZ) {
			throw new IllegalArgumentException(
					"Frequency must be one of HT16K33_BLINK_OFF, HT16K33_BLINK_2HZ, HT16K33_BLINK_1HZ, or HT16K33_BLINK_HALFHZ.");
		}
		try {
			device.write(HT16K33_BLINK_CMD | HT16K33_BLINK_DISPLAYON | frequency, buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set brightness of entire display to specified value (16 levels, from 0 to
	 * 15).
	 * 
	 * @param brightness
	 */
	public void setBrightness(int brightness) {
		if (brightness < 0 || brightness > 15) {
			throw new IllegalArgumentException("Brightness must be a value of 0 to 15.");
		}
		try {
			device.write(HT16K33_CMD_BRIGHTNESS | brightness, buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets specified LED (value of 0 to 127) to the specified value, 0/False for
	 * off and 1 (or any True/non-zero value) for on.
	 * 
	 * @param led
	 * @param value
	 */
	public void setLED(byte led, boolean value) {
		if (led < 0 || led > 127) {
			throw new IllegalArgumentException("LED must be value of 0 to 127.");
		}
		int pos = led / 8;
		int offset = led % 8;
		if (value) {
			buffer[pos] |= (1 << offset);
		} else {
			buffer[pos] &= ~(1 << offset);
		}
		writeDisplay();
	}

	/**
	 * Write display buffer to display hardware.
	 */
	protected void writeDisplay() {
		try {
			device.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear contents of display.
	 */
	public void clear() {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0;
		}
		writeDisplay();
	}
}
