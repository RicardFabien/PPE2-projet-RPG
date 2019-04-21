package commandControl;

import characterControl.CharacterManager;
import clientSideConnection.ClientConnectionManager;
import gui.WindowManager;
import serverSideConnection.ServerManager;

public class CommandManager {
	
	private ClientConnectionManager clientConnectionManager;
	
	private WindowManager windowManager;
	
	private CharacterManager characterManager;
	
	private ServerManager serverManager;
	
	private Terminal terminal;
	
	//Ensemble de constante representant les differentes commandes
	
	public final static String TERMINAL = "terminal";
	
	public final static String SEND = "send"; 
	public static final String SEND_COMBAT = "send_combat";
	
	public final static String CLIENT_CONNECT = "client_connect";
	public final static String CLIENT_AUTOCONNECT = "client_autoconnect";
	
	public final static String NOTIFY_NAME ="notify_name";
	
	public final static String SHOW = "show";
	public final static String CHANGE_MENU = "change_menu"; 
	public final static String COMMAND_CURRENT_MENU = "command_current_menu"; 
	public final static String COMMAND_MENU = "command_menu"; 
	
	public final static String SEND_CHAT_WINDOW = "send_chat_window";
	public final static String SEND_CHAT_WINDOW_INFORMATION = "send_chat_window_information";
	
	public final static String CHANGE_CHARACTER ="change_character";
	public final static String CHANGE_BATTLE_TEAM = "change_battle_team";
	public final static String CHANGE_ABILITY_LEVEL = "change_abilty_level";
	public final static String NOTIFY_ABILITY = "notify_ability";
	public final static String NOTIFY_OWN_CHARACTER = "notify_own_character";
	public final static String GAIN_XP = "gain_xp";
	
	public final static String SEND_SERVER_CHARACTER = "send_server_character";
	public final static String NOTIFY_CHARACTER ="notify_character";
	
	
	public final static String SERV_ON = "serv_on";
	public final static String SERV_OFF = "serv_off";
	
	public final static String BATTLE_START = "battle_start";
	
	public final static String SEND_COMBAT_SiMULATOR = "send_combat_simulator";

	
	
	
	
	public CommandManager()
	{
		terminal = new Terminal(this);
		
	}
	
	//Interprete la commande
	public void useCommand(String command)
	{
		useCommand(command, null);
	}
	
