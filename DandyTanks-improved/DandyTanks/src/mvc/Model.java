package mvc;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import block.objects.Brick;
import block.objects.Iron;
import block.objects.Leafage;
import block.objects.Water;
import constants.Const;
import javafx.util.Pair;
import map.objects.BType;
import map.objects.Block;
import map.objects.Bonus;
import map.objects.Crystal;
import map.objects.Drawable;
import map.objects.MovableGObject;
import map.objects.Projectile;
import map.objects.Tank;
import tank.objects.BaseTankM1;
import tank.objects.TankFactory;

public class Model {
	Block map[][];
	LinkedList<Tank> tanks;
	LinkedList<Projectile> projectiles;
	LinkedList<Bonus> bonuses;

	TankFactory factory;
	Crystal crystal;
	Tank mainTank;

	Thread thread;

	int lives = 3;

	int frameCount = 0;

	int score = 0;

	boolean moving = false, emptyFactory = false;

	volatile boolean running, winner, looser;

	public boolean isRunning() {
		return running;
	}

	public void loadStageData(int[][] map, TankFactory builder) {
		lives = 3;
		frameCount = 0;
		moving = false;
		emptyFactory = false;
		thread = null;
		this.map = new Block[Const.MAP_SIZE][Const.MAP_SIZE];

		for (int i = 0; i < Const.MAP_SIZE; i++) {
			for (int j = 0; j < Const.MAP_SIZE; j++) {
				int value = map[i][j];
				int x = j * Const.pxTitleW;
				int y = i * Const.pxTitleH;
				if (value == 1)
					this.map[i][j] = new Brick(x, y);
				else if (value == 2)
					this.map[i][j] = new Iron(x, y);
				else if (value == 3)
					this.map[i][j] = new Leafage(x, y);
				else if (value == 4)
					this.map[i][j] = new Water(x, y);
			}
		}

		tanks = new LinkedList<Tank>();
		projectiles = new LinkedList<Projectile>();
		bonuses = new LinkedList<Bonus>();

		crystal = new Crystal(12 * Const.pxTitleW, 24 * Const.pxTitleH);
		mainTank = new BaseTankM1(9 * Const.pxTitleW, 24 * Const.pxTitleH);

		this.factory = builder;
	}

	public synchronized void move(int dx, int dy) {
		moving = true;
		mainTank.setDirectHalf(dx, dy);
		if (checkCollision(mainTank))
			mainTank.redirect();
		mainTank.setImage();
	}

	private boolean checkCollision(Tank tank) {
		int x = tank.getX(), y = tank.getY();

		boolean result = false;
		for (int i = y; i <= y + tank.getHeight() - 1 && !result; i += Const.pxTitleH / 4) {
			result = result || blockCollisions(new Point(x, i), new Point(x + tank.getWidth() - 1, i), tank);
		}
		return result;
	}

	public synchronized void stopMotion() {
		moving = false;
	}

	public synchronized void shoot() {
		Projectile proj = mainTank.shoot();
		if (proj != null)
			projectiles.add(proj);
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(() -> {
			long delta = 0;
			long nowTime;
			long lastTime = System.nanoTime();

			while (running) {
				nowTime = System.nanoTime();
				delta += nowTime - lastTime;
				lastTime = nowTime;
				if (delta >= Const.UPDATE_TIME) {
					update();
					delta = 0;
					frameCount = (frameCount + 1) % 240;
				}
			}
		});
		thread.start();
		System.out.println("The game is started.");
	}

