package commandControl;

import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Terminal {
	
	protected boolean on;
	
	CommandManager commandManager;
	
	
	JFrame jFrame;
	
	JPanel jPanel;
	
	JTextArea textArea;
	
	JTextField textField;
	
	protected Terminal(CommandManager commandManager)
	{
		this.commandManager = commandManager;
		
		Debug.addSelfToList(this);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		
		JScrollPane sp = new JScrollPane(textArea);
		
		textField = new JTextField();
		
		JButton enterButton = new JButton (new AbstractAction("useCommand") {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String command = textField.getText();
				textField.setText("");
				commandManager.useCommand(command);
			}
		});
		
		JPanel j = new JPanel();
		j.setLayout(new BorderLayout());
		
		j.add(textField, BorderLayout.CENTER);
		j.add(enterButton, BorderLayout.EAST);
		
		
		jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		
		jPanel.add(j, BorderLayout.SOUTH);
		jPanel.add(sp, BorderLayout.CENTER);
		
		

		jFrame = new JFrame("Terminal");
		
		jFrame.add(jPanel);
		
		jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		jFrame.setSize(500,400);
		jFrame.setLocationRelativeTo(null);
		jPanel.getRootPane().setDefaultButton(enterButton);
		
	}
	
	public void showCommand(String message)
	{
		textArea.append(message);
	}
	
	protected void show()
	{
		this.jFrame.setVisible(true);
	}
	
	protected void hide()
	{
		this.jFrame.setVisible(false);
	}
	
	protected boolean isVisible()
	{
		return jFrame.isVisible();
	}
	
	

}
