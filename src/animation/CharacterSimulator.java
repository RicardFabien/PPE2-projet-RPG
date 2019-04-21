package animation;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import battle.Ability;

public class CharacterSimulator {
	
	BattleAnimatedPanel battleAnimatedPanel;
	
	String owner;
	
	String name;
	
	protected int maxHP;
	protected int maxMana;
	protected int maxLimitBreak;
	
	protected int currentHP;
	protected int currentMana;
	protected int currentLimitBreak = 0;
	
	protected int x;
	protected int y;
	
	protected HashMap<String, Ability> abilityMap = new HashMap<String, Ability>();
	
	protected Sprite sprite;
	
	public CharacterSimulator(String owner, String name,String team, int maxHp, int maxMana, int maxLimitBreak)
	{
		this.owner = owner;
		
		this.name = name;
		
		this.maxHP = maxHp;
		this.maxMana = maxMana;
		this.maxLimitBreak = maxLimitBreak;
		
		this.currentHP = maxHp;
		this.currentMana = maxMana;
		
		this.sprite = new Sprite(this);
		
	}
	
	public CharacterSimulator(String owner, String name,String team, int maxHp, int maxMana, int maxLimitBreak, HashMap<String, Ability> abilityMap)
	{
		this.owner = owner;
		
		this.name = name;
		
		this.maxHP = maxHp;
		this.maxMana = maxMana;
		this.maxLimitBreak = maxLimitBreak;
		
		this.currentHP = maxHp;
		this.currentMana = maxMana;
		
		this.sprite = new Sprite(this);
		
		this.abilityMap = abilityMap;
		
	}
	
	/*private void drawSprite(Graphics g)
	{
		sprite.draw(g);
	
	}*/

	protected int getX()
	{
	//	return battleAnimatedPanel.getWidth()/2 + x;
		return x;
	}
	
	protected int getY()
	{
		return y;
		//return battleAnimatedPanel.getHeight()/2 + y;
	}
	
	
	protected  void addTime(int time) 
	{
		sprite.clock += time;
	}

	protected void draw(Graphics g)
	{
		//drawSprite( g);
		
		int barHeight = 16;
		int barWidth = 150;
		
		int riseUp = -32; 
		
		g.setColor(Color.red);
		
		g.fillRect(getX(), getY()+riseUp, barWidth, barHeight);

		g.setColor(Color.green);
		
		g.fillRect(getX() , getY()+ riseUp, (int)( barWidth * ((float)currentHP / (float) maxHP)), barHeight);
		
		g.setColor(Color.cyan);
		
		g.drawString(owner, getX(), getY()+ riseUp);
	}
	
	
	
}
