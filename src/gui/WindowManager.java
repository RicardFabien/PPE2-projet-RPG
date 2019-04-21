package gui;


import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;

import commandControl.CommandManager;
import commandControl.Debug;
import util.CommandControl;


public class WindowManager {
	
	public static final String CONNECTION = "connection";
	public static final String FIGHT = "fight";
	public static final String CHARACTER_SELECTION = "character_selection";
	public static final String MAIN_MENU = "main_menu";
	public static final String SERVER = "server";
	
	
	public CommandManager guiCommandManager;
	
	
	
	
	protected JFrame mainFrame;
	
	protected JPanel mainJPanel;
	
	protected Menu inUse;
	
	protected Menu lastInUse;
	
	private ArrayList<Chat> chatList = new ArrayList<Chat>();
	
	private HashMap<String,Menu> menuList = new HashMap<String,Menu>();
	
	
	
	public WindowManager(String name)
	{
		this.mainFrame = new JFrame(name);
		
		//Place holder
		inUse = new Menu();
		mainFrame.add(inUse);
		
		
		this.mainFrame.setSize(800, 600);
		
		this.mainFrame.setMinimumSize(new Dimension(800, 600));
		
		this.mainFrame.setLocationRelativeTo(null);
		
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.mainFrame.setVisible(true);
		
		//this.chatList.add(new Chat(this));
		
	}
	

	public void useCommand(String command)
	{
		this.guiCommandManager.useCommand(command);
	}
	
	public void show(String message)
	{
		inUse.display(message);
		
	}
	
	public String getChatText()
	{
		return this.inUse.getChatText();
	}
	
	
	
	public void changeMenu(String menuName)
	{
		lastInUse = inUse;
		
		inUse.offUse();
		
		mainFrame.remove(inUse);
		mainFrame.add(menuList.get(menuName));
		
		inUse = menuList.get(menuName);
		
		mainFrame.invalidate();
		mainFrame.validate();
		
		inUse.repaint();
		
		inUse.onUse();
		
		Debug.out(this, "menu changed from " + lastInUse.getName() + " to " + inUse.getName());
	}
	
	
	public void setMenu(String name , Menu menu)
	{
		this.addMenu(name, menu);
		this.changeMenu(name);
	}
	
	
	public void addMenu(String name, Menu menu)
	{
		this.menuList.put(name, menu);
		menu.windowManager = this;
	}
	


	public void sendToChat(String chatMessage) 
	{
		for(Chat chat  : chatList)
		chat.show(chatMessage);	
	}
	
	public void sendRawToChat(String message)
	{	
		for(Chat chat  : chatList)
		chat.showRaw(message);
	}



	public void treatCommandCurrentMenu(String menuCommand)
	{
		inUse.treatCommand(menuCommand);
	}

	public void treatCommand(String menuCommand) 
	{
		Debug.out(this, "got command " + menuCommand);
		
		String[] cutCommand = menuCommand.split(" ");
		
		
		
		if(cutCommand.length <= 1)
			return;
			
		
		if(menuList.get(cutCommand[0]) != null)
		{
			String finalMenuCommand = CommandControl.removeIndicatorFromCommand(menuCommand, cutCommand[0]);
			menuList.get(cutCommand[0]).treatCommand(finalMenuCommand);
		}
		else
		{
			Debug.out(this, "no menu by the name " + cutCommand[0]);
		}
		
		
	}
	
	protected Chat getNewChat()
	{	
		Chat c = new Chat(this);
		
		chatList.add(c);
		
		return c;
		
	}

}
