package battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import battle.Ability.AbilityType;
import commandControl.Debug;
import util.RNG;

public class Puppet {
	
	public static final String ULT_KEY = "ult";
	
	protected enum PuppetClass
	{
		NUKER,HEALER;
		
		public String toString()
		{
			if(this == NUKER)
			{
				return "NUKER";
			}
			else if (this == HEALER)
			{
				return "HEALER";
			}
			
			return "N0";
		}
	}

	protected Battle battle;
	
	
	protected PuppetClass classe;
	
	protected String name;
	
	protected boolean isAI;
	
	protected int team;
	
	
	protected String owner;
	
	
	
	private int maxHp;
	
	private int maxMana;
	
	
	
	private int shield = 0;
	
	private int currentHP = maxHp;
	
	private int currentMana = maxMana;
	
	
	private float damageBoost = 1.0f;
	private float protectionBoost = 1.0f;
	
	private float damageDebuff = 1.0f;
	private float defenseDebuff = 1.0f;
	
	
	private float baseDamageBoost = 1.0f;
	private float baseProtectionBoost = 1.0f;
	
	private float baseDamageDebuff = 1.0f;
	private float baseDefenseDebuff = 1.0f;
	
	
	protected boolean impotentHealer;
	
	protected boolean invincible;
	
	protected int dodgeLevel = -1;
	
	
	private int limitBreakHpLossPercent = 70;
	
	private int maxLimitBreak = 100 ;
	
	private int currentLimitBreak = 0;
	
	private final int limitBreakTurnGain = 10;
	
	
	protected HashMap<String, Ability> abilityMap = new HashMap<String, Ability>();
	
	
	private List<Effect> effectList = new ArrayList<Effect>();
	
	
	protected Puppet(String name,PuppetClass classe, int team, String owner, int maxHp, int maxMana, int shield, Battle battle)
	{
		this.battle = battle;
		
		this.name = name;
		
		this.classe = classe;
		this.team =team;
		this.owner = owner;
		
		this.maxHp = maxHp;
		this.maxMana = maxMana;
		
		this.currentHP = this.maxHp;
		this.currentMana = this.maxMana;
		
		this.setMaxLimitBreak(maxLimitBreak);
	}
	
	
	protected void isUsedOn(Puppet attacker , Ability ability)
	{
		
		for(Effect effect : effectList)
		{
			effect.onUsedOnAction(this, attacker, ability);
		}
	}
	
	protected void use(Puppet target, String abilityId) throws UnusableAbilityException
	{
		Ability ability;
		
		ability = abilityMap.get(abilityId);
		
		if(ability == null)
			throw new NullPointerException();
		
		if(! ability.isUsableBy(this))
			throw new UnusableAbilityException();
		
		
		use(target,ability);
	}
			
	
	private void use(Puppet target, Ability ability) throws UnusableAbilityException
	{
		
		battle.informAll(BattleManager.USE + " " + this.owner + " " + ability.getLabel() + " " + target.owner );
		
		if(target.dodge(ability))
		{
			battle.informAll(BattleManager.DODGE + " " + target.owner );
				return;
		}
		
		
		target.isUsedOn(this, ability);
		
		for(Effect effect : effectList)
		{
			effect.onAction(this, target, ability);
		}
		
		ability.use(this, target);
		
	}
	
	
	
	protected void loseHP(int amount)
	{
		
		amount *= defenseDebuff;
		amount /= protectionBoost;
		
		loseRawHp(amount);
		
	}
	
	protected void loseHPIgnoreDefense(int amount)
	{
		loseRawHp(amount);
	}
	
	private void loseRawHp(int amount)
	{
		Debug.out(this,this.owner + " loses " + amount +" hp");
		
		if(invincible)
			amount = 0;
		
		if(shield < amount)
		{
			currentHP -= (amount - shield);
			shield = 0;
		}
			
		else
			shield -= amount; 
		
		
		gainLimitBreak( (int)(  ( (double)amount / (double)this.currentHP ) * 100 * 100 / limitBreakHpLossPercent ) );
		informHPChange();
	}
	
	
	protected void gainHp(int amount)
	{
		Debug.out(this,this.owner + " gain " + amount +" hp");
		
		this.currentHP += amount;
		if(this.currentHP > this.maxHp)
		{
			this.currentHP = this.maxHp;
		}
		
		informHPChange();
	}
	
	
	protected int getFinalUsePower(int baseDamage)
	{
		int d = RNG.variation(baseDamage, 3);
		d = (int)( ((double) d) * damageBoost/damageDebuff );
		
		Debug.out(this, "spell has a raw power of " +  d);
		
		return d;
	}
	
	
	protected void loseMana(int amount)
	{
		this.currentMana -= amount;
		
		if(currentMana < 0)
			currentMana = 0;
		Debug.out(this,this.owner + " loses " + amount +" mana");
		
		informManaChange();
		
	}
	
	
	protected void gainMana(int amount)
	{
		this.currentMana += amount;
		
		if(currentMana > maxMana)
			currentMana = maxMana;
		
		Debug.out(this,this.owner + " gain " + amount +" mana");
		
		informManaChange();
	}
	
	protected void gainLimitBreak(int amount)
	{
		currentLimitBreak += amount;
		
		informLimitBreakChange();
	}
	
