package battle;

import java.util.ArrayList;
import java.util.HashMap;

import animation.CharacterSimulator;
import characterControl.SimplifiedPuppet;

public abstract class CharacterFetcher {
	
	public static CharacterSimulator getCharacter(String owner, String name,String team, String arguments)
	{
		Puppet p = PuppetCreator.createPuppet(name, 0, owner, null);
		
		int maxHp = p.getMaxHP();
		int maxMana = p.getMaxMana();
		int maxLimitBreak = p.getMaxLimitBreak();
		
		CharacterSimulator c = new CharacterSimulator(owner, name,team, maxHp, maxMana, maxLimitBreak, p.abilityMap);
		
		return c;

	}
	
	
	public static SimplifiedPuppet getSimplifiedPuppet(String name)
	{
		String owner = "default";
		
		Puppet p = PuppetCreator.createPuppet(name, 0, owner, null);
		
		SimplifiedPuppet s = new SimplifiedPuppet(p);
		
		
		return s;
	}
	
	public static HashMap<String, SimplifiedPuppet> getSimplifiedPuppetMap()
	{
		HashMap<String, SimplifiedPuppet> h = new HashMap<String, SimplifiedPuppet>();
		
		for(Puppet p :PuppetCreator.allPuppet)
		{
			h.put(p.name,new SimplifiedPuppet(p));
		}
		
		return h;
	}
	
	public static ArrayList<SimplifiedPuppet> getAllSimplifiedPuppet()
	{
		ArrayList<SimplifiedPuppet> s = new ArrayList<SimplifiedPuppet>();
		
		for(Puppet p :PuppetCreator.allPuppet)
		{
			s.add(new SimplifiedPuppet(p));
		}
			
		return s;
			
	}


}
