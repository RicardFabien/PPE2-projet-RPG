package clientSideConnection;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import commandControl.CommandManager;
import commandControl.Debug;

public class ClientConnectionManager {
	
	public static final String AUTO_CHECK_INDICATOR = "auto";
	
	public CommandManager commandManager;
	
	protected ClientConnection clientConnection;
	
	protected ClientConnectionConnector clientConnectionConnector; 
	
	public String name;
	
	
	public ClientConnectionManager()
	{
		this.clientConnectionConnector = new ClientConnectionConnector(this);
		
		
		
	}
	
	public void useCommand(String command , Object object)
	{
		commandManager.useCommand(command, object);
	}
	
	
	public void sendChat(String message)
	{
		if(clientConnection == null || clientConnection.isOn == false)
		{
			Debug.out(this, "pas de server connecté");
			return;
		}
			
		clientConnection.sendChat(message);
	}
	
	public void sendCombat(String message)
	{
		if(clientConnection == null || clientConnection.isOn == false)
		{
			Debug.out(this, "pas de server connecté");
			return;
		}
			
		clientConnection.sendBattle(message);
	}


	public void tryConnect(String adress) 
	{
		if(adress.equals(AUTO_CHECK_INDICATOR))
		{
			clientConnectionConnector.autoCheck();
			return;
		}
		
		try 
		{
			clientConnectionConnector.checkAdress(adress, 100, true);
		} 
		catch (IOException e)
		{
			Debug.out(this, e);
		}
	}
	
	
	
	
	
	
	
	 
}
