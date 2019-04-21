package characterControl;

import java.util.HashMap;

import battle.Ability;
import battle.Puppet;

public class SimplifiedPuppet 
{
	
	protected final static int XP_COST_PER_POINT = 100;
	
	
	String name = "n0";
	
	public HashMap<String , Ability > abilityMap = new HashMap<String, Ability>();
	
	String type = "n0";
	
	int xp = 0;
	
	
	
	public SimplifiedPuppet(Puppet p)
	{
		this.name = p.getName();
		
		this.type = p.getClasseName();
		
		this.abilityMap = p.getAbilityMap();
	}
	
	protected String getAbilityString()
	{
		String s = "";
		
		for(String key : abilityMap.keySet())
		{
			s += String.format("%s:%s ", key, abilityMap.get(key).getLevel());
		}
		
		return s;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	protected int getUsedAbilityPoint()
	{
		int usedAbilityPoint = 0;
		
		for(Ability a : abilityMap.values())
		{
			usedAbilityPoint += a.getLevel() - 1;
		}
		
		return usedAbilityPoint;
	
	}
	
	protected int getAvailableAbilityPoint()
	{
		int availableAbilityPoint = (xp/XP_COST_PER_POINT) -  getUsedAbilityPoint();
		
		return availableAbilityPoint;
		
	}
	
	protected void addXp(int amount)
	{
		this.xp += amount;
	}
}
