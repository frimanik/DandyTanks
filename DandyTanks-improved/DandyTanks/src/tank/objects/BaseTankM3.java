package tank.objects;

import java.io.File;

import javax.imageio.ImageIO;

import map.objects.GType;
import map.objects.Tank;

public class BaseTankM3 extends Tank {
	public BaseTankM3(int x, int y) {
		super(x, y, 1, 150);

		rate = 0.5;

		type = GType.Main;

		anmCount = 1;
		try {
			for (int i = 0; i < anmCount; i++) {
				currentSprite = ImageIO.read(new File("./sprites/tanks/main3_" + i + ".png"));

				rotateSprites.get(0).add(currentSprite);
				rotateSprites.get(1).add(rotate(currentSprite, 90));
				rotateSprites.get(2).add(rotate(currentSprite, 180));
				rotateSprites.get(3).add(rotate(currentSprite, 270));
			}
			currentSprite = rotateSprites.get(0).get(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void boom() {
		// TODO Auto-generated method stub

	}
}
