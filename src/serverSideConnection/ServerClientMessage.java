package serverSideConnection;

public class ServerClientMessage {
	
	public String origin;
	
	public String text;
	
	public ServerClientMessage(String origin, String message)
	{
		this.origin = origin;
		this.text = message;
	}

}
