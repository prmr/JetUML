package ca.mcgill.cs.jetuml.commands;

import ca.mcgill.cs.jetuml.graph.Property;

/**
 * Represents a change to the property of a GraphElement.
 * 
 * @author Martin P. Robillard
 */
public class ChangePropertyCommand implements Command
{
	private Property aProperty;
	private Object aOldValue; 
	private Object aNewValue;
	
	/**
	 * Create a new command.
	 * 
	 * @param pProperty The changed property.
	 * @param pOldValue The former value for the property.
	 * @param pNewValue The value the property should have after executing the command
	 * @pre pProperty != null && pOldValue != null && pNewValue != null
	 */
	public ChangePropertyCommand( Property pProperty, Object pOldValue, Object pNewValue)
	{
		assert pProperty != null && pOldValue != null && pNewValue != null;
		aProperty = pProperty;
		aOldValue = pOldValue;
		aNewValue = pNewValue;
	}
	
	@Override
	public void execute()
	{
		aProperty.set(aNewValue);
	}

	@Override
	public void undo()
	{
		aProperty.set(aOldValue);		
	}
}