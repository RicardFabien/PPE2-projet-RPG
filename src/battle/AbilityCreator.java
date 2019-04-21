package battle;

import battle.Ability.AbilityType;

public abstract class AbilityCreator {
	
	
	
	
	//<------Common ability------>
	
	protected static Ability basicAttack( int power )
	{
		Ability ability = new Ability() 
		{
			@Override
			protected void using(Puppet user, Puppet target)
			{	
				int damage = user.getFinalUsePower(power);
				
				target.loseHP(damage);
			}
		};
		
		ability.setLabel("basic_attack");
		
		ability.setName("attaque basique");
		
		ability.setLevel(1);
		
		ability.setType(AbilityType.OFFENSIVE);
		
		ability.setPower(power);
		
		ability.description = "Une attaque toute simple : un poing, une épée, un pistolet et on tape";
		
		return ability;
		
	}

	//<------Roger----->
	
	//<------Dylan------>
	
	protected static Ability insuranceHeal( int power )
	{
		Ability ability = new Ability() 
		{
			@Override
			protected void using(Puppet user, Puppet target)
			{	
				int heal = power;
				
				if(target.team != user.team)
					heal /= 2;
				
				target.gainHp(heal);
				target.loseMana(getManaCost());
			}
		};
		
		ability.setLabel("insurance_heal");
		
		ability.setName("Soin d'assurance");
		
		ability.setLevel(1);
		
		ability.setType(AbilityType.HEAL);
		
		ability.setBasePower(power);
		
		ability.setManaCost(50);
		
		ability.setCooldown(1);
		
		ability.description = "Un soin dont le coût en mana est prélevé sur la cible, ne coute pas de mana à Dylan ";
		
		return ability;
		
	}
	
	
	
	protected static Ability Pact()
	{
		Ability ability = new Ability() 
		{
			@Override
			protected void using(Puppet user, Puppet target)
			{	
				target.loseHPIgnoreDefense(target.getCurrentHP()/10);
				
				user.applyOn(target, EffectList.OTHER_PACT.getEffectLevel(this.getLevel()));
				user.applyOn(user, EffectList.OWN_PACT.getEffectLevel(this.getLevel()));
			}
		};
		
		ability.setLabel("Blood_Pact");
		
		ability.setName("Pacte de sang");
		
		ability.setLevel(1);
		
		ability.setType(AbilityType.BUFF);
		
		ability.setManaCost(100);
		
		ability.setPower(10);
		
		ability.setCooldown(3);
		
		ability.description = "Dylan fait un pacte avc sa cible : il lui prends  10% de ses Pvs"
				+ " et réduit sa défense mais augmente son attaque, Dylan récupère un bonus d'attaque et de défense";
		
		return ability;
		
	}
	
	
	protected static Ability BouclierDemoniaque()
	{
		Ability ability = new Ability() 
		{
			@Override
			protected void using(Puppet user, Puppet target)
			{	
				user.applyOn(target, EffectList.DEMONIC_SHIELD.getEffect());
			}
		};
		
		ability.setLimitBreak(true);
		
		ability.setLabel("Demonic_shield");
		
		ability.setName("Bouclier demoniaque");
		
		ability.setLevel(1);
		
		ability.setType(AbilityType.BUFF);
		
		ability.description = "Dylan couvre sa cible d'un bouclier, la rendant inataquable mais l'empechant de soigner et réduisant grandement son attaque";
		
		return ability;
		
	}
	
}
