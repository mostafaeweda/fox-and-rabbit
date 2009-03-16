package FoxRabbit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import FoxRabbit.Fox.Observation;
import FoxRabbit.Loader.CompositeClass;

/**
 * Graphical representation of the game manipulated by arrays data structures
 * Works as a view and controller of that manipulates the model layer of
 * abstraction The class is internally implemented to oscillate between the
 * windows as the state pattern to provide one window handling to all user
 * actions without the need to external dialogues
 * 
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see AbstractBoard
 * @see Fox
 * @see Rabbit
 * @since JDK 1.6
 */
public class FoxRabbitUI implements Observer {

	/**
	 * private instance of the class to support the singleton pattern
	 */
	private static FoxRabbitUI instance;

	private Shell shell;
	private Display display;
	private AbstractBoard board;
	private Loader loader;
	private Composite composite;
	private int size;
	private Rabbit rabbit;
	private Fox[] foxes;
	private Label[][] view;
	private Image rabbitImg, foxImg, blankImg, whiteImg, activeImg;
	private Font font;
	private String style = "style5.txt";
	private Image startUpImg;
	private Image customBackGround;
	private Cursor rabbitCursor;
	private Cursor standardCursor;
	private Image grass;
	private boolean previouslyHovered = false;
	private int constructMode = Constants.CHOOSEN_ATTRIBUTES;
	private Image looseAnimate;

	/**
	 * Implementation of the singleton pattern to be represented as a facade and
	 * get the same instance of the class to process internal logic of
	 * interfacing
	 * 
	 * @return the same instance of the FoxRabbitUI class
	 */
	public static synchronized FoxRabbitUI getInstance() {
		if (instance == null)
			return instance = new FoxRabbitUI();
		return instance;
	}

	private FoxRabbitUI() {
		loader = new Loader("foxandrabbit.properties");
	}

	private void init() {
		rabbit = new Rabbit(board, this);
		size = board.size;
	}

