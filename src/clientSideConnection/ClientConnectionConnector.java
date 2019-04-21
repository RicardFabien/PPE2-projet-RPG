package clientSideConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import commandControl.CommandManager;
import commandControl.Debug;
import serverSideConnection.ServerManager;

public class ClientConnectionConnector {
	
	
	
	private ClientConnectionManager clientConnectionManager;
	
	private Socket socket;
	
	protected boolean correct;
	
	private BufferedInputStream  bufferedInputStream;
	
	private OutputStreamWriter outputStreamWriter;
	
	private String name = "test";
	
	protected ClientConnectionConnector(ClientConnectionManager clientConnectionManager) 
	{
		this.clientConnectionManager = clientConnectionManager;
	}


	protected void checkAdress( String adress, int timeout, boolean showMessage) throws IOException
	{
		this.socket = new Socket();
		
		  Debug.out( this ,"now trying : " + adress )  ;
		
		try 
		{
			
			
			 this.socket.connect(new InetSocketAddress(adress, 6666), timeout);
			
			correct = true;
			
			
			
			
			bufferedInputStream = new BufferedInputStream(socket.getInputStream());
			
			outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
			
			createClientConnection();
			
			Debug.out(this, "connection avec succes pour " +  adress);
			
			
			if(correct == false)
				Debug.out(this, "connection refusé pour " +  adress);
			
			 
			
		}
		
		catch(SocketTimeoutException e)
		{
			if (showMessage) 
				Debug.out( this,"Timeout Exception");
			
			
		}
		
		catch(UnknownHostException e)
		{
			
			if (showMessage) 
			{
			
				if (adress == "localhost")
					Debug.out( this,"Il n'y a pas de serveur à cette adresse ");
			
				else
					Debug.out( this," écriture non valide");
			}
			
		}
		catch(SocketException e)
		{
			
			if (showMessage) 
				Debug.out( this,"Pas de serveur pour " + adress  );
		}
		
		if (!correct)
		{
			reset();
		}
		
	}
	
	
	protected void autoCheck() 
	{	
		String adress ;
		
		String baseAdress;
		
			/*try {
				baseAdress = InetAddress.getLocalHost().getHostAddress();
				
				baseAdress = getLocalNetworkAdressModel(baseAdress);
				
				
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}*/
			
			baseAdress = "192.168.0.";
		
			for(int i = 1; i < 255 && !this.correct ; i++)
			{
				adress = baseAdress + i;
				try 
				{
					checkAdress(adress,100, true);
				} 
				
				catch (IOException e) 
				{
					correct = false;
				}
			}

		}
	

	private static String getLocalNetworkAdressModel(String adress)
	{
		int i ;
		
		System.out.println(adress);
		
		for(i = 0; !(adress.charAt(adress.length() - 1 - i) == '.'); i++) {System.out.println(i);
		System.out.println(adress.charAt(i));
		}
		System.out.println(i);
		adress = adress.substring(0, adress.length() - i);
		
		System.out.println(adress);
		
		return adress;
		
	}
	

	
	private void createClientConnection() throws IOException 
	{ 
		
		if(communicationProtocol())
			clientConnectionManager.clientConnection = new ClientConnection(clientConnectionManager,
					name, socket, bufferedInputStream, outputStreamWriter);
		
		clientConnectionManager.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION + " "
				+ "Vous êtes connectés sous le nom " + name, null);
		
		clientConnectionManager.useCommand(CommandManager.NOTIFY_NAME + " " + name, null);
		
	}
	
	private boolean communicationProtocol() throws IOException
	{	
		
		
		
		Debug.out(this, "début protocol");
		
		String message = read(bufferedInputStream);
		
		Debug.out(this, "received : " + message);
		
		if(message.startsWith(ServerManager.CONNECTION_REFUSED))
		{
			
			String reason = message.split(" ")[1];
			
			clientConnectionManager.useCommand(CommandManager.SHOW + " " + reason , null);
			
			this.correct = false;
			
			return false;
		}
		
		else if(message.startsWith(ServerManager.CONNECTION_ACCEPTED))
		{
			Debug.out(this, "connection accepté");
			
			try 
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				Debug.out(this, e);
			}
			
			OutputStreamWriter	outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
			
			outputStreamWriter.write(name);
			outputStreamWriter.flush();
			
			Debug.out(this, "nom envoyé");
			
			message = read(bufferedInputStream);
			
			Debug.out(this, "Extension reçu : " + message);
			
			if (message.split(" ")[0].equals(ServerManager.NAME_EXTENTION))
					{
						if(message.split(" ").length > 1)
						{
							//l'argument de name_extention est 
							
							String nameExtension = message.split(" ")[1];
							name += nameExtension;
							
							Debug.out(this, "new name is : " + name);
						}
							
						
						return true;
					}
			
		}
		
		return false;
		
	}
	
	private void reset()
	{
		
			try 
			{
				if(bufferedInputStream != null)
					bufferedInputStream.close();
				
				if(outputStreamWriter != null)
					outputStreamWriter.close();
				
			} 
			catch (IOException e) {
				Debug.out(this, e);
			}
			
			try 
			{
				if(socket != null)
					socket.close();
			} 
			catch (IOException e) 
			{
				Debug.out(this,e);
			}
		
		
	}
	
	private String read(BufferedInputStream bufferedInputStream) throws IOException
	{      

	    	String response = "";

	    	int stream;

	    	byte[] b = new byte[4096];

	    	stream = bufferedInputStream.read(b);

	    	response = new String(b, 0, stream);

	    	return response;

	}

}
