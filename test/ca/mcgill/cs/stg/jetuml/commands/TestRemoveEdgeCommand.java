package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

public class TestRemoveEdgeCommand 
{
    private Graph aGraph;
    private Field aEdgesToBeRemoved;
    private Edge aEdge;
    private RemoveEdgeCommand aRemoveEdgeCommand;

    @Before
    public void setUp() throws Exception 
    {
        aGraph = new ClassDiagramGraph();
        aEdge = new CallEdge();
        aEdgesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aEdgesToBeRemoved");
        aEdgesToBeRemoved.setAccessible(true);
        aRemoveEdgeCommand = new RemoveEdgeCommand(aGraph, aEdge);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute() 
    {
        aRemoveEdgeCommand.execute();
        ArrayList<Edge> aListEdgesToBeRemoved;
        try 
        {
            aListEdgesToBeRemoved = (ArrayList<Edge>) aEdgesToBeRemoved.get(aGraph);
            assertTrue(aListEdgesToBeRemoved.contains(aEdge));
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
    }

    @Test
    public void testUndo() 
    {
        aRemoveEdgeCommand.execute();
        aRemoveEdgeCommand.undo();
        assertTrue(aGraph.getEdges().contains(aEdge));
    }

}
