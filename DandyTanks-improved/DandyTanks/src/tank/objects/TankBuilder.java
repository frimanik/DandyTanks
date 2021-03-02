package tank.objects;

import java.util.Random;

import constants.Const;
import map.objects.Tank;

public class TankBuilder {
	Class<?> tankType;
	int maxCount, count = 0;
	Random rand = new Random();

	public TankBuilder(Class<?> tankType, int maxCount) {
		this.maxCount = maxCount;
		this.tankType = tankType;
	}

	@Override
	public boolean equals(Object obj) {
		return tankType == ((TankBuilder) obj).tankType;
	}

	@Override
	public int hashCode() {
		return tankType.hashCode();
	}

	public Tank createTank() {
		try {
			if (count++ == maxCount)
				return null;
			else {
				int r = rand.nextInt(3);

				int x, y = 0;

				if (r == 0)
					x = 0;
				else if (r == 1)
					x = Const.mapWidth / 2 - Const.pxTitleW;
				else
					x = Const.mapWidth - 2 * Const.pxTitleW;

				Object obj = tankType.getConstructors()[0].newInstance(new Object[] { x, y });
				return (Tank) obj;
			}
		} catch (Exception e) {
			System.out.println("The object can't be converted in Tank.");
			return null;
		}
	}
}
