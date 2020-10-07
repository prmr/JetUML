package ca.mcgill.cs.jetuml.gui;

import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * Tip element representing text. 
 */
public class TipText implements TipElement 
{
	private String aText;
	
	/**
	 * @param pText the text content
	 */
	public TipText(String pText)
	{
		aText = pText;
	}
	
	/**
	 * @return Node containing the text
	 */
	public Node getAsNode() 
	{
		Text textNode = new Text(aText);
		return textNode;
	}

}
