package tank.objects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import map.objects.Tank;

public class TankFactory {
	HashSet<TankBuilder> builders;

	Random rand = new Random();

	public TankFactory() {
		builders = new HashSet<TankBuilder>();
	}

	public void add(TankBuilder... builders) {
		for (TankBuilder tb : builders) {
			this.builders.add(tb);
		}
	}

	public Tank createTank() {
		Tank result = null;
		while (result == null && !builders.isEmpty()) {
			int i = 0;
			int r = rand.nextInt(builders.size());
			Iterator<TankBuilder> iter = builders.iterator();
			while (r != i++)
				iter.next();
			result = iter.next().createTank();
			if (result == null)
				iter.remove();
		}
		return result;
	}

}
