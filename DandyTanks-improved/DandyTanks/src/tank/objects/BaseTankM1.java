package tank.objects;

import java.io.File;

import javax.imageio.ImageIO;

import map.objects.GType;
import map.objects.Tank;

public class BaseTankM1 extends Tank {
	public BaseTankM1(int x, int y) {
		super(x, y, 1, 120);

		rate = 0.7;

		type = GType.Main;

		try {
			for (int i = 0; i < 2; i++) {
				currentSprite = ImageIO.read(new File("./sprites/tanks/main1_" + i + ".png"));

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
