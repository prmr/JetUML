package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

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

public class TestRemoveEdgeCommandTest {
    private Graph aGraph;
    private Field aNeedsLayout;
    private Field aEdgesToBeRemoved;
    Edge aEdge;
    private ArrayList<Edge> aEdges;
    private RemoveEdgeCommand aRemoveEdgeCommand;
    private boolean hasExecuted = false;

    @Before
    public void setUp() throws Exception {
        aGraph = new ClassDiagramGraph();
        aEdge = new CallEdge();
        aEdges = (ArrayList<Edge>) aGraph.getEdges();
        aEdges.add(aEdge);
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aEdgesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aEdgesToBeRemoved");
        aEdgesToBeRemoved.setAccessible(true);
        
        aRemoveEdgeCommand = new RemoveEdgeCommand(aGraph, aEdge);  
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute() {
        aRemoveEdgeCommand.execute();
        hasExecuted = true;
        try {
            ArrayList<Edge> aListEdgesToBeRemoved = (ArrayList<Edge>) aEdgesToBeRemoved.get(aGraph);
            assertTrue(aListEdgesToBeRemoved.contains(aEdge));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
      
        try {
            assertTrue((boolean)aNeedsLayout.get(aGraph));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUndo(){
        if (!hasExecuted)
        {
            aRemoveEdgeCommand.execute();
        }
        aRemoveEdgeCommand.undo();
        assertTrue(aEdges.contains(aEdge));
        try {
            assertTrue((boolean)aNeedsLayout.get(aGraph));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
