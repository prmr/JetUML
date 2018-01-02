package ca.mcgill.cs.jetuml.graph.edges;

/**
 * An edge with a single middle label.
 * 
 * @author Martin P. Robillard
 */
public abstract class SingleLabelEdge extends AbstractEdge
{
	private String aLabelText = "";
	
	/**
	 * Creates an edge with a single, empty label.
	 */
	protected SingleLabelEdge()
	{
		properties().add("middleLabel", ()-> aLabelText, pLabel -> aLabelText = (String) pLabel );
	}
	
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
}
