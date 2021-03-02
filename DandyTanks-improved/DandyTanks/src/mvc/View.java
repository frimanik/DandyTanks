package mvc;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import constants.Const;
import map.objects.Drawable;

public class View extends JFrame {
	private static final long serialVersionUID = -7928489707831639832L;

	Thread thread;
	boolean running;

	Canvas map;
	JPanel menu;
	JButton startBtn;
	JTextField nameField;
	JTextArea scoreList;

	Controller c;
	Model m;

	HashMap<String, Integer> playersScore;

	BufferedImage image;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	BufferStrategy bs;

	View(String name) {
		super(name);
		playersScore = new HashMap<String, Integer>();
		c = new Controller();
	}

	public synchronized void start() {
		init();
		running = true;
		map.requestFocus();
		thread = new Thread(() -> {
			while (running) {
				render();
			}
		});
		thread.start();
	}

	private void render() {
		bs = map.getBufferStrategy();
		if (bs == null) {
			map.createBufferStrategy(3);
		} else if (c.running) {
			Graphics g = bs.getDrawGraphics();

			// System.out.println("render()");

			List<Drawable> objects = m.getDrawableObjects();

			// System.out.println(objects.size());

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, Const.mapWidth, Const.mapHeight);

			synchronized (objects) {
				for (Drawable dr : objects) {
					Point p = dr.getPosition();
					Dimension d = dr.getSize();
					BufferedImage image = dr.getRenderingImage();

					g.drawImage(image, p.x, p.y, d.width, d.height, null);

				}
			}

			if (m != null) {
				playersScore.put(nameField.getText(), m.score);
				StringBuilder sb = new StringBuilder();

				for (Map.Entry<String, Integer> p : sortByValue(playersScore).entrySet()) {
					sb.append(p.getKey()).append(": ").append(p.getValue()).append("\n");
				}
				scoreList.setText(sb.toString());

				if (!m.running) {
					int stage = c.currentStage;
					c.stopGame();
					if (m.winner) {
						m = null;
						m = new Model();
						c.startGame(m, stage + 1);
					} else
						m = null;
					startBtn.setText("Start");
				}

				if (fire)
					m.shoot();
			}

			g.dispose();
			bs.show();
		}
	}

	public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	public synchronized void stop() throws InterruptedException {
		running = false;
		thread.join();
	}

	boolean fire = false;

	private void init() {
		setBounds((screenSize.width - Const.width) / 2, (screenSize.height - Const.height) / 2, Const.width + 14,
				Const.height - 1);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel container = new JPanel();
		container.setLayout(null);
		container.setBackground(Color.DARK_GRAY);

		map = new Canvas();
		map.setBounds(0, 0, Const.mapWidth, Const.mapHeight);

		map.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					fire = false;
				else
					c.stopMotion(e.getKeyCode());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					fire = true;
				else
					c.startMotion(e.getKeyCode());
			}
		});

		JPanel ui = new JPanel();
		ui.setLayout(null);
		ui.setBackground(Color.LIGHT_GRAY);
		ui.setBounds(Const.mapWidth, 0, Const.width - Const.mapWidth, Const.mapHeight);

		startBtn = new JButton("Start");
		startBtn.setBounds((Const.width - Const.mapWidth - 100) / 2, 20, 100, 30);
		startBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (startBtn.getText().equals("Start")) {
					m = new Model();
					c.startGame(m, 1);
					map.requestFocus();
					startBtn.setText("Stop");
				} else {
					c.stopGame();
					m = null;
					startBtn.setText("Start");
				}
			}
		});

		JButton pauseBtn = new JButton("Pause");
		pauseBtn.setBounds((Const.width - Const.mapWidth - 100) / 2, startBtn.getY() + startBtn.getHeight() + 10, 100,
				30);
		pauseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pauseBtn.getText().equals("Pause")) {
					map.requestFocus();
					c.pauseGame();
					pauseBtn.setText("Resume");
				} else {
					c.resumeGame();
					pauseBtn.setText("Pause");
				}
			}
		});

		nameField = new JTextField();
		nameField.setBounds((Const.width - Const.mapWidth - 130) / 2, pauseBtn.getY() + pauseBtn.getX() + 10, 130, 30);

		scoreList = new JTextArea();
		scoreList.setBounds((Const.width - Const.mapWidth - 130) / 2, nameField.getY() + nameField.getHeight() + 10,
				130, 300);
		scoreList.setBackground(Color.YELLOW);
		scoreList.setLayout(null);
		Font font = scoreList.getFont();
		float size = font.getSize() + 2.0f;
		scoreList.setFont(font.deriveFont(size));

		ui.add(startBtn);
		ui.add(pauseBtn);
		ui.add(nameField);
		ui.add(scoreList);

		container.add(map);
		container.add(ui);

		add(container);

		setVisible(true);
	}

	public static void main(String args[]) throws Exception {
		View frame = new View("BattleCityS");

		frame.start();
	}

}
