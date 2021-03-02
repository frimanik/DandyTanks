package map.objects;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import constants.Const;
import javafx.util.Pair;

public class Crystal implements Drawable, Breaking {
	BufferedImage currentSprite;

	int x, y, width, height;

	boolean alive = true;

	GType type = GType.Block;

	int hp = 5;

	public Crystal(int x, int y) {
		this.x = x;
		this.y = y;

		width = 2 * Const.pxTitleW;
		height = 2 * Const.pxTitleH;

		try {
			currentSprite = ImageIO.read(new File("./sprites/crystal.png"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public boolean collide(MovableGObject obj) {
		boolean destroy = obj.isDestroy(type);

		Pair<Point, Point> points = obj.getGuidePoints();

		int x1 = points.getKey().x, y1 = points.getKey().y;
		int x2 = points.getValue().x, y2 = points.getValue().y;

		if (x1 >= x && x1 < x + width && y1 >= y && y1 < y + height
				|| x2 >= x && x2 < x + width && y2 >= y && y2 < y + height) {
			if (destroy)
				if (--hp <= 0) {
					alive = false;
				}
			return true;
		}
		return false;
	}

	@Override
	public void kill() {
		alive = false;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public BufferedImage getRenderingImage() {
		return currentSprite;
	}

	@Override
	public Point getPosition() {
		return new Point(x, y);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(width, height);
	}
}
