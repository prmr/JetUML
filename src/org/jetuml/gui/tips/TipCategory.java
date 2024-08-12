package org.jetuml.gui.tips;

/**
 *  An enum declared with a View parameter to define
 *  all tip categories and the View that the category belongs to.
 */
public enum TipCategory 
{
	CREATING(View.TOPIC), MODIFYING(View.TOPIC), SELECTING(View.TOPIC), COPYING(View.TOPIC), SEMANTICS(View.TOPIC), SETTINGS(View.TOPIC),
	BEGINNER(View.LEVEL), INTERMEDIATE(View.LEVEL), ADVANCED(View.LEVEL),
	CLASS(View.DIAGRAM), SEQUENCE(View.DIAGRAM), OBJECT(View.DIAGRAM), STATE(View.DIAGRAM), ALL(View.DIAGRAM);
	
	private final View aView;
	
	TipCategory(View pView)
	{
		aView = pView;
	}
	
	/**
	 * @return The View that the tip belongs to.
	 */
	public View getView()
	{
		return aView;
	}
}
