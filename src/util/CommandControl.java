package util;

public abstract class CommandControl {
	
	public static String removeIndicatorFromCommand(String command , String commandIndicator)
	{
		if(command.startsWith(commandIndicator))
		{
			command = command.replace(commandIndicator, "");
			
			while(command.startsWith(" "))
			{
				command = command.replaceFirst(" " , "");
			}
		}
		
		return command;
	}
	
	public static String getAllArgumentsAfter(int index, String[] stringArray)
	{
		String s = "";
		
		for(int i = index + 1; i < stringArray.length; i++)
		{
			s +=" " + stringArray[index];
		}
		
		s.trim();
		
		return s;
	}

}
