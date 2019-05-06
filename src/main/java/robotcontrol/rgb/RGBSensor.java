package robotcontrol.rgb;

import java.util.DuplicateFormatFlagsException;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.GpioUtil;

import robotcontrol.pin.PinInput;
import robotcontrol.pin.PinOutput;

public class RGBSensor {

	private PinOutput gpio1;
	private PinOutput gpio2;
	private PinInput sSpec;

	private final int NUM_CYCLES = 50;
	private int cycles;
	private double start, duration;
	private RGBValue rgbValue;

	private RGBListener rgbListener;

	public RGBSensor(int gpio1, int gpio2, int signal) {
		this.gpio1 = new PinOutput(gpio1);
		this.gpio1 = new PinOutput(gpio2);
		this.sSpec = new PinInput(signal);

		GpioUtil.setEdgeDetection(signal, GpioUtil.EDGE_FALLING);

		sSpec.getGpioPinDigital().addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				cycles++;
			}
		});
		rgbValue = new RGBValue();
	}

	private void setValues() {
		rgbValue.setRed(getColor(PinState.LOW, PinState.LOW));
		rgbValue.setGreen(getColor(PinState.LOW, PinState.HIGH));
		rgbValue.setBlue(getColor(PinState.HIGH, PinState.HIGH));
	}

	private double getColor(PinState s2State, PinState s3State) {
		gpio1.setPinState(s2State);
		gpio2.setPinState(s3State);
		sleep(300);
		cycles = 0;
		start = getTime();
		while (true) {
			sleep(0);
			if (cycles > NUM_CYCLES) {
				break;
			}
		}
		duration = getTime() - start;
		return NUM_CYCLES / duration;
	}

	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private double getTime() {
		return System.currentTimeMillis() / 1000.0;
	}

	public void addListener(RGBListener rgbListener) {
		if (this.rgbListener == null) {
			this.rgbListener = rgbListener;
			new Thread(() -> {
				while (true) {
					rgbListener.value(getRgbValue());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			throw new DuplicateFormatFlagsException(
					"The RGB-Sensor with the GPIO-Pins " + gpio1.getPinNumber() + " and " + gpio2.getPinNumber()
							+ " and the signal " + sSpec.getPinNumber() + " already contains a listener!");
		}
	}

	public RGBValue getRgbValue() {
		setValues();
		return rgbValue;
	}
}
