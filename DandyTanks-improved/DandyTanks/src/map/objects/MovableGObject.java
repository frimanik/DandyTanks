package map.objects;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.util.Pair;

public abstract class MovableGObject implements Drawable, Breaking {
	static final int FPS = 60; // 60 frames per second

	protected BufferedImage currentSprite;

	protected ArrayList<ArrayList<BufferedImage>> rotateSprites;

	protected double speed;
	protected int x, y, width, height;
	protected int dx, dy;

	boolean alive = true;

	protected GType type;

	private double frameCount = 0.0;

	protected int anmShift = 0;
	protected int curRot, anmCount;

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void move() {
		frameCount += speed;
		int delta = (int) frameCount;
		if (delta >= 1) {
			x += dx * delta;
			y += dy * delta;
			frameCount -= delta;
			currentSprite = rotateSprites.get(curRot).get(anmShift % anmCount);
			anmShift = anmShift % anmCount;
		}
		anmShift++;
	}

	public boolean isDestroy(GType type) {
		if (type != this.type)
			return true;
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

	public abstract void boom();

	public Pair<Point, Point> getGuidePoints() {
		Point point = getMoveCoord();

		Point p1, p2;

		int w = width - 1;
		int h = height - 1;
		if (dy == -1) {
			p1 = new Point(point.x, point.y);
			p2 = new Point(point.x + w, point.y);
		} else if (dx == 1) {
			p1 = new Point(point.x + w, point.y);
			p2 = new Point(point.x + w, point.y + h);
		} else if (dy == 1) {
			p1 = new Point(point.x + w, point.y + h);
			p2 = new Point(point.x, point.y + h);
		} else {
			p1 = new Point(point.x, point.y + h);
			p2 = new Point(point.x, point.y);
		}

		return new Pair<Point, Point>(p1, p2);
	}

	private Point getMoveCoord() {
		int delta = (int) (frameCount + speed);
		return new Point(x + dx * delta, y + dy * delta);
	}

	protected BufferedImage rotate(BufferedImage image, int degree) {
		double radian = degree * Math.PI / 180;

		AffineTransform tx = new AffineTransform();
		tx.rotate(radian, image.getWidth() / 2, image.getHeight() / 2);

		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		return op.filter(image, null);
	}
}
