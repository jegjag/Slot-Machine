package slotmachine.resources;

import java.awt.Image;
import java.awt.Toolkit;

public class Resources
{
	public static Image get(String name)
	{
		try
		{
			return Toolkit.getDefaultToolkit().createImage(Resources.class.getResource(name));
		}
		catch(Exception e)
		{
			System.out.println("Could not find image '" + name + "'.");
			return null;
		}
	}
}
