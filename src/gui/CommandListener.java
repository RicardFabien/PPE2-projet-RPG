package gui;

import java.awt.Button;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CommandListener implements MouseListener 
{
	
	Menu menu;
	
	public CommandListener(Menu menu)
	{
		this.menu = menu;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		String command = ((Button) e.getSource()).getName();
		
		menu.useCommand(command);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
