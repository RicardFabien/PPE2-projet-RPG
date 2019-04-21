package serverSideConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import commandControl.Debug;

public class ServerClientListenerInitialiser implements Runnable{

	ServerManager serverManager;
	
	Thread thread;
	
	ServerSocket serverSocket ;
	
	BufferedInputStream  bufferedInputStream;
	
	OutputStreamWriter outputStreamWriter;
	
	final AtomicBoolean running = new AtomicBoolean(false);
	
	
	public ServerClientListenerInitialiser(ServerManager serverManager) {
		
		this.serverManager = serverManager;
		
		try
		{
			this.serverSocket = new ServerSocket(6666);
		}
	catch (IOException e)
		{
			Debug.out(this,e.getMessage());
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
		turnServerSocketOff();
		
	}
	
	
	
	@Override
	public void run() {
		
		running.set(true);
		
		Debug.out(this, "began running");
		
		while(running.get())
		{
			Debug.out(this, "in loop");
			
				Socket socket = null;
				
				try 
				{
					Debug.out(this, "waiting for socket");
					socket= serverSocket.accept();
					Debug.out(this, "connection accepted");
				} 
				catch (IOException e) 
				{
					Debug.out(this, "connection cut short");
					continue;
				}
				
				
				try 
				{
					bufferedInputStream = new BufferedInputStream(socket.getInputStream());
					outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
				} 
				catch (IOException e) 
				{
					Debug.out(this,e.getMessage());
				}
				
				Debug.out(this, "testing fullness");
				
				//Le serveur peut accepter les demandes de connections
				if(serverManager.maxNumberOfConnection >= serverManager.numberOfConnection())
				{
					acceptConnection(socket);
				}
				
				//le nombre de joueur connecté est égale ou supérieur au nombre max de connection
				//le server n'accepte plus de connection, cette connection est refusé
				else
				{
					refuseConnection(socket, ServerManager.IS_FULL);	
				}
				
				Debug.out(this, "fullness sent");
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Debug.out(this, e);
				}
				
				reinitialise();

		}
		
		Debug.out(this, "out loop");
	
	}
	
	private void turnServerSocketOff()
	{
		if(serverSocket != null)
			try {
					Debug.out(this, "closing ServerSocket");
					serverSocket.close();
					Debug.out(this, "ServerSocket closed");
				} 
			catch (IOException e)
				{
					Debug.out(this, e.getMessage());
				}
	}
	
	private void acceptConnection(Socket socket )
	{
		Debug.out(this, "protocol beginning");
		
		int clientId = serverManager.getNextClientid();
		
		try 
		{
			outputStreamWriter.write(ServerManager.CONNECTION_ACCEPTED +" " + clientId );
			outputStreamWriter.flush();
			
			Debug.out(this, "ask for name");
			
			//Le protocol est pour le client d'envoyer son nom
			String name = read();
			
			Debug.out(this, "name received " + name);
			
			String nameExtention = serverManager.getNameExtention(name);
			
			name += nameExtention;
			
			//Le protocol est pour le serveur de renvoyer une String à concatener au nom s'il y a doublon
			outputStreamWriter.write(ServerManager.NAME_EXTENTION + " " + nameExtention);
			outputStreamWriter.flush();
			
			serverManager.addClient( new ServerClientListener(this.serverManager,clientId, name, socket,bufferedInputStream, outputStreamWriter) );
			
		} 
		catch (IOException e)
		{
			Debug.out(this , e);
		}
	}
	
	private void refuseConnection(Socket socket, String reason )
	{
		try 
		{
			outputStreamWriter.write(ServerManager.CONNECTION_REFUSED +" " + reason );
			outputStreamWriter.write(ServerManager.END);
			outputStreamWriter.flush();
			
			socket.close();
		} 
		catch (IOException e)
		{
			Debug.out(this , e);
		}
		
		
	}
	
	private void reinitialise()
	{
		
		/*closeBufferedInputStream();
		closeOutputStreamWriter();*/
	}
	
	private void closeBufferedInputStream()
	{
		if (bufferedInputStream == null)
			return;
		
		try 
		{
			bufferedInputStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		bufferedInputStream = null;
	}
	
	private void closeOutputStreamWriter()
	{
		if (outputStreamWriter == null)
			return;
		
		try 
		{
			outputStreamWriter.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		outputStreamWriter = null;
		
	}
	
	
	
	private String read() throws IOException
	{      

	    	String response = "";

	    	int stream;

	    	byte[] b = new byte[4096];

	    	stream = bufferedInputStream.read(b);

	    	response = new String(b, 0, stream);
	    	
	    	Debug.out(this, "reçu " + response);
	    	
	    	return response;

	}

}
