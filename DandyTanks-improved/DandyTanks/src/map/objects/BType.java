package map.objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum BType {
	Star, Tank, Bomb, Clock, Shovel, Armor;

	private static final List<BType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();

	public static BType random() {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
}
