package FoxRabbit;

import java.util.Observer;
import java.util.Random;

import org.eclipse.swt.graphics.Point;

/**
 * Fox class represents the logic provided by the fox to search and traverse the rabbit over the board.
 * The class has different strategies for traversing the board and locating the rabbit location.
 * It also has the logic to inform other foxes about the rabbit location to provide full attack
 * on the rabbit when seen
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Animal
 * @see Rabbit
 * @since JDK 1.6
 */
public class Fox extends Animal {

	private static int[] MAPPING = new int[] { Constants.UP | Constants.LEFT,
			Constants.UP, Constants.UP | Constants.RIGHT, Constants.RIGHT,
			Constants.DOWN | Constants.RIGHT, Constants.DOWN,
			Constants.DOWN | Constants.LEFT, Constants.LEFT };
	private Random generator;
	private Point lastSeen;
	private Observation search;

	public Fox(AbstractBoard board, Observer observer) {
		super(board, observer);
		generator = new Random();
		int x, y;
		do {
			x = Math.abs(generator.nextInt()) % board.size;
			y = Math.abs(generator.nextInt()) % board.size;
		} while (board.getAt(x, y) != Constants.EMPTY);
		location = new Point(x, y);
		board.put(x, y, Constants.FOX);
	}

	public Observation action() {
		return search = search(location);
	}

	public void move() {
		byte moveTo = 0;
		if (search == null) {
			if (lastSeen == null) {
				int trials = 0;
				do {
					moveTo = (byte) (Math.abs(generator.nextInt()) % 8);
				} while (! move(location, MAPPING[moveTo], Constants.NORMAL_STEP) && trials++ < 20);
			} else {
				int direction = 0;
				if (location.x > lastSeen.x)
					direction |= Constants.UP;
				else
					direction |= Constants.DOWN;
				if (location.y > lastSeen.y)
					direction |= Constants.LEFT;
				else
					direction |= Constants.RIGHT;
				if (! move(location, direction, Constants.NORMAL_STEP)) {
					do {
						moveTo = (byte) (Math.abs(generator.nextInt()) % 8);
					} while (! move(location, MAPPING[moveTo],
							Constants.NORMAL_STEP));
				}
			}
		} else /* seen */{
			if (search.distance > Constants.FOX_STEP)
				move(location, search.direction, Constants.FOX_STEP);
			else {
				move(location, search.direction, search.distance);
				observer.update(null, "lost");
			}
		}
		search = null;
	}

	public Observation search(Point foxLocation) {
		Observation o = new Observation();
		o.location = new Point(board.rabbit.location.x, board.rabbit.location.y);
		Point dest = board.rabbit.location;
		if (dest.x == foxLocation.x) {
			if (dest.y > foxLocation.y)
				o.direction = Constants.RIGHT;
			else
				o.direction = Constants.LEFT;
			o.distance = Math.abs(dest.y - foxLocation.y);
			// go through the path and make sure that there are no blocks in the way
			if (emptyWay(foxLocation, o))
				return o;
			else
				return null;
		}
		else if (dest.y == foxLocation.y) {
			if (dest.x > foxLocation.x)
				o.direction = Constants.DOWN;
			else
				o.direction = Constants.UP;
			o.distance = Math.abs(dest.x - foxLocation.x);
			// go through the path and make sure that there are no blocks in the way
			if (emptyWay(foxLocation, o))
				return o;
			else
				return null;
		}
		else if (Math.abs(dest.x - foxLocation.x) == Math.abs(dest.y - foxLocation.y)) {
				int foundHere = 0;
				if (dest.x > foxLocation.x)
					foundHere |= Constants.DOWN;
				else
					foundHere |= Constants.UP;
				if (dest.y > foxLocation.y)
					foundHere |= Constants.RIGHT;
				else
					foundHere |= Constants.LEFT;
				o.direction = foundHere;
				o.distance = Math.abs(dest.x - foxLocation.x);
				// go through the path and make sure that there are no blocks in the way
				if (emptyWay(foxLocation, o))
					return o;
				else
					return null;
			}
		else
			return null;
	}

	private boolean emptyWay(Point fox, Observation o) {
		int m = 0, n = 0;
		if ((o.direction & Constants.DOWN) == Constants.DOWN)
			m = 1;
		else if ((o.direction & Constants.UP) == Constants.UP)
			m = -1;
		if ((o.direction & Constants.LEFT) == Constants.LEFT)
			n = -1;
		else if ((o.direction & Constants.RIGHT) == Constants.RIGHT)
			n = 1;
		for (int i = 1; i < o.distance; i++)
			if (board.getAt(fox.x + m * i, fox.y + n * i) == Constants.BLOCK)
				return false;
		return true;
	}


	public boolean move(Point location, int direction, int step) {
		Point moved = new Point(location.x, location.y);
		if ((direction & Constants.DOWN) == Constants.DOWN)
			moved.x += step;
		else if ((direction & Constants.UP) == Constants.UP)
			moved.x -= step;
		if ((direction & Constants.LEFT) == Constants.LEFT)
			moved.y -= step;
		else if ((direction & Constants.RIGHT) == Constants.RIGHT)
			moved.y += step;
		int x = board.getAt(moved.x, moved.y);
		if (x == -1 || x == Constants.BLOCK || x == Constants.FOX)
			return false;
		board.put(moved.x, moved.y, board.getAt(location.x, location.y));
		board.put(location.x, location.y, Constants.EMPTY);
		location.x = moved.x;
		location.y = moved.y;
		return true;
	}

	public void update(Point lastSeen) {
		this.lastSeen = lastSeen;
	}

	/**
	 * A composite class to hold the representation of the observation needed for the movement of the fox
	 * @author Mostafa Mahmod Mahmod Eweda
	 * @since JDK 1.6
	 */
	class Observation {
		Point location;
		int distance;
		int direction;
	}
}