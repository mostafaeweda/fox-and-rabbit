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

	protected AbstractBoard board;

	protected Point location;

	protected Observer observer;

	public Animal(AbstractBoard board, Observer observer) {
		this.observer = observer;
		this.board = board;
	}
}