	public void useCommand(String command, Object object)
	{
		
		//coupe la commande en partie, considerant " " comme la separation
		//ex : "atk Roger" donne "atk" et "Roger" , "send" donne juste "send"
		
		//on appelera ici "pseudo-argument" la deuxieme partie de la commande ("Roger" dans l'exemple précedent)
		
		String[] cutCommand = command.split(" ");
	
		
		Debug.out(this , cutCommand);
		
			//chaque case contient une command à effectuer
		switch(cutCommand[0])
		{
				//essaie de se connecter avec l'adresse fourni
			case(CLIENT_CONNECT) : 
				{
					if (cutCommand.length <= 1)
					
						break;
					
					clientConnectionManager.tryConnect(cutCommand[1]);
					break;
				}
			
			case(CLIENT_AUTOCONNECT) : 
			{
				clientConnectionManager.tryConnect(ClientConnectionManager.AUTO_CHECK_INDICATOR);
				break;
			}
		
				//recupere tout apres "SEND" et l'envoie au connectionManager à travers sa methode .send
			case(SEND) : 
			{
				if (cutCommand.length <= 1)
					break;
				
				String clientServerMessage = getAllArgument(cutCommand);
				
				clientConnectionManager.sendChat(clientServerMessage);
				break;
			}
				
				
			case(SEND_COMBAT) : 
			{
				if (cutCommand.length <= 1)
					break;
				
				String clientServerCombatMessage = getAllArgument(cutCommand);
				
				clientConnectionManager.sendCombat(clientServerCombatMessage);
				break;
			}
				
		
				//change le menu de la fenetre en le remplaçant par celui dont le nom est le pseudo-argument
			case(CHANGE_MENU) : 
				
				windowManager.changeMenu(cutCommand[1]);
				break;
				
			case(COMMAND_CURRENT_MENU) :
				
				String CurrentMenuCommand = getAllArgument(cutCommand);
				
				windowManager.treatCommandCurrentMenu(CurrentMenuCommand);
				break;
				
			case(COMMAND_MENU) :
				
				String menuCommand = getAllArgument(cutCommand);
				
				windowManager.treatCommand(menuCommand);
				break;
				
				//Allume le serveur
			case(SERV_ON) : 
				serverManager.turnOn();
				break;
				
				//éteint le serveur
			case(SERV_OFF) : 
				serverManager.turnOff();
				break;
			
			case(BATTLE_START) : 
				serverManager.startBattle();
				break;
				
				
			case(TERMINAL) : 
				if(terminal.isVisible())
					terminal.hide();
				else
					terminal.show();
				break;
				
			case(SEND_CHAT_WINDOW) :
				if (cutCommand.length <= 1)
					break;
				
				String chatMessage = getAllArgument(cutCommand);
				
				windowManager.sendToChat(chatMessage);
				break;
				
			case(SEND_CHAT_WINDOW_INFORMATION) :
				if (cutCommand.length <= 1)
					break;
				
				String informationMessage = getAllArgument(cutCommand);
				
				windowManager.sendRawToChat(informationMessage);
				break;
				
			case(SEND_SERVER_CHARACTER) :
				characterManager.sendCharacterToServer();
				break;
				
			case(CHANGE_CHARACTER) : 
				if (cutCommand.length <= 1)
					break;
				
				String character = getAllArgument(cutCommand);
				characterManager.changeCharacter(character);
					break;
				
			case(NOTIFY_CHARACTER) : 
				if (cutCommand.length <= 1)
					break;
			windowManager.treatCommand(WindowManager.FIGHT+" "+NOTIFY_CHARACTER+" " +cutCommand[1]);
				break;
				
			case(NOTIFY_NAME) : 
				if (cutCommand.length <= 1)
					break;
			windowManager.treatCommand(WindowManager.FIGHT+" "+NOTIFY_NAME+" " +cutCommand[1]);
				break;
				
			case(CHANGE_BATTLE_TEAM) : 
				if (cutCommand.length <= 1)
					break;
					
			String teamString = getAllArgument(cutCommand);
			
			characterManager.changeTeam(teamString);
					break;
				
					
			case(CHANGE_ABILITY_LEVEL) : 
				if (cutCommand.length <= 3)
					break;
					
			String abilityChangeString = getAllArgument(cutCommand);
			
			characterManager.changeAbilityLevel(abilityChangeString);
					break;
				
			case(NOTIFY_ABILITY) : 
			{
				if (cutCommand.length <= 1)
					break;
				windowManager.treatCommand(WindowManager.CHARACTER_SELECTION+" "+NOTIFY_ABILITY+" " +getAllArgument(cutCommand));
					break;
			}
			
			case(NOTIFY_OWN_CHARACTER) : 
			{
				if (cutCommand.length <= 1)
					break;
				windowManager.treatCommand(WindowManager.CHARACTER_SELECTION+" "+NOTIFY_OWN_CHARACTER+" " +getAllArgument(cutCommand));
					break;
			}
			
			
			case(GAIN_XP) : 
			{
				if (cutCommand.length < 3)
					break;
				
				characterManager.addXp(cutCommand[1], cutCommand[2]);
					break;
			}
			
			//indique que la commande est inconnu
			default : 
				Debug.out(this, "unknown command");
				
		}
	}
	
	
	private String getAllArgument(String[] cutCommand)
	{
		String arguments = "";
		
		for(int i = 1 ; i < cutCommand.length; i++)
			arguments +=" " + cutCommand[i];
		
		arguments = arguments.trim();
		
		 return arguments;
		
	}
	
	//<-----Setters----->
	
	public void setWindowManager(WindowManager windowManager)
	{
		this.windowManager = windowManager;
		windowManager.guiCommandManager = this;
	}
		
		
	public void setClientConnection(ClientConnectionManager connectionManager)
	{
		this.clientConnectionManager = connectionManager;
		connectionManager.commandManager = this;
	}
		
	public void setCharacterManager(CharacterManager characterManager)
	{
			this.characterManager = characterManager;
			characterManager.commandManager = this;
	}
		
	public void setServerManager(ServerManager serverManager)
	{
			this.serverManager = serverManager;
			serverManager.commandManager = this;
	}

}
