package animation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import battle.Ability;
import battle.Ability.AbilityType;
import battle.BattleManager;
import battle.CharacterFetcher;
import commandControl.CommandManager;
import commandControl.Debug;
import gui.Menu;

public class BattleAnimatedPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 15646146865198L;
	
	protected Menu menu;
	private Vector<String> messageVector = new Vector<String>();
	private String message = "";
	
	private String ownName = "";
	
	private int timerTime =20;
	private Timer timer =new Timer(timerTime,this) ;
	
	private int messageClock = 0;
	private int messageClockChange = 2000;
	
	HashMap<String, CharacterSimulator> characterMap = new HashMap<String, CharacterSimulator>();
	
	private JPanel figthPanel = new JPanel(new GridLayout(0, 2));
	private JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
	
	private ArrayList<JButton> buttonList = new ArrayList<JButton>();
	
	public BattleAnimatedPanel(Menu menu)
	{
		this.menu = menu;
		timer.setRepeats(true);
		timer.start();
		
		
		
		
		this.setLayout(new GridLayout(0, 2));
		
		this.add(figthPanel);
		this.add(buttonPanel);
		
		for(int i = 0; i < 4; i++)
			buttonList.add(new JButton(""+i));
		
		for(JButton b : buttonList)
			buttonPanel.add(b);
		
		
		
		
		
		
		this.setPreferredSize(new Dimension(400, 400));
	}
	
	public void addCharacter(String ownerName, String characterName,String team, String arguments)
	{
		CharacterSimulator c = CharacterFetcher.getCharacter(ownerName, characterName, team, arguments);
		
		
		c.battleAnimatedPanel = this;
		
		characterMap.put(ownerName, c );
		
		Iterator<Ability> it = c.abilityMap.values().iterator();
		
		for(int i=0; it.hasNext(); i++ )
		{
			Ability a = it.next();
			
			JButton b = buttonList.get(i);
			
			b.setText(a.getName());
			
			switch(a.getAbilityType())
			{
			case BUFF:
				b.setBackground(Color.BLUE);
				break;
			case DEBUFF:
				b.setBackground(Color.orange);
				break;
			case HEAL:
				b.setBackground(Color.green);
				break;
			case OFFENSIVE:
				b.setBackground(Color.red);
				break;
			}
		}
			
		
		
		JPanel p = new JPanel();
		p.setBackground(Color.red);
		
		if(ownerName.equals(ownName))
			p.setBackground(Color.BLUE);
		
		figthPanel.add(p);
		
		
		
		
		
		
	//	this.add(new Sprite(c));
		
		menu.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION+ " " 
				+ c.name +"("+ ownerName+")" +" rejoint la bataille dans l'équipe " + team + " ("+c.currentHP+","+c.currentMana+")");
		
	}
	
	public void setOwnName(String name)
	{
		this.ownName = name;
	}
	
	public void addMessage(String message)
	{
		messageVector.add(message);
	}
	
	
	public void informCurrentTurn(String ownerName)
	{
		showToAll("C'est le tour de " +ownerName );
	}
	
	public void informYourTurn()
	{
		showToAll("Votre tour");
	}
	
	public int hpChange(int newHP, String ownerName)
	{
		int oldHP = characterMap.get(ownerName).currentHP;
		
		characterMap.get(ownerName).currentHP = newHP;
		
		int hpLost = oldHP - characterMap.get(ownerName).currentHP;
		int i = hpLost;
		
		String w;
		
		if(hpLost >= 0)
		{
			w = "perd";
		}
		else
		{
			i= -hpLost;
			w = "gagne";
		}
		
		
		/*String toSend = String.format("%s %s %s %d pv (%d) ", CommandManager.SEND_CHAT_WINDOW_INFORMATION
				,ownerName, w,i ,characterMap.get(ownerName).currentHP );*/
		
		 showToAll( ownerName +" " +w+" "+ i +"pv ("+characterMap.get(ownerName).currentHP+")");
				
		//menu.useCommand(toSend);
		
	
		return hpLost;
	}
	
	
	
	public int manaChange(int newMana, String ownerName)
	{
		int oldMana = characterMap.get(ownerName).currentMana;
		
		characterMap.get(ownerName).currentMana = newMana;
		
		int manaLost = oldMana - characterMap.get(ownerName).currentMana;
		int i = manaLost;
		
		String w;
		
		if(manaLost >= 0)
		{
			
			w = "perd";
		}
		else
		{
			w = "gagne";
			i= -manaLost;
		}
		
		 showToAll( ownerName +" " +w+" "+ i +" mana ("+ characterMap.get(ownerName).currentMana + ")");
		return manaLost;

	}
	
	public int limitBreakChange(int newLimitBreak, String ownerName)
	{
	int oldLimitBreak = characterMap.get(ownerName).currentLimitBreak;
		
		characterMap.get(ownerName).currentLimitBreak = newLimitBreak;
		
		int limitBreakLost = oldLimitBreak - characterMap.get(ownerName).currentLimitBreak;
		int i = limitBreakLost;
		
		String w;
		
		if(limitBreakLost >= 0)
		{
			
			w = "perd";
		}
		else
		{
			w = "gagne";
			i= -limitBreakLost;
		}
		
		 showToAll( ownerName +" " +w+" "+ i +" limit ("+ characterMap.get(ownerName).currentLimitBreak + ")");
		return limitBreakLost;
	}
	
	
	public void putEffect(String ownerName,String effectLabel,int level,  int turnLeft,String origin)
	{
		
		 showToAll(ownerName +" " +"gagne l'effet " + effectLabel+" niveau "+ level + " pour "+ turnLeft+" tour"+
				" venant de " + origin );
	}
	
	public void removeEffect(String ownerName,String effectLabel)
	{
		
		 showToAll(ownerName +" " +"perd l'effet " +effectLabel);
	}
	
	public void notifyEffectActivation(String ownerName,String effectLabel)
	{
		 showToAll(ownerName +" " +"active l'effet " +effectLabel);
	}
	
	
	
	public void endBattle(String status, int xp)
	{
		if(status.equals(BattleManager.WIN))
			 showToAll("vous avez gagné ");
		else
			 showToAll("vous avez perdu ");
		
		showToAll("vous gagnez " + xp + " xp");
		 
		 
		menu.useCommand(CommandManager.GAIN_XP+ " " + "Roger " + xp);
	}
	
	
	private void showToAll(String message)
	{
		menu.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION+ " " + message);
		
		addMessage(message);
	}
	
	
	@Override
	protected  void paintComponent(Graphics g) {
		//super.paint(g);
		
		g.setColor(Color.GRAY);
		
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.setColor(Color.WHITE);
		
		g.drawString(message, this.getWidth()/2, 50);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		for(CharacterSimulator c : characterMap.values())
		{
			c.addTime(timerTime);
		}
		
		messageClock += timerTime;
		
		
		if(messageClock > messageClockChange  && messageVector.size() > 0)
		{
			message = messageVector.get(0);
			messageVector.remove(0);
			
			messageClock = 0;
		}
			
		
		this.repaint();	
	}
	
	

}
