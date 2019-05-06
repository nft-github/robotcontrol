package robotcontrol.pin;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class PinOutput extends Pin {

	private GpioPinDigitalOutput output;

	public PinOutput(int pinNumber) {
		super(pinNumber);
	}

	@Override
	protected void setGpioPinDigital(int pinNumber) {
		output = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(pinNumber));
		gpioPinDigital = output;
	}

	public void setPinState(PinState pinState) {
		output.setState(pinState);
	}
}
