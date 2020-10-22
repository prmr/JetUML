package ca.mcgill.cs.jetuml.gui.tips;

/**
 * Names of the fields used for encoding tips
 * in their JSON file. The actual field names 
 * are lowercase versions of the enum constant names.
 */
enum TipFieldName
{
	ID, TITLE, CONTENT;
	
	public String asString()
	{
		return name().toLowerCase();
	}
}
