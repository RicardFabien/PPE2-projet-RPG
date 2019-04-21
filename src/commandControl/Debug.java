package commandControl;

import java.util.ArrayList;
import java.util.List;

public class Debug {
	
	private static List<Terminal> terminalList = new ArrayList<Terminal>();
	
	
	
	public static void out(Object caller, String log)
	{
		sendingOut( "[" + caller.getClass() + "] :|" +  log);
	}
	
	public static void out(Object caller, boolean booleanLog)
	{
		sendingOut( "[" + caller.getClass() + "] :|" +  booleanLog);
	}
	
	public static void out(Object caller, Throwable throwableLog)
	{
		String log = throwableLog.getMessage();
		
		sendingOut( "[" + caller.getClass() + "]|: " +  log);
	}
	
	
	//aditionne les differentes parts de l'array de log
	public static void out(Object caller, String[] logArray)
	{
		String debugLog = "";
		
		for(String part : logArray)
		{
			debugLog += "|" + part ;
		}
		
		sendingOut( "[" + caller.getClass() + "] : " +  debugLog);
	}
	
	public static void unknownCommandOut(Object caller, String command)
	{
		out(caller, "unknown command : " + command);
	}
	
	
	
	
	
	private static void sendingOut(String message)
	{
		useSenderThread(message);
		System.out.println(message);
	}
	
	private static void useSenderThread(String message)
	{
		(new Thread() 
		{
			
			@Override
			public void run() 
			{
				sendToTerminals(message + "\n");
			}
			
		}).start();
	}
	
	private synchronized static void sendToTerminals(String message)
	{
		for(Terminal terminal : terminalList)
		{
			terminal.showCommand(message);
		}
	}
	
	protected static void addSelfToList(Terminal terminal)
	{
		terminalList.add(terminal);
	}
	

}
