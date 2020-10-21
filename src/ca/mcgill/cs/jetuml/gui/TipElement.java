package ca.mcgill.cs.jetuml.gui;

/**
 * Tip element represented as a Media/content pair.
 */
public final class TipElement 
{
	private final Media aMedia;
	private final String aContent;
	
	/**
	 * @param pMedia Media of the tip element
	 * @param pContent content that will be displayed by the tip (image name with file
	 * 		  extension if the Media is IMAGE). 
	 * @pre pMedia != null && pContent != null
	 */
	public TipElement(Media pMedia, String pContent)
	{
		assert pMedia != null && pContent != null;
		aMedia = pMedia;
		aContent = pContent;
	}
	
	/**
	 * @return String containing the tip content
	 */
	public String getContent()
	{
		return aContent;
	}
	
	/**
	 * @return Media type of the TipElement
	 */
	public Media getMedia()
	{
		return aMedia;
	}
}
