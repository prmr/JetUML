package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.framework.ToolBar;
import ca.mcgill.cs.stg.jetuml.graph.ActorNode;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestComponudCommand 
{
    private Graph aGraph;
    private Node aClassNode;
    private Node aActorNode;
    private Edge aCallEdge;
    private Field aNodesToBeRemoved;
    private Field aEdgesToBeRemoved;
    private Field aNeedsLayout;
    private Field aRootNodes;
    private AddNodeCommand aAddClassNodeCommand;
    private AddNodeCommand aAddActorNodeCommand;
    private AddEdgeCommand aAddEdgeCommand;
    private MoveCommand aMoveCommand1;
    private MoveCommand aMoveCommand2;
    private DeleteNodeCommand aDeletNodeCommand;
    private CompoundCommand aCompoundCommand1;
    private CompoundCommand aCompoundCommand2;
    private CompoundCommand aCompoundCommand3;
    private CompoundCommand aCompoundCommand4;
    
    @Before
    public void setUp() throws Exception
    {
        aGraph = new ClassDiagramGraph();
        aClassNode = new ClassNode();
        aActorNode = new ActorNode();
        aCallEdge = new CallEdge();
        
        aAddClassNodeCommand = new AddNodeCommand(aGraph, aClassNode);
        aAddEdgeCommand = new AddEdgeCommand(aGraph, aCallEdge);
        aAddActorNodeCommand = new AddNodeCommand(aGraph, aActorNode);
        aMoveCommand1 = new MoveCommand(aGraph, aClassNode, 5, 5);
        aMoveCommand2 = new MoveCommand(aGraph, aClassNode, -3, 3);
        aDeletNodeCommand = new DeleteNodeCommand(aGraph, aClassNode);
        
        aNodesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aNodesToBeRemoved");
        aNodesToBeRemoved.setAccessible(true);
        aEdgesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aEdgesToBeRemoved");
        aEdgesToBeRemoved.setAccessible(true);
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aRootNodes = aGraph.getClass().getSuperclass().getDeclaredField("aRootNodes");
        aRootNodes.setAccessible(true);
        
        aCompoundCommand1 = new CompoundCommand();
        aCompoundCommand1.add(aAddClassNodeCommand);
        aCompoundCommand1.add(aAddEdgeCommand);
        aCompoundCommand1.add(aMoveCommand1);
        
        aCompoundCommand2 = new CompoundCommand();
        aCompoundCommand2.add(aAddActorNodeCommand);
        aCompoundCommand2.add(aMoveCommand2);
        
        aCompoundCommand3 = new CompoundCommand();
        aCompoundCommand3.add(aCompoundCommand1);
        aCompoundCommand3.add(aCompoundCommand2);
        
        aCompoundCommand4 = new CompoundCommand();
        aCompoundCommand4.add(aCompoundCommand1);
        aCompoundCommand4.add(aMoveCommand2);
        aCompoundCommand4.add(aDeletNodeCommand);
    }

    @Test
    public void testExecute_ContainsSimpleCommands() 
    {
    	assertFalse(aGraph.getRootNodes().contains(aClassNode));
        assertFalse(aGraph.getEdges().contains(aCallEdge));
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(0, 0, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        aCompoundCommand1.execute();        
        assertTrue(aGraph.getRootNodes().contains(aClassNode));
        assertTrue(aGraph.getEdges().contains(aCallEdge));
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(5, 5, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUndo_ContainsSimpleCommands() 
    {
        aCompoundCommand1.execute();
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(5, 5, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        aCompoundCommand1.undo();
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(0, 0, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        try 
        {
            ArrayList<Node> aListNodesToBeRemoved = (ArrayList<Node>) (aNodesToBeRemoved.get(aGraph));
            assertTrue(aListNodesToBeRemoved.contains((Node) aClassNode));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
        try 
        {
            ArrayList<Edge> aListEdgesToBeRemoved = (ArrayList<Edge>) aEdgesToBeRemoved.get(aGraph);
            assertTrue(aListEdgesToBeRemoved.contains(aCallEdge));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
    }
    
    @Test
    public void testExecute_ContainsCompoundCommands() 
    {
    	assertFalse(aGraph.getRootNodes().contains(aClassNode));
        assertFalse(aGraph.getEdges().contains(aCallEdge));
        assertFalse(aGraph.getRootNodes().contains(aActorNode));
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(0, 0, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        assertEquals(aActorNode.getBounds(), new Rectangle2D.Double(0, 0, aActorNode.getBounds().getWidth(), aActorNode.getBounds().getHeight()));
        aCompoundCommand3.execute();        
        assertTrue(aGraph.getRootNodes().contains(aClassNode));
        assertTrue(aGraph.getEdges().contains(aCallEdge));
        assertTrue(aGraph.getRootNodes().contains(aActorNode));
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(2, 8, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        assertEquals(aActorNode.getBounds(), new Rectangle2D.Double(0, 0, aActorNode.getBounds().getWidth(), aActorNode.getBounds().getHeight()));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUndo_ContainsCompoundCommands() 
    {
        aCompoundCommand3.execute();
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(2, 8, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        assertEquals(aActorNode.getBounds(), new Rectangle2D.Double(0, 0, aActorNode.getBounds().getWidth(), aActorNode.getBounds().getHeight()));
        aCompoundCommand3.undo();
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(0, 0, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        assertEquals(aActorNode.getBounds(), new Rectangle2D.Double(0, 0, aActorNode.getBounds().getWidth(), aActorNode.getBounds().getHeight()));
        try 
        {
            ArrayList<Node> aListNodesToBeRemoved = (ArrayList<Node>) (aNodesToBeRemoved.get(aGraph));
            assertTrue(aListNodesToBeRemoved.contains((Node) aClassNode));
            assertTrue(aListNodesToBeRemoved.contains((Node) aActorNode));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
        try 
        {
            ArrayList<Edge> aListEdgesToBeRemoved = (ArrayList<Edge>) aEdgesToBeRemoved.get(aGraph);
            assertTrue(aListEdgesToBeRemoved.contains(aCallEdge));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testExecute_ContainsBothCommands() 
    {
    	assertFalse(aGraph.getRootNodes().contains(aClassNode));
        assertFalse(aGraph.getEdges().contains(aCallEdge));
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(0, 0, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
       
        aCompoundCommand4.execute();      

        assertTrue(aGraph.getRootNodes().contains(aClassNode));
        assertTrue(aGraph.getEdges().contains(aCallEdge));
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(2, 8, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        try 
        {
			ArrayList<Node> aListNodesToBeRemoved = (ArrayList<Node>) (aNodesToBeRemoved.get(aGraph));
            assertTrue(aListNodesToBeRemoved.contains((Node) aClassNode));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUndo_ContainsBothCommands() 
    {
        aCompoundCommand4.execute();
        try
		{
			assertTrue((boolean)aNeedsLayout.get(aGraph));
			((ArrayList<Node>)aRootNodes.get(aGraph)).removeAll((ArrayList<Node>)aNodesToBeRemoved.get(aGraph));
			((ArrayList<Node>)aNodesToBeRemoved.get(aGraph)).clear();
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			fail();
		}

        aCompoundCommand4.undo();
        
        assertTrue(aGraph.getRootNodes().contains(aClassNode));
        assertEquals(aClassNode.getBounds(), new Rectangle2D.Double(0, 0, aClassNode.getBounds().getWidth(), aClassNode.getBounds().getHeight()));
        try 
        {
            ArrayList<Node> aListNodesToBeRemoved = (ArrayList<Node>) (aNodesToBeRemoved.get(aGraph));
            assertTrue(aListNodesToBeRemoved.contains((Node) aClassNode));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
        try 
        {
            ArrayList<Edge> aListEdgesToBeRemoved = (ArrayList<Edge>) aEdgesToBeRemoved.get(aGraph);
            assertTrue(aListEdgesToBeRemoved.contains(aCallEdge));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
    }
    
}
