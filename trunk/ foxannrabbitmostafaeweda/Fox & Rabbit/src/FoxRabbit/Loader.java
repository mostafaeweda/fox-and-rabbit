package FoxRabbit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Mostafa Mahmod Mahmod Eweda
 * A loader class that is used to load plug-in classes from the jar of the project using a properties file
 * that contain data about their creation.
 * @version 1.0
 * @see FoxRabbitUI
 * @since JDK 1.6
 */
public class Loader {

	/**
	 * table containing the loaded classes and their related data
	 */
	private Hashtable<String, CompositeClass<AbstractBoard>> classes;

	/**
	 * list of the loaded classes 
	 */
	private ArrayList<String> classNames;

	/**
	 * creates a loader that loads the related data from a properties file
	 * @param properties the path of the properties file to load the data from
	 */
	@SuppressWarnings("unchecked")
	public Loader(String properties) {
		classes = new Hashtable<String, CompositeClass<AbstractBoard>>(10);
		classNames = new ArrayList<String>();
		try {
			InputStream i = Loader.class.getResourceAsStream(properties);
			BufferedReader in = new BufferedReader(new InputStreamReader(i));
			String load;;
			while ((load = in.readLine()) != null) {
				String image = load.substring(load.indexOf('-')+1, load.lastIndexOf('-'));
				String toolTip  = load.substring(load.lastIndexOf('-')+1);
				load = load.substring(0, load.indexOf('-'));
				Class<AbstractBoard> loaded = (Class<AbstractBoard>) Class.forName("FoxRabbit."+load);
				Constructor<AbstractBoard> constructor = loaded.getConstructor(int.class, int.class);
				Constructor<AbstractBoard> fileConstructor = loaded.getConstructor(String.class);
				CompositeClass<AbstractBoard> compo = new CompositeClass<AbstractBoard>(constructor,
						fileConstructor, image, toolTip);
				classNames.add(load);
				classes.put(load, compo);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the names of the classes loaded by the loader
	 */
	public ArrayList<String> getClasses() {
		return classNames;
	}

	/**
	 * 
	 * @param className the name of the class to get the data to
	 * @return the composite class having the needed data to construct the class
	 */
	public CompositeClass<AbstractBoard> getData(String className) {
		return classes.get(className);
	}

	/**
	 * Grouping class that holds the needed data to the construction class(es)
	 * @author Mostafa Mahmoud Mahmoud Eweda
	 *
	 * @param <E> the type of the class or interface for which the composite class should hold data from
	 */
	class CompositeClass <E> {
		public Constructor<E> construct;
		public Constructor<E> fileConstructor;
		public String imgPath;
		public String defintion;

		/**
		 * creates a composite class with the given data
		 * @param construct the constructor of the class
		 * @param fileConstructor the constructor that depends on a file to load the data from
		 * @param imgPath the path of the image
		 * @param text the definition of the class
		 */
		public CompositeClass(Constructor<E> construct, Constructor<E> fileConstructor,
				String imgPath, String text) {
			this.construct = construct;
			this.imgPath = imgPath;
			this.fileConstructor = fileConstructor;
			this.defintion = text;
		}
	}
}
