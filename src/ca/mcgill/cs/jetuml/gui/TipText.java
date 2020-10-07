package ca.mcgill.cs.jetuml.gui;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class TipText implements TipElement 
{
	private String aText;
	
	public TipText(String pText)
	{
		aText = pText;
	}
	
	public Node getAsNode() 
	{
		Text textNode = new Text(aText);
		return textNode;
	}

}
