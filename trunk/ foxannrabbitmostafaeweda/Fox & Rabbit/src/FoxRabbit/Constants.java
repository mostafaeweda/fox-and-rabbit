package FoxRabbit;

/**
 * Constant interface that defines common constants needed to manipulate data representation in
 * {@link AbstractBoard} representations of arrays.
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Animal
 * @see Rabbit
 * @since JDK 1.6
 */
public interface Constants {

	/**
	 * max number of escapes the rabbit could be still eaten by the fox
	 * After this number of escapes, the rabbit wins
	 */
	byte MAX_ESCAPES = 5;

	/**
	 * Block code representation in the array
	 */
	byte BLOCK = 1;

	/**
	 * Animal representation --> used mainly to be bitwised to check the availability of the next movement.
	 * e.g. Animal 12 = 1100, Rabbit 14 = 1110, Fox 15 = 1111; contains the first tow bits alike to 
	 * distinguish them from other data
	 * @see Animal 
	 */
	byte ANIMAL = 12;

	/**
	 * Rabbit code representation in the array
	 * @see Rabbit
	 */
	byte RABBIT = 14;

	/**
	 * Rabbit code representation in the array
	 * @see Fox
	 */
	byte FOX = 15;

	/**
	 * code representation for empty location in the array
	 * @see Rabbit
	 */
	byte EMPTY = 0;

	/**
	 * moving up if allowed
	 */
	int UP = 1 << 4;

	/**
	 * moving left if allowed
	 */
	int LEFT = 1 << 5;

	/**
	 * moving right if allowed
	 */
	int RIGHT = 1 << 6;

	/**
	 * moving down if allowed
	 */
	int DOWN = 1 << 7;

	/**
	 * The normal rabbit step; also it is considered the fox step when it doesn't see the rabbit
	 */
	int NORMAL_STEP = 1;

	/**
	 * The fox step when it sees the rabbit
	 */
	int FOX_STEP = 5;

	/**
	 * The mode of operation to load a file that contains the data representation
	 */
	int STYLE_FILE = 1 << 8;

	/**
	 * The mode of operation to select preferences and generate the locations randomly
	 */
	int CHOOSEN_ATTRIBUTES = 1 << 9;
}
