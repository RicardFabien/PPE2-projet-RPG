package characterControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.AbstractMap.SimpleImmutableEntry;

import battle.Ability;
import battle.BattleManager;
import battle.CharacterFetcher;
import battle.PuppetCreator;
import commandControl.CommandManager;
import commandControl.Debug;

public class CharacterManager {
	
	public final  static String CHARACTER_EXTENTION = ".char";
	
	protected final String META_FILE_PATH = "saves/meta.meta";
	
	
	public CommandManager commandManager;
	
	protected HashMap<String,SimplifiedPuppet> localPuppetMap = new HashMap<String,SimplifiedPuppet>();
	
	SimplifiedPuppet inUse; 
	
	public CharacterManager(CommandManager commandManager) 
	{
		this.commandManager = commandManager;
		
		//creating local version of puppet
		localPuppetMap = CharacterFetcher.getSimplifiedPuppetMap();
		
	//--
		//testing existence of file directories
		Debug.out(this, "Testing existence of save directory");
		
		File saveDirectory = new File("saves");
		
		if(saveDirectory.exists() && saveDirectory.isDirectory())
			Debug.out(this, "save directory exist");

		else
			saveDirectory.mkdirs();
		
		
		Debug.out(this, "Testing existence of ressource directory");
		
		File ressourceDirectory = new File("ressources");
		
		if(ressourceDirectory.exists() && ressourceDirectory.isDirectory())
			Debug.out(this, "ressource directory exist");
		
		Debug.out(this, "ressource directory exist");
	//--
		
			
//--
		//reading file to update local version of puppet
		for(SimplifiedPuppet p : localPuppetMap.values())
		{
			File saveFile;
			
			if(! new File("saves/" + p.name + CHARACTER_EXTENTION).exists())
				continue;
				
			saveFile = new File("saves/" + p.name + CHARACTER_EXTENTION);
			
			try 
			{
				updateLocalCharacterFromSave(p, saveFile);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		}
//--		
		//Character utilisé
			String metaLine1 = null;

			File metaFile = new File(META_FILE_PATH);
			
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(metaFile));
				metaLine1 = br.readLine().trim();
				br.close();
			} 
			
			catch (IOException e) 
			{
				Debug.out(this, e);
			}
			catch(NullPointerException e1)
			{
				Debug.out(this, "!!!!! meta.meta is empty");
			}
			
			if(localPuppetMap.get(metaLine1) == null)
				changeCharacter(localPuppetMap.values().iterator().next().name);
			else
				changeCharacter(metaLine1);
			
