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

	private Hashtable<String, CompositeClass<AbstractBoard>> classes;
	private ArrayList<String> classNames;

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

	public ArrayList<String> getClasses() {
		return classNames;
	}

	public CompositeClass<AbstractBoard> getData(String className) {
		return classes.get(className);
	}

	class CompositeClass <E> {
		public Constructor<E> construct;
		public Constructor<E> fileConstructor;
		public String imgPath;
		public String toolTip;

		public CompositeClass(Constructor<E> construct, Constructor<E> fileConstructor,
				String imgPath, String toolTip) {
			this.construct = construct;
			this.imgPath = imgPath;
			this.fileConstructor = fileConstructor;
			this.toolTip = toolTip;
		}
	}
}
