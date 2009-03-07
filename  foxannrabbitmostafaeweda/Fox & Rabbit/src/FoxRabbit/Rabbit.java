package FoxRabbit;
import java.util.Observer;
import java.util.Random;

import org.eclipse.swt.graphics.Point;

/**
 * Class to represents the motion of the rabbit around the array and mainly controlled
 * by the game player through the user interfacing --> mainly escaping from foxes the min number needed to win
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Fox
 * @since JDK 1.6
 */
public class Rabbit extends Animal {

	private int escapes;

	/**
	 * @param board the board on which the rabbit will be put on
	 * @param observer the observer that desires notification when the rabbit wins (exceeds the minimum
	 *  number of moves needed to win)
	 *  The rabbit is located randomly on the board
	 */
	public Rabbit(AbstractBoard board, Observer observer) {
		super(board, observer);
		escapes = 0;
		int x, y;
		Random random = new Random();
		do {
			x = Math.abs(random.nextInt()) % board.size;
			y = Math.abs(random.nextInt()) % board.size;
		} while (board.getAt(x, y) != Constants.EMPTY);
		location = new Point(x, y);
		board.put(x, y, Constants.RABBIT);
		board.setRabbit(this);
	}

	/** Moves the rabbit with the user provided new point
	 * @param newLocation the new loaction provided by the user as a point
	 */
	public void move(Point newLocation) {
		board.move(location, newLocation);
		location = newLocation;
		if (++escapes == Constants.MAX_ESCAPES)
			observer.update(null, "win");
		// TODO : notify wining
	}

	/**
	 * @return the number of escapes done by the rabbit till the time of the calling
	 */
	public int getEscapes() {
		return escapes;
	}
}
