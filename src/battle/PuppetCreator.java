package battle;

public abstract class PuppetCreator {
	
	
	public final static String MISSING_NO = "MissingNo";
	
	public final static String ROGER = "Roger"; 
	public final static String DYLAN = "Dylan";
	public final static String PATATE = "Patate";
	public final static String NADIR= "Nadir";
	public final static String PIERRE = "Pierre";
	//public final static String
	
	
	protected static Puppet[] allPuppet = { 
			
			Roger(0,"test",null),  Dylan (0,"test",null), MissingNo(0, "test", null)
			
			};
	
	protected static Puppet createPuppet(String puppetName,int team, String owner,Battle battle)
	{
		Puppet p = MissingNo(team, owner,battle);
		
		switch(puppetName)
		{
		case(ROGER) : 
			p = Roger(team,owner, battle);
			break;
		
		case(DYLAN) : 
			p = Dylan(team,owner, battle);
			break;
			
		default : 
			p = MissingNo(team, owner, battle);
			break;
		}
		
		return p;
	}
	
	
	//name , class , team , owner , maxHP , maxMana, limitBreak , shield
	
	
	private static Puppet Roger(int team, String owner,Battle battle)
	{
		Puppet roger = new Puppet("Roger",Puppet.PuppetClass.NUKER, team, owner, 1500, 1500, 0 ,battle);
		
		roger.addAbility(AbilityCreator.basicAttack(300));
		
		return roger;
	}
	
	private static Puppet Dylan (int team, String owner, Battle battle)
	{
		Puppet dylan = new Puppet("Dylan",Puppet.PuppetClass.HEALER, team, owner, 2500, 1500, 0,battle);
		
		dylan.addAbility(AbilityCreator.basicAttack(150));
		dylan.addAbility(AbilityCreator.insuranceHeal(300));
		dylan.addAbility(AbilityCreator.Pact());
		dylan.addUlt(AbilityCreator.BouclierDemoniaque());
		
		dylan.initialiseAddEffect(EffectList.DEMONIC_PACT.getEffect());
		
		return dylan;
	}

	
	private static Puppet MissingNo(int team, String owner, Battle battle)
	{
		Puppet m = new Puppet("MissingNo",Puppet.PuppetClass.NUKER, team, owner, 1500, 1500, 0,battle);
		
		m.addAbility(AbilityCreator.basicAttack(100));
		
		return m;
	}
	
	

}
