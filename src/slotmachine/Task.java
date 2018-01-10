package slotmachine;

import static slotmachine.SlotMachine.*;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class Task
{
	public abstract void update();
	public abstract Image render(double delta);
	
	public static final Task slotTask = new Task()
	{
		@Override
		public void update()
		{
			
		}
		
		@Override
		public Image render(double delta)
		{
			BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = canvas.createGraphics();
			
			
			
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
		
		@Override
		public Image render(double delta)
		{
			BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = canvas.createGraphics();
			
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
			
			// That line thing
			g2d.setColor(UI_SLOT_LINE_COLOR);
			g2d.fillRect(0, (HEIGHT / 2) - UI_SLOT_LINE_SIZE, WIDTH, UI_SLOT_LINE_SIZE);
			
			g2d.dispose();
			return canvas;
		}
	};
}
