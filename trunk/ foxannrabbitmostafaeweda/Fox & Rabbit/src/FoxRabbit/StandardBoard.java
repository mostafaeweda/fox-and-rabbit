package FoxRabbit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * 2 dimensional array representation
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Animal
 * @see Rabbit
 * @since JDK 1.6
 */
public class StandardBoard extends AbstractBoard {

	/**
	 * Takes a file that contains a predetermined size and data of the array represented as 2- dimensional array.
	 * Just filling the array with the file contents.
	 * @param stylePath
	 */
	public StandardBoard(String stylePath) {
		super();
		try {
			BufferedReader in = new BufferedReader(new FileReader(stylePath));
			size = Integer.parseInt(in.readLine());
			data = new byte[size * size];
			int i = 0, j, k = 0;
			String[] temp;
			while (i++ < size) {
				temp = in.readLine().split(" ");
				j = 0;
				while (j < size) {
					if (data[k] != -1)
						data[k++] = Byte.parseByte(temp[j++]);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Randomly distribute the blocks and empty spaces on the board
	 * @param size the desired size of the board
	 * @param blockNum the number of blocks desired to be put in the array
	 */
	public StandardBoard(int size, int blockNum) {
		data = new byte[size * size];
		this.size = size;
		int i = 0;
		int x, y;
		Random gen = new Random();
		while (i++ < blockNum) {
			do {
				x = Math.abs(gen.nextInt()) % size;
				y = Math.abs(gen.nextInt()) % size;
			} while (getAt(x, y) != Constants.EMPTY);
			put(x, y, Constants.BLOCK);
		}
	}

	/**
	 * @param x the x location of the data to be put
	 * @param y the y location of the data to be put
	 * @return the data contained at the specified location with the point x and y
	 */
	@Override
	public byte getAt(int x, int y) {
		if (x >= size || x < 0 || y >= size || y < 0)
			return -1;
		return data[x * size + y];
	}

	/**
	 * puts the data in the specified location
	 * @param x the x location of the data to be put
	 * @param y the x location of the data to be put
	 * @param data the data to be hosted
	 */
	@Override
	public void put(int x, int y, byte data) {
		this.data[x * size + y] = data;
	}
}
