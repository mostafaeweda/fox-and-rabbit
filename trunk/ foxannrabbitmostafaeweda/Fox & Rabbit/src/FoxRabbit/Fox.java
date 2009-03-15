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
		return search = search();
	}

	/**
	 * move the fox with the current data the fox know about from his sight or other foxes
	 * sight if there are more than one fox or moves randomly if no data is avaialable on the rabbit location
	 */
	public void move() {
		byte moveTo = 0;
		int trials = 0; // the number of trials after which the fox recognizes that he is trapped is 100
		if (search == null) {
			if (lastSeen == null) { // the rabbit was never seen
				do {
					moveTo = (byte) (Math.abs(generator.nextInt()) % 8);
				} while (! move(MAPPING[moveTo], Constants.NORMAL_STEP) && trials++ < 100);
			} else { // the rabbit was seen by a fox
				int direction = 0;
				if (location.x > lastSeen.x)
					direction |= Constants.UP;
				else
					direction |= Constants.DOWN;
				if (location.y > lastSeen.y)
					direction |= Constants.LEFT;
				else
					direction |= Constants.RIGHT;
				trials = 0;
				if (! move(direction, Constants.NORMAL_STEP)) {
					do {
						moveTo = (byte) (Math.abs(generator.nextInt()) % 8);
					} while (! move(MAPPING[moveTo],
							Constants.NORMAL_STEP) && trials < 100);
				}
			}
		} else /* seen */ { // the movement isn't restricted
			observer.update(null, "seen");
			if (search.distance > Constants.FOX_STEP)
				move(search.direction, Constants.FOX_STEP);
			else {
				move(search.direction, search.distance);
				observer.update(null, "lost");
			}
		}
		search = null;
	}

	/**
	 * The search method returns an observation having the information available from the current fox
	 * @return an observation containg the data
	 * @see Observation
	 */
	public Observation search() {
		Observation o = new Observation();
		o.location = new Point(board.rabbit.location.x, board.rabbit.location.y);
		Point dest = board.rabbit.location;
		if (dest.x == location.x) {
			if (dest.y > location.y)
				o.direction = Constants.RIGHT;
			else
				o.direction = Constants.LEFT;
			o.distance = Math.abs(dest.y - location.y);
			// go through the path and make sure that there are no blocks in the way
			if (emptyWay(location, o))
				return o;
			else
				return null;
		}
		else if (dest.y == location.y) {
			if (dest.x > location.x)
				o.direction = Constants.DOWN;
			else
				o.direction = Constants.UP;
			o.distance = Math.abs(dest.x - location.x);
			// go through the path and make sure that there are no blocks in the way
			if (emptyWay(location, o))
				return o;
			else
				return null;
		}
		// diagonally moving
		else if (Math.abs(dest.x - location.x) == Math.abs(dest.y - location.y)) {
				int foundHere = 0;
				if (dest.x > location.x)
					foundHere |= Constants.DOWN;
				else
					foundHere |= Constants.UP;
				if (dest.y > location.y)
					foundHere |= Constants.RIGHT;
				else
					foundHere |= Constants.LEFT;
				o.direction = foundHere;
				o.distance = Math.abs(dest.x - location.x);
				// go through the path and make sure that there are no blocks in the way
				if (emptyWay(location, o))
					return o;
				else // no data is available
					return null;
			}
		else // no data is available
			return null;
	}

	/**
	 * checks if there is empty way between the fox and the observation received
	 * @param o the observation having the data
	 * @return true if the way is empty between the fox and the rabbit; false otherwise
	 */
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

	/**
	 * moves the fox withe 
	 * @param direction the desired direction
	 * @param step
	 * @return true if the move is accomplished successfully; else returns false
	 */
	public boolean move(int direction, int step) {
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

	/**
	 * updates the observer with the new location of the fox
	 * @param lastSeen
	 */
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