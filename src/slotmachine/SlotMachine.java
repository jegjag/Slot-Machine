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
	public static final int FPS_LIMIT = 60;
	public static boolean isRunning = true;
	
	// Speed params
	public static final double	SLOT_SPEED = HEIGHT / 1000;
	
	// Objects
	private static SlotMachine obj;
	public static Random r = new Random();
	
	// Stuff for handling the things (Yes I'm tired and can't do the english rn)
	public static Line left, middle, right;
	
	public static class Symbol
	{
		public SlotSymbol symbol;
		public double y = 0D;
		
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
		total = 0;
		for(int i = 0; i < symbols.length; i++)
		{
			if(num > total && num < total + symbols[i].chance)
			{
				return symbols[i];
			}
			
			total += symbols[i].chance;
		}
		
		return null;
	}
	
	public static class Line
	{
		public Symbol[] symbols = new Symbol[3];
		private final int x; 
		
		public Line(int x)
		{
			this.x = x;
			
			symbols[0] = new Symbol(randomSymbol());
			symbols[1] = new Symbol(randomSymbol());
			symbols[2] = new Symbol(randomSymbol());
			
			symbols[0].y = HEIGHT / 12;
			symbols[1].y = HEIGHT / 2 - UI_SLOT_ICON_SIZE / 2;
			symbols[2].y = (HEIGHT - (HEIGHT / 12) - (WIDTH / 6)) + UI_SLOT_ICON_SIZE / 2;
		}
		
		public void update()
		{
			
		}
		
		public Image render(double delta)
		{
			BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = canvas.createGraphics();
			
			for(int i = 0; i < symbols.length; i++)
			{
				g2d.drawImage(symbols[i].symbol.icon,
						x + UI_SLOT_BORDER_SIZE + ((WIDTH / 6) - UI_SLOT_ICON_SIZE) / 2,
						(int) (symbols[i].y * delta), UI_SLOT_ICON_SIZE - UI_SLOT_BORDER_SIZE,
						UI_SLOT_ICON_SIZE - UI_SLOT_BORDER_SIZE,
						null
				);
			}
			
			g2d.dispose();
			return canvas;
		}
	}
	
	public static enum SlotSymbol
	{
		CHERRY("cherry.png", 100, 0.50F),
		ORANGE("orange.png", 60, 2.00F),
		LEMON("lemon.png", 50, 3.00F),
		BELL("bell.png", 40, 5.00F),
		STAR("star.png", 20, 10.00F),
		SKULL("skull.png", 40, -2.00F),
		PENGUIN("penguin.png", 10, 100.00F);
		
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
		left	= new Line(WIDTH / 5);
		middle	= new Line((WIDTH / 2) - (WIDTH / 12));
		right	= new Line(WIDTH - (WIDTH / 5) - (WIDTH / 6));
		
		frame.setSize(SIZE);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		obj = new SlotMachine();
		new Thread(obj).start();
		
		obj.addTask(backgroundRenderTask);
		obj.addTask(slotTask);
		obj.addTask(lineTask);
	}
	
	// Frametime stuff
	private static int frames = 0;
	private static int currentFrameTime = 0;
	private static Timer mainLoopTimer = new Timer();
	
	public int getCurrentFrameTime()
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
			g2d.drawImage(task.render(delta), 0, 0, null);
		}
		
		g2d.dispose();
		
		g2d = (Graphics2D) frame.getGraphics();
		g2d.drawImage(canvas, 0, 0, null);
		g2d.dispose();
	}
	
	@Override
	public void run()
	{
		boolean limitFPS = FPS_LIMIT > 0;
		
		// Start loop
		final long UPDATE_NANOS = 1000000000 / FPS_LIMIT;
		long previous = System.nanoTime();
		long delay = 0L;
		
		canvas = frame.createVolatileImage(WIDTH, HEIGHT);
		
		mainLoopTimer.scheduleAtFixedRate(
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
					Thread.sleep((1000 / FPS_LIMIT) - (delay / 1000 / 1000));
				}
				catch(Exception e){}
			}
		}
	}
}
