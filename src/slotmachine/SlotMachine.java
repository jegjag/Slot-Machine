package slotmachine;

import static slotmachine.resources.Resources.get;
import static slotmachine.Task.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class SlotMachine implements Runnable
{
	// Colours
	public static final Color	UI_BACKGROUND_COLOR			= new Color(45, 45, 45);
	public static final Color	UI_SLOT_BORDER_COLOR		= new Color(20, 20, 20);
	public static final int		UI_SLOT_BORDER_SIZE			= 20;
	public static final Color	UI_SLOT_BACKGROUND_COLOR	= new Color(255, 255, 255);
	public static final Color	UI_SLOT_LINE_COLOR			= new Color(255, 0, 0, 120);
	public static final int		UI_SLOT_LINE_SIZE			= 4;
	
	// Dimensions
	public static final Dimension SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WIDTH = SIZE.width, HEIGHT = SIZE.height;
	public static JFrame frame = new JFrame("Slot machine");
	
	// Game loop stuff
	public static final int FPS_LIMIT = 60;
	public static boolean isRunning = true;
	
	// Speed params
	public static final double	SLOT_SPEED = HEIGHT / 1000;
	
	private static SlotMachine obj;
	
	// Stuff for handling the things (Yes I'm tired and can't do the english rn)
	public Line left	= new Line();
	public Line middle	= new Line();
	public Line right	= new Line();
	
	public static class Line
	{
		public Symbol[] symbols = new Symbol[3];
		
		public double y_master = 0D;
		
		
	}
	
	public static class Symbol
	{
		public SlotSymbol symbol;
		public double y = 0D;
		
		public Symbol(SlotSymbol symbol)
		{
			this.symbol = symbol;
		}
	}
	
	public static enum SlotSymbol
	{
		CHERRY("cherry.png", 100D, 0.50F),
		ORANGE("orange.png", 60D, 2.00F),
		LEMON("lemon.png", 50D, 3.00F),
		BELL("bell.png", 40D, 5.00F),
		STAR("star.png", 20D, 10.00F),
		SKULL("skull.png", 40D, -2.00F),
		PENGUIN("penguin.png", 5D, 100.00F);
		
		final Image icon;
		final double chance;
		final double payout;
		
		SlotSymbol(String iconPath, double chance, float payout)
		{
			icon = get(iconPath);
			this.chance = chance;
			this.payout = payout;
		}
	}
	
	public static void main(String[] args)
	{
		frame.setSize(SIZE);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		obj = new SlotMachine();
		new Thread(obj).start();
		
		obj.addTask(backgroundRenderTask);
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
