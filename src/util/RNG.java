package util;

import commandControl.Debug;

public class RNG {
	
	public static int generateRandomInt(int min, int max)
	{
		//max is included
		return (int)  (Math.random() * (max+1 - min ) + min);
	}
	
	
	public static int variation(int initialNumber,int percent)
	{
		int numberPercent = (percent*initialNumber)/100;
				
		int variation = generateRandomInt(0, numberPercent * 2);
		
		int r = initialNumber - numberPercent + variation;
		
		return r;
		
	}

}