	protected void resetLimitBreak()
	{
		currentLimitBreak = 0;
		
		informLimitBreakChange();
	}
	
	
	protected boolean dodge(Ability ability)
	{
		return ability.getType() == AbilityType.OFFENSIVE && this.dodgeLevel >= 0 && this.dodgeLevel > ability.getLevel() - 2;
	}
	
	
	
	protected void informLimitBreakChange()
	{
		battle.informAll(BattleManager.LIMIT_BREAK + " " + this.owner + " " + currentLimitBreak);
	}
	
	
	private void informHPChange()
	{
		battle.informAll(BattleManager.HP + " " + this.owner + " " + currentHP); 
	}
	
	private void informManaChange()
	{
		battle.informAll(BattleManager.MANA + " " + this.owner + " " + currentMana); 
	}
	
	protected void beginTurn()
	{
	
	}
	
	
	protected void endTurn()
	{
		gainLimitBreak(limitBreakTurnGain);
		decrementTurnEffectList();
	}
	
	
	private void decrementTurnEffectList()
	{
		Iterator<Effect> it = effectList.iterator();
		
		boolean update = false;
		
		while(it.hasNext())
		{
			Effect e = it.next();
			
			if( ! e.infinite)
				e.turnLeft--;
			
			if(e.turnLeft <  0 &&  ! e.infinite)
			{
				removeEffect(e, it);
				
				update = true;
			}
		}
		
		if(update == true)
			updateStatusEffect();

	}
	
	
	protected void updateStatusEffect()
	{
			this.damageBoost = baseDamageBoost;
			this.protectionBoost = baseProtectionBoost;
			
			this.damageDebuff = baseDamageDebuff;
			this.defenseDebuff = baseDefenseDebuff;
			
			this.dodgeLevel = -1;
			
			this.impotentHealer = false;
			this.invincible = false;
			
		
		for(Effect effect : effectList)
		{
			this.damageBoost *= effect.getDamageBoost();		
			this.protectionBoost *= effect.getDefenseBoost();
			
			this.damageDebuff *= effect.getDamageDebuff();
			this.defenseDebuff *= effect.getDefenseDebuff();
			
			this.impotentHealer = (this.impotentHealer || effect.healImpotencyGiver);
			this.invincible = (this.invincible || effect.invincibilityGiver);
			
			if(this.dodgeLevel < effect.dodgeLevel)
				this.dodgeLevel = effect.dodgeLevel;
		}
	}
	
	
	protected void die()
	{
		
	}
	
	protected boolean isAlive()
	{
		return this.currentHP > 0;
				
	}
	
	
	protected void applyOn(Puppet target, Effect e)
	{
		e.origin = this;
		
		target.addEffect(e);
	}
	
	protected void addEffect(Effect e)
	{
		e.onApplication(this);
		
		battle.informAll(BattleManager.EFFECT_ON + " " + this.owner + " " + e.label+" " + e.level +
				" " + e.turnLeft + " " + e.origin.owner ); 

		effectList.add(e);
		updateStatusEffect();
	}
	
	protected void removeEffect(Effect e, Iterator<Effect> it)
	{
		e.end(this);
		
		battle.informAll(BattleManager.EFFECT_OFF + " " + this.owner + " " + e.label);
		
		it.remove();
	}
	
	protected void notifyAction(Effect effect) 
	{
		battle.informAll(BattleManager.EFFECT_ACTIVATE + " " + this.owner + " " + effect.label);
	}
	
	//<------Initialization------>//
	
	protected void initialiseAddEffect(Effect e)
	{
		e.origin = this;
		effectList.add(e);
	}
	
	protected void addAbility(Ability ability)
	{
		int n = 0;
		
		if(abilityMap.get(ULT_KEY) == null)
			n = abilityMap.size() + 1;
		
		else
			n = abilityMap.size();
	
		abilityMap.put(Integer.toString(n), ability);
	}
	
	protected void addUlt(Ability ability)
	{
		abilityMap.put(ULT_KEY, ability);
	}
	
	/*<------ Search fonction and util ------>*/
	
	protected boolean hasEffectLabeled(String label)
	{
		for(Effect e : effectList)
		{
			if(e.label.equals(label))
				return true;
		}
		
		return false;
	}
	
	/*<------getters and setters------>*/
	
	public String getName()
	{
		return this.name;
	}
	
	public HashMap<String, Ability> getAbilityMap()
	{
		return this.abilityMap;
	}
	
	public String getClasseName()
	{
		return this.classe.toString();	
	}
	
	protected int getCurrentMana()
	{
		return this.currentMana;
	}
	
	protected int getCurrentHP()
	{
		return this.currentHP;
	}
	
	protected int getMaxHP()
	{
		return this.maxHp;
	}
	
	protected int getMaxMana()
	{
		return this.maxMana;
	}


	protected float getDamageBoost() {
		return damageBoost;
	}


	protected void setDamageBoost(float damageBoost) {
		this.damageBoost = damageBoost;
	}


	protected float getProtectionBoost() {
		return protectionBoost;
	}


	protected void setProtectionBoost(float protectionBoost) {
		this.protectionBoost = protectionBoost;
	}


	protected int getMaxLimitBreak() {
		return maxLimitBreak;
	}


	protected void setMaxLimitBreak(int maxLimitBreak) {
		this.maxLimitBreak = maxLimitBreak;
	}


	protected int getCurrentLimitBreak() {
		return currentLimitBreak;
	}


	protected void setCurrentLimitBreak(int currentLimitBreak) {
		this.currentLimitBreak = currentLimitBreak;
	}




	
}
