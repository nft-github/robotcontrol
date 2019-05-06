package robotcontrol.ir;

import java.util.DuplicateFormatFlagsException;

import robotcontrol.pin.PinInput;


public class IRSensor extends PinInput {

	private IRListener irListener;

	public IRSensor(int gpioPinNumber) {
		super(gpioPinNumber);
	}

	public void addListener(IRListener irListener) {
		if (this.irListener == null) {
			this.irListener = irListener;
			new Thread(() -> {
				while (true) {
					irListener.isActive(isActive());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			throw new DuplicateFormatFlagsException(
					"The IR-Sensor with the GPIO-Pin " + getPinNumber() + " already contains a listener!");
		}
	}
}
