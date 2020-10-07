package ca.mcgill.cs.jetuml.gui;

import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * Tip element representing a tip's title. 
 */
public class TipTitle implements TipElement 
{
	private String aTitle;
	
	/**
	 * @param pTitle the tip's title
	 */
	public TipTitle(String pTitle)
	{
		aTitle = pTitle;
	}
	
	/**
	 * @return node containing the title
	 */
	public Node getAsNode() 
	{
		Text titleNode = new Text(aTitle);
		return titleNode;
	}
}