	public synchronized void stop() {
		try {
			running = false;
			thread.join();
			System.out.println("The game is stopped.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized List<Drawable> getDrawableObjects() {
		List<Drawable> list = new LinkedList<Drawable>();

		for (int i = 0; i < Const.MAP_SIZE; i++)
			for (int j = 0; j < Const.MAP_SIZE; j++)
				if (map[i][j] != null)
					list.add(map[i][j]);

		for (Tank t : tanks)
			list.add(t);
		for (Projectile p : projectiles)
			list.add(p);
		for (Bonus b : bonuses)
			list.add(b);

		if (crystal.isAlive())
			list.add(crystal);
		if (mainTank.isAlive())
			list.add(mainTank);

		return list;
	}

	private synchronized void checkAliveObjects() {
		for (int i = 0; i < Const.MAP_SIZE; i++)
			for (int j = 0; j < Const.MAP_SIZE; j++)
				if (map[i][j] != null) {
					if (!map[i][j].isAlive())
						map[i][j] = null;
				}

		Iterator<Tank> iter = tanks.iterator();
		while (iter.hasNext()) {
			Tank tank = iter.next();

			if (!tank.isAlive()) {
				score += tank.getScore();
				if (tank.bonus())
					bonuses.add(new Bonus(tank.getX(), tank.getY(), BType.random()));
				iter.remove();
			}
		}

		Iterator<Projectile> iter2 = projectiles.iterator();
		while (iter2.hasNext()) {
			if (!iter2.next().isAlive())
				iter2.remove();
		}

		Iterator<Bonus> iter3 = bonuses.iterator();
		while (iter3.hasNext()) {
			if (!iter3.next().isAlive())
				iter3.remove();
		}

		if (!crystal.isAlive() || !mainTank.isAlive()) {
			running = false;
			looser = true;
		}

		if (emptyFactory && tanks.isEmpty()) {
			running = false;
			winner = true;
		}
	}

	private synchronized void update() {
		checkAliveObjects();

		if (moving && mainTank.isAlive() && canMove(mainTank)) {
			mainTank.move();
		}

		tanksUpdate();

		projectilesUpdate();

		bonusesUpdate();
	}

	private void bonusesUpdate() {
		int x1 = mainTank.getX(), y1 = mainTank.getY(), x2 = x1 + mainTank.getWidth(), y2 = y1 + mainTank.getWidth();

		for (Bonus b : bonuses) {
			Point p = b.getPosition();
			Dimension d = b.getSize();

			if ((p.x >= x1 && p.x <= x2 || p.x + d.width >= x1 && p.x + d.width <= x2)
					&& (p.y >= y1 && p.y <= y2 || p.y + d.height >= y1 && p.y + d.height <= y2)) {
				b.kill();
				handleBonusAction(b.getType());
			}
		}
	}

	private void handleBonusAction(BType type) {
		switch (type) {
		case Star: {
			try {
				int x = mainTank.getX();
				int y = mainTank.getY();
				int dx = mainTank.getDx();
				int dy = mainTank.getDy();

				int level = mainTank.getLevel() + 1;
				int lives = mainTank.getLives();

				Class<?> tankType = Class.forName("tank.objects.BaseTankM" + level);
				mainTank = (Tank) tankType.getConstructors()[0].newInstance(new Object[] { x, y });
				mainTank.setLevel(level);
				mainTank.setLives(lives);
				mainTank.setDirect(dx, dy);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case Armor:
			new Thread(() -> {
				try {
					mainTank.changeImossible();
					Thread.sleep(10000);
					mainTank.changeImossible();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			;
			break;
		case Tank:
			mainTank.setLives(mainTank.getLives() + 1);
			break;
		case Shovel:
			new Thread(() -> {
				try {
					changeCrystalProtection(Iron.class);
					Thread.sleep(20000);
					changeCrystalProtection(Brick.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			break;
		case Clock:

			new Thread(() -> {
				try {
					for (Tank t : tanks)
						t.setMove(false);
					Thread.sleep(10000);
					for (Tank t : tanks)
						t.setMove(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			break;
		case Bomb:
			for (Tank t : tanks) {
				t.kill();
			}
			break;
		}
	}

	private void changeCrystalProtection(Class<? extends Block> type) {
		int y = 23;
		int x;
		try {
			for (x = 11; x < 15; x++)
				map[y][x] = (Block) type.getConstructors()[0]
						.newInstance(new Object[] { x * Const.pxTitleW, y * Const.pxTitleH });
			map[++y][--x] = (Block) type.getConstructors()[0]
					.newInstance(new Object[] { x * Const.pxTitleW, y * Const.pxTitleH });
			map[++y][x] = (Block) type.getConstructors()[0]
					.newInstance(new Object[] { x * Const.pxTitleW, y * Const.pxTitleH });
			map[y][x -= 3] = (Block) type.getConstructors()[0]
					.newInstance(new Object[] { x * Const.pxTitleW, y * Const.pxTitleH });
			map[--y][x] = (Block) type.getConstructors()[0]
					.newInstance(new Object[] { x * Const.pxTitleW, y * Const.pxTitleH });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void tanksUpdate() {
		if (tanks.size() < 4 && frameCount == 0 && !emptyFactory) {
			Tank tank = factory.createTank();
			if (tank != null) {
				if (!tanks.isEmpty() && !tanks.getFirst().isMove())
					tank.setMove(false);
				tanks.add(tank);
			} else
				emptyFactory = true;
		}

		Iterator<Tank> iter = tanks.iterator();
		while (iter.hasNext()) {
			Tank tank = iter.next();

			int count = 0;
			while (!canMove(tank) && count < 100) {
				count++;
				tank.setRandomDirect();
			}

			Projectile proj = tank.shoot();
			if (proj != null)
				projectiles.add(proj);

			if (count < 100) {
				tank.setImage();
				tank.move();
			}
		}
	}

	private void projectilesUpdate() {
		Iterator<Projectile> iter = projectiles.iterator();
		while (iter.hasNext()) {
			Projectile proj = iter.next();

			if (proj.isAlive()) {
				if (canMove(proj))
					proj.move();
				else {
					proj.boom();
					iter.remove();
				}
			}
		}

	}

	private boolean canMove(MovableGObject obj) {
		boolean result = true;

		for (Tank t : tanks) {
			if (!t.equals(obj) && t.isAlive())
				result = result && !t.collide(obj);
		}

		if (!mainTank.equals(obj) && mainTank.isAlive())
			result = result && !mainTank.collide(obj);

		for (Projectile p : projectiles) {
			if (!p.equals(obj) && p.isAlive())
				result = result && !p.collide(obj);
		}

		result = result && !crystal.collide(obj);

		Pair<Point, Point> pair = obj.getGuidePoints();
		return result && !blockCollisions(pair.getKey(), pair.getValue(), obj);
	}

	private boolean blockCollisions(Point p1, Point p2, MovableGObject obj) {

		int pxW = Const.pxTitleW;
		int pxH = Const.pxTitleH;

		boolean result = false;

		int xMin = p1.x < p2.x ? p1.x : p2.x;
		int xMax = p2.x > p1.x ? p2.x : p1.x;
		int yMin = p1.y < p2.y ? p1.y : p2.y;
		int yMax = p2.y > p1.y ? p2.y : p1.y;

		for (int x = xMin; x <= xMax; x += Const.pxTitleW / 4) {
			for (int y = yMin; y <= yMax; y += Const.pxTitleH / 4) {
				if (!(x >= 0 && x < Const.mapWidth && y >= 0 && y < Const.mapHeight))
					return true;
				Block b = map[y / pxH][x / pxW];

				boolean collision = false;
				if (b != null)
					collision = b.collide(x % pxW * 2 / pxW, y % pxH * 2 / pxH, obj);
				result = result || collision;
			}
		}

		return result;
	}
}
