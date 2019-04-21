package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import commandControl.CommandManager;
import commandControl.Debug;
import util.CommandControl;

public class Chat extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 54224646546524651L;

	WindowManager windowManager;
	
	JTextArea textArea;
	
	JTextField textField;
	
	protected Chat(WindowManager windowManager)
	{
		super();
		
		this.windowManager = windowManager;
		
		this.setLayout(new BorderLayout());
		
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		
		JScrollPane j = new JScrollPane(textArea);
		
		this.add(j , BorderLayout.CENTER);
		
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		Button sendButton = createButton("send", CommandManager.SEND);
		sendButton.addActionListener(new ActionPerfomer());
		
		textField = new JTextField();
		
		p.add(textField, BorderLayout.CENTER);
		p.add(sendButton, BorderLayout.EAST);
		
		this.add(p, BorderLayout.SOUTH);
		
	}
		
	private class ActionPerfomer implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String command =((Button)e.getSource()).getName();
			
			if(textField.getText().startsWith("/c"))
			{
				String battleCommand  = CommandControl.removeIndicatorFromCommand(textField.getText(), "/c");
				windowManager.useCommand(CommandManager.SEND_COMBAT + " " +battleCommand );
				
			}
			else
			{
				windowManager.useCommand(command + " " +textField.getText());
			textField.setText("");
			}
			
			
		}
		
	}
	
	
	private static Button createButton(String name, String command)
	{
		Button button = new Button(name); 
		button.setName(command);
		
		return button;
		
	}

	
	protected void showRaw(String chatMessage)
	{
		textArea.append(chatMessage + "\n");
	}

	protected void show(String chatMessage) 
	{
		String finalChatMessage = "";
		
		while(chatMessage.startsWith(" "))
				chatMessage = chatMessage.replaceFirst(" ", "");
		
		
		String[] cutChatMessage = chatMessage.split(" ");
		
		Debug.out(this, cutChatMessage[0]);
		
		finalChatMessage += dressPseudo(cutChatMessage[0]);
		
		finalChatMessage += getAllArgument(cutChatMessage);
		
		
		showRaw(finalChatMessage);
	}
	
	
	private String getAllArgument(String[] cutCommand)
	{
		String arguments = "";
		
		for(int i = 1 ; i < cutCommand.length; i++)
			arguments += " " + cutCommand[i];
		
		 return arguments;
		
	}
	
	private String dressPseudo(String pseudo)
	{
		String dressedPseudo = "["+ pseudo +"]:";
		Debug.out(this, "dressed pseudo : " + dressedPseudo);
		return dressedPseudo;
	}
}
