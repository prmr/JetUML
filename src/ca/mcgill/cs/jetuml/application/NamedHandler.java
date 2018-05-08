package ca.mcgill.cs.jetuml.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * An event handler with a name.
 * 
 * @author Martin P. Robillard
 */
public class NamedHandler implements EventHandler<ActionEvent>
{
	private final String aName;
	private final EventHandler<ActionEvent> aHandler;
	
	/**
	 * Creates a new named handler.
	 * 
	 * @param pName The name of the handler.
	 * @param pHandler The action event handler.
	 */
	public NamedHandler( String pName, EventHandler<ActionEvent> pHandler)
	{
		assert pName != null && pHandler != null;
		aName = pName;
		aHandler = pHandler;
	}

	@Override
	public void handle(ActionEvent pEvent)
	{
		aHandler.handle(pEvent);
	}
	
	/**
	 * @return The name of this handler.
	 */
	public String getName()
	{
		return aName;
	}
	
	/**
	 * @param pNewName The name for the renamed handler.
	 * @return A handler for the same event, but the the name specified.
	 */
	public NamedHandler renamed(String pNewName)
	{
		return new NamedHandler( pNewName, aHandler);
	}
}
