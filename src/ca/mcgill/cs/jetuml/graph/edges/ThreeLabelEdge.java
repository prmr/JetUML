package ca.mcgill.cs.jetuml.graph.edges;

import ca.mcgill.cs.jetuml.graph.ValueExtractor;
import ca.mcgill.cs.jetuml.persistence.Properties;

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
	
	@Override
	public Properties properties()
	{
		Properties properties = super.properties();
		properties.put("startLabel", aStartLabel);
		properties.put("endLabel", aEndLabel);
		return properties;
	}
	
	@Override
	public void initialize(ValueExtractor pExtractor)
	{
		super.initialize(pExtractor);
		aStartLabel = (String)pExtractor.get("startLabel", ValueExtractor.Type.STRING);
		aEndLabel = (String)pExtractor.get("endLabel", ValueExtractor.Type.STRING);
	}
}
