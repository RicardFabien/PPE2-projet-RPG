package battle;

import commandControl.CommandManager;
import commandControl.Debug;
import serverSideConnection.ServerClientMessage;
import serverSideConnection.ServerManager;
import util.CommandControl;

public class BattleManager {

	protected ServerManager serverManager;
	
	private Battle battle;
	
	final static public String USE = "use";
	public static final String DODGE = "dodge";
	
	final static public String ASK_ACTION = "ask_action";
	final static public String NOT_YOUR_TURN = "not_your_turn";
	final static public String TURN = "turn";
	
	
	final static public String INFORM = "inform";
	
	final static public String HP = "hp";
	final static public String MANA = "mana";
	final static public String LIMIT_BREAK = "limit_break";
	
	final static public String EFFECT_ON = "effect_on";
	final static public String EFFECT_OFF = "effect_off";
	final static public String EFFECT_ACTIVATE = "effect_activate";
	
	
	final static public String USE_ERROR = "use_error";
	final static public String USE_ERROR_UNUSABLE_ABILITY = "use_error_unusable_ability";
	
	
	final static public String GET_CHARACTER = "get_character";
	final static public String INITIALISE = "initialise";
	
	final static public String CHANGE_TEAM = "change_team";
	

	final static public String START = "start";
	final static public String CHARACTER_ERROR = "character_error";
	
	final static public String END_BATTLE = "end_battle";
	final static public String WIN = "win";
	final static public String LOSS = "loss";
	
	
	final static public String DEFAULT_TEAM = "-1";

	
	
	
	public BattleManager(ServerManager serverManager)
	{
		this.serverManager = serverManager;
		this.battle = new Battle(this);
		Debug.out(this, "battleManager is on");
	}
	
	
	public void treatCommand(ServerClientMessage serverClientMessage) 
	{   
		String[] cutBattleCommand = serverClientMessage.text.split(" ");
		
		switch(cutBattleCommand[0])
		{
		case(INITIALISE) : 
			if(cutBattleCommand.length < 3)
			{
				Debug.out(this, "command too short : " + serverClientMessage.text);
				break;
			}
				
			
			serverClientMessage.text = CommandControl.removeIndicatorFromCommand(serverClientMessage.text, INITIALISE);
			
			//team, owner, idChara, attkModif*
			battle.addPlayer(serverClientMessage);
				break;
				
				
		case(CHANGE_TEAM) : 
			if(cutBattleCommand.length < 1)
			{
				Debug.out(this, "command too short : " + serverClientMessage.text);
				break;
			}
			serverClientMessage.text = CommandControl.removeIndicatorFromCommand(serverClientMessage.text, CHANGE_TEAM);
			
			battle.changeTeam(serverClientMessage);
				break;
			
		case(USE) : 
			if(!battle.isStarted)
			{
				Debug.out(this, "battle haven't started yet");
					break;
			}
		
			serverClientMessage.text = CommandControl.removeIndicatorFromCommand(serverClientMessage.text, USE);
			battle.use(serverClientMessage);
				break;
				
			default : 
				Debug.unknownCommandOut(this, serverClientMessage.text);
				break;
		}
	}

	
	protected void askForActingCommand(String owner) 
	{
		serverManager.sendToAllClient(ServerManager.COMBAT_INDICATOR+" "+INFORM+" " + TURN +" " + owner);
		serverManager.sendClient(owner, ServerManager.COMBAT_INDICATOR+" "+ASK_ACTION);
	}
	
	protected void informAll(String information)
	{
		serverManager.sendToAllClient(ServerManager.COMBAT_INDICATOR+" "+INFORM+" " + information);
	}
	
	protected void inform(String owner, String information)
	{
		serverManager.sendClient(owner,ServerManager.COMBAT_INDICATOR+" "+INFORM+" " + information);
	}
	
	protected void initialise( String information)
	{
		serverManager.sendToAllClient(ServerManager.COMBAT_INDICATOR+" "+INITIALISE+" " + information);
	}


	public void startBattle() 
	{
		battle.start();
	}
	
	
	
	

}
