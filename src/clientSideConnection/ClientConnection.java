package clientSideConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.Buffer;
import java.util.concurrent.atomic.AtomicBoolean;

import commandControl.Debug;
import serverSideConnection.ServerManager;

public class ClientConnection {
	
	
	public ClientConnectionManager clientConnectionManager;
	
	boolean isOn;
	
	String name;
	
	Socket socket;
	
	OutputStreamWriter outputStreamWriter;
	
	ClientServerListener clientServerListener;

	
	
	public ClientConnection(ClientConnectionManager clientConnectionManager ,String name,Socket socket, 
			BufferedInputStream bufferedInputStream, OutputStreamWriter outputStreamWriter)
	{
		
		this.clientConnectionManager = clientConnectionManager;
		this.name = name;
		this.socket = socket;
		
		isOn = true;
	
		this.outputStreamWriter = outputStreamWriter;
		
		clientServerListener = new ClientServerListener(bufferedInputStream, clientConnectionManager);
		clientServerListener.start();
		
	}
	
	
	protected synchronized void  sendChat(String message)  
	{		
		message = ServerManager.CHAT_INDICATOR + " "+ message;

		sendRaw(message);

	}

	protected synchronized void  sendBattle(String message)  
	{		
		message = ServerManager.COMBAT_INDICATOR + " "+ message;

		sendRaw(message);

	}

	
	protected void sendRaw(String message) {
		
		try 
		{
			Debug.out(this, "sent to server " + message);
			outputStreamWriter.write(message);
			outputStreamWriter.flush();
		} 
		catch (IOException e) 
		{
			Debug.out(this, e);
		}
		
		
	}

	
	protected void kill()
	{
		clientServerListener.kill();
		try 
		{
			if(socket != null)
			socket.close();
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	
}
