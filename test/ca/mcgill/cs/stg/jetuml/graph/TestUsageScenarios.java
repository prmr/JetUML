package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.StateDiagramGraph;

/**
 * Tests various interactions normally triggered from the 
 * GUI. To the extent possible, use GUI-triggered methods
 * to create and manipulate the diagrams.
 */
public class TestUsageScenarios
{
	@Test
	public void testStateDiagramCreate() throws Exception
	{
		// Create a state diagram with two state nodes, one start node, one end node
		StateDiagramGraph diagram = new StateDiagramGraph();
		StateNode node1 = new StateNode();
		node1.getName().setText("Node 1");
		StateNode node2 = new StateNode();
		node2.getName().setText("Node 2");
		CircularStateNode start = new CircularStateNode();
		start.setFinal(false);
		CircularStateNode end = new CircularStateNode();
		end.setFinal(true);
		diagram.addNode(node1, new Point2D.Double(30,30));
		diagram.addNode(node2, new Point2D.Double(30, 100));
		diagram.addNode(start, new Point2D.Double(5, 5));
		diagram.addNode(end, new Point2D.Double(30, 200));
		assertEquals(4, diagram.getRootNodes().size());
		
		// Add edges between all of these, including back-and-forth between two states. 
		StateTransitionEdge edge1 = new StateTransitionEdge();
		edge1.setLabel("Edge 1");
		diagram.addEdge(edge1, new Point2D.Double(6, 6), new Point2D.Double(35, 35));
		
		StateTransitionEdge edge2 = new StateTransitionEdge();
		edge2.setLabel("Edge 2");
		diagram.addEdge(edge2, new Point2D.Double(35, 35), new Point2D.Double(35, 105));
		
		StateTransitionEdge edge3 = new StateTransitionEdge();
		edge3.setLabel("Edge 3");
		diagram.addEdge(edge3, new Point2D.Double(35, 105), new Point2D.Double(35, 35));
		
		StateTransitionEdge edge4 = new StateTransitionEdge();
		edge4.setLabel("Edge 4");
		diagram.addEdge(edge4, new Point2D.Double(35, 105), new Point2D.Double(32, 202));
		
		NoteEdge noteEdge = new NoteEdge();
		diagram.addEdge(noteEdge, new Point2D.Double(6, 6), new Point2D.Double(35, 35));
		diagram.addEdge(noteEdge, new Point2D.Double(35, 35), new Point2D.Double(35, 105));
		diagram.addEdge(noteEdge, new Point2D.Double(35, 105), new Point2D.Double(35, 35));
		diagram.addEdge(noteEdge, new Point2D.Double(35, 105), new Point2D.Double(32, 202));
		
		// VALIDATION NODES
		assertEquals(4, diagram.getRootNodes().size());
		assertEquals(new Rectangle2D.Double(30, 30, 80, 60), node1.getBounds());
		assertEquals("Node 1", node1.getName().getText());
		assertEquals(new Rectangle2D.Double(30, 100, 80, 60), node2.getBounds());
		assertEquals("Node 2", node2.getName().getText());
		assertEquals(new Rectangle2D.Double(5, 5, 14, 14), start.getBounds());
		assertFalse(start.isFinal());
		assertEquals(new Rectangle2D.Double(30, 200, 20, 20), end.getBounds());
		assertTrue(end.isFinal());
		
		// VALIDATION EDGES
		assertEquals(4, diagram.getEdges().size());
		
		assertEquals(new Rectangle2D.Double(17, 3, 54, 27), edge1.getBounds());
		assertEquals("Edge 1", edge1.getLabel());
		assertEquals(start, edge1.getStart());
		assertEquals(node1, edge1.getEnd());
		
		assertEquals(new Rectangle2D.Double(72, 90, 43, 29), edge2.getBounds());
		assertEquals("Edge 2", edge2.getLabel());
		assertEquals(node1, edge2.getStart());
		assertEquals(node2, edge2.getEnd());
		
		assertEquals(new Rectangle2D.Double(25, 90, 43, 29), edge3.getBounds());
		assertEquals("Edge 3", edge3.getLabel());
		assertEquals(node2, edge3.getStart());
		assertEquals(node1, edge3.getEnd());
		
		assertEquals(new Rectangle2D.Double(44, 160, 52, 41), edge4.getBounds());
		assertEquals("Edge 4", edge4.getLabel());
		assertEquals(node2, edge4.getStart());
		assertEquals(end, edge4.getEnd());
	}
}
