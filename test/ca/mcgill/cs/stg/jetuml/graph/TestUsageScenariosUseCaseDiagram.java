package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.Clipboard;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;

/**
 * Tests various interactions with Use Case Diagram normally triggered from the 
 * GUI. Here we use the API to simulate GUI Operation for Use Case Diagram.
 * 
 * @author Jiajun Chen
 *
 */

public class TestUsageScenariosUseCaseDiagram 
{
	private UseCaseDiagramGraph aDiagram;
	private Graphics2D aGraphics;
	private GraphPanel aPanel;
	private Grid aGrid;
	private Clipboard aClipboard;
	private ActorNode aActorNode1;
	private ActorNode aActorNode2;
	private UseCaseNode aUseCaseNode1;
	private UseCaseNode aUseCaseNode2;
	private UseCaseAssociationEdge aAssociationEdge;
	private UseCaseDependencyEdge aDependencyEdge;
	private UseCaseGeneralizationEdge aGeneralEdge;
	
	/**
	 * General setup.
	 */
	@Before
	public void setup()
	{
		aDiagram = new UseCaseDiagramGraph();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aPanel = new GraphPanel(aDiagram, new ToolBar(aDiagram));
		aGrid = new Grid();
		aClipboard = new Clipboard();
		aActorNode1 = new ActorNode();
		aActorNode2 = new ActorNode();
		aUseCaseNode1 = new UseCaseNode();
		aUseCaseNode2 = new UseCaseNode();
		aAssociationEdge = new UseCaseAssociationEdge();
		aDependencyEdge = new UseCaseDependencyEdge();
		aGeneralEdge = new UseCaseGeneralizationEdge();
	}
	
	/**
	 * Below are methods testing basic nodes and edge creation
	 * for a Use Case diagram.
	 * 
	 * 
	 * 
	 * Testing create a Use Case diagram.
	 */
	@Test
	public void testCreateUseCaseDiagram()
	{
		// create an ActorNode
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.draw(aGraphics, aGrid);
		MultiLineString name = new MultiLineString();
		name.setText("Car");
		aActorNode1.setName(name);
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals("Car", aActorNode1.getName().getText());
		
		// create some UseCaseNode
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(120, 80));
		name = new MultiLineString();
		name.setText("driving");
		aUseCaseNode1.setName(name);
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals("driving", aUseCaseNode1.getName().getText());

