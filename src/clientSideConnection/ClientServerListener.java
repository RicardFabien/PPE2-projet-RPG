package clientSideConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import battle.BattleManager;
import commandControl.CommandManager;
import commandControl.Debug;
import gui.WindowManager;
import serverSideConnection.ServerManager;
import util.CommandControl;

public class ClientServerListener implements Runnable {
	
	Thread thread;
	
	final AtomicBoolean running = new AtomicBoolean();
	
	BufferedInputStream bufferedInputStream;
	
	ClientConnectionManager clientConnectionManager;
	
	
	protected ClientServerListener(BufferedInputStream bufferedInputStream, ClientConnectionManager clientConnectionManager)
	{
		this.bufferedInputStream = bufferedInputStream;
		this.clientConnectionManager = clientConnectionManager;
	}

	
	protected void start()
	{
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	


	@Override
	public void run() 
	{
		String message;
		
		Debug.out(this, "is running");
		
		running.set(true);
		
		while(running.get())
		{
			try 
			{
				message = read();
				
				Debug.out(this, "message received : " + message);

				treatMessage(message);
				
			} 
			catch (IOException e) 
			{
				//Debug.out(this, e);
			}
			catch(IndexOutOfBoundsException e2)
			{
				clientConnectionManager.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION + " "
						+ "Vous avez été déconnectés", null);
				running.set(false);
			}
		}
	}
	
		private void treatMessage(String message)
		{
			Debug.out(this,"message treated " + message);
			
			String[] cutMessage =  message.split("/n");
			
			
			
			for(String command : cutMessage)
			{
				if(command.startsWith(ServerManager.CHAT_INDICATOR))
				{	
					command = command.replace(ServerManager.CHAT_INDICATOR, " ");
					clientConnectionManager.useCommand(CommandManager.SEND_CHAT_WINDOW +" "+ command,(Object) null);
				}
				
				else if(command.startsWith(ServerManager.COMBAT_INDICATOR))
				{
					/*if(command.equals(ServerManager.COMBAT_INDICATOR+" "+BattleManager.GET_CHARACTER))
					{
						clientConnectionManager.sendCombat("initialise 2 Roger 5:1 1:8");
						continue;
					}*/
					command = CommandControl.removeIndicatorFromCommand(command, ServerManager.COMBAT_INDICATOR);
					
					clientConnectionManager.useCommand(CommandManager.COMMAND_MENU+" "+WindowManager.FIGHT+ " " +command,(Object) null);

				}
				
				
				
				
			}
		}
	


	protected void kill()
	{
		running.set(false);
	}
	
	
	private String read() throws IOException
	{      

	    	String response = "";

	    	int stream;
	    	
	    	byte[] b = new byte[4096];
	    	
	    	stream = bufferedInputStream.read(b);

	    	response = new String(b, 0, stream);

	    	
	    	
	    	return response;

	}

}
