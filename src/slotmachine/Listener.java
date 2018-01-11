package slotmachine;

import static slotmachine.SlotMachine.*;
import static java.awt.event.KeyEvent.*;

import java.awt.Image;
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
	}
	
	@Override
	public Image render(double delta)
	{
		return null;
	}
}
