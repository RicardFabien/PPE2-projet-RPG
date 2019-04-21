package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.MouseEvent;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import animation.BattleAnimatedPanel;
import battle.Ability;
import battle.BattleManager;
import battle.CharacterFetcher;
import battle.Puppet;
import battle.PuppetCreator;
import characterControl.CharacterManager;
import characterControl.SimplifiedPuppet;
import commandControl.CommandManager;
import commandControl.Debug;
import serverSideConnection.ServerManager;
import util.CommandControl;




public abstract class MenuCreator {
	
	public static Menu mainMenu()
	{
		Menu menu = new Menu();
		menu.setName(WindowManager.MAIN_MENU);
		
		
		Button fightButton = new Button("fight"); 
		fightButton.setName(CommandManager.CHANGE_MENU + " " + WindowManager.FIGHT);
		
		Button characterButton = new Button("chara"); 
		characterButton.setName(CommandManager.CHANGE_MENU + " " + WindowManager.CHARACTER_SELECTION);
		
		Button serverButton = new Button("server"); 
		serverButton.setName(CommandManager.CHANGE_MENU + " " + WindowManager.SERVER);
		
		Button connectionButton = new Button("connection"); 
		connectionButton.setName(CommandManager.CHANGE_MENU + " " + WindowManager.CONNECTION);
		
		Button terminalButton = createButton(menu, "Terminal", CommandManager.TERMINAL);
		
		//Le commandListener doit avoir acces à son menu
		characterButton.addMouseListener(new CommandListener(menu));
		fightButton.addMouseListener(new CommandListener(menu));
		connectionButton.addMouseListener(new CommandListener(menu));
		serverButton.addMouseListener(new CommandListener(menu));
		
		menu.add(fightButton);
		menu.add(characterButton);
		menu.add(connectionButton);
		menu.add(serverButton);
		menu.add(terminalButton);
		
		
		return menu;
	}
	
	
	public static final String FIGHT_MENU_ANIMATOR = "fight_menu_animator";

	
	public static Menu fightMenu(WindowManager windowManager)
	{
		Menu menu = new Menu();
		
		menu.setLayout(new GridBagLayout());
		menu.setName(WindowManager.FIGHT);
		
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.anchor = GridBagConstraints.NORTHWEST;
		
		
		//menu.setLayout(new BorderLayout());
		
		Button button = new Button("Retour"); 
		button.setName(CommandManager.CHANGE_MENU + " " + WindowManager.MAIN_MENU);
		
		button.addMouseListener(new CommandListener(menu));
		
		
		BattleAnimatedPanel animatedPanel = new BattleAnimatedPanel(menu);
		
		menu.modifiableElementList.put(FIGHT_MENU_ANIMATOR,animatedPanel);
		
		
		c1.weightx = 0.9;
		c1.weighty = 0.7;
		
		c1.fill = GridBagConstraints.BOTH;
		
		c1.gridx = 0;
		c1.gridy = 1;
		
		c1.gridheight = 1;
		c1.gridwidth = 1;
		
		menu.add(animatedPanel, c1);
		
		
		c1.weighty = 0;
		
		c1.fill = GridBagConstraints.HORIZONTAL;
		
		//c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		
		c1.ipady =0;
		c1.ipadx = 0;
		
		c1.gridheight = 1;
		c1.gridwidth = 1;
		
		menu.add(button, c1);
		
		c1.weighty = 0.5;
		c1.fill = GridBagConstraints.BOTH;
		
		c1.gridheight = 1;
		c1.gridwidth = 1;
	
		c1.gridy = 5;
		
		
		
		menu.add(windowManager.getNewChat(), c1);

		
		
		
		menu.mainTextDisplay = new TextArea();

		menu.setBackground(Color.red);
		
		menu.commandHandler = (String command, HashMap<String, Object> modifiableElementList) -> 
		{
			Debug.out(menu.commandHandler, menu.name + "" + "got command " + command);
			
			//menu.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION +" "+"|MenuCreatorCombatDebug|" +command);
			
			String[] cutCommand = command.split(" ");
			
			
			
			switch(cutCommand[0])
			{
			
			case(BattleManager.NOT_YOUR_TURN) :
				menu.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION + " Pas ton tour");
			
			case (BattleManager.GET_CHARACTER) :
				menu.useCommand(CommandManager.SEND_SERVER_CHARACTER);
				break;
				
			case (BattleManager.INITIALISE) :
				animatedPanel.addCharacter(cutCommand[1], cutCommand[2],cutCommand[3], CommandControl.getAllArgumentsAfter(3, cutCommand));
				break;
				
			case(BattleManager.ASK_ACTION) :
				animatedPanel.informYourTurn();
				break;
				
				
			case (BattleManager.INFORM) :
				
				switch(cutCommand[1])
				{
				case(BattleManager.HP) :
					
					int newHp = 0;
					
					try
					{
						newHp  = Integer.parseInt(cutCommand[3]);
					}
					catch(NumberFormatException e)
					{
						Debug.out(menu, e);
					}
				
					animatedPanel.hpChange(newHp,cutCommand[2]);
					break;
				
				case(BattleManager.MANA) :
					
					int newMana = 0;
				
					try
					{
						newMana  = Integer.parseInt(cutCommand[3]);
					}
					catch(NumberFormatException e)
					{
						Debug.out(menu, e);
					}
			
				animatedPanel.manaChange(newMana,cutCommand[2]);
					
					break;
				
					
				case(BattleManager.LIMIT_BREAK) :
					
					int newLimitBreak = 0;
					
					try
					{
						newLimitBreak  = Integer.parseInt(cutCommand[3]);
					}
					catch(NumberFormatException e)
					{
						Debug.out(menu, e);
					}
			
					animatedPanel.limitBreakChange(newLimitBreak,cutCommand[2]);
					
					break;
					
					
				case(BattleManager.EFFECT_ON) :
										
					String ownerName = cutCommand[2];
					String effectLabel = cutCommand[3];
				
					int level =  1;
					int turnLeft = 0;
					
					String origin = cutCommand[6];
				
					try
					{
						level = Integer.parseInt(cutCommand[4]);
						turnLeft = Integer.parseInt(cutCommand[5]);
					}
					catch(NumberFormatException e)
					{
						Debug.out(menu, e);
					}
				
					animatedPanel.putEffect(ownerName, effectLabel, level, turnLeft, origin);
					break;
				
				case(BattleManager.EFFECT_OFF) :
					animatedPanel.removeEffect(cutCommand[2], cutCommand[3]);
					
					break;
				
				case(BattleManager.EFFECT_ACTIVATE) :
					animatedPanel.notifyEffectActivation(cutCommand[2], cutCommand[3]);
					break;
				
				case(BattleManager.END_BATTLE) :
					
					int xpGained = 0;
					
					try
					{
						xpGained  = Integer.parseInt(cutCommand[3]);
					}
					catch(NumberFormatException e)
					{
						Debug.out(menu, e);
					}
					
					animatedPanel.endBattle(cutCommand[2],  xpGained);
					break;
					
				case(BattleManager.TURN) :
					animatedPanel.informCurrentTurn(cutCommand[2]);
					break;
					
					
					default : 
						windowManager.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION + " "
								+ "unknown informCommand : " +  command);
						break;
						
				}
			
				break;
				
				
			case(CommandManager.NOTIFY_NAME) :
				animatedPanel.setOwnName(cutCommand[1]);
				break;
		
				
			default :
				windowManager.useCommand(CommandManager.SEND_CHAT_WINDOW_INFORMATION + " "
						+ "unknown command : " +  command);
				break;
				
				
			}
			
			
					
		};
		
		
		return menu;
	}
	
	
	public static Menu connectionMenu(WindowManager windowManager)
	{
		Menu menu = new Menu();
		menu.setLayout(new BorderLayout());
		menu.setName(WindowManager.CONNECTION);
		
		Button menuButton = new Button("Retour"); 
		menuButton.setName(CommandManager.CHANGE_MENU + " " + WindowManager.MAIN_MENU);
		
		menuButton.addMouseListener(new CommandListener(menu));
		
		
		JPanel connectionButtonPanel = new JPanel(new GridLayout(0, 1));
		
		Button localConnectButton = new Button("localconect"); 
		localConnectButton.setName(CommandManager.CLIENT_CONNECT+ " " + "localhost");
		localConnectButton.addMouseListener(new CommandListener(menu));
		
		connectionButtonPanel.add(localConnectButton);
		connectionButtonPanel.add(createButton(menu, "autoconnect (T3)" , CommandManager.CLIENT_AUTOCONNECT));
		
		
		menu.add(windowManager.getNewChat(), BorderLayout.CENTER);
		menu.add(menuButton, BorderLayout.WEST);
		menu.add(connectionButtonPanel, BorderLayout.EAST);
		
		menu.setBackground(Color.blue);
		
		return menu;
	}
	
	
	public static Menu characterMenu()
	{
		Menu menu = new Menu();
		menu.name = WindowManager.CHARACTER_SELECTION;
		
		String ownName = "";
		
		int rowNumber = 4;
		
		ArrayList<JTextArea> abilityLabelList = new ArrayList<JTextArea>();		
		
		JPanel[][] abilityLevelPanels = new JPanel[rowNumber][Ability.MAX_LEVEL - 1]; 
		
		Button button = new Button("Retour"); 
		button.setName(CommandManager.CHANGE_MENU + " " + WindowManager.MAIN_MENU);
		
		button.addMouseListener(new CommandListener(menu));
		
		
		JTextArea nameArea = new JTextArea();
		nameArea.setEditable(false);
		nameArea.setText("what?");
		
		JTextArea abilityPointArea = new JTextArea();
		nameArea.setEditable(false);
		nameArea.setText("finite");
		
		menu.add(abilityPointArea);
		
		menu.add(nameArea);
		
		
		JPanel generalUIpanel = new JPanel();
		
		JPanel buttonPanel = new JPanel(new GridLayout(rowNumber,2,1,1));
		
		JPanel indicatorPanel = new JPanel(new GridLayout(rowNumber,Ability.MAX_LEVEL - 1,1,1));
		
		//JPanel backButtonPanel = new JPanel(new GridLayout(4,1,1,1));
		
		for(int i = 0 ; i < rowNumber; i++)
		{
			JTextArea t = new JTextArea();
			t.setEditable(false);
			abilityLabelList.add(t);
			
			buttonPanel.add(t);
			if( !(i == rowNumber - 1) )
				buttonPanel.add(createTextAreaDynamicButton(menu, "-", "" + (i+1), "-", nameArea));
			else
				buttonPanel.add(createTextAreaDynamicButton(menu, "-", Puppet.ULT_KEY, "-", nameArea));
			
			for(int y = 0 ; y < 5; y++)
			{
				
				
				JPanel j = new JPanel();
				j.setBackground(Color.CYAN);
				
				abilityLevelPanels[i][y] = j;
				
				indicatorPanel.add(j);
			}
			
			if( !(i == rowNumber - 1) )
				indicatorPanel.add(createTextAreaDynamicButton(menu, "+", "" + (i+1), "+", nameArea));
			else
				indicatorPanel.add(createTextAreaDynamicButton(menu, "+", Puppet.ULT_KEY, "+", nameArea));
			
		}
		
		generalUIpanel.add(buttonPanel);
		generalUIpanel.add(indicatorPanel);
		//generalUIpanel.add(backButtonPanel);
		
		
		menu.add(button);
		for(SimplifiedPuppet p : CharacterFetcher.getAllSimplifiedPuppet())
		{
			menu.add(createButton(menu, "change perso: " + p.getName(), CommandManager.CHANGE_CHARACTER + " " + p.getName()));
		}
		
		menu.add(createButton(menu, "team 1", CommandManager.CHANGE_BATTLE_TEAM + " " + 1));
		menu.add(createButton(menu, "team 2", CommandManager.CHANGE_BATTLE_TEAM  + " " + 2));
		/*menu.add(createButton(menu, "team 3", CommandManager.CHANGE_BATTLE_TEAM  + " " + 3));  */
		
		menu.add(generalUIpanel);
		
		menu.setBackground(Color.green);
		
		menu.commandHandler = (String command ,HashMap<String ,Object> modifiableElementList ) -> 
		{
			Debug.out(menu.commandHandler, menu.name + "" + " got command " + command);
			
			String[] cutCommand = command.split(" ");
			
			switch(cutCommand[0])
			{
				case(CommandManager.NOTIFY_OWN_CHARACTER) :
					{
						String name = cutCommand[1];
						nameArea.setText(name);
						
						abilityPointArea.setText(cutCommand[cutCommand.length - 1]);
						
						int abilityPoint = 0;
						
						
						for(JTextArea j : abilityLabelList)
						{
							j.setText("");
						}
						
						for(JPanel[] arr1 : abilityLevelPanels)
						{
							for(JPanel pan : arr1)
							{
								pan.setBackground(Color.RED);
							}
						}
						
						SimplifiedPuppet s = CharacterFetcher.getSimplifiedPuppet(cutCommand[1]);
						HashMap<String,Ability> abilityMap = s.abilityMap;
						
						//for(int i = 0 ; i < abilityLabelList.size() && i < abilitySet.size()  ; i++ )
						//{
							
							
							try
							{
								if(cutCommand.length > 2)
								{
									//Iterator abilityIterator  = abilityMap.values().iterator();
									
									for(int k = 2 ; k < cutCommand.length - 1; k++)
									{
										String[] st =  cutCommand[k].split(":");
										
										JTextArea abilityLabel = abilityLabelList.get(k-2);
										
										abilityLabel.setText(abilityMap.get(st[0]).getName() );
										
										try 
										{
											Debug.out(menu, st);
											int levelChange = Integer.parseInt(st[1]);
											
											for(int l = 1; l < levelChange ; l++)
											{
												abilityLevelPanels[k-2][l -1].setBackground(Color.GREEN);
											}
										}
										catch(NumberFormatException |NullPointerException e)
										{
											continue;
										}
										catch(Exception e)
										{
											Debug.out(menu , e );
										}
									}
								}
									
							}
						
							catch(NumberFormatException e)
							{
								Debug.out(menu, e);
							}
							//abilityLabelList.get(i).setText(abilityIterator.next().getName());
						}
						
						break;
					}
				
			//}
			
		};
		
		return menu;
	}
	
	
	public static Menu serverMenu(WindowManager windowManager)
	{
		Menu menu = new Menu();
		menu.setName(WindowManager.SERVER);
		
		menu.setLayout(new BorderLayout());
		
		JPanel topPanel  =new JPanel();
		
		Button button = new Button("Retour"); 
		button.setName(CommandManager.CHANGE_MENU + " " + WindowManager.MAIN_MENU);
		
		button.addMouseListener(new CommandListener(menu));
		
		
		Button servOnButton = new Button("turn serv On"); 
		servOnButton.setName(CommandManager.SERV_ON);
		
		servOnButton.addMouseListener(new CommandListener(menu));
		
		Button servOffButton = createButton(menu, "turn serv off", CommandManager.SERV_OFF );
		
		Button battleOnButton = createButton(menu, "turn battle on", CommandManager.BATTLE_START );
		
		topPanel.add(battleOnButton);
		topPanel.add(servOffButton);
		topPanel.add(button);
		topPanel.add(servOnButton);
		
		
		menu.add(windowManager.getNewChat(), BorderLayout.CENTER);
		
		menu.add(topPanel,BorderLayout.NORTH);
		
		menu.setBackground(Color.gray);
		
		return menu;
	}
	

	/**
	 * La methode permet de créer un bouton adapté à l'utilisation par le MouseListener par défaut </br>
	 * 
	 * @param menu
	 * Le menu auquel ce bouton est relié, nécsessaire car le CommandListener utilise le menu pour agir
	 * @see CommandListener
	 * @param label
	 * Le "label" visible par les utilisateur
	 * @param command
	 * La commande utilisé par le bouton quand il est cliqué, remplace la variable nom de bouton
	 * @return Un bouton correspondant au standart de bouton utilisé par les menues
	 */
	private static Button createButton(Menu menu,String label, String command)
	{
		Button button = new Button(label); 
		button.setName(command);
		button.addMouseListener(new CommandListener(menu));
		
		return button;
		
	}
	
	private static Button createTextAreaDynamicButton(Menu menu,String label, String abilityKey,String levelChange, JTextArea textArea)
	{
		Button button = new Button(label); 
		
		button.addMouseListener(new CommandListener(menu)
			{
			
				@Override
				public void mousePressed(MouseEvent e) 
				{
					
					menu.useCommand(String.format("%s %s %s %s",CommandManager.CHANGE_ABILITY_LEVEL,
							textArea.getText(),abilityKey, levelChange) );
				}
			
			}
		);
		
		return button;
		
	}
	
	
	
	
	
	
	
}
