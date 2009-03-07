package FoxRabbit;
import java.util.Observable;

import org.eclipse.swt.graphics.Point;

/**
 * An abstract class that holds the common characteristics between the different implementations of the board.
 *    The application can be developed later by providing appropriate subclasses of thus class
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see StandardBoard
 * @see TriangularBoard
 * @see BoundedBoard
 * @since JDK 1.6
 */
public abstract class AbstractBoard extends Observable {

	/**
	 * the data that holds the locations of the foxes and rabbits
	 */
	protected byte[] data;

	/**
	 * The rabbit of the game
	 */
	protected Rabbit rabbit;

	/**
	 * the size of the equivalent 2-dimensional array representation of the board
	 */
	protected int size;

	public AbstractBoard() {
	}

	/**
	 * sets the current rabbit at the board at a specified location
	 * @param rabbit
	 */
	public void setRabbit(Rabbit rabbit) {
		this.rabbit = rabbit;
	}

	/**
	 * @param x the x location of the data to be put
	 * @param y the y location of the data to be put
	 * @return the data contained at the specified location with the point x and y
	 */
	public abstract byte getAt(int x, int y);

	/**
	 * puts the data in the specified location
	 * @param x the x location of the data to be put
	 * @param y the x location of the data to be put
	 * @param data the data to be hosted
	 */
	public abstract void put(int x, int y, byte data);

	/**
	 * moves data from one location to another
	 * @param oldLocation the old location of the data
	 * @param newLocation the new location of the data
	 */
	public final void move(Point oldLocation, Point newLocation) {
		put(newLocation.x, newLocation.y, getAt(oldLocation.x, oldLocation.y));
		put(oldLocation.x, oldLocation.y, Constants.EMPTY);
	}
}
