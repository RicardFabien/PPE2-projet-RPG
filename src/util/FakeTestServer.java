package util;

import java.util.Scanner;

import battle.BattleManager;
import serverSideConnection.ServerManager;

public class FakeTestServer extends ServerManager {

	Scanner sc = new Scanner(System.in);
	
	
	
	public void sendClient(String nameClient , String message)
	{
		System.out.println("sent to client " + nameClient + ":" + message);
	}
	
	public void sendToAllClient(String message)
	{
		System.out.println("sent to all client :"+message);
	}
	
	public void run()
	{
		while(true)
		{	String message = sc.nextLine();
		
		//	sendToBattleManager(message);
			
			if(message.equals(BattleManager.START))
				startBattle();
				
				

			
		}
	}
		
	
}
