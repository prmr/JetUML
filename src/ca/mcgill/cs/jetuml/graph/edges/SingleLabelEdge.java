package ca.mcgill.cs.jetuml.graph.edges;

import ca.mcgill.cs.jetuml.graph.ValueExtractor;
import ca.mcgill.cs.jetuml.persistence.Properties;

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
		properties.put("middleLabel", aLabelText);
		return properties;
	}
	
	@Override
	public void initialize(ValueExtractor pExtractor)
	{
		aLabelText = (String)pExtractor.get("middleLabel", ValueExtractor.Type.STRING);
	}
}
