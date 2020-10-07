package ca.mcgill.cs.jetuml.gui;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class TipTitle implements TipElement 
{
	private String aTitle;
	
	public TipTitle(String pTitle)
	{
		aTitle = pTitle;
	}
	
	public Node getAsNode() 
	{
		Text titleNode = new Text(aTitle);
		return titleNode;
	}
}
