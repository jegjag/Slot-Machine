package slotmachine;

import static slotmachine.resources.Resources.get;
import static slotmachine.Task.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class SlotMachine implements Runnable
{
	// Dimensions
	public static final Dimension SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WIDTH = SIZE.width, HEIGHT = SIZE.height;
	public static JFrame frame = new JFrame("Slot machine");
	
	// Colours
	public static final Color	UI_BACKGROUND_COLOR			= new Color(45, 45, 45);
	public static final Color	UI_SLOT_BORDER_COLOR		= new Color(20, 20, 20);
	public static final int		UI_SLOT_BORDER_SIZE			= 20;
	public static final Color	UI_SLOT_BACKGROUND_COLOR	= new Color(255, 255, 255);
	public static final Color	UI_SLOT_LINE_COLOR			= new Color(255, 0, 0, 120);
	public static final int		UI_SLOT_LINE_SIZE			= 4;
	public static final int		UI_SLOT_ICON_SIZE			= (int)((WIDTH / 6) / 1.3);
	
	// Game loop stuff
	public static final int UPDATE_LIMIT = 144;
	public static final int FPS_CAP = 144;
	public static boolean isRunning = true;
	
	// Objects
	private static SlotMachine obj;
	public static Listener listener = new Listener();
	public static Random r = new Random();
	
	// Slot machine not useful stuff
	protected static boolean	leftReturn = false,
								middleReturn = false,
								rightReturn = false;
	public static boolean hasReturned = true;
	
	// Slot machine stuff
	public static Line left, middle, right;
	public static Symbol selectedLeft = null, selectedMiddle = null, selectedRight = null;
	public static boolean isSpinning = false;
	
	// Actual super important change stuff
	public static float balance = 1.00F;
	public static float totalSpent = 0.00F;
	public static float totalWon = 0.00F;
	
	public static final float SPIN_COST = 0.10F;
	
	public static void spin()
	{
		if(balance > 0)
		{
			balance		-= SPIN_COST;
			totalSpent	+= SPIN_COST;
			
			left.speed_multiplier = 1D;
			middle.speed_multiplier = 1D;
			right.speed_multiplier = 1D;
			
			isSpinning = true;
		}
	}
	
	public static void finishSpin()
	{
		left.hasReturned = false;
		middle.hasReturned = false;
		right.hasReturned = false;
		
		hasReturned = false;
		isSpinning = false;
		
		selectedLeft	= getSelectedSymbol(left.symbols);
		selectedMiddle	= getSelectedSymbol(middle.symbols);
		selectedRight	= getSelectedSymbol(right.symbols);
		
		if(selectedLeft.symbol == selectedMiddle.symbol && selectedMiddle.symbol == selectedRight.symbol)
		{
			// 3 of the same
			balance += selectedMiddle.symbol.payout;
			totalWon += selectedMiddle.symbol.payout;
		}
	}
	
	public static Symbol getSelectedSymbol(Symbol[] symbols)
	{
		int closestIndex = 0;
		double closest = 0x7FFFFFFF;
		for(int i = 0; i < symbols.length; i++)
		{
			double current = symbols[i].y - (HEIGHT / 2);
			if(current < 0)
			{
				current = -current;
			}
			if(current < closest)
			{
				// If smaller
				closest = current;
				closestIndex = i;
			}
		}
		
		return symbols[closestIndex];
	}
	
	public static class Symbol
	{
		public SlotSymbol symbol;
		public double y = 0D;
		public double y_speed = 20D;
		
		public Symbol(SlotSymbol symbol)
		{
			this.symbol = symbol;
		}
	}
	
	public static SlotSymbol randomSymbol()
	{
		SlotSymbol[] symbols = SlotSymbol.values();
		int total = 0;
		
		for(SlotSymbol s : symbols)
		{
			total += s.chance;
		}
		
		int num = r.nextInt(total);
		int current = 0;
		
		for(int i = 0; i < symbols.length; i++)
		{
			if(num < symbols[i].chance + current)
			{
				return symbols[i];
			}
			current += symbols[i].chance;
		}
		
		return null;
	}
	
	public static class Line
	{
		public Symbol[] symbols = new Symbol[4];
		private final int x; 
		public double speed_multiplier = 1.0D;
		public boolean hasReturned = false;
		
		public Line(int x, double speed_mult)
		{
			this.x = x;
			speed_multiplier = speed_mult;
			
			symbols[0] = new Symbol(randomSymbol());
			symbols[1] = new Symbol(randomSymbol());
			symbols[2] = new Symbol(randomSymbol());
			symbols[3] = new Symbol(randomSymbol());
			
			symbols[0].y = HEIGHT / 12;
			symbols[1].y = (HEIGHT / 12) * 2 + UI_SLOT_ICON_SIZE;
			symbols[2].y = (HEIGHT / 12) * 3 + (UI_SLOT_ICON_SIZE * 2);
			symbols[3].y = (HEIGHT / 12) * 4 + (UI_SLOT_ICON_SIZE * 3);
		}
		
		public void update()
		{
			for(int i = 0; i < symbols.length; i++)
			{
				symbols[i].y += symbols[i].y_speed * speed_multiplier;
				
				if(symbols[i].y >= HEIGHT)
				{
					symbols[i].y = -UI_SLOT_ICON_SIZE;
					symbols[i].symbol = randomSymbol();
				}
			}
		}
		
		public Image render(double delta)
		{
			BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = canvas.createGraphics();
			
			for(int i = 0; i < symbols.length; i++)
			{
				try
				{
					int x = this.x + UI_SLOT_BORDER_SIZE + ((WIDTH / 6) - UI_SLOT_ICON_SIZE) / 2;
					int y = (int) (symbols[i].y * delta);
					int w = UI_SLOT_ICON_SIZE - UI_SLOT_BORDER_SIZE;
					int h = UI_SLOT_ICON_SIZE - UI_SLOT_BORDER_SIZE;
					
					g2d.drawImage(symbols[i].symbol.icon, x, y, w, h, null);
				}
				catch(Exception e){}
			}
			
			g2d.dispose();
			return canvas;
		}
	}
	
	public static enum SlotSymbol
	{
		CHERRY("cherry.png", 175, 0.50F),
		ORANGE("orange.png", 100, 2.00F),
		LEMON("lemon.png", 80, 5.00F),
		BELL("bell.png", 35, 10.00F),
		STAR("star.png", 20, 20.00F),
		SKULL("skull.png", 50, -2.50F),
		PENGUIN("penguin.png", 15, 100.00F);
		
		final Image icon;
		final int chance;
		final double payout;
		
		SlotSymbol(String iconPath, int chance, float payout)
		{
			icon = get(iconPath);
			this.chance = chance;
			this.payout = payout;
		}
	}
	
	public static void main(String[] args)
	{
		System.setProperty("sun.java2d.opengl", "true");
		
		left	= new Line(WIDTH / 5, 1.0D);
		middle	= new Line((WIDTH / 2) - (WIDTH / 12), 1.0D);
		right	= new Line(WIDTH - (WIDTH / 5) - (WIDTH / 6), 1.0D);
		
		frame.setSize(SIZE);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(listener);
		frame.setVisible(true);
		
		obj = new SlotMachine();
		new Thread(obj).start();
		
		obj.addTask(listener);
		obj.addTask(backgroundRenderTask);
		obj.addTask(slotTask);
		obj.addTask(lineTask);
		obj.addTask(middleReturnTask);
		obj.addTask(task_renderUI);
	}
	
	// Frametime stuff
	private static int frames = 0;
	private static int currentFrameTime = 0;
	private static Timer mainLoopTimer = new Timer();
	
	public static int getCurrentFrameTime()
	{
		return currentFrameTime;
	}
	
	private List<Task> tasks = new ArrayList<Task>();
	
	public void addTask(Task task)
	{
		tasks.add(task);
	}
	
	public void removeTask(Task task)
	{
		tasks.remove(task);
	}
	
	public List<Task> getTasks()
	{
		return tasks;
	}
	
	public void update()
	{
		for(Task task : tasks)
		{
			task.update();
		}
	}
	
	protected VolatileImage canvas;
	
	public void render(double delta)
	{
		Graphics2D g2d = canvas.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		
		for(Task task : tasks)
		{
			task.render(g2d, delta);
		}
		
		g2d.dispose();
		
		g2d = (Graphics2D) frame.getGraphics();
		g2d.drawImage(canvas, 0, 0, null);
		g2d.dispose();
		
		frames++;
	}
	
	@Override
	public void run()
	{
		boolean limitFPS = FPS_CAP > 0;
		
		// Start loop
		final long UPDATE_NANOS = 1000000000 / UPDATE_LIMIT;
		long previous = System.nanoTime();
		long delay = 0L;
		
		canvas = frame.createVolatileImage(WIDTH, HEIGHT);
		
		mainLoopTimer.scheduleAtFixedRate
		(
			new TimerTask()
			{
				@Override
				public void run()
				{
					currentFrameTime = frames;
					frames = 0;
				}
			}
		, 0, 1000);
		
		while(isRunning)
		{
			long current = System.nanoTime();
			long elapsed = current - previous;
			previous = current;
			delay += elapsed;
			
			// Pre-update
			
			// Update loop until it has caught up to real time
			while(delay >= UPDATE_NANOS)
			{
				update();
				delay -= UPDATE_NANOS;
			}
			
			// Render
			render(1.0D + (delay / UPDATE_NANOS));
			if(limitFPS)
			{
				try
				{
					Thread.sleep((1000 / FPS_CAP) - (delay / 1000 / 1000));
				}
				catch(Exception e){}
			}
		}
	}
}
