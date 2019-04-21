package animation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import commandControl.Debug;

public class Sprite extends Component {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8084901502670553158L;

	
	
	protected CharacterSimulator characterSimulator;
	
	protected int clock;
	
	public Sprite(CharacterSimulator characterSimulator)
	{
		this.characterSimulator = characterSimulator;
	}

	
	private Color getColor()
	{
		if(clock % 1000 < 500)
		{
			
			return Color.red;
		}
			
		return Color.blue;
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(getColor());
		
		g.fillRect(characterSimulator.getX(), characterSimulator.getY(),
				(int)getSize().getWidth() , (int)getSize().getHeight());
		}

}
