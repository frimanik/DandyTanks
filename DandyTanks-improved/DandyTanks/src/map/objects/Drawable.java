package map.objects;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

public interface Drawable {
	BufferedImage getRenderingImage();

	Point getPosition();

	Dimension getSize();
}
