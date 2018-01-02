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
	private final Properties aProperties = new Properties();
	
	@Override
	public Properties properties()
	{
		return aProperties;
	}
}
