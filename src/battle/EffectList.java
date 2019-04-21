package battle;

public enum EffectList {
	
	POISON
	(
		 new Effect("Poison","Poison", 1, "Un poison infligeant un certain nombre de dégat par tour", 100)
		 {
			 	
				@Override
				protected void onUsedOnAction(Puppet user, Puppet attacker, Ability ability){}
				@Override
				protected void onAction(Puppet user, Puppet target, Ability ability) {}
				@Override
				protected void onApplication( Puppet target){}
				@Override
				protected void onEndTurn(Puppet user)
				{
					notifyAction(user);
					user.loseHPIgnoreDefense(power);
				}

				@Override
				protected void end(Puppet user){}
				@Override
				protected void isInterrupted(Puppet user){}
				
		}
			
	),
	
	DEMONIC_PACT
	(
			new Effect("demonic_pact","Pacte demoniaque", 42, "Attaquer Dylan retire de la mana à l'attaquant"
					+ "", 100,true) 
			{

				@Override
				protected void onUsedOnAction(Puppet user, Puppet attacker, Ability ability) 
				{
					if(ability.getType() == Ability.AbilityType.OFFENSIVE)
					{
						notifyAction(attacker);
						attacker.loseMana(power);
					}
				}

				@Override
				protected void onAction(Puppet user, Puppet target, Ability ability) {}
				@Override
				protected void onApplication( Puppet target) {}

				@Override
				protected void onEndTurn(Puppet user) {}
				@Override
				protected void end(Puppet user) {}
				@Override
				protected void isInterrupted(Puppet user) {}
				
			}
	),
	
	OTHER_PACT
	(
			new Effect("other_pact", "Pacte de sang", 3, "Un pacte à été réalisé avec Dylan")
			{	
				
				
				
				@Override
				protected void onUsedOnAction(Puppet user, Puppet attacker, Ability ability) {}
				@Override
				protected void onAction(Puppet user, Puppet target, Ability ability) {}
				@Override
				protected void onApplication(Puppet target) {}
				@Override
				protected void onEndTurn(Puppet user) {}
				@Override
				protected void end(Puppet user) {}
				@Override
				protected void isInterrupted(Puppet user) {}
				
		
			}.setLevelModifiers(0.2f,0f,0f,0.1f).setBuffs(1.2f,1.0f,1.0f,1.2f)
	),
	
	OWN_PACT
	(
			new Effect("own_pact", "Pacte de sang", 3, "Dylan à réalisé un pacte")
			{	
				
				
				
				@Override
				protected void onUsedOnAction(Puppet user, Puppet attacker, Ability ability) {}
				@Override
				protected void onAction(Puppet user, Puppet target, Ability ability) {}
				@Override
				protected void onApplication(Puppet target) {}
				@Override
				protected void onEndTurn(Puppet user) {}
				@Override
				protected void end(Puppet user) {}
				@Override
				protected void isInterrupted(Puppet user) {}
				
		
			}.setLevelModifiers(0.2f,0.2f,0f,0f).setBuffs(1.2f,1.2f,1.0f,1.0f)
	),
	
	DEMONIC_SHIELD
	(
			new Effect("demonic_shield_effect","Bouclier demoniaque", 1, "Un bouclier empechant de prendre des dégats, réduisant l'attaque et empechant de soigner") 
			{

				@Override
				protected void onUsedOnAction(Puppet user, Puppet attacker, Ability ability) 
				{}
				@Override
				protected void onAction(Puppet user, Puppet target, Ability ability) {}
				@Override
				protected void onApplication( Puppet target)
				{
					
				}

				@Override
				protected void onEndTurn(Puppet user) {}
				@Override
				protected void end(Puppet user) {}
				@Override
				protected void isInterrupted(Puppet user) {}
				
				
			}.sethealImpotencyGiver(true).setInvincibilityGiver(true)
	),
	
	
	;
	
	
	
	
	private final Effect effect;
	
	EffectList(Effect e)
	{
		this.effect = e;
	}
	
	protected Effect getEffect()
	{
		return this.effect;
	}
	
	protected Effect getEffectLevel(int level)
	{
		Effect e = this.effect;
		
		e.setLevel(level);
		
		if(level == Ability.MAX_LEVEL)
			e.turnLeft++;
		
		return e;
	}
	
	protected Effect getEffectLevelNoTurnIncrementation(int level)
	{
		Effect e = this.effect;
		
		e.setLevel(level);
		
		if(level == Ability.MAX_LEVEL)
			e.turnLeft++;
		
		return e;
	}
	
	

}
