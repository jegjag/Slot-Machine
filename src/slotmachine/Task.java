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
	
	public static final Task slotTask = new Task()
	{
		private final int HALF_HEIGHT = (HEIGHT / 2) - (UI_SLOT_ICON_SIZE / 2);
		
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
					
					Symbol selected = getSelectedSymbol(left.symbols);
					
					double speed = 0D;
					if(selected.y < HALF_HEIGHT)
					{
						speed = 0.2D;
					}
					else if(selected.y > HALF_HEIGHT)
					{
						speed = -0.2D;
					}
					
					left.speed_multiplier = speed;
					return;
				}
				
				if(middle.speed_multiplier > 0)	middle.speed_multiplier -= 0.004;
				else
				{
					middle.speed_multiplier = 0;
				}
				
				if(right.speed_multiplier > 0)	right.speed_multiplier -= 0.003;
				else
				{
					// Finish spinning
					isSpinning = false;
					right.speed_multiplier = 0;
					finishSpin();
				}
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
