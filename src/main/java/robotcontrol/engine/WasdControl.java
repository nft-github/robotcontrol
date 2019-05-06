package robotcontrol.engine;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowListener;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class WasdControl {

	private Engine engine;

	private Terminal terminal;
	private Screen screen;
	private BasicWindow window;

	public WasdControl(Engine engine) {
		this.engine = engine;
		try {
			terminal = new DefaultTerminalFactory().createTerminal();
			screen = new TerminalScreen(terminal);
			screen.startScreen();
		} catch (IOException e) {
			e.printStackTrace();
		}
		window = new BasicWindow();
		MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace());
		gui.addWindowAndWait(window);
		reactToInput();
	}

	private void reactToInput() {
		window.addWindowListener(new WindowListener() {

			@Override
			public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
			}

			@Override
			public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
				switch (keyStroke.getCharacter()) {
				case 'w':
					engine.forward();
					break;
				case 'a':
					engine.forwardRight();
					break;
				case 's':
					engine.backward();
					break;
				case 'd':
					engine.forwardLeft();
					break;
				case '-':
					engine.stop();
					break;
				}
			}

			@Override
			public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
			}

			@Override
			public void onMoved(Window window, TerminalPosition oldPosition, TerminalPosition newPosition) {
			}
		});
	}

	public Engine getEngine() {
		return engine;
	}
}
