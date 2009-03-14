package FoxRabbit;

import java.util.Observer;

import org.eclipse.swt.graphics.Point;

/**
 * Grouping superclass that is used to combine common and related behavior and
 * attributes need by the Fox and rabbit classes. Can also be subclassed to
 * provide new animal and its behavior to logically expand the game.
 * 
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Animal
 * @see Rabbit
 * @since JDK 1.6
 */
abstract class Animal {

	/**
	 * The board the animal is moving on
	 */
	protected AbstractBoard board;

	/**
	 * The location of the animal on board
	 */
	protected Point location;

	/**
	 * The observer the animal should notify when an event occurs to it
	 * @see Observer
	 */
	protected Observer observer;

	/**
	 * Creates an animal but this constructor isn't a stand-alone constructor -->
	 * it is called from the subclasses constructor
	 * @param board the board to move on
	 * @see #board
	 * @param observer the notifying observer
	 * @see Animal#observer
	 */
	public Animal(AbstractBoard board, Observer observer) {
		this.observer = observer;
		this.board = board;
	}
}