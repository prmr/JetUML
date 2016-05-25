package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestDeleteNodeCommand 
{
    private Graph aGraph;
    private Field aNeedsLayout;
    private Field aNodesToBeRemoved;
    private Node aNode;
    private DeleteNodeCommand aDeleteNodeCommand;

    @Before
    public void setUp() throws Exception 
    {
        aGraph = new ClassDiagramGraph();
        aNode = new ClassNode();
        aGraph.addNode(aNode, new Point2D.Double());
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aNodesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aNodesToBeRemoved");
        aNodesToBeRemoved.setAccessible(true);
        aDeleteNodeCommand = new DeleteNodeCommand(aGraph, aNode);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute() 
    {
        aDeleteNodeCommand.execute();
        try 
        {
            ArrayList<Node> aListNodesToBeRemoved = (ArrayList<Node>) (aNodesToBeRemoved.get(aGraph));
            assertTrue(aListNodesToBeRemoved.contains((Node) aNode));
        } 
        catch (Exception e) 
        {
            fail();
        }
        try 
        {
            assertTrue((boolean) aNeedsLayout.get(aGraph));
        } 
        catch (Exception e) 
        {
            fail();
        }
    }

    @Test
    public void testUndo() 
    {
        aDeleteNodeCommand.execute();
        int numOfNodes = aGraph.getRootNodes().size();
        aDeleteNodeCommand.undo();
        assertEquals(numOfNodes + 1, aGraph.getRootNodes().size());
        try 
        {
            assertTrue((boolean) aNeedsLayout.get(aGraph));
        } 
        catch (Exception e) 
        {
            fail();
        }
    }
}
