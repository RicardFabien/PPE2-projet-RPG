package launcher;

import characterControl.CharacterManager;
import clientSideConnection.ClientConnectionManager;
import commandControl.CommandManager;
import gui.MenuCreator;
import gui.WindowManager;
import serverSideConnection.ServerManager;

public class Launcher {

	public static void main(String[] args) {

		CommandManager masterManager = new CommandManager();
		
		
		WindowManager windowManager = new WindowManager("LapisBrawl Alpha");

		windowManager.setMenu(WindowManager.MAIN_MENU, MenuCreator.mainMenu());
		
		windowManager.addMenu(WindowManager.FIGHT, MenuCreator.fightMenu(windowManager));
		
		windowManager.addMenu(WindowManager.SERVER, MenuCreator.serverMenu(windowManager));
		
		windowManager.addMenu(WindowManager.CONNECTION, MenuCreator.connectionMenu(windowManager));
		
		windowManager.addMenu(WindowManager.CHARACTER_SELECTION, MenuCreator.characterMenu());
		
		
		masterManager.setWindowManager(windowManager);
		masterManager.setServerManager(new ServerManager());
		masterManager.setClientConnection(new ClientConnectionManager());
		masterManager.setCharacterManager(new CharacterManager(masterManager));
	}

}
