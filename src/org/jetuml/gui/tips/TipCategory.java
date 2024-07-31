package org.jetuml.gui.tips;

/**
 * Tip category represented as a View/category pair.
 */
public class TipCategory 
{
	private final View aView;
	private final String aCategory;
	
	/**
	 * @param pView The View of the tip category.
	 * @param pCategory The category that the tip belongs to,
	 * associated with its particular View.
	 * @pre pView != null && pCategory != null.
	 */
	TipCategory(View pView, String pCategory)
	{
		assert pView != null && pCategory != null;
		aView = pView;
		aCategory = pCategory;
	}
	
	/**
	 * @return String containing the tip's category.
	 */
	public String getCategory()
	{
		return aCategory;
	}
	
	/**
	 * @return View type of the tip.
	 */
	public View getView()
	{
		return aView;
	}
}
