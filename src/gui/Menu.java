package gui;

import java.awt.TextArea;
import java.awt.TextField;
import java.util.HashMap;
import javax.swing.JPanel;

import commandControl.Debug;
import gui.WindowManager;

public class Menu extends JPanel {

	private static final long serialVersionUID = 1L;
	
	String name;
	
	public WindowManager windowManager;
	
	public TextArea mainTextDisplay;
	
	public TextField mainChatBox;
	
	CommandHandler commandHandler;
	
	protected HashMap<String ,Object> modifiableElementList = new HashMap<String ,Object>(); 
	
	
	private static final String OFF = "off";
	private static final String ON = "on";
	
	//Montre le message par l'objet gerant le GUI
	public void display(String message)
	{
		mainTextDisplay.append(message);
	}
	
	
	//recupere le chatText
	public String getChatText()
	{
		this.mainChatBox.setText("");
		return this.mainChatBox.getText();
		
	}
	
	public void useCommand(String command)
	{
		this.windowManager.useCommand(command);
	}
	
	public void treatCommand(String command)
	{
		if(this.commandHandler == null)
			return;
	
		
		Debug.out(this, "command treated : " + command);
		this.commandHandler.treatCommand(command, modifiableElementList);
	}
	
	public void onUse()
	{
		treatCommand(ON);
	}
	
	public void offUse()
	{
		treatCommand(OFF);
	}
	
	

}
