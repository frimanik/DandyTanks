package mvc;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

import constants.Const;
import tank.objects.TankBuilder;
import tank.objects.TankFactory;

public class Controller {
	Model model;

	int currentStage;

	volatile boolean running = false;

	LinkedList<Integer> keys = new LinkedList<Integer>();

	int keyMove;

	public void startGame(Model model, int stage) {
		this.model = model;
		currentStage = stage;
		loadNextStage();
		running = true;
		System.out.println("Controller running = true");
	}

	public void pauseGame() {
		running = false;

	}

	public void resumeGame() {
		running = true;
	}

	public void stopGame() {
		model.stop();
		running = false;
		System.out.println("Controller running = false");
	}

	public void startMotion(int keyCode) {
		if (running) {
			if (!keys.contains(keyCode))
				keys.add(keyCode);
			if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP)
				model.move(0, -1);
			else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)
				model.move(1, 0);
			else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)
				model.move(0, 1);
			else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT)
				model.move(-1, 0);
			else
				keys.remove((Object) keyCode);
		}

	}

	public void stopMotion(int keyCode) {
		if (running) {
			keys.remove((Object) keyCode);
			if (keys.isEmpty())
				model.stopMotion();
			else
				startMotion(keys.pop());

		}
	}

	public void fire() {
		model.shoot();
	}

	public void loadNextStage() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("./maps/stage" + currentStage + ".txt"));
			BufferedReader br2 = new BufferedReader(new FileReader("./data/stage" + currentStage + ".txt"));

			int data[][] = new int[Const.MAP_SIZE][Const.MAP_SIZE];

			int i = 0;
			while (br.ready()) {
				String str = br.readLine();
				for (int j = 0; j < str.length(); j++)
					data[i][j] = Character.getNumericValue(str.charAt(j));
				i++;
			}

			TankFactory tf = new TankFactory();
			while (br2.ready()) {
				String[] strs = br2.readLine().split(" ");
				tf.add(new TankBuilder(Class.forName("tank.objects.TankE" + strs[0]), Integer.parseInt(strs[1])));
			}

			model.loadStageData(data, tf);
			model.start();

			br.close();
			br2.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
