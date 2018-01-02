package ca.mcgill.cs.jetuml.graph;

/**
 * Base class for nodes and edges. Responsible for storing the single
 * Properties object used to describe the properties of this element.
 * There is only a single Properties object associated with a GraphElement, 
 * used to represent the element's properties through its life-cycle.
 * 
 * @author Martin P. Robillard
 */
public class AbstractGraphElement implements GraphElement
{
	private Properties aProperties;
	
	/**
	 * Initializes the properties for this object.
	 */
	protected AbstractGraphElement()
	{
		buildProperties();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected GraphElement clone()
	{
		try
		{
			AbstractGraphElement clone = (AbstractGraphElement) super.clone();
			clone.buildProperties();
			return clone;
		}
		catch(CloneNotSupportedException pException)
		{
			return null;
		}
	}
	
	@Override
	public final Properties properties()
	{
		return aProperties;
	}
	
	/**
	 * Builds the properties object associated with this object.
	 * Must be outside the constructor because of cloning.
	 * Subclasses should call super.buildProperties() before
	 * adding their own properties.
	 */
	protected void buildProperties()
	{
		aProperties = new Properties();
	}
}
