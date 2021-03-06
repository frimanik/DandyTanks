package block.objects;

import java.io.File;

import javax.imageio.ImageIO;

import map.objects.Block;
import map.objects.MovableGObject;

public class Leafage extends Block {

	public Leafage(int x, int y) {
		super(x, y);
		try {
			currentSprite = ImageIO.read(new File("./sprites/blocks/leafage.jpg"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public boolean collide(int x, int y, MovableGObject obj) {
		return false;
	}

}
