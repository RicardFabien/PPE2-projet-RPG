package battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import commandControl.Debug;
import serverSideConnection.ServerClientMessage;
import serverSideConnection.ServerManager;
import util.RNG;

public class Battle {
	
	
	
	/**
	 * BattleManager gerant ce battle, nécessaire au bon fonctionnement du Battle
	 * 
	 * @see BattleManager
	 * 
	 */
	protected BattleManager battleManager;
	
	/**
	 * nombre de point d'experience gagné par les Puppet à la fin de la Battle. <br>
	 * Valeur divisé par deux pour les Puppet perdantes.
	 * 
	 * @see Puppet
	 * 
	 */
	private int xp = 100;

	/**représente le nombre de tour
	 * 
	 * @deprecated non utilisé présentement
	 * 
	 */
	private int turn = 0;
	
	/**
	 * index de la Puppet dont c'est le tour, initialisé à l'appel de la methode start à -1 avant de représenter des valeurs utilisable.
	 * <br> valeur normale en utilisation comprise entre 0 et le nombre de Puppet-1
	 * 
	 *  
	 */
	private int actingPuppetIndex;
	
	private HashMap<String , Puppet> puppetMap = new HashMap<String , Puppet>();
	
	private ArrayList<Puppet> puppetList = new ArrayList<Puppet>();
	
	protected boolean isStarted = false;
	
	
	public Battle(BattleManager battleManager)
	{
		this.battleManager = battleManager;
	}
	
	
	
	/**
	 * Permet de faire traité la commande par le Battle, est censé être utilisé par le Battle Manager 
	 * et ne gere donc pas les erreur d'écriture
	 * 
	 * @param message
	 * 		Le message qui sera traité par la methode
	 * 
	 * @see BattleManager
	 * 
	 */
	protected void use(ServerClientMessage message)
	{
		//use  [ability's id], [target]
		
		String[] cutCommand = message.text.split(" ");
		
		//Puppet dont c'est le tour
		String actingPuppetName = puppetList.get(actingPuppetIndex).owner;
		
		
		if(message.origin.equals(actingPuppetName))
		{
			
			
			Debug.out(this, "command used : " + message.text + " from " + message.origin);
			
			Puppet user = puppetMap.get(message.origin);
			
			try 
			{
				
				
				String abilityId = cutCommand[0];
				Puppet target = puppetMap.get(cutCommand[1]);
				
				user.use(target, abilityId);
				
				endActingPuppetTurn();
				
			}
			
			catch(NullPointerException e)
			{
				battleManager.inform(user.owner, BattleManager.USE_ERROR);
			}
			catch(UnusableAbilityException e)
			{
				battleManager.inform(user.owner, BattleManager.USE_ERROR_UNUSABLE_ABILITY);
				
			}
		}
		
		else
		{
			battleManager.inform(message.origin, BattleManager.NOT_YOUR_TURN);
		}
		
	}
	
	
	private void endActingPuppetTurn()
	{
		puppetList.get(actingPuppetIndex).endTurn();
		
		if(isOngoing())
		{
			Puppet nextPuppet = nextActingPuppet();
			
			nextPuppet.beginTurn();
			
			battleManager.askForActingCommand(nextPuppet.owner);
		}
		else
		{
			endBattle();
		}
	}
	
	private void endBattle()
	{
		int winningTeam = getFirstAlivePlayer().team;
		
		for(Puppet puppet : puppetList)
		{
			String message = BattleManager.END_BATTLE;
			
			if(puppet.team == winningTeam)
			{
				message += " " + BattleManager.WIN + " " + xp;
			}
			else
			{
				message += " " + BattleManager.LOSS + " " + xp/2;
			}
			
			battleManager.inform(puppet.owner, message);
			
			this.actingPuppetIndex = -1;
		}
	}
	
	protected void start()
	{
		if(isStarted)
			return;
		
		Debug.out(this, "battle started");
		
		isStarted = true;
		
		puppetList = getPuppetList(puppetMap);
		
		actingPuppetIndex = -1;
		
		communicateAllPupets();
		
		
		
		battleManager.askForActingCommand(nextActingPuppet().owner);

	}
	
	
	private Puppet nextActingPuppet()
	{	
		actingPuppetIndex++;
		
		if(actingPuppetIndex >= puppetList.size())
		{
			goNextTurn();
		}
		
		return puppetList.get(actingPuppetIndex);
	}
	
	
	private void goNextTurn()
	{
		actingPuppetIndex = 0;
		turn++;
		
	}

	
	private boolean isOngoing()
	{
		int firstTeam = getFirstAlivePlayer().team;
		
		for(Puppet puppet : puppetList)
		{
			if (puppet.isAlive() && puppet.team != firstTeam)
				return true;
		}
		return false;
		
	}

	
	private Puppet getFirstAlivePlayer()
	{
		for(Puppet puppet : puppetList)
		{
			if (puppet.isAlive())
				return puppet;
		}
		
		return null;
	}

	
	
	
	protected void informAll(String information)
	{
		battleManager.informAll(information);
	}
	
	
	