			Debug.out(this, "Currently in use : " + inUse.name);
		
		
	}

	
	
	public void changeCharacter(String characterName)
	{
		SimplifiedPuppet character  = localPuppetMap.get(characterName);
		
		if(character == null)
			return;

		changeMetaCharacterData(characterName);
		
		inUse = character;
		
		System.out.println(character.name);
		
		sendCharacterToServer() ;
		
		useCommand(String.format("%s %s %s",CommandManager.NOTIFY_OWN_CHARACTER, getCharacterString(character), character.getAvailableAbilityPoint()));
	}
	
	
	public void changeMetaCharacterData(String data)
	{
		File metaFile = new File(META_FILE_PATH);

			FileWriter fw;
			try
			{
				fw = new FileWriter(metaFile);
				
				Debug.out(this, "write : " + data);
				fw.write(data);
				fw.flush();
				
				fw.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}

	}
	
	
	public void sendCharacterToServer() 
	{
		useCommand(CommandManager.SEND_COMBAT + " " + BattleManager.INITIALISE +" " + BattleManager.DEFAULT_TEAM +" " + getCharacterString(inUse) );
	}
	
	public void changeTeam(String teamString)
	{
		
		Debug.out(this, "used : " + CommandManager.SEND_COMBAT + " " + BattleManager.CHANGE_TEAM  +" " + teamString);
		useCommand(CommandManager.SEND_COMBAT + " " + BattleManager.CHANGE_TEAM  +" " + teamString );
		
	}
	
	public void changeAbilityLevel(String commandString)
	{
		Debug.out(this, "treated : " + commandString);
		
		String[] cutCommand = commandString.trim().split(" ");
		
		String simplifiedPuppetName = cutCommand[0];
		
		String abilityKey = cutCommand[1];
		
		
		if(cutCommand[2].equals("+"))
		{
			increaseAbilityLevel(simplifiedPuppetName, abilityKey);
				return;
		}
		else if(cutCommand[2].equals("-"))
		{
			reduceAbilityLevel(simplifiedPuppetName, abilityKey);
				return;
		}
			
		int level = 1;
		
		try
		{
			level = Integer.parseInt(cutCommand[2]);
		}
		catch(NumberFormatException e)
		{}
		catch(NullPointerException e1)
		{
				Debug.out(this, e1);
		}
		
		
		
		changeAbilityLevel(simplifiedPuppetName, abilityKey, level);
		
	}
	
	private void changeAbilityLevel(String simplifiedPuppetName, String abilityKey, int level)
	{
		SimplifiedPuppet simplifiedPuppet = localPuppetMap.get(simplifiedPuppetName);
		
		if(simplifiedPuppet == null)
			return;
		
		Ability ability = simplifiedPuppet.abilityMap.get(abilityKey);
		
		if(ability == null)
			return;
		
		ability.setLevel(level);
		
		try
		{
			saveCharacter(simplifiedPuppet.name);
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			Debug.out(this, e);
		}

	}
	
	
	protected void increaseAbilityLevel(String simplifiedPuppetName, String abilityKey)
	{
		SimplifiedPuppet simplifiedPuppet = localPuppetMap.get(simplifiedPuppetName);
		
		if(simplifiedPuppet == null)
			return;
		
		if(simplifiedPuppet.getAvailableAbilityPoint() < 1)
			return;
		
		Ability ability = simplifiedPuppet.abilityMap.get(abilityKey);
		
		if(ability == null)
			return;
		
		ability.setLevel( ability.getLevel() + 1 );
		
		endOfAbilityChange(simplifiedPuppet);

	}
	
	protected void reduceAbilityLevel(String simplifiedPuppetName, String abilityKey)
	{
		SimplifiedPuppet simplifiedPuppet = localPuppetMap.get(simplifiedPuppetName);
		
		if(simplifiedPuppet == null)
			return;
		
		Ability ability = simplifiedPuppet.abilityMap.get(abilityKey);
		
		if(ability == null)
			return;
		
		ability.setLevel(ability.getLevel() - 1);
				
		endOfAbilityChange(simplifiedPuppet);
	}
	
	private void endOfAbilityChange(SimplifiedPuppet simplifiedPuppet)
	{
		useCommand(String.format("%s %s %s",CommandManager.NOTIFY_OWN_CHARACTER, getCharacterString(simplifiedPuppet), simplifiedPuppet.getAvailableAbilityPoint()));
		
		try
		{
			saveCharacter(simplifiedPuppet.name);
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			Debug.out(this, e);
		}
	}
	
	public void addXp(String puppetName, String xpString)
	{
		int xp = 0;
		
		try
		{
			xp = Integer.parseInt(xpString);
		}
		catch(NumberFormatException e)
		{
			Debug.out(this, e);
		}
		
		SimplifiedPuppet puppet = localPuppetMap.get(puppetName);
		
		if( puppet == null )	
		{
			return;
		}
			
		
		puppet.addXp(xp);
		
		try 
		{
			saveCharacter(puppetName);
		}
		catch (FileNotFoundException e) 
		{
			Debug.out(this, e);
		}
		catch (UnsupportedEncodingException e)
		{
			Debug.out(this, e);
		}
	}
	
	
	public void useCommand(String command)
	{
		this.commandManager.useCommand(command);
	}
	
	

	private void updateLocalCharacterFromSave(SimplifiedPuppet simplifiedPuppet , File saveFile ) throws FileNotFoundException, IOException
	{
		
		//prevent the method from updating a puppet that isn't contained in the local array
		if( ! localPuppetMap.containsValue(simplifiedPuppet))
			return;
		
		
		BufferedReader br = new BufferedReader(new FileReader(saveFile));
		//xp
		String line1 = br.readLine();
		//Ability
		String line2 = br.readLine();	
		
		line1 = line1.trim();
		
		line2 = line2.trim();
		
		br.close();
		
		if(line1.equals("") || line2.equals(""))
			return;
		
		
		Debug.out(this, "Charging " + simplifiedPuppet.name);
		
		int xp = 0;
		
		try
		{
			xp = Integer.parseInt(line1.trim());
		}
		catch(NumberFormatException e)
		{
			Debug.out(this, e);
		}
		simplifiedPuppet.xp = xp;
		
		
		String[] cutLine = line2.split(" "); 
		
		for(int i = 0 ; i < cutLine.length; i++)
		{
			String[] s =  cutLine[i].split(":");
		
			int level = 0;
			
			try 
			{
				level = Integer.parseInt(s[1]);
			}
			catch(NumberFormatException| NullPointerException e1)
			{
				continue;
			}
			
			if(simplifiedPuppet.abilityMap.get(s[0]) != null)
				simplifiedPuppet.abilityMap.get(s[0]).setLevel(level);
		}
		
		
	}
	
	

	private void saveCharacter(String characterName) throws FileNotFoundException, UnsupportedEncodingException
	{
		SimplifiedPuppet p = localPuppetMap.get(characterName);
		
		
		String path = String.format("saves/%s%s",characterName , CHARACTER_EXTENTION);
		
		File saveFile = new File(path);
		
		
		
		saveCharacter(p, saveFile);
	}
	
	private void saveCharacter(SimplifiedPuppet simplifiedPuppet ,File saveFile) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer;
		
		writer = new PrintWriter(saveFile, "UTF-8");
		writer.println(simplifiedPuppet.xp);
		writer.println(simplifiedPuppet.getAbilityString());
		writer.close();
		
	}
	
	private String  getCharacterString(SimplifiedPuppet simplifiedPuppet)
	{
		String s = "";
		
		s += simplifiedPuppet.name;
		
		for(String abilityKey : simplifiedPuppet.abilityMap.keySet())
		{
			s += String.format(" %s:%d", abilityKey, simplifiedPuppet.abilityMap.get(abilityKey).getLevel());
		}
		
		s = s.trim();
		
	return s;
		
	}
	
	
	/*private boolean isFileSetViable()
	{
		
	}*/
	
	
	//-----Setters and getters-----//
	
	
	

}
