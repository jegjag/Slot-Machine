package slotmachine;

import static slotmachine.Task.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
	
	private static SlotMachine obj;
	
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
