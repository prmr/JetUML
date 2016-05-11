package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;

import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.SelectionList;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;

/**
 * Tests various interactions with Sequence Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Sequence Diagram.
 * 
 * @author Jiajun Chen
 *
 */

public class TestUsageScenariosSequenceDiagram 
{
	private SequenceDiagramGraph diagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard clipboard;
	private SelectionList aList;
	private ImplicitParameterNode paraNode1;
	private ImplicitParameterNode paraNode2;
	

	@Before
	public void setup()
	{
		diagram = new SequenceDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(diagram, new ToolBar(diagram));
		aGrid = new Grid();
		clipboard = new Clipboard();
		aList = new SelectionList();
		paraNode1 = new ImplicitParameterNode();
		paraNode2 = new ImplicitParameterNode();
	}
	
	@Test
	public void testCreateAndLinkParameterNode()
	{
		paraNode1.getName().setText("client");
		paraNode2.getName().setText("platform");
		diagram.addNode(paraNode1, new  Point2D.Double(5, 0));
		diagram.addNode(paraNode2, new  Point2D.Double(25, 0));
		assertEquals(2, diagram.getRootNodes().size());
		assertEquals("client", paraNode1.getName().getText());
		assertEquals("platform", paraNode2.getName().getText());
		
		CallEdge edge1 = new CallEdge();
		ReturnEdge edge2 = new ReturnEdge();
		NoteEdge edge3 = new NoteEdge();
		diagram.addEdge(edge1,  new  Point2D.Double(7, 0),  new  Point2D.Double(26, 0));
		diagram.addEdge(edge2,  new  Point2D.Double(7, 0),  new  Point2D.Double(26, 0));
		diagram.addEdge(edge3,  new  Point2D.Double(7, 0),  new  Point2D.Double(26, 0));
		assertEquals(0, diagram.getEdges().size());
	}
	
}
