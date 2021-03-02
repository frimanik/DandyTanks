package map.objects;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import constants.Const;

public class Bonus implements Drawable, Breaking {
	int x, y, width, height;

	BufferedImage sprite;

	BType type;

	boolean alive = true;

	public Bonus(int x, int y, BType type) {
		this.type = type;

		this.x = x;
		this.y = y;
		width = Const.pxTitleW << 1;
		height = Const.pxTitleH << 1;

		try {
			sprite = ImageIO.read(new File("./sprites/bonuses/" + type.name().toLowerCase() + ".png"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public BType getType() {
		return type;
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
		return sprite;
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
