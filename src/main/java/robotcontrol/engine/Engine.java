package robotcontrol.engine;

import com.pi4j.io.gpio.PinState;

import robotcontrol.pin.PinOutput;

public class Engine {

	private PinOutput[] enginePins;

	/**
	 * Define the pins that are connected to the engine. For Example 19, 26, 16, 20.
	 * 
	 * @param left1
	 * @param left2
	 * @param right1
	 * @param right2
	 */
	public Engine(int left1, int left2, int right1, int right2) {
		enginePins = new PinOutput[4];
		enginePins[0] = new PinOutput(left1);
		enginePins[1] = new PinOutput(left2);
		enginePins[2] = new PinOutput(right1);
		enginePins[3] = new PinOutput(right2);
	}

	public void forwardLeft() {
		power(PinState.LOW, PinState.HIGH, PinState.LOW, PinState.LOW);
	}

	public void forwardRight() {
		power(PinState.LOW, PinState.LOW, PinState.LOW, PinState.HIGH);
	}

	public void forward() {
		power(PinState.LOW, PinState.HIGH, PinState.LOW, PinState.HIGH);
	}

	public void backwardLeft() {
		power(PinState.HIGH, PinState.LOW, PinState.LOW, PinState.LOW);
	}

	public void backwardRight() {
		power(PinState.LOW, PinState.LOW, PinState.HIGH, PinState.LOW);
	}

	public void backward() {
		power(PinState.HIGH, PinState.LOW, PinState.HIGH, PinState.LOW);
	}

	public void stop() {
		power(PinState.LOW, PinState.LOW, PinState.LOW, PinState.LOW);
	}

	private void power(PinState... pinStates) {
		for (int output = 0; output < enginePins.length; output++) {
			enginePins[output].setPinState(pinStates[output]);
		}
	}

	public PinOutput[] getEnginePins() {
		return enginePins;
	}
}
