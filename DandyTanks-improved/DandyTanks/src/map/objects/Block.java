package map.objects;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

import constants.Const;

public abstract class Block implements Drawable, Breaking {
	protected int x, y, width, height;

	protected BufferedImage currentSprite;

	protected boolean alive = true;

	protected Block(int x, int y) {
		this.x = x;
		this.y = y;

		width = Const.pxTitleW;
		height = Const.pxTitleH;
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

	@Override
	public void kill() {
		alive = false;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	public abstract boolean collide(int x, int y, MovableGObject obj);

}
