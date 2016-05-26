package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestAddNodeCommand 
{
    private Graph aGraph;
    private Field aNeedsLayout;
    private Field aNodesToBeRemoved;
    private Node aNode;
    private AddNodeCommand aAddNodeCommand;

    @Before
    public void setUp() throws Exception 
    {
        aGraph = new ClassDiagramGraph();
        aNode = new ClassNode();
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aNodesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aNodesToBeRemoved");
        aNodesToBeRemoved.setAccessible(true);
        aAddNodeCommand = new AddNodeCommand(aGraph, aNode);  
    }

    @Test
    public void testExecute() 
    {
        int numOfNodes = aGraph.getRootNodes().size();
        aAddNodeCommand.execute();
        assertEquals(numOfNodes+1, aGraph.getRootNodes().size());
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
        aAddNodeCommand.execute();
        try 
        {
            aNeedsLayout.set(aGraph, false);
        } 
        catch (IllegalArgumentException | IllegalAccessException e2) 
        {
            fail();
        }
        aAddNodeCommand.undo();
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
            assertTrue((boolean) aNeedsLayout.get(aGraph));
        } 
        catch (IllegalArgumentException | IllegalAccessException e) 
        {
            fail();
        }
    }
}
