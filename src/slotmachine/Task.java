package slotmachine;

import static slotmachine.SlotMachine.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class Task
{
	public abstract void update();
	public abstract Image render(double delta);
	
	public static final double RETURN_SPEED = 0.2D;
	private static final int HALF_HEIGHT = (HEIGHT / 2) - (UI_SLOT_ICON_SIZE / 2);
	private static final int THRESHOLD = 10;
	
	public static final Task middleReturnTask = new Task()
	{
		private void handle(Symbol symbol, Line line)
		{
			if(!line.hasReturned && symbol.y % HALF_HEIGHT > THRESHOLD)
			{
				double speed = 0D;
				if(symbol.y < HALF_HEIGHT)
				{
					speed = RETURN_SPEED;
				}
				else if(symbol.y > HALF_HEIGHT)
				{
					speed = -RETURN_SPEED;
				}
				line.speed_multiplier = speed;
			}
			else if(!line.hasReturned)
			{
				System.out.println(line + " has returned");
				line.hasReturned = true;
				line.speed_multiplier = 0D;
			}
		}
		
		@Override
		public void update()
		{
			if(!isSpinning)
			{
				if(!hasReturned)
				{
					handle(getSelectedSymbol(left.symbols), left);
					handle(getSelectedSymbol(middle.symbols), middle);
					handle(getSelectedSymbol(right.symbols), right);
					
					if(left.hasReturned && middle.hasReturned && right.hasReturned)
					{
						hasReturned = true;
					}
				}
			}
		}
		
		public Image render(double delta)
		{
			return null;
		}
	};
	
	public static final Task slotTask = new Task()
	{
		@Override
		public void update()
		{
			if(isSpinning)
			{
				left.update();
				middle.update();
				right.update();
				
				if(left.speed_multiplier > 0)	left.speed_multiplier -= 0.005;
				else
				{
					left.speed_multiplier = 0;
				}
				
				if(middle.speed_multiplier > 0)	middle.speed_multiplier -= 0.003;
				else
				{
					middle.speed_multiplier = 0;
				}
				
				if(right.speed_multiplier > 0)	right.speed_multiplier -= 0.002;
				else
				{
					// Finish spinning
					isSpinning = false;
					right.speed_multiplier = 0;
					finishSpin();
				}
			}
			else if(!hasReturned)
			{
				left.update();
				middle.update();
				right.update();
			}
		}
		
		private BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		@Override
		public Image render(double delta)
		{
			Graphics2D g2d = canvas.createGraphics();
			g2d.setBackground(new Color(0, 0, 0, 0));
			g2d.clearRect(0, 0, WIDTH, HEIGHT);
			
			g2d.drawImage(left.render(delta), 0, 0, null);
			g2d.drawImage(middle.render(delta), 0, 0, null);
			g2d.drawImage(right.render(delta), 0, 0, null);
			
			g2d.dispose();
			return canvas;
		}
	};
	
	public static final Task lineTask = new Task()
	{
		@Override
		public void update()
		{
			
		}
		
		private BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		@Override
		public Image render(double delta)
		{
			Graphics2D g2d = canvas.createGraphics();
			g2d.setBackground(new Color(0, 0, 0, 0));
			g2d.clearRect(0, 0, WIDTH, HEIGHT);
			
			g2d.setColor(UI_SLOT_LINE_COLOR);
			g2d.fillRect(0, (HEIGHT / 2) - UI_SLOT_LINE_SIZE, WIDTH, UI_SLOT_LINE_SIZE);
			
			g2d.dispose();
			return canvas;
		}
	};
	
	/**
	 * Renders the background.
	 */
	public static final Task backgroundRenderTask = new Task()
	{
		@Override
		public void update()
		{
			
		}
		
		private BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		@Override
		public Image render(double delta)
		{
			Graphics2D g2d = canvas.createGraphics();
			g2d.setBackground(new Color(0, 0, 0, 0));
			g2d.clearRect(0, 0, WIDTH, HEIGHT);
			
			g2d.setColor(UI_BACKGROUND_COLOR);
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			
			// Left slot
			g2d.setColor(UI_SLOT_BORDER_COLOR);
			g2d.fillRect(WIDTH / 5, 0, WIDTH / 6, HEIGHT);
			
			g2d.setColor(UI_SLOT_BACKGROUND_COLOR);
			g2d.fillRect((WIDTH / 5) + UI_SLOT_BORDER_SIZE,
					0,
					(WIDTH / 6) - (UI_SLOT_BORDER_SIZE * 2),
					HEIGHT
			);
			
			// Middle slot
			g2d.setColor(UI_SLOT_BORDER_COLOR);
			g2d.fillRect((WIDTH / 2) - (WIDTH / 12), 0, WIDTH / 6, HEIGHT);
			
			g2d.setColor(UI_SLOT_BACKGROUND_COLOR);
			g2d.fillRect(
					(WIDTH / 2) - (WIDTH / 12) + UI_SLOT_BORDER_SIZE,
					0,
					(WIDTH / 6) - (UI_SLOT_BORDER_SIZE * 2),
					HEIGHT
			);
			
			// Right slot
			g2d.setColor(UI_SLOT_BORDER_COLOR);
			g2d.fillRect(WIDTH - (WIDTH / 5) - (WIDTH / 6), 0, WIDTH / 6, HEIGHT);
			
			g2d.setColor(UI_SLOT_BACKGROUND_COLOR);
			g2d.fillRect(WIDTH - (WIDTH / 5) - (WIDTH / 6) + UI_SLOT_BORDER_SIZE,
					0,
					(WIDTH / 6) - (UI_SLOT_BORDER_SIZE * 2),
					HEIGHT
			);
			
			g2d.dispose();
			return canvas;
		}
	};
}
