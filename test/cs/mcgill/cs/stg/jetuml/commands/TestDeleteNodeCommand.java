package cs.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.commands.DeleteNodeCommand;
import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestDeleteNodeCommand 
{
    private Graph aGraph;
    private Field aNodesToBeRemoved;
    private Node aNode;
    private DeleteNodeCommand aDeleteNodeCommand;

    @Before
    public void setUp() throws Exception 
    {
        aGraph = new ClassDiagramGraph();
        aNode = new ClassNode();
        aNodesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aNodesToBeRemoved");
        aNodesToBeRemoved.setAccessible(true);
        aDeleteNodeCommand = new DeleteNodeCommand(aGraph, aNode);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute() 
    {
        aDeleteNodeCommand.execute();
        ArrayList<Node> aListNodesToBeRemoved;
        try 
        {
            aListNodesToBeRemoved = (ArrayList<Node>) (aNodesToBeRemoved.get(aGraph));
            assertTrue(aListNodesToBeRemoved.contains((Node) aNode));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
    }

    @Test
    public void testUndo() 
    {
        aDeleteNodeCommand.execute();
        assertEquals(0, aGraph.getRootNodes().size());
        aDeleteNodeCommand.undo();
        assertEquals(1, aGraph.getRootNodes().size());
    }
}
