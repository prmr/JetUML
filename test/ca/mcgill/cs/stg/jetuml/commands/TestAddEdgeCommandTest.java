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

public class TestAddEdgeCommandTest {
    private Graph aGraph;
    private Field aNeedsLayout;
    private Field aEdgesToBeRemoved;
    private Edge aEdge;
    private AddEdgeCommand aAddEdgeCommand;

    @Before
    public void setUp() throws Exception 
    {
        aGraph = new ClassDiagramGraph();
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aEdgesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aEdgesToBeRemoved");
        aEdgesToBeRemoved.setAccessible(true);
        aEdge = new CallEdge();
        aAddEdgeCommand = new AddEdgeCommand(aGraph, aEdge);  
    }

    @Test
    public void testExecute() 
    {
        aAddEdgeCommand.execute();
        assertTrue(aGraph.getEdges().contains(aEdge));
        try 
        {
            assertTrue((boolean)aNeedsLayout.get(aGraph));
        } 
        catch (IllegalArgumentException | IllegalAccessException e) 
        {
            fail();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUndo(){

        aAddEdgeCommand.execute();
        try 
        {
            aNeedsLayout.set(aGraph, false);
        } 
        catch (IllegalArgumentException | IllegalAccessException e2) 
        {
            fail();
        }
        aAddEdgeCommand.undo();
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
