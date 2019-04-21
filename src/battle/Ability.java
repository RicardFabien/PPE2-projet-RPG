package battle;

import commandControl.Debug;

public abstract class Ability {
	
	final public static int MAX_LEVEL = 6;
	
	private String label = "n0";
	private String name = "missing name";
	
	private int level = 1;
	
	private  AbilityType type = AbilityType.OFFENSIVE;;
	
	private  int cooldown = 0;
	private  int currentCooldown = 0;
	
	private  int hpCost = 0;
	private  int manaCost = 0;
	
	private int basePower = 0;
	private float levelPowerModification = 0.2f ;
	protected int power = (int) (getBasePower() * ( 1 + (getLevel()-1) * levelPowerModification)); 
	
	protected boolean isLimitBreak = false;
	
	protected String description = "missing description";
	
	public Ability()
	{
		
	}
	
	public Ability(String name, AbilityType type, int cooldown, int hpCost, int manaCost, int basePower, String description )
	{
		this.name = name;
		
		this.type = type;
		
		this.cooldown = cooldown;
		
		this.hpCost = hpCost;
		this.manaCost = manaCost;
		
		this.basePower = basePower;
		power = (int) (getBasePower() * ( 1 + (getLevel()-1) * levelPowerModification));
		
		this.description = description;
	}
	


	protected void use(Puppet user, Puppet target) throws UnusableAbilityException
	{
		if(! isUsableBy(user))
			throw new UnusableAbilityException();
		
		using(user,target);
		this.currentCooldown = cooldown;
		
		if(this.isLimitBreak)
			user.setCurrentLimitBreak(0);
	}
	
	
	protected abstract void using(Puppet user, Puppet target) ;
	
	
	protected boolean isUsableBy(Puppet user)
	{
		boolean cooldownTest = (currentCooldown <= 0);
		boolean manaTest = (user.getCurrentMana() > getManaCost()); 
		boolean limitBreakTest = ( !this.isLimitBreak || user.getCurrentLimitBreak() > user.getMaxLimitBreak());
		boolean healTest = ( !(user.impotentHealer && this.type == AbilityType.HEAL) );
		 
		return cooldownTest && manaTest && limitBreakTest && healTest;
	}
	
	
	
	
	private void reinitialisePower()
	{
		this.power = (int) (getBasePower() * ( 1 + (getLevel()-1) * levelPowerModification));
	}
	
	
	public enum AbilityType
	{
		OFFENSIVE,BUFF,DEBUFF,HEAL
	}
	
	
	
	protected void setPower(int power)
	{
		this.setBasePower(power);
		
		reinitialisePower();
	}


	public String getName() {
		return name;
	}


	protected void setName(String name) {
		this.name = name;
	}


	protected String getLabel() {
		return label;
	}


	protected void setLabel(String label) {
		this.label = label;
	}


	protected AbilityType getType() {
		return type;
	}


	protected void setType(AbilityType type) {
		this.type = type;
	}


	protected int getHpCost() {
		return hpCost;
	}


	protected void setHpCost(int hpCost) {
		this.hpCost = hpCost;
	}


	public void setLevel(int level) 
	{
		if(level < 1)
			level = 1;
		if(level > MAX_LEVEL)
			level = MAX_LEVEL;
		
		this.level = level;
		
		reinitialisePower();
	}


	protected int getBasePower() {
		return basePower;
	}


	protected void setBasePower(int basePower) {
		this.basePower = basePower;
		
		reinitialisePower();
	}


	protected int getManaCost() {
		return manaCost;
	}


	protected void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}


	public int getLevel() {
		return level;
	}
	
	protected void setCooldown(int cooldown)
	{
		this.cooldown = cooldown;
	}

	public String getDescription()
	{
		return this.description;
	}
	
	public void setLimitBreak(boolean arg)
	{
		this.isLimitBreak = arg;
	}
	
	public boolean isLimitBreak()
	{
		return this.isLimitBreak;
	}
	
	public AbilityType getAbilityType()
	{
		return this.type;
	}
	
}
