package ca.mcgill.cs.jetuml.graph.edges;

/**
 * An edge with three labels.
 * 
 * @author Martin P. Robillard
 */
public abstract class ThreeLabelEdge extends SingleLabelEdge
{
	private String aStartLabel = "";
	private String aEndLabel = "";
	
	/**
	 * @param pLabel The new start label.
	 */
	public void setStartLabel(String pLabel)
	{
		aStartLabel = pLabel;
	}
	
	/**
	 * @param pLabel The new end label.
	 */
	public void setEndLabel(String pLabel)
	{
		aEndLabel = pLabel;
	}
	
	/**
	 * @return The start label.
	 */
	public String getStartLabel()
	{
		return aStartLabel;
	}
	
	/**
	 * @return The middle label.
	 */
	public String getEndLabel()
	{
		return aEndLabel;
	}
}
