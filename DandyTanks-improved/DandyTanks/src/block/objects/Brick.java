package block.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import map.objects.Block;
import map.objects.GType;
import map.objects.MovableGObject;

public class Brick extends Block {
	boolean[] parts;

	public Brick(int x, int y) {
		super(x, y);
		try {
			BufferedImage[] parts = new BufferedImage[2];
			parts[0] = ImageIO.read(new File("./sprites/blocks/brick_left.jpg"));
			parts[1] = ImageIO.read(new File("./sprites/blocks/brick_right.jpg"));

			currentSprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			concatImages(parts);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		parts = new boolean[4];
		for (int i = 0; i < 4; i++)
			parts[i] = true;
	}

	@Override
	public boolean collide(int x, int y, MovableGObject obj) {
		if (obj.isDestroy(GType.Block) && parts[x + 2 * y])
			return destroy(obj.getDx(), obj.getDy());
		return parts[x + 2 * y];
	}

	private boolean destroy(int dx, int dy) {
		boolean one, two;

		if (dy == -1) {
			one = destroyPart(0, 1, dx, dy);
			two = destroyPart(1, 1, dx, dy);
		} else if (dx == 1) {
			one = destroyPart(0, 0, dx, dy);
			two = destroyPart(0, 1, dx, dy);
		} else if (dy == 1) {
			one = destroyPart(0, 0, dx, dy);
			two = destroyPart(1, 0, dx, dy);
		} else {
			one = destroyPart(1, 0, dx, dy);
			two = destroyPart(1, 1, dx, dy);
		}

		for (int i = 0; i < 4; i++) {
			alive = alive || parts[i];
		}

		return one || two;
	}

	private boolean destroyPart(int x, int y, int dx, int dy) {
		if (x < 0 || x > 1 || y < 0 || y > 1)
			return false;

		if (!parts[x + 2 * y])
			return destroyPart(x + dx, y + dy, dx, dy);

		Graphics g = currentSprite.getGraphics();
		g.setColor(Color.BLACK);

		int wh = width / 2;
		int hh = height / 2;
		g.fillRect(x * wh, y * hh, wh, hh);

		g.dispose();

		parts[x + 2 * y] = false;

		return true;
	}

	private void concatImages(BufferedImage[] parts) {
		Graphics graph = currentSprite.getGraphics();
		int wh = width / 2;
		int hh = height / 2;
		graph.drawImage(parts[0], 0, 0, wh, hh, null);
		graph.drawImage(parts[1], wh, 0, wh, hh, null);
		graph.drawImage(parts[1], 0, hh, wh, hh, null);
		graph.drawImage(parts[0], wh, hh, wh, hh, null);
		graph.dispose();
	}
}
