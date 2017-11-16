package ca.mcgill.cs.stg.jetuml.graph.edges;

/**
 * An edge with a single middle label.
 * 
 * @author Martin P. Robillard
 */
public abstract class AbstractSingleLabeledEdge extends AbstractEdge2
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
}
