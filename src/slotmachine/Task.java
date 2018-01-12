package slotmachine;

import static slotmachine.SlotMachine.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

public abstract class Task
{
	public abstract void update();
	public abstract void render(Graphics2D g2d, double delta);
	
	public static final double RETURN_SPEED = 0.2D;
	private static final int HALF_HEIGHT = (HEIGHT / 2) - (UI_SLOT_ICON_SIZE / 2);
	private static final int THRESHOLD = 10;
	
	public static final Task task_renderUI = new Task()
	{
		private String format(float balance)
		{
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			
			String formattedBalance = null;
			if(df.format(balance).length() == 3)
			{
				formattedBalance = df.format(balance) + "0"; 
			}
			else if(df.format(balance).length() == 1)
			{
				formattedBalance = df.format(balance) + ".00";
			}
			else
			{
				formattedBalance = df.format(balance);
			}
			
			return formattedBalance;
		}
		
		@Override
		public void update(){}
		
		@Override
		public void render(Graphics2D g2d, double delta)
		{
			boolean dead = balance <= 0 && hasReturned && !isSpinning;
			
			// Render UI here
			g2d.setColor(new Color(200, 200, 200));
			
			final Font bigFont = new Font("Open Sans", Font.BOLD, HEIGHT / 12);
			g2d.setFont(bigFont);
			
			if(dead)
			{
				final String OUT_OF_MONEY = "Out of money, press 'r' to restart.";
				g2d.drawString(OUT_OF_MONEY, (int) (WIDTH / 2 - g2d.getFontMetrics(bigFont).getStringBounds(OUT_OF_MONEY, g2d).getWidth() / 2), (int) (HEIGHT / 2 - g2d.getFontMetrics(bigFont).getStringBounds(OUT_OF_MONEY, g2d).getHeight()));
			}
			
			final Font standard = new Font("Open Sans", Font.BOLD, HEIGHT / 50);
			
			g2d.setFont(standard);
			g2d.drawString("FPS: " + getCurrentFrameTime(), WIDTH - 10 - (int)g2d.getFontMetrics(standard).getStringBounds("FPS: " + getCurrentFrameTime(), g2d).getWidth(), g2d.getFontMetrics(standard).getHeight());
			
			if(!dead)
			{
				g2d.drawString("Balance: £" + format(balance), WIDTH / 48, HEIGHT / 24);
				g2d.drawString("Total Spent: £" + format(totalSpent), WIDTH / 48, (HEIGHT / 24) * 2);
				g2d.drawString("Total Won: £" + format(totalWon), WIDTH / 48, (HEIGHT / 24) * 3);
			}
		}
	};
	
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
		
		@Override
		public void render(Graphics2D g2d, double delta){}
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
				
				if(left.speed_multiplier > 0)	left.speed_multiplier -= 0.004;
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
		
		@Override
		public void render(Graphics2D g2d, double delta)
		{
			g2d.drawImage(left.render(delta), 0, 0, null);
			g2d.drawImage(middle.render(delta), 0, 0, null);
			g2d.drawImage(right.render(delta), 0, 0, null);
		}
	};
	
	public static final Task lineTask = new Task()
	{
		@Override
		public void update()
		{
			
		}
		
		@Override
		public void render(Graphics2D g2d, double delta)
		{
			g2d.setColor(UI_SLOT_LINE_COLOR);
			g2d.fillRect(0, (HEIGHT / 2) - UI_SLOT_LINE_SIZE, WIDTH, UI_SLOT_LINE_SIZE);
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
		public void render(Graphics2D g2d, double delta)
		{
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
		}
	};
}
