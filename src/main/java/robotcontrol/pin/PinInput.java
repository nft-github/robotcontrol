package robotcontrol.pin;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;

public class PinInput extends Pin {

	private GpioPinDigitalInput input;

	public PinInput(int pinNumber) {
		super(pinNumber);
	}

	@Override
	protected void setGpioPinDigital(int pinNumber) {
		input = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(pinNumber));
		gpioPinDigital = input;
	}
}
