package org.jetuml.gui.tips;

public class TipCategory 
{
	private final View aView;
	private final String aCategory;
	
	/**
	 * @param pMedia Media of the tip element
	 * @param pContent content that will be displayed by the tip (image name with file
	 * 		  extension if the Media is IMAGE). 
	 * @pre pMedia != null && pContent != null
	 */
	TipCategory(View pView, String pCategory)
	{
		assert pView != null && pCategory != null;
		aView = pView;
		aCategory = pCategory;
	}
	
	/**
	 * @return String containing the tip content
	 */
	public String getCategory()
	{
		return aCategory;
	}
	
	/**
	 * @return Media type of the TipElement
	 */
	public View getView()
	{
		return aView;
	}
}
