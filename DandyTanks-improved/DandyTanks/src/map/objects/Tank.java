package map.objects;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import constants.Const;
import javafx.util.Pair;

public abstract class Tank extends MovableGObject {
	static int id = 0;
	protected int score;
	boolean bonus = false;

	int hp;
	protected double projSpeed, rate;

	protected int lives = 3;
	protected int level = 1;

	int reX, reY;
	int originX, originY, originHp;

	Projectile projectile;

	boolean canMove = true, impossible = false;

	public Tank(int x, int y, int hp, int speed) {
		if ((++id - 5) % 7 == 0)
			bonus = true;

		this.x = x;
		this.y = y;
		originX = 9 * Const.pxTitleW;
		originY = 24 * Const.pxTitleH;

		this.hp = originHp = hp;
		this.rate = 0.8;
		projSpeed = 200;
		anmCount = 2;

		dx = 0;
		dy = -1;

		width = 2 * Const.pxTitleW;
		height = 2 * Const.pxTitleH;

		rotateSprites = new ArrayList<ArrayList<BufferedImage>>(4);
		for (int i = 0; i < 4; i++)
			rotateSprites.add(new ArrayList<BufferedImage>(4));

		this.speed = (double) speed / FPS;
	}

	public boolean bonus() {
		return bonus;
	}

	public void setDirect(int dx, int dy) {
		if (setDirectHalf(dx, dy)) {
			setImage();
		}
	}

	public int getScore() {
		return score;
	}

	public void setRandomDirect() {
		Random rand = new Random();
		int r = rand.nextInt(4);

		if (r == 0)
			setDirectHalf(0, -1);
		else if (r == 1)
			setDirectHalf(1, 0);
		else if (r == 2)
			setDirectHalf(0, 1);
		else
			setDirectHalf(-1, 0);
	}

	public boolean setDirectHalf(int dx, int dy) {
		if (!(this.dx == dx && this.dy == dy)) {
			if (dx != 0 && this.dx != -dx || dy != 0 && this.dy != -dy) {
				int xHalf = (x + width / 2) % Const.pxTitleW;
				int yHalf = (y + height / 2) % Const.pxTitleH;

				reX = x;
				reY = y;

				if (xHalf < Const.pxTitleW / 2) {
					x -= xHalf;
					reX -= (xHalf - Const.pxTitleW);
				} else if (xHalf > Const.pxTitleW / 2) {
					x -= (xHalf - Const.pxTitleW);
					reX -= xHalf;
				}

				if (yHalf < Const.pxTitleW / 2) {
					y -= yHalf;
					reY -= (yHalf - Const.pxTitleH);
				} else if (yHalf > Const.pxTitleW / 2) {
					y -= (yHalf - Const.pxTitleH);
					reY -= yHalf;
				}
			}
			this.dx = dx;
			this.dy = dy;

			return true;
		}
		return false;
	}

	public void redirect() {
		x = reX;
		y = reY;
	}

	public void setImage() {
		if (dy == -1)
			curRot = 0;
		else if (dx == 1)
			curRot = 1;
		else if (dy == 1)
			curRot = 2;
		else
			curRot = 3;
		currentSprite = rotateSprites.get(curRot).get(0);
	}

	public boolean collide(MovableGObject obj) {
		boolean destroy = obj.isDestroy(type);

		Pair<Point, Point> points = obj.getGuidePoints();

		int x1 = points.getKey().x, y1 = points.getKey().y;
		int x2 = points.getValue().x, y2 = points.getValue().y;

		if (x1 >= x && x1 < x + width && y1 >= y && y1 < y + height
				|| x2 >= x && x2 < x + width && y2 >= y && y2 < y + height) {
			if (!impossible && destroy)
				if (--hp <= 0) {
					if (--lives <= 0)
						alive = false;
					else
						respawn();
				}
			return true;
		}
		return false;
	}

	public void setMove(boolean move) {
		canMove = move;
	}

	public boolean isMove() {
		return canMove;
	}

	public void changeImossible() {
		impossible = !impossible;
	}

	long lastSec = System.currentTimeMillis();

	public Projectile shoot() {
		long newSec = System.currentTimeMillis();

		double delta = newSec - lastSec;

		if (canMove && delta / 1000 > rate) {
			lastSec = newSec;
			return new Projectile(projSpeed, x + width / 2, y + height / 2, dx, dy, type);
		}
		return null;
	}

	private void respawn() {
		x = originX;
		y = originY;
		hp = originHp;

		currentSprite = rotateSprites.get(0).get(0);
		dy = -1;
		dx = 0;
	}

	@Override
	public void move() {
		if (canMove)
			super.move();
	}

	@Override
	public boolean isDestroy(GType type) {
		return false;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}
}
