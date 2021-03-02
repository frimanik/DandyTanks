package map.objects;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import constants.Const;
import javafx.util.Pair;

public class Projectile extends MovableGObject {

	public Projectile(double speed, int x, int y, int dx, int dy, GType type) {
		this.speed = speed;
		this.dx = dx;
		this.dy = dy;

		this.type = type;

		anmCount = 1;

		rotateSprites = new ArrayList<ArrayList<BufferedImage>>(4);
		for (int i = 0; i < 4; i++)
			rotateSprites.add(new ArrayList<BufferedImage>(4));

		try {
			currentSprite = ImageIO.read(new File("./sprites/Projectile.png"));
			curRot = 0;
			if (dx == 1) {
				curRot = 1;
				currentSprite = rotate(currentSprite, 90);
			} else if (dy == 1) {
				curRot = 2;
				currentSprite = rotate(currentSprite, 180);
			} else if (dx == -1) {
				curRot = 3;
				currentSprite = rotate(currentSprite, 270);
			}

			for (int i = 0; i < 4; i++)
				rotateSprites.get(i).add(currentSprite);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		width = Const.pxTitleW >> 1;
		height = Const.pxTitleH >> 1;

		this.x = x - (width >> 1) + dx * Const.pxTitleW;
		this.y = y - (height >> 1) + dy * Const.pxTitleH;

		this.speed /= FPS;
	}

	public boolean collide(MovableGObject obj) {
		if (obj instanceof Projectile) {
			Pair<Point, Point> points = obj.getGuidePoints();

			int x1 = points.getKey().x, y1 = points.getKey().y;
			int x2 = points.getValue().x, y2 = points.getValue().y;

			if (x1 >= x && x1 < x + width && y1 >= y && y1 < y + height
					|| x2 >= x && x2 < x + width && y2 >= y && y2 < y + height) {
				alive = false;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isDestroy(GType type) {
		if (type == GType.Block)
			return true;
		return super.isDestroy(type);
	}

	@Override
	public void boom() {
		// System.out.println("Boom!");
	}

}
