package constants;

public interface Const {
	final double UPDATE_TIME = 1.0e9 / 60;
	final int MAP_SIZE = 26;

	int mapWidth = 624;
	int mapHeight = mapWidth;
	int width = mapWidth * 5 / 4;
	int height = mapHeight + 40;

	int pxTitleW = mapWidth / MAP_SIZE;
	int pxTitleH = mapHeight / MAP_SIZE;
}
