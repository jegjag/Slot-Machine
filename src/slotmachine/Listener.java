package slotmachine;

import static slotmachine.SlotMachine.*;
import static java.awt.event.KeyEvent.*;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Listener extends Task implements KeyListener
{
	private boolean[] isPressed = new boolean[65535];
	
	public boolean isPressed(int key)
	{
		return isPressed[key];
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		isPressed[e.getKeyCode()] = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		isPressed[e.getKeyCode()] = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e){}
	
	@Override
	public void update()
	{
		if(isPressed(VK_SPACE) && !isSpinning && hasReturned)
		{
			spin();
		}
		
		if(isPressed(VK_ESCAPE))
		{
			System.exit(0);
		}
		
		if(isPressed(VK_R) && !isSpinning && hasReturned)
		{
			balance = 2.50F;
		}
	}
	
	@Override
	public void render(Graphics2D g2d, double delta){}
}
