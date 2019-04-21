package battle;

public abstract class Effect {

	protected String label = "n0";
	
	protected String name = "missing name";
	
	protected int turnLeft = 0;
	
	protected Puppet origin;
	
	protected float LevelModifierDamageBoost = 0.0f;
	protected float LevelModifierDefenseBoost = 0.0f;
	
	protected float LevelModifierDamageDebuff = 0.0f;
	protected float LevelModifierDefenseDebuff = 0.0f;
	
	
	protected float damageBoost = 1.0f;
	protected float defenseBoost = 1.0f;
	
	protected float damageDebuff = 1.0f;
	protected float defenseDebuff = 1.0f;
	
	protected boolean invincibilityGiver;
	protected boolean healImpotencyGiver;
	
	protected int dodgeLevel = -1;
	
	protected int level = 1;
	private float levelPowerModifier = 0.2f;
	
	protected int power = 0;
	
	protected boolean infinite;
	
	protected String description = "missing description";
	
	
	public Effect(String label,String name, int turnLeft, String description)
	{
		this.label = label;
		
		this.name = name;
		this.turnLeft = turnLeft;
		
		this.description = description;
	}
	
	public Effect(String label,String name, int turnLeft, String description, int power)
	{
		this(label,name,  turnLeft,description);
		
		this.power = power;
	}
	
	public Effect(String label,String name, int turnLeft, String description, int power, boolean infinite)
	{
		this(label,name,turnLeft,description, power);
		
		this.infinite = true;
	}
	
	protected abstract void onUsedOnAction(Puppet user,Puppet attacker , Ability ability);

	protected abstract void onAction(Puppet user,Puppet target , Ability ability);

	protected abstract void onApplication(Puppet target );
	
	protected abstract void onEndTurn(Puppet user);
	
	
	protected abstract void end(Puppet user);
	
	protected abstract void isInterrupted(Puppet user);
	
	protected void notifyAction(Puppet user)
	{
		user.notifyAction(this);
	}
	
	//<------Setters and getters------>
	
	protected Effect setDodgeLevel(int level)
	{
		this.dodgeLevel = level;
		
		return this;
	}
	
	protected Effect setInvincibilityGiver(boolean arg)
	{
		this.invincibilityGiver = arg;
		
		return this;
	}
	
	protected Effect sethealImpotencyGiver(boolean arg)
	{
		this.healImpotencyGiver = arg;
		
		return this;
	}
	
	protected Effect setLevelModifiers(float LevelModifierdamageBoost, float LevelModifierdefenseBoost, float LevelModifierdamageDebuff, float LevelModifierdefenseDebuff)
	{
		this.LevelModifierDamageBoost = LevelModifierdamageBoost;
		this.LevelModifierDefenseBoost = LevelModifierdefenseBoost;
		
		this.LevelModifierDamageDebuff = LevelModifierdamageDebuff;
		this.LevelModifierDefenseDebuff = LevelModifierdefenseDebuff;
		
		return this;
	}
	
	protected Effect setBuffs(float damageBoost, float defenseBoost, float damageDebuff, float defenseDebuff)
	{
		this.damageBoost = damageBoost;
		this.defenseBoost = defenseBoost;
		
		this.damageDebuff = damageDebuff;
		this.defenseDebuff = defenseDebuff;
		
		return this;
	}
	
	protected float getDamageBoost()
	{
		return damageBoost + LevelModifierDamageBoost* (level - 1) ;   
	}
	
	protected float getDefenseBoost()
	{
		return defenseBoost +  LevelModifierDefenseBoost * (level - 1);
	}
	
	protected float getDamageDebuff()
	{
		return  damageDebuff + LevelModifierDamageDebuff * (level - 1);
	}
	
	protected float getDefenseDebuff()
	{
		return defenseDebuff + LevelModifierDefenseDebuff * (level - 1);
	}
	
	
	protected int getPower()
	{
		return power + (int)((level - 1) * power * levelPowerModifier);
	}
	
	protected void  setLevel(int level)
	{
		this.level = level;
		
	}
}
