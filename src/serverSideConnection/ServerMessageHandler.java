package serverSideConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import commandControl.Debug;
import util.CommandControl;

public class ServerMessageHandler implements Runnable{

	ServerManager serverManager;
	
	Vector<ServerClientMessage> messageList = new Vector<ServerClientMessage>();
	
	Thread thread;
	
	final AtomicBoolean running = new AtomicBoolean(true);
	
	
	public ServerMessageHandler(ServerManager serverManager)
	{
		this.serverManager = serverManager;
	}
	
	public void start()
	{
		thread = new Thread(this);
		
		thread.setDaemon(true);
		
		thread.start();
	}
	
	public void kill()
	{
		Debug.out(this, "killing MessageHandler");
		running.set(false);
	}
	
	
	@Override
	public void run() {
		
			
			while(running.get())
			{
				
				if(messageList.size() > 0)
				{
					ServerClientMessage message = messageList.get(0);
					
					try
					{
							if(message.text == null)
							{
								Debug.out(this, "message elapsed  " );
								messageList.remove(message);
								continue;
							}
					}
					catch(NullPointerException e)
					{
						Debug.out(this, "message elapsed  ");
						continue;
					}
					
					
					
					Debug.out(this, "message treated : " + message.text);
					
					treatMessage(message);
					
					messageList.remove(message);
					
					
					
				}
			}
		
	}
	
	
	protected void addMessage(ServerClientMessage message)
	{
		this.messageList.add(message);
	}
	
	
	private void treatMessage(ServerClientMessage message)
	{
		String[] cutMessage =  message.text.split("/n");
		
		
		
		for(String command : cutMessage)
		{
			if(command.startsWith(ServerManager.CHAT_INDICATOR))
			{	
				command = CommandControl.removeIndicatorFromCommand(command, ServerManager.CHAT_INDICATOR);
				
				serverManager.sendToAllClient(ServerManager.CHAT_INDICATOR +" "+message.origin + " "+command);
			}
			
			else if(command.startsWith(ServerManager.COMBAT_INDICATOR))
			{
				message.text = CommandControl.removeIndicatorFromCommand(message.text, ServerManager.COMBAT_INDICATOR);
				
				serverManager.sendToBattleManager(message);
			}
			
			
		}
	}

}
