package serverSideConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import battle.BattleManager;
import commandControl.Debug;


public class ServerClientListener implements Runnable{
	
	protected int id;
	
	protected String ipAdress;
	
	protected String name;
	
	protected int team;
	
	protected boolean isReady;
	
	protected Socket socket;
	
	protected ServerManager serverManager;
	
	protected Thread thread;
	
	
	BufferedInputStream  bufferedInputStream;
	
	OutputStreamWriter outputStreamWriter;
	
	
	
	final AtomicBoolean running = new AtomicBoolean(false);

	
	public ServerClientListener(ServerManager serverManager,int id, String name, Socket socket, BufferedInputStream bufferedinputStream , OutputStreamWriter outputStreamWriter) throws IOException
	{
		this.serverManager = serverManager;
		
		this.bufferedInputStream = bufferedinputStream;
		
		this.outputStreamWriter = outputStreamWriter;
		
		
		this.socket = socket;
		this.name = name;
		this.id = id;
		
		this.ipAdress = socket.getInetAddress().getHostName();
		
		Debug.out(this,"new client listener : " + name);
	}
	

	
	public synchronized void send(String message)
	{
		Debug.out(this, "sent to client " + name +" : " + message);
		
		try 
		{
			outputStreamWriter.write(message);
			wait(50);
			outputStreamWriter.flush();
		} 
		catch (IOException e) 
		{
			Debug.out(this, e);
		} 
		catch (InterruptedException e)
		{
			Debug.out(this, e);
		}
		
	}
	
	
	
	public void start()
	{
		thread = new Thread(this);
		
		thread.setDaemon(true);
		
		thread.start();
	}
	
	
	
	public void stop()
	{
		running.set(false);
		
		try 
		{
			socket.close();
		} 
		catch (IOException e) 
		{
			Debug.out(this, e.getMessage());
		}
			
		//serverManager.notifyClientStop(this);
	}
	
	
	
	@Override
	public void run(){
		
		running.set(true);
		
		Debug.out(this, "clientListener started running");
		
		String message = null;
		
		askForCharacter() ;
		
		while(running.get())
		{
			
			try {
					message = read();
					
					if(message.length() > 1000)
						continue;
					
					sendMessageToServer(message);
				}
			
			catch (IOException e) 
				{
					Debug.out(this,e);
				}

		}
		
		
		
		
	}
	
	private void sendMessageToServer(String message)
	{
		serverManager.sendMessagetoSystem(new ServerClientMessage(this.name, message));
	}
	
	private void askForCharacter() 
	{
		send(ServerManager.COMBAT_INDICATOR+" "+BattleManager.GET_CHARACTER) ;
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
