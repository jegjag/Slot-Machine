package slotmachine.resources;

import java.awt.Image;
import java.awt.Toolkit;

public class Resources
{
	public static Image get(String name)
	{
		return Toolkit.getDefaultToolkit().createImage(Resources.class.getResource(name));
	}
}
