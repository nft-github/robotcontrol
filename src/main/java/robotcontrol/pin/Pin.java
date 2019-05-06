package robotcontrol.pin;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigital;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;

public abstract class Pin {

	protected static final GpioController gpio;

	static {
		gpio = GpioFactory.getInstance();
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
	}

	protected GpioPinDigital gpioPinDigital;

	public Pin(int pinNumber) {
		setGpioPinDigital(pinNumber);
	}

	protected abstract void setGpioPinDigital(int pinNumber);

	public GpioPinDigital getGpioPinDigital() {
		return gpioPinDigital;
	}

	public PinState getPinState() {
		return gpioPinDigital.getState();
	}

	public int getPinNumber() {
		return gpioPinDigital.getPin().getAddress();
	}

	public boolean isActive() {
		return gpioPinDigital.getState() == PinState.LOW;
	}
}
