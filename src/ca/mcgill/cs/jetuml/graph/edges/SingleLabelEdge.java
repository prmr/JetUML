package ca.mcgill.cs.jetuml.graph.edges;

import ca.mcgill.cs.jetuml.graph.Properties;

/**
 * An edge with a single middle label.
 * 
 * @author Martin P. Robillard
 */
public abstract class SingleLabelEdge extends AbstractEdge
{
	private String aLabelText = "";
	
	/**
     * Sets the label property value.
     * @param pNewValue the new value
	 */
	public void setMiddleLabel(String pNewValue)
	{
		aLabelText = pNewValue;
	}

	/**
     * Gets the label property value.
     * @return the current value
	 */
	public String getMiddleLabel()
	{
		return aLabelText;
	}
	
	@Override
	public Properties properties()
	{
		Properties properties = super.properties();
		properties.add("middleLabel", ()-> aLabelText, pLabel -> aLabelText = (String) pLabel );
		return properties;
	}
}
