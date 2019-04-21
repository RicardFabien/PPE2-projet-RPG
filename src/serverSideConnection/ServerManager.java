package serverSideConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.plaf.basic.BasicToggleButtonUI;

import battle.BattleManager;
import commandControl.CommandManager;
import commandControl.Debug;

public class ServerManager {
	
	
	boolean on;
	
	
	public CommandManager commandManager;
	
	ServerMessageHandler messageHandler;
	
	ServerClientListenerInitialiser serverClientListenerInitialiser;
	
	BattleManager battleManager = new BattleManager(this);
	
	
	List<ServerClientListener> clientList = new ArrayList<ServerClientListener>();
	
	
	
	int maxNumberOfConnection = 4;
	
	
	int clientInitialiserUnexpectedStop = 0;
	
	
	//<-----Name extention----->
	
	private String[] nameExtention= {"Bis","Alter","Doppelganger"}; 
	
	//<-----Commandes----->
	
	public static final String STOP_COMMUNICATION = "stop_communication";
	
	public static final String CONNECTION_ACCEPTED = "connection_accepted";
	public static final String CONNECTION_REFUSED = "connection_refused";
	
	public static final String NAME_EXTENTION = "name_extention";
	
	public static final String UNSPECIFIED = "unspecified";
	public static final String IS_FULL = "is_full";
	
	public static final String END = "end";
	
	//<-----Protocol de communication----->
	
	//Les trois premiers character sont utilisé pour les communication de protocol
	
	public static final String CHAT_INDICATOR = "Cµ@t***";
	public static final String SYSTEM_INDICATOR = "$Y§tem*";
	public static final String COMBAT_INDICATOR = "{0ùmbat";
	
	
	
	
	//<-----Methodes----->
	
	public void sendClient(ServerClientListener client,String message)
	{
		client.send(message);
	}
	
	public void sendClient(String nameClient , String message)
	{
		ServerClientListener client = getClientByName(nameClient);
		
		client.send(message);
	}
	
	
	public void sendToAllClient(String message)
	{
		for(ServerClientListener client : clientList)
		{
			sendClient(client, message);

		}
	}
	
	
	
	
	public void turnOn() 
	{
		if (on) 
			return;
		
		this.messageHandler = new ServerMessageHandler(this);
		this.messageHandler.start();
		
		this.serverClientListenerInitialiser = new ServerClientListenerInitialiser(this);
		this.serverClientListenerInitialiser.start();
		
		this.battleManager = new BattleManager(this);
		
		on = true;
		
		useCommand(CommandManager.CLIENT_CONNECT+ " " + "localhost");
		
		Debug.out(this, "server on");
		
	}
	
	
	
	public void turnOff()
	{
		if(!on)
			return;
		
		stopClientInitialiser();
		deleteAllClient();
		
		deleteMessageHandler();
		
		
		on = false;
		
		Debug.out(this, "server off");
	}
	
	public void startBattle()
	{
		battleManager.startBattle();
	}
	
	
	
	
	
	public void addClient(ServerClientListener client)
	{
		this.clientList.add(client);
		client.start();
	}
	
	public void removeClient(ServerClientListener client)
	{
		this.clientList.remove(client);
	}
	
	
	
	public synchronized void sendMessagetoSystem(ServerClientMessage message)
	{
		Debug.out(this, "message sent to messageHandler : " + message);
		messageHandler.addMessage(message);	
	}
	
	
	public int numberOfConnection()
	{
		return clientList.size() + 1 ;
	}

	
	protected synchronized void notifyUnexpectedClientStop() 
	{
		clientInitialiserUnexpectedStop ++;
		
		if(clientInitialiserUnexpectedStop < 4)
		{
			
			restartClientInitialiser();
		}
		
		else
		{
			Debug.out(this, "une erreur à été detecté dans le clientInitialiser ");
		}
		
		
	}
	
	protected void notifyClientStop(ServerClientListener client)
	{
		if(client != null)
			clientList.remove(client);	
		
	}
	
	
	protected synchronized int  getNextClientid() 
	{
		for(int i = 1; i <= maxNumberOfConnection; i++ )
		{
			boolean used = false;
			
			for(ServerClientListener client : clientList)
			{
				if(client.id == i)
					used = true;
			}
			
			if(used == false)
				return i;	
		}
		
		return -1;
	}
	
	
	protected synchronized String getNameExtention(String name) 
	{
		int i =0;
		
		while(isNameDuplicate(name))
		{
			for(String extention : nameExtention)
			{
				String testName = name + extention;
				
				if(i != 0)
					testName += i;
				Debug.out(this, "tested name : " + testName);
				
				if( !isNameDuplicate(testName) )
				{
					return extention;
				}
			}
		}
		
		return "";
	}
	
	
	protected void useCommand(String command , Object object)
	{
		commandManager.useCommand(command, object);
	}
	
	private void restartClientInitialiser()
	{
		
		Debug.out(this, "restarting ClientListener Initialiser");
		stopClientInitialiser();
		
		this.serverClientListenerInitialiser = new ServerClientListenerInitialiser(this);
		this.serverClientListenerInitialiser.start();
		
	}
	
	private void stopClientInitialiser()
	{
		if(serverClientListenerInitialiser != null)
			serverClientListenerInitialiser.stop();
	}
	
	private void deleteMessageHandler()
	{
		messageHandler.kill();
		messageHandler = null;
	}
	
	/*private void deleteClient(ServerClientListener client)
	{
		if(client != null)
			client.stop();
		
		removeClient(client);	
	}*/
	
	private synchronized void deleteAllClient()
	{	
		while(clientList.size() > 0)
		{
			clientList.remove(0);
		}
	}
	
	private ServerClientListener getClientByName(String nameClient)
	{
		for(ServerClientListener client : clientList)
		{
			if(client.name.equals(nameClient))
			return client;
		}
		
		return null;
	}
	
	private boolean isNameDuplicate(String name)
	{
		if(getClientByName(name) == null)
			return false;
		
		return true;
		
	}

	protected void useCommand(String command) 
	{
		useCommand(command, null);
	}
	
	
	public void sendToBattleManager(ServerClientMessage serverClientMessage) 
	{
		battleManager.treatCommand(serverClientMessage);
	}

}
