package battle;

public class UnusableAbilityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8200319983773613535L;

	@Override
	public String getMessage()
	{
		return "Le Personnage indiqué ne peut pas utilisé la capacité donné";
	}
	
	
}
