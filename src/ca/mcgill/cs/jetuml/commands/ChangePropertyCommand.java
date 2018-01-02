package ca.mcgill.cs.jetuml.commands;

import ca.mcgill.cs.jetuml.graph.Properties;

/**
 * Represents a change to the property of a GraphElement.
 * 
 * @author Martin P. Robillard
 */
public class ChangePropertyCommand implements Command
{
	private Properties aProperties;
	private String aProperty;
	private Object aOldValue; 
	private Object aNewValue;
	
	/**
	 * Create a new command.
	 * 
	 * @param pProperties The properties object containing the property changed.
	 * @param pProperty The name of the changed property.
	 * @param pNewValue The new value for the property.
	 * @pre pProperties != null && pProperty != null && pOldValue != null && pNewValue != null
	 */
	public ChangePropertyCommand( Properties pProperties, String pProperty, Object pNewValue)
	{
		assert pProperties != null && pProperty != null && pNewValue != null;
		aProperties = pProperties;
		aProperty = pProperty;
		aOldValue = pProperties.get(pProperty);
		aNewValue = pNewValue;
	}
	
	@Override
	public void execute()
	{
		aProperties.set(aProperty, aNewValue);
	}

	@Override
	public void undo()
	{
		aProperties.set(aProperty, aOldValue);		
	}
}