package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestComponudCommand 
{

    private Graph aGraph;
    private Field aNeedsLayout;
    private Node aNode;
    private Edge aEdge;
    private Field aNodesToBeRemoved;
    private Field aEdgesToBeRemoved;
    private AddNodeCommand aAddNodeCommand;
    private AddEdgeCommand aAddEdgeCommand;
    private MoveCommand aMoveCommand;
    private CompoundCommand aCompoundCommand;
    
    @Before
    public void setUp() throws Exception
    {
        aGraph = new ClassDiagramGraph();
        aNode = new ClassNode();
        aEdge = new CallEdge();
        aAddNodeCommand = new AddNodeCommand(aGraph, aNode);
        aAddEdgeCommand = new AddEdgeCommand(aGraph, aEdge);
        aMoveCommand = new MoveCommand(aGraph, aNode, 5, 5);
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aNodesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aNodesToBeRemoved");
        aNodesToBeRemoved.setAccessible(true);
        aEdgesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aEdgesToBeRemoved");
        aEdgesToBeRemoved.setAccessible(true);
        aCompoundCommand = new CompoundCommand();
        aCompoundCommand.add(aAddNodeCommand);
        aCompoundCommand.add(aAddEdgeCommand);
        aCompoundCommand.add(aMoveCommand);
    }

    @Test
    public void testExecute() 
    {
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(0, 0, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        aCompoundCommand.execute();        
        assertTrue(aGraph.getRootNodes().contains(aNode));
        assertTrue(aGraph.getEdges().contains(aEdge));
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(5, 5, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        try 
        {
            assertTrue((boolean) aNeedsLayout.get(aGraph));
        } 
        catch (IllegalArgumentException | IllegalAccessException e) 
        {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUndo() 
    {

        aCompoundCommand.execute();
        try 
        {
            aNeedsLayout.set(aGraph, false);
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(5, 5, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        aCompoundCommand.undo();
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(0, 0, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        try 
        {
            ArrayList<Node> aListNodesToBeRemoved = (ArrayList<Node>) (aNodesToBeRemoved.get(aGraph));
            assertTrue(aListNodesToBeRemoved.contains((Node) aNode));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
        try 
        {
            ArrayList<Edge> aListEdgesToBeRemoved = (ArrayList<Edge>) aEdgesToBeRemoved.get(aGraph);
            assertTrue(aListEdgesToBeRemoved.contains(aEdge));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
        try 
        {
            assertTrue((boolean) aNeedsLayout.get(aGraph));
        } 
        catch (IllegalArgumentException | IllegalAccessException e) 
        {
            fail();
        }
    }
}
