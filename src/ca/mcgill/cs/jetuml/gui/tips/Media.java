package ca.mcgill.cs.jetuml.gui.tips;

/**
 * A media type for a tip element.
 */
enum Media 
{
	TEXT, IMAGE;
	
	public String asString()
	{
		return name().toLowerCase();
	}
}
