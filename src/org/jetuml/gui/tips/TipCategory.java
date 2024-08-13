package org.jetuml.gui.tips;

import static org.jetuml.gui.tips.View.TOPIC;
import static org.jetuml.gui.tips.View.LEVEL;
import static org.jetuml.gui.tips.View.DIAGRAM;

/**
 *  An enum declared with a View parameter to define
 *  all tip categories and the View that the category belongs to.
 */
public enum TipCategory 
{
	CREATING(TOPIC), MODIFYING(TOPIC), SELECTING(TOPIC), COPYING(TOPIC), SEMANTICS(TOPIC), SETTINGS(TOPIC),
	BEGINNER(LEVEL), INTERMEDIATE(LEVEL), ADVANCED(LEVEL),
	CLASS(DIAGRAM), SEQUENCE(DIAGRAM), OBJECT(DIAGRAM), STATE(DIAGRAM), ALL(DIAGRAM);
	
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