	/**
	 * manages all the creation of the first time use application user interface
	 */
	public void run() {
		display = new Display();
		Rectangle rect = display.getBounds();
		System.out.println(rect);
		standardCursor = display.getSystemCursor(SWT.CURSOR_ARROW);
		font = new Font(display, "Comic Sans MS", 16, SWT.BOLD); // font of the
		// most of
		// the
		// application
		// controls
		/* Background of the main game windowing */
		customBackGround = new Image(display, new ImageData(FoxRabbitUI.class
				.getResourceAsStream("foxbackground.jpg")).scaledTo(rect.width,
				rect.height));
		/* The land on which the rabbit party when winning */
		grass = new Image(display, new ImageData(FoxRabbitUI.class
				.getResourceAsStream("grass.jpg")).scaledTo(rect.width,
				rect.height));
		shell = new Shell(display, SWT.NO_TRIM);
		createContents();
		// dispose resources and exit the system when the shell is disposed
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
				System.exit(0);
			}
		});
		shell.setMaximized(true);
		shell.setFullScreen(true);
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}

	/**
	 * dispose the ui system to release system resources
	 */
	private void dispose() {
		font.dispose();
		if (rabbitImg != null) {
			grass.dispose();
			rabbitCursor.dispose();
			startUpImg.dispose();
			rabbitImg.dispose();
			foxImg.dispose();
			whiteImg.dispose();
			blankImg.dispose();
			display.dispose();
		}
	}

	/**
	 * create the contents of the UI
	 */
	private void createContents() {
		shell.setLayout(new FillLayout());
		composite = startUpComposite2();
	}

	/**
	 * @return the custom composite of the game
	 */
	private Composite customComposite() {
		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackgroundImage(customBackGround);
		GridLayout gridLayout = new GridLayout(size, true);
		gridLayout.horizontalSpacing = 2;
		gridLayout.verticalSpacing = 2;
		int y = (shell.getSize().y - 2 * board.size) / board.size;
		int x = (shell.getSize().x - 2 * board.size) / board.size;
		composite.setLayout(gridLayout);
		ImageData data = new ImageData(FoxRabbitUI.class
				.getResourceAsStream("rabbit.png")).scaledTo(x, y);
		data.transparentPixel = data.getPixel(0, 0);
		rabbitCursor = new Cursor(display, data, data.width / 2,
				data.height / 2);
		rabbitImg = new Image(display, new ImageData(FoxRabbitUI.class
				.getResourceAsStream("rabbit.png")).scaledTo(x, y));
		foxImg = new Image(display, new ImageData(FoxRabbitUI.class
				.getResourceAsStream("fox.png")).scaledTo(x, y));
		// white image to represent empty places
		whiteImg = new Image(display, x, y);
		GC gc = new GC(whiteImg);
		gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		gc.fillRectangle(whiteImg.getBounds());
		gc.dispose();
		// block image to represent blocks
		blankImg = new Image(display, x, y);
		gc = new GC(blankImg);
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.fillRectangle(blankImg.getBounds());
		gc.dispose();
		// block image to represent active next available location
		activeImg = new Image(display, whiteImg, SWT.IMAGE_COPY);
		gc = new GC(activeImg);
		gc.setLineWidth(5);
		gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
		gc.drawRectangle(activeImg.getBounds());
		gc.dispose();
		view = new Label[size][size];
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view.length; j++) {
				final Label label = new Label(composite, SWT.NONE);
				label.setData(new Point(i, j));
				view[i][j] = label;
				label.addMouseTrackListener(new MouseTrackAdapter() {
					@Override
					public void mouseEnter(MouseEvent e) {
						Point point = (Point) label.getData();
						int xDiff = Math.abs(point.x - rabbit.location.x), yDiff = Math
								.abs(point.y - rabbit.location.y);
						if (xDiff <= 1
								&& yDiff <= 1
								&& board.getAt(point.x, point.y) == Constants.EMPTY) {
							label.setImage(activeImg);
							if (! rabbitCursor.isDisposed())
								label.setCursor(rabbitCursor);
						}
					}

					@Override
					public void mouseExit(MouseEvent e) {
						Point point = (Point) label.getData();
						if (board.getAt(point.x, point.y) == Constants.EMPTY) {
							label.setImage(whiteImg);
							label.setCursor(standardCursor);
						}
					}
				});
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						Point point = (Point) label.getData();
						if (label.getImage() == activeImg) {
							rabbit.move(point);
							Observation o = null;
							for (int i = 0; i < foxes.length && o == null; i++)
								o = foxes[i].action();
							if (o != null) {
								for (int i = 0; i < foxes.length; i++)
									foxes[i].update(o.location);
							}
							for (int i = 0; i < foxes.length; i++)
								foxes[i].move();
							if (!label.isDisposed())
								refresh();
						}
					}
				});
			}
		}
		refresh();
		final TraverseListener traverser = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ESC) {
					composite.dispose();
					FoxRabbitUI.this.composite = startUpComposite2();
					shell.layout();
					shell.redraw();
				}
			}
		};
		shell.addTraverseListener(traverser);
		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				shell.removeTraverseListener(traverser);
				previouslyHovered = false;
				rabbitImg.dispose();
				foxImg.dispose();
				blankImg.dispose();
				whiteImg.dispose();
				activeImg.dispose();
				rabbitCursor.dispose();
			}
		});
		return composite;
	}

	/**
	 * refresh the view of fox, rabbit and blocks
	 */
	private void refresh() {
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view.length; j++) {
				int k = board.getAt(i, j);
				if (k == Constants.BLOCK)
					view[i][j].setImage(blankImg);
				else if (k == Constants.EMPTY)
					view[i][j].setImage(whiteImg);
				else if (k == Constants.FOX) {
					view[i][j].setImage(foxImg);
				} else if (k == Constants.RABBIT) {
					view[i][j].setImage(rabbitImg);
				}
				// else
				// view[i][j].setVisible(false);// else :: leave it empty
			}
		}
	}

	private Composite startUpComposite2() {
		FormData data;
		final Color oneHot = new Color(display, 255, 111, 32);
		final Color twoHot = new Color(display, 236, 198, 177);
		final Color threeHot = new Color(display, 255, 111, 32);
		final Color oneNormal = new Color(display, 105, 103, 101);
		final Color twoNormal = new Color(display, 0, 0, 0);
		final Color threeNormal = new Color(display, 105, 101, 101);
		final Color frontHot = display.getSystemColor(SWT.COLOR_BLACK);
		final Color frontNormal = display.getSystemColor(SWT.COLOR_WHITE);
		final Color green = display.getSystemColor(SWT.COLOR_GREEN);
		final Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		final Color boardItemHot = new Color(display, 21, 8, 42);
		final Image transientBackground;
		ImageData arrowDat = new ImageData(FoxRabbitUI.class
				.getResourceAsStream("big arrow.png")).scaledTo(80, 80);
		arrowDat.transparentPixel = arrowDat.getPixel(0, 0);
		final Cursor bigArrow = new Cursor(display, arrowDat, 0, 0);

		MouseTrackListener tracker = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				CLabel label = (CLabel) e.widget;
				label.setBackground(new Color[] { oneHot, twoHot, threeHot },
						new int[] { 50, 100 });
				label.setForeground(frontHot);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				CLabel label = (CLabel) e.widget;
				label.setBackground(new Color[] { oneNormal, twoNormal,
						threeNormal }, new int[] { 50, 100 });
				label.setForeground(frontNormal);
			}
		};

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FormLayout());
		composite.setCursor(bigArrow);
		composite.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
		/*
		 * CLabel newGame = new CLabel(composite, SWT.NONE);
		 * newGame.setAlignment(SWT.CENTER); newGame.setBackground(new Color[] {
		 * display.getSystemColor(SWT.COLOR_DARK_GRAY),
		 * display.getSystemColor(SWT.COLOR_BLACK),
		 * display.getSystemColor(SWT.COLOR_DARK_GRAY) }, new int[] { 50, 100
		 * }); newGame.setText("New Game"); newGame.setFont(font);
		 * newGame.setForeground(frontNormal); newGame.addMouseListener(new
		 * MouseAdapter() { public void mouseDown(MouseEvent e) {
		 * composite.dispose(); customComposite(); shell.layout(); } });
		 * newGame.addMouseTrackListener(tracker); data = new FormData();
		 * data.left = new FormAttachment(0, 20); data.top = new
		 * FormAttachment(0, 100); data.height = 150; data.width = 200;
		 * newGame.setLayoutData(data);
		 */

		CLabel exit = new CLabel(composite, SWT.NONE);
		exit.setAlignment(SWT.CENTER);
		exit.setBackground(new Color[] {
				display.getSystemColor(SWT.COLOR_DARK_GRAY),
				display.getSystemColor(SWT.COLOR_BLACK),
				display.getSystemColor(SWT.COLOR_DARK_GRAY) }, new int[] { 50,
				100 });
		exit.setText("Exit");
		exit.setFont(font);
		exit.setForeground(frontNormal);
		exit.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				shell.dispose();
			}
		});
		exit.addMouseTrackListener(tracker);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, -100);
		data.height = 150;
		data.width = 320;
		exit.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 100);
		data.bottom = new FormAttachment(exit, 0);
		data.width = 320;
		final Composite boards = new Composite(composite, SWT.NONE);
		boards.setBackground(display.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		Rectangle displayBounds = display.getBounds();
		boards.setData("bounds", new Rectangle(0, 250, 300,
				displayBounds.height - 500));
		boards.setLayoutData(data);
		boards.setLayout(new GridLayout(1, true));

		data = new FormData();
		data.left = new FormAttachment(boards, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		final Canvas canvas = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		boards.setData("canvas", canvas);
		final Rectangle canvasBounds = new Rectangle(300, 0,
				displayBounds.width - 320, displayBounds.height);
		boards.setData("canvas bounds", canvasBounds);
		/* Background of the start up windowing */
		startUpImg = new Image(display, new ImageData(FoxRabbitUI.class
				.getResourceAsStream("start.jpg")).scaledTo(canvasBounds.width,
				canvasBounds.height));
		transientBackground = new Image(display, new ImageData(
				FoxRabbitUI.class.getResourceAsStream("fox and rabbit 3.png"))
				.scaledTo(canvasBounds.width, canvasBounds.height));
		final Point rabbitLocaton = new Point(0, 0);
		final Point foxLocaton = new Point(canvasBounds.width - 100, 0);
		ImageData imgDat = new ImageData(FoxRabbitUI.class
				.getResourceAsStream("rabbit.png")).scaledTo(100, 100);
//		imgDat.transparentPixel = imgDat.getPixel(0, 0);
		final Image rabbitRun = new Image(display, imgDat);
		imgDat = new ImageData(FoxRabbitUI.class.getResourceAsStream("fox.png"))
				.scaledTo(100, 100);
		imgDat.transparentPixel = imgDat.getPixel(0, 0);
		final Image foxRun = new Image(display, imgDat);
		boards.setData("do animate", new Boolean(false));
		canvas.setLayoutData(data);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		canvas.addPaintListener(new PaintListener() {
			private int startX = 100;
			private int startY = 100;

			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				Image chosenImg = (Image) boards.getData("choosen");
				if (chosenImg != null) {
					gc.drawImage(chosenImg, startX, startY);
					if (((Boolean) boards.getData("do animate")).booleanValue()) {
						gc.drawImage(foxRun, foxLocaton.x, foxLocaton.y);
						gc.drawImage(rabbitRun, rabbitLocaton.x,
								rabbitLocaton.y);
					}
				} else {
					if (previouslyHovered)
						gc.drawImage(transientBackground, 0, 0);
					else
						gc.drawImage(startUpImg, 0, 0);
				}
				gc.setLineWidth(20);
				gc.drawRectangle(canvas.getClientArea());
			}
		});

		GridData gridData = new GridData(GridData.FILL_BOTH);
		ArrayList<String> classes = loader.getClasses();
		Iterator<String> iter = classes.iterator();

		while (iter.hasNext()) {
			String className = iter.next();
			CLabel label = new CLabel(boards, SWT.NONE);
			final CompositeClass<AbstractBoard> compo = loader
					.getData(className);
			int width = canvasBounds.width - 200;
			int height = canvasBounds.height - 200;
			final Image image = new Image(display, new ImageData(
					FoxRabbitUI.class.getResourceAsStream(compo.imgPath))
					.scaledTo(width, height));
			label.setLayoutData(new GridData(GridData.FILL_BOTH));
			label.setText(compo.defintion);
			label.setLayoutData(gridData);
			label.setBackground(new Color[] { frontNormal, boardItemHot,
					frontNormal }, new int[] { 50, 100 });
			label.setFont(font);
			label.setAlignment(SWT.CENTER);
			label.addMouseTrackListener(new MouseTrackAdapter() {
				private FoxRabbitRun runner = new FoxRabbitRun(boards,
						rabbitLocaton, foxLocaton);

				@Override
				public void mouseEnter(MouseEvent e) {
					previouslyHovered = true;
					CLabel label = (CLabel) e.widget;
					label.setBackground(new Color[] { green, blue, green },
							new int[] { 50, 100 });
					boards.setData("choosen", image);
					boards.setData("do animate", new Boolean(true));
					display.timerExec(1000, runner);
					((Canvas) boards.getData("canvas")).redraw();
				}

				@Override
				public void mouseExit(MouseEvent e) {
					boards.setData("choosen", null);
					display.timerExec(-1, runner);
					boards.setData("do animate", new Boolean(false));
					runner.reset();
					canvas.redraw();
					CLabel label = (CLabel) e.widget;
					label.setBackground(new Color[] { frontNormal,
							boardItemHot, frontNormal }, new int[] { 50, 100 });
				}
			});
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					try {
						// construct the appropriate selected kind of board
						if (constructMode == Constants.STYLE_FILE)
							board = compo.fileConstructor.newInstance(style);
						else if (constructMode == Constants.CHOOSEN_ATTRIBUTES) {
							Combo sizes = (Combo) composite.getData("sizes");
							Combo blocks = (Combo) composite.getData("blocks");
							board = compo.construct.newInstance(Integer
									.parseInt(sizes.getItem(sizes
											.getSelectionIndex())), Integer
									.parseInt(blocks.getItem(blocks
											.getSelectionIndex())));
						}
						Combo foxes = (Combo) composite.getData("foxes");
						int foxesNumber = Integer.parseInt(foxes.getItem(foxes
								.getSelectionIndex()));
						FoxRabbitUI.this.foxes = new Fox[foxesNumber];
						for (int i = 0; i < foxesNumber; i++)
							FoxRabbitUI.this.foxes[i] = new Fox(board,
									FoxRabbitUI.this);
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
					composite.dispose();
					init();
					FoxRabbitUI.this.composite = customComposite();
					shell.layout();
					shell.redraw();
				}
			});
		}

		ExpandBar expandBar = new ExpandBar(boards, SWT.V_SCROLL);
		expandBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		expandBar.setFont(font);
		expandBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		expandBar.setForeground(display.getSystemColor(SWT.COLOR_BLUE));

		Composite controls = new Composite(expandBar, SWT.NONE);
		controls.setLayout(new GridLayout(2, false));
		CLabel sizeLabel = new CLabel(controls, SWT.NONE);
		sizeLabel.setFont(font);
		sizeLabel.setText("Board Size");
		GridData gridDat = new GridData();
		gridDat.widthHint = 50;
		final Combo sizes = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
		composite.setData("sizes", sizes);
		sizes.setFont(font);
		sizes.setLayoutData(gridDat);
		CLabel blocksLabel = new CLabel(controls, SWT.NONE);
		blocksLabel.setFont(font);
		blocksLabel.setText("Blocks number");
		final Combo blocks = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
		composite.setData("blocks", blocks);
		blocks.setLayoutData(gridDat);
		blocks.setFont(font);

		CLabel foxesLabel = new CLabel(controls, SWT.NONE);
		foxesLabel.setFont(font);
		foxesLabel.setText("Foxes number");
		final Combo foxes = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
		composite.setData("foxes", foxes);
		foxes.setLayoutData(gridDat);
		foxes.setFont(font);

		ExpandItem expandItem = new ExpandItem(expandBar, SWT.NONE);
		expandItem.setText("Preferences");
		expandItem.setControl(controls);
		expandItem.setHeight(controls.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		expandItem.setExpanded(true);

		int i = 0;
		while (i++ <= 20) {
			sizes.add(i + "");
		}
		sizes.select(4);
		i = 0;
		while (i++ <= 10) {
			blocks.add(i + "");
		}
		blocks.select(4);
		i = 0;
		while (i++ < 4)
			foxes.add(i + "");
		foxes.select(0);
		sizes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int size = sizes.getSelectionIndex() + 1;
				int allowedBlocks = size * 3 - 5;
				blocks.removeAll();
				int i = 0;
				while (i++ <= allowedBlocks) {
					blocks.add(i + "");
				}
				blocks.select(0);
				int allowedFoxes = size / 2;
				foxes.removeAll();
				i = 0;
				while (i++ < allowedFoxes)
					foxes.add(i + "");
				foxes.select(0);
			}
		});

		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				transientBackground.dispose();
				bigArrow.dispose();
				oneHot.dispose();
				oneNormal.dispose();
				twoHot.dispose();
				twoNormal.dispose();
				threeHot.dispose();
				threeNormal.dispose();
			}
		});

		return composite;
	}

	private class FoxRabbitRun implements Runnable {
		private static final int STEP = 4;
		private Canvas canvas;
		private boolean down = true;
		private Point rabbitLocaton;
		private Rectangle canvasBounds;
		private Point foxLocaton;
		private Composite boards;
		private boolean soundStarted = false;
		private Player player;

		public FoxRabbitRun(Composite boards, Point rabbitLocation,
				Point foxLocation) {
			this.boards = boards;
			canvas = (Canvas) boards.getData("canvas");
			canvasBounds = (Rectangle) boards.getData("canvas bounds");
			this.rabbitLocaton = rabbitLocation;
			this.foxLocaton = foxLocation;
		}

		public void reset() {
			rabbitLocaton.x = 0;
			rabbitLocaton.y = 0;
			foxLocaton.x = canvasBounds.width - 100;
			foxLocaton.y = 0;
		}

		@Override
		public void run() {
			if (down) {
				rabbitLocaton.y += STEP;
				foxLocaton.y += STEP;
			} else {
				rabbitLocaton.x += STEP;
				foxLocaton.x -= STEP;
				if (foxLocaton.x - rabbitLocaton.x <= 300) {
					if (!soundStarted) {
						player = startMedia("loose.wav");
						soundStarted = true;
					}
					if (foxLocaton.x - rabbitLocaton.x <= 0) {
						boards.setData("do animate", new Boolean(false));
						reset();
					}
				}
			}
			if (rabbitLocaton.y >= canvasBounds.height - 100) {
				rabbitLocaton.y = canvasBounds.height - 100;
				foxLocaton.y = canvasBounds.height - 100;
				down = false;
			}
			if (canvas.isDisposed()) {
				display.timerExec(-1, this);
				if (player != null)
					player.stop();
			} else {
				canvas.redraw();
				display.timerExec(10, this);
			}
		}
	}

	/**
	 * @return the startup composite of the game
	 */
	private Composite startUpComposite() {
		ArrayList<String> classes = loader.getClasses();
		FormData data;

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackgroundImage(startUpImg);
		composite.setLayout(new FormLayout());

		// exit item
		CLabel exit = new CLabel(composite, SWT.NONE);
		exit.setAlignment(SWT.CENTER);
		exit.setBackground(new Color[] {
				display.getSystemColor(SWT.COLOR_DARK_GRAY),
				display.getSystemColor(SWT.COLOR_BLACK),
				display.getSystemColor(SWT.COLOR_DARK_GRAY) }, new int[] { 50,
				100 });
		exit.setText("Exit");
		exit.setFont(font);
		exit.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		exit.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				shell.dispose();
			}
		});
		data = new FormData();
		data.left = new FormAttachment(85, 0);
		data.top = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, 0);
		data.height = 150;
		exit.setLayoutData(data);

		// boards chooser
		Composite boards = new Composite(composite, SWT.NONE);
		ExpandBar expandBar = new ExpandBar(composite, SWT.V_SCROLL);
		expandBar.setFont(font);
		expandBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		expandBar.setForeground(display.getSystemColor(SWT.COLOR_BLUE));

		Composite controls = new Composite(expandBar, SWT.NONE);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(80, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		expandBar.setLayoutData(data);
		controls.setLayout(new GridLayout(2, false));
		CLabel sizeLabel = new CLabel(controls, SWT.NONE);
		sizeLabel.setFont(font);
		sizeLabel.setText("Board Size");
		GridData gridDat = new GridData();
		gridDat.widthHint = 50;
		final Combo sizes = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
		sizes.setFont(font);
		sizes.setLayoutData(gridDat);
		CLabel blocksLabel = new CLabel(controls, SWT.NONE);
		blocksLabel.setFont(font);
		blocksLabel.setText("Blocks number");
		final Combo blocks = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
		blocks.setLayoutData(gridDat);
		blocks.setFont(font);

		CLabel foxesLabel = new CLabel(controls, SWT.NONE);
		foxesLabel.setFont(font);
		foxesLabel.setText("Foxes number");
		final Combo foxes = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
		foxes.setLayoutData(gridDat);
		foxes.setFont(font);

		ExpandItem expandItem = new ExpandItem(expandBar, SWT.NONE);
		expandItem.setText("Preferences");
		expandItem.setControl(controls);
		expandItem.setHeight(controls.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		expandItem.setExpanded(true);

		int i = 0;
		while (i++ <= 20) {
			sizes.add(i + "");
		}
		sizes.select(4);
		i = 0;
		while (i++ <= 10) {
			blocks.add(i + "");
		}
		blocks.select(4);
		i = 0;
		while (i++ < 4)
			foxes.add(i + "");
		foxes.select(0);
		sizes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int size = sizes.getSelectionIndex() + 1;
				int allowedBlocks = size * 3 - 5;
				blocks.removeAll();
				int i = 0;
				while (i++ <= allowedBlocks) {
					blocks.add(i + "");
				}
				blocks.select(0);
				int allowedFoxes = size / 2;
				foxes.removeAll();
				i = 0;
				while (i++ < allowedFoxes)
					foxes.add(i + "");
				foxes.select(0);
			}
		});

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(35, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(80, 0);
		boards.setLayoutData(data);
		boards.setLayout(new GridLayout(classes.size(), true));
		Iterator<String> iter = classes.iterator();
		int width = display.getBounds().width / classes.size();
		int height = display.getBounds().height / 2;
		while (iter.hasNext()) {
			String className = iter.next();
			Label label = new Label(boards, SWT.NONE);
			final CompositeClass<AbstractBoard> compo = loader
					.getData(className);
			Image image = new Image(display, new ImageData(FoxRabbitUI.class
					.getResourceAsStream(compo.imgPath))
					.scaledTo(width, height));
			label.setImage(image);
			label.setLayoutData(new GridData(GridData.FILL_BOTH));
			label.setToolTipText(compo.defintion);
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					try {
						// construct the appropriate selected kind of board
						if (constructMode == Constants.STYLE_FILE)
							board = compo.fileConstructor.newInstance(style);
						else if (constructMode == Constants.CHOOSEN_ATTRIBUTES)
							board = compo.construct.newInstance(Integer
									.parseInt(sizes.getItem(sizes
											.getSelectionIndex())), Integer
									.parseInt(blocks.getItem(blocks
											.getSelectionIndex())));
						int foxesNumber = Integer.parseInt(foxes.getItem(foxes
								.getSelectionIndex()));
						FoxRabbitUI.this.foxes = new Fox[foxesNumber];
						for (int i = 0; i < foxesNumber; i++)
							FoxRabbitUI.this.foxes[i] = new Fox(board,
									FoxRabbitUI.this);
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
					composite.dispose();
					init();
					FoxRabbitUI.this.composite = customComposite();
					shell.layout();
					shell.redraw();
				}
			});
		}
		return composite;
	}

	/**
	 * Processed state when the uses pauses the game pressing escape
	 * 
	 * @return the resulting composite
	 */
	private Composite escapeComposite() {
		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FormLayout());
		final Composite controls = new Composite(composite, SWT.NONE);
		FormData data = new FormData();
		data.left = new FormAttachment(30, 0);
		data.top = new FormAttachment(30, 0);
		data.right = new FormAttachment(70, 0);
		data.bottom = new FormAttachment(70, 0);
		controls.setLayoutData(data);
		controls.setFont(font);
		controls.setLayout(new GridLayout(1, true));
		Button newGame = new Button(controls, SWT.PUSH);
		newGame.setLayoutData(new GridData(GridData.FILL_BOTH));
		newGame.setText("New Game");
		newGame.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				composite.dispose();

				init();
				FoxRabbitUI.this.composite = customComposite();
				shell.layout();
				shell.redraw();
			}
		});
		Button resume = new Button(controls, SWT.PUSH);
		resume.setLayoutData(new GridData(GridData.FILL_BOTH));
		resume.setText("Resume");
		resume.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				composite.dispose();
				FoxRabbitUI.this.composite = customComposite();
				shell.layout();
				shell.redraw();
			}
		});
		this.composite = composite;
		return composite;
	}

	/**
	 * win process state
	 * 
	 * @return the resulting composite
	 */
	private Composite winComposite() {
		final Player player = startMedia("win rabbit.mp3");
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		Color black = display.getSystemColor(SWT.COLOR_BLACK);
		PaletteData palette = new PaletteData(new RGB[] { white.getRGB(),
				black.getRGB() });
		ImageData sourceData = new ImageData(16, 16, 1, palette);
		sourceData.transparentPixel = 0;
		final Cursor cursor = new Cursor(display, sourceData, 0, 0);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setCursor(cursor);
		composite.setLayout(new FormLayout());
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		final Canvas canvas = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(data);
		ImageData imData = new ImageData(FoxRabbitUI.class
				.getResourceAsStream("rabbit win.gif")).scaledTo(400, 400);
		imData.transparentPixel = imData.getPixel(0, 0);
		final Image rabbitWin = new Image(display, imData);
		final Rectangle rect = display.getBounds();
		final Point location = new Point(-50, rect.height / 2
				- rabbitWin.getImageData().height / 2);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(grass, 0, 0);
				e.gc.drawImage(rabbitWin, location.x, location.y);
			}
		});
		// animation runnable to animate fox winning
		final Runnable mover = new Runnable() {
			private boolean directionX = true; // moving right
			private boolean jumb = false;
			private ImageData data = rabbitWin.getImageData();

			@Override
			public void run() {
				if (directionX) {
					location.x += 5;
					if (location.x + data.width >= rect.width)
						directionX = false;
				} else {
					location.x -= 5;
					if (location.x <= -50)
						directionX = true;
				}
				// process jump state
				int i = Math
						.abs(rect.width / 2 - (location.x + data.width / 2));
				if (i < 100) {
					if (!jumb) {
						jumb = true;
						location.y -= 100;
					}
				} else {
					if (jumb) {
						location.y += 100;
					}
					jumb = false;
				}
				canvas.redraw();
				display.timerExec(10, this);
			}
		};
		display.timerExec(10, mover);
		shell.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ESC) {
					display.timerExec(-1, mover);
					composite.dispose();
					shell.removeTraverseListener(this);
					FoxRabbitUI.this.composite = startUpComposite2();
					shell.layout();
					shell.redraw();
				}
			}
		});
		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// anim.stop();
				previouslyHovered = false;
				stopMedia(player);
				cursor.dispose();
				display.timerExec(-1, mover);
			}
		});
		return composite;
	}

	/**
	 * loose state processing
	 * 
	 * @return the resulting composite
	 */
	private Composite looseComposite() {
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		Color black = display.getSystemColor(SWT.COLOR_BLACK);
		PaletteData palette = new PaletteData(new RGB[] { white.getRGB(),
				black.getRGB() });
		ImageData sourceData = new ImageData(16, 16, 1, palette);
		sourceData.transparentPixel = 0;
		final Cursor cursor = new Cursor(display, sourceData, 0, 0);

		final Font medFont = new Font(display, "Comic Sans MS", 50, SWT.BOLD);
		final Image image = new Image(display, 1000, 1000);
		final Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
		Color borwn = new Color(display, 170, 43, 0);
		GC gc = new GC(image);
		gc.setBackground(white);
		gc.setForeground(borwn);
		gc.fillGradientRectangle(0, 0, 1000, 1000, true);
		for (int i = -500; i < 1000; i += 10) {
			gc.setForeground(yellow);
			gc.drawLine(i, 0, 500 + i, 1000);
			gc.drawLine(500 + i, 0, i, 1000);
		}
		gc.dispose();

		final Pattern pattern;
		try {
			pattern = new Pattern(display, image);
		} catch (SWTException e) {
			// Advanced Graphics not supported.
			// This new API requires the Cairo Vector engine on GTK and Motif
			// and GDI+ on Windows.
			System.err.println(e.getMessage());
			return null;
		}

		final Rectangle dispBounds = display.getBounds();
		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setCursor(cursor);
		composite.setLayout(new FormLayout());
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		final Canvas canvas = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(data);
		ImageData imData = new ImageData(FoxRabbitUI.class
				.getResourceAsStream("rabbit loose.jpg")).scaledTo(
				dispBounds.width, dispBounds.height);
		final Image rabbitLoose = new Image(display, imData);
		final Random gen = new Random();
		final String msg = "Game Over" + "\nMoves done: " + rabbit.getEscapes()
				+ "\nWin moves: " + Constants.MAX_ESCAPES;
		gc = new GC(canvas);
		gc.setFont(medFont);
		final Point pt = gc.stringExtent("Moves done: " + rabbit.getEscapes());
		pt.y *= 3;
		gc.dispose();
		int x = Math.abs(gen.nextInt() % (dispBounds.width - pt.x));
		int y = Math.abs(gen.nextInt() % (dispBounds.height - pt.y));
		final Point location = new Point(x, y);
		looseAnimate = new Image(display, pt.x + 5, pt.y);
		gc = new GC(looseAnimate);
		gc.setForegroundPattern(pattern);
		gc.setFont(medFont);
		gc.drawString(msg, 0, 0);
		gc.dispose();
		imData = looseAnimate.getImageData();
		imData.transparentPixel = imData.palette
				.getPixel(new RGB(255, 255, 255));
		looseAnimate = new Image(display, imData);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(rabbitLoose, 0, 0);
				e.gc.drawImage(looseAnimate, location.x, location.y);
			}
		});
		// animator runnable to animate message movement
		final Runnable mover = new Runnable() {
			boolean right = gen.nextBoolean();
			boolean up = gen.nextBoolean();

			@Override
			public void run() {
				if (right) {
					location.x += 5;
					if (location.x + pt.x >= dispBounds.width - 35)
						right = false;
				} else {
					location.x -= 5;
					if (location.x <= 0)
						right = true;
				}
				if (up) {
					location.y -= 5;
					if (location.y <= 0)
						up = false;
				} else {
					location.y += 5;
					if (location.y + pt.y >= dispBounds.height - 30)
						up = true;
				}
				canvas.redraw();
				display.timerExec(10, this);
			}
		};
		display.timerExec(10, mover);
		shell.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ESC) {
					display.timerExec(-1, mover);
					composite.dispose();
					shell.removeTraverseListener(this);
					FoxRabbitUI.this.composite = startUpComposite2();
					shell.layout();
					shell.redraw();
				}
			}
		});
		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// anim.stop();
				looseAnimate.dispose();
				yellow.dispose();
				previouslyHovered = false;
				cursor.dispose();
				display.timerExec(-1, mover);
			}
		});
		return composite;
	}

	/**
	 * update the state of the game depending on the message received from
	 * notifiers
	 */
	@Override
	public void update(Observable o, Object arg) {
		refresh();
		String choice = (String) arg;
		if ("win".equals(choice)) {
			startMedia("win.wav");
			composite.setCursor(standardCursor);
			display.timerExec(100, new Runnable() {
				private boolean imageOn = true;
				private static final int REPEATS = 5;
				private int counter = 0;
				Point pt = rabbit.location;
				private Image temp = view[pt.x][pt.y].getImage();

				@Override
				public void run() {
					if (imageOn)
						view[pt.x][pt.y].setImage(null);
					else
						view[pt.x][pt.y].setImage(temp);
					imageOn = !imageOn;
					counter++;
					if (counter < REPEATS)
						display.timerExec(500, this);
					else {
						display.timerExec(-1, this);
						composite.dispose();
						FoxRabbitUI.this.composite = winComposite();
						shell.layout();
						shell.redraw();
					}
				}
			});
		} else if ("lost".equals(choice)) {
			rabbitCursor.dispose();
			composite.setCursor(standardCursor);
			display.timerExec(100, new Runnable() {
				private boolean imageOn = true;
				private static final int REPEATS = 5;
				private int counter = 0;
				Point pt = rabbit.location;
				private Image temp = view[pt.x][pt.y].getImage();

				@Override
				public void run() {
					if (view[pt.x][pt.y].isDisposed())
						display.timerExec(-1, this);
					else {
						if (imageOn)
							view[pt.x][pt.y].setImage(null);
						else
							view[pt.x][pt.y].setImage(temp);
						imageOn = !imageOn;
						counter++;
						if (counter < REPEATS)
							display.timerExec(500, this);
						else {
							display.timerExec(-1, this);
							composite.dispose();
							FoxRabbitUI.this.composite = looseComposite();
							shell.layout();
							shell.redraw();
						}
					}
				}
			});
		} else if ("seen".equals(choice)) {
			startMedia("shot.wav");
		}
	}

	/**
	 * @param path
	 *            the path of the media
	 * @return player of the media file
	 */
	private Player startMedia(String path) {
		MediaLocator loc;
		Player player = null;
		File file = new File(path);
		// Create a Medialocator that represents our clip.
		// This should be a file URL, so first we create
		// an object representing the file and then we
		// get the URL from that File object
		try {
			loc = new MediaLocator(file.toURI().toURL().toExternalForm());
			// Create the JMF Player for the audio file
			player = Manager.createPlayer(loc);
			// Play it
			player.start();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return player;
	}

	private void stopMedia(Player player) {
		player.stop();
	}
}