	protected void addPlayer(ServerClientMessage message)
	{
		//initialise// [team], [idChara], [attkModif]*

		message.text = message.text.trim();		
		Debug.out(this, "add player : " + message.text);
		
		String[] cutArgs =message.text.split(" ");
		
		String owner = message.origin;
		
		String characterId = cutArgs[1];
		
		try
		{
			int team = Integer.parseInt(cutArgs[0].trim());  
			
			if(cutArgs[0].trim().equals(BattleManager.DEFAULT_TEAM) && puppetMap.get(owner) != null)
				team = puppetMap.get(owner).team;
			
			puppetMap.put(owner, PuppetCreator.createPuppet(characterId, team, owner,this));
			
			Debug.out(this, "character accepted : " + puppetMap.get(owner).name);
			
			if(cutArgs.length > 2)
			{
				for(int i = 2 ; i < cutArgs.length; i++)
				{
					String[] s =  cutArgs[i].split(":");
					try 
					{
						Debug.out(this, s);
						int levelChange = Integer.parseInt(s[1]);
						
						puppetMap.get(owner).abilityMap.get(s[0]).setLevel(levelChange);
					}
					catch(NumberFormatException |NullPointerException e)
					{
						continue;
					}
					catch(Exception e)
					{
						Debug.out(this, "an error has been detected during ability initialisation : " + e );
					}
					
					
				}
			}
	
		}
		catch(NumberFormatException e)
		{
			battleManager.inform(owner, ServerManager.COMBAT_INDICATOR+ " " + BattleManager.CHARACTER_ERROR);
		}
		
	
	}
	
	
	protected void changeTeam(ServerClientMessage serverClientMessage)
	{
		String owner = serverClientMessage.origin;
		String teamString = serverClientMessage.text.trim();
		
		Debug.out(this, owner +" " + teamString );
		
		if(puppetMap.get(owner) == null)
		{
			return;
		}
			
		
		try
		{
			int team = Integer.parseInt(teamString);
			
			puppetMap.get(owner).team = team;
			
			Debug.out(this, "inform al" );
			battleManager.informAll(BattleManager.CHANGE_TEAM + " " +owner +" "+ teamString );
		}
		catch(NumberFormatException e)
		{
			Debug.out(this, e);
		}

	}
	
	
	
	private void communicateAllPupets()
	{
		for(Puppet puppet : puppetList)
		{
			String abilityInfo = "";
			
			for(Entry<String, Ability> abilityEntry : puppet.abilityMap.entrySet())
			{
				if(abilityEntry.getValue().getLevel() > 1)
					abilityInfo += " " + abilityEntry.getKey() + ":" + abilityEntry.getValue().getLevel() ;
			}
			
			battleManager.initialise( puppet.owner + " " + puppet.name +" "+ puppet.team + abilityInfo );

		}
	}
	
	
	
	private ArrayList<Puppet> getPuppetList(HashMap<String,Puppet> puppetMap)
	{
		ArrayList<Puppet> l = new ArrayList<Puppet>(puppetMap.values());
		
		int i = RNG.generateRandomInt(0, puppetMap.size() -1);
		
		Collections.swap(l, 0, i);
		
		
		 l = damierOrderTeam(l);
		
		 
		return l;
		
	}
	
	
	
	private ArrayList<Puppet> damierOrderTeam(ArrayList<Puppet> l) 
	{
		
		//pour tous les persos
		for(int i = 0 ; i < l.size() - 1; i++)
		{
			
			//Si la team du personnage d'après est differente, c'est bon, on continue
			if(l.get(i).team != l.get(i+1).team)
				continue;
			
			//Sinon, on récupere le prochain de la bonne team et on switch le perso d'apres avec un perso de la bonne team
			else
			{
				int index = getNextEnnemyPlayerTeamIndex(i + 1 ,l);
				
				if (index == -1 )
					break;
				
				else
				{
					Collections.swap(l, i + 1, index);
				}
			}
			
		}
		return l;
	}


	private int getNextEnnemyPlayerTeamIndex(int p ,ArrayList<Puppet> l) 
	{
		for(int i = p + 1 ; i < l.size() ; i++)
		{
			if(l.get(p).team != l.get(i).team )
				return i;
		}

		return -1;
	}

	
	/*<------Setters and getters------>*/
	
	protected int getTurn() 
	{
		return turn;
	}

	protected void setTurn(int turn)
	{
		this.turn = turn;
	}


}
