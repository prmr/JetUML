package ca.mcgill.cs.stg.jetuml.framework;

/**
 * @author JoelChev
 * 
 * A class that will be used to store the current graph for cutting/copying and then pasting.
 *
 */
public final class Clipboard 
{
	private Object aContents;
	
	/**
	 * A constructor for a Clipboard object.
	 */
	public Clipboard() 
	{
	}
	
	/**
	 * @param pContents Sets the contents of the clipboard.
	 */
	public void setContents(Object pContents) 
	{
		aContents = pContents;
	}

	/**
	 * @return Gets the contents of the clipboard.
	 */
	public Object getContents() 
	{
		return aContents;
	}
	
	
		
}