		// create field nodes inside ObjectNode
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(noteNode, new Point2D.Double(50, 50));
		name = new MultiLineString();
		name.setText("something...\nsomething");
		noteNode.setText(name);
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals("something...\nsomething", noteNode.getText().getText());
	}
	
	/**
	 * Testing edge creation except NoteEdge.
	 */
	@Test
	public void testGeneralEdgeCreation()
	{
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aActorNode2, new Point2D.Double(250, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.addNode(aUseCaseNode2, new Point2D.Double(140, 20));
		
		aDiagram.addEdge(aAssociationEdge,  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point2D.Double(80, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point2D.Double(20, 20), new Point2D.Double(140, 20));
		assertEquals(3, aDiagram.getEdges().size());
		
		// create more edges
		aDiagram.addEdge(new UseCaseAssociationEdge(),  new Point2D.Double(80, 20), new Point2D.Double(140, 20));
		aDiagram.addEdge(new UseCaseDependencyEdge(),  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(new UseCaseGeneralizationEdge(),  new Point2D.Double(80, 20), new Point2D.Double(140, 20));
		assertEquals(6, aDiagram.getEdges().size());
		
		// connect nodes with NoteEdge (not allowed)
		aDiagram.addEdge(new NoteEdge(),  new Point2D.Double(80, 20), new Point2D.Double(140, 20));
		aDiagram.addEdge(new NoteEdge(),  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		assertEquals(6, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing NoteEdge creation.
	 */
	@Test
	public void testNoteEdgeCreation()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aActorNode2, new Point2D.Double(250, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.addNode(aUseCaseNode2, new Point2D.Double(140, 20));
		aDiagram.addNode(noteNode, new Point2D.Double(100, 100));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		
		// if begin with a non-NoteNode type, both point needs to be valid
		aDiagram.addEdge(noteEdge1, new Point2D.Double(9, 9), new Point2D.Double(209, 162));
		assertEquals(0, aDiagram.getEdges().size());
		aDiagram.addEdge(noteEdge1, new Point2D.Double(20, 20), new Point2D.Double(100, 100));
		assertEquals(1, aDiagram.getEdges().size());
		assertEquals(noteEdge1.getStart(), aActorNode1);
		assertEquals(noteEdge1.getEnd(), noteNode);
		aDiagram.addEdge(noteEdge2, new Point2D.Double(85, 25), new Point2D.Double(110, 110));
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(noteEdge2.getStart(), aUseCaseNode1);
		assertEquals(noteEdge2.getEnd(), noteNode);
		
		// if begin with a NoteNode, the end point can be anywhere
		aDiagram.addEdge(noteEdge3, new Point2D.Double(100, 100), new Point2D.Double(9,9));
		assertEquals(noteEdge3.getStart(), noteNode);
		assertEquals(noteEdge3.getEnd().getClass(), new PointNode().getClass());
		assertEquals(3, aDiagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing nodes movement.
	 * 
	 * 
	 * 
	 * Testing individual node movement.
	 */
	@Test
	public void testIndividualNodeMovement()
	{
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.addNode(noteNode, new Point2D.Double(100, 100));

		aActorNode1.translate(3, 12);
		aUseCaseNode1.translate(3, 2);
		noteNode.translate(40, 20);
		assertEquals(new Rectangle2D.Double(23, 32, 48, 64), aActorNode1.getBounds());
		assertEquals(new Rectangle2D.Double(83, 22, 110, 40), aUseCaseNode1.getBounds());
		assertEquals(new Rectangle2D.Double(140, 120, 60, 40), noteNode.getBounds());
	}
	
	/**
	 * Testing nodes and edges movement.
	 */
	@Test
	public void testNodesAndEdgesMovement()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aActorNode2, new Point2D.Double(250, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.addNode(aUseCaseNode2, new Point2D.Double(140, 20));
		aDiagram.addNode(noteNode, new Point2D.Double(100, 100));
		aDiagram.addEdge(aAssociationEdge,  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point2D.Double(80, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point2D.Double(20, 20), new Point2D.Double(140, 20));
		aDiagram.addEdge(noteEdge1, new Point2D.Double(85, 25), new Point2D.Double(110, 110));

		aPanel.selectAll();
		for(GraphElement element: aPanel.getSelectionList())
		{
			if(element instanceof Node)
			{
				((Node) element).translate(26, 37);
			}
		}
		assertEquals(new Rectangle2D.Double(46, 57, 48, 64), aActorNode1.getBounds());
		assertEquals(new Rectangle2D.Double(276, 57, 48, 64), aActorNode2.getBounds());
		assertEquals(new Rectangle2D.Double(106, 57, 110, 40), aUseCaseNode1.getBounds());
		assertEquals(new Rectangle2D.Double(166, 57, 110, 40), aUseCaseNode2.getBounds());
		assertEquals(new Rectangle2D.Double(126, 137, 60, 40), noteNode.getBounds());
		
		// move a node connect to another node, edge should redraw accordingly,
		Rectangle2D associationEdgeBounds = aAssociationEdge.getBounds();
		aActorNode1.translate(10, 20);
		assertFalse(associationEdgeBounds == aAssociationEdge.getBounds());
		assertEquals(aActorNode1, aAssociationEdge.getStart());
		assertEquals(aActorNode2, aAssociationEdge.getEnd());
	}
	
	/**
	 * Below are methods testing deletion and undo feature for Use Case diagram.
	 * 
	 * 
	 * 
	 * Testing delete an Node. 
	 */
	@Test
	public void testDeleteNode()
	{
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aPanel.getSelectionList().add(aActorNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(0, aDiagram.getRootNodes().size());
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());
		
		NoteNode noteNode = new NoteNode();
		aDiagram.addNode(noteNode, new Point2D.Double(75, 75));
		aPanel.getSelectionList().add(noteNode);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());
		aPanel.undo();
		assertEquals(2, aDiagram.getRootNodes().size());
		
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(420, 420));
		aPanel.getSelectionList().add(aUseCaseNode1);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		aPanel.undo();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(3, aDiagram.getRootNodes().size());
	}

	/**
	 * Testing delete an edge.
	 */
	@Test
	public void testDeleteEdge()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aActorNode2, new Point2D.Double(250, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.addNode(aUseCaseNode2, new Point2D.Double(140, 20));
		aDiagram.addNode(noteNode, new Point2D.Double(100, 100));
		aDiagram.addEdge(aAssociationEdge,  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point2D.Double(80, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point2D.Double(20, 20), new Point2D.Double(140, 20));
		aDiagram.addEdge(noteEdge1, new Point2D.Double(85, 25), new Point2D.Double(110, 110));
		
		// delete aAssociationEdge and aGeneralEdge
		aPanel.getSelectionList().add(aAssociationEdge);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(3, aDiagram.getEdges().size());
		aPanel.getSelectionList().add(aGeneralEdge);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(3, aDiagram.getEdges().size());
		aPanel.undo();
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Testing delete a combination of node and edge.
	 */
	@Test
	public void testDeleteCombinationNodeAndEdge()
	{
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aActorNode2, new Point2D.Double(250, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.addNode(aUseCaseNode2, new Point2D.Double(140, 20));
		aDiagram.addNode(noteNode, new Point2D.Double(100, 100));
		aDiagram.addEdge(aAssociationEdge,  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point2D.Double(80, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point2D.Double(20, 20), new Point2D.Double(140, 20));
		aDiagram.addEdge(noteEdge1, new Point2D.Double(85, 25), new Point2D.Double(110, 110));

		// delete aActorNode1 and all 4 edges
		aPanel.getSelectionList().add(aActorNode1);
		aPanel.getSelectionList().add(aAssociationEdge);
		aPanel.getSelectionList().add(aDependencyEdge);
		aPanel.getSelectionList().add(aGeneralEdge);
		aPanel.getSelectionList().add(noteEdge1);

		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(0, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(5, aDiagram.getRootNodes().size());
		assertEquals(4, aDiagram.getEdges().size());
		
		/* now delete aUseCaseNode2, aActorNode2 and aGeneralEdge
		 * aAssociationEdge and aDependencyEdge will also be deleted
		 * since they are connected to aActorNode2
		 */
		aPanel.getSelectionList().add(aUseCaseNode2);
		aPanel.getSelectionList().add(aActorNode2);
		aPanel.getSelectionList().add(aGeneralEdge);
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(1, aDiagram.getEdges().size());
		
		aPanel.undo();
		assertEquals(5, aDiagram.getRootNodes().size());
		assertEquals(4, aDiagram.getEdges().size());
	}
	
	/**
	 * Below are methods testing copy and paste feature for Use Case Diagram.
	 * 
	 * 
	 * 
	 * Testing copy a Node.
	 */
	@Test
	public void testCopyNode()
	{
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.draw(aGraphics, aGrid);
		aPanel.getSelectionList().add(aActorNode1);
		aClipboard.copy(aPanel.getSelectionList());
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(new Rectangle2D.Double(0, 0, 60, 80), (((ActorNode) aDiagram.getRootNodes().toArray()[2]).getBounds()));
	}
	
	/**
	 * 
	 * Testing cut a Node.
	 */
	@Test
	public void testCutNode()
	{
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.draw(aGraphics, aGrid);
		
		aPanel.getSelectionList().add(aUseCaseNode1);
		aClipboard.copy(aPanel.getSelectionList());
		aPanel.removeSelected();
		aPanel.getSelectionList().clearSelection();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(1, aDiagram.getRootNodes().size());

		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(new Rectangle2D.Double(0, 0, 120, 40), (((UseCaseNode) aDiagram.getRootNodes().toArray()[1]).getBounds()));
		assertEquals(2, aDiagram.getRootNodes().size());
	}
	
	/**
	 * 
	 * Testing copy two Node with an edge.
	 */
	@Test
	public void testCopyNodesWithEdge()
	{
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aActorNode2, new Point2D.Double(250, 20));
		aDiagram.addEdge(aAssociationEdge,  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		aPanel.selectAll();
		aClipboard.copy(aPanel.getSelectionList());
		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(new Rectangle2D.Double(0, 0, 60, 80), (((ActorNode) aDiagram.getRootNodes().toArray()[2]).getBounds()));
	}

	/**
	 * 
	 * Testing cut two Node with an edge.
	 */
	@Test
	public void testCutNodesWithEdge()
	{
		aDiagram.addNode(aActorNode1, new Point2D.Double(20, 20));
		aDiagram.addNode(aActorNode2, new Point2D.Double(250, 20));
		aDiagram.addNode(aUseCaseNode1, new Point2D.Double(80, 20));
		aDiagram.addNode(aUseCaseNode2, new Point2D.Double(140, 20));
		aDiagram.addEdge(aAssociationEdge,  new Point2D.Double(20, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aDependencyEdge,  new Point2D.Double(80, 20), new Point2D.Double(250, 20));
		aDiagram.addEdge(aGeneralEdge,  new Point2D.Double(20, 20), new Point2D.Double(140, 20));
		
		aPanel.getSelectionList().add(aActorNode1);
		aPanel.getSelectionList().add(aUseCaseNode2);
		aPanel.getSelectionList().add(aGeneralEdge);
		aClipboard.copy(aPanel.getSelectionList());

		aPanel.removeSelected();
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(2, aDiagram.getRootNodes().size());
		// aAssociationEdge is connected with aAcotrNode, should also be removed
		assertEquals(1, aDiagram.getEdges().size());

		aClipboard.paste(aPanel);
		aDiagram.draw(aGraphics, aGrid);
		assertEquals(4, aDiagram.getRootNodes().size());
		assertEquals(2, aDiagram.getEdges().size());
		assertEquals(new Rectangle2D.Double(0, 0, 60, 80), (((ActorNode) aDiagram.getRootNodes().toArray()[2]).getBounds()));
	}
}
