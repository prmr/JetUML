package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ActorNode;
import ca.mcgill.cs.stg.jetuml.graph.AggregationEdge;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestComponudCommand {

    private Graph aGraph;
    private ArrayList<Node> aRootNodes;
    private Field aNeedsLayout;
    private CompoundCommand aCompoundCommand;
    private Node aNodeA;
    private Node aNodeB;
    private Edge aEdgeA;
    private Edge aEdgeB;
    private AddNodeCommand aAddNodeCommand;
    private DeleteNodeCommand aDeleteNodeCommand;
    private boolean hasExecuted = false;
    
    @Before
    public void setUp() throws Exception
    {
        aGraph = new ClassDiagramGraph();
         
        aNodeA = new ClassNode();
//        aNodeB = new ActorNode();
        aEdgeA = new CallEdge();
        aEdgeB = new AggregationEdge();
        aAddNodeCommand = new AddNodeCommand(aGraph, aNodeA);
        aDeleteNodeCommand = new DeleteNodeCommand(aGraph, aNodeA);  
        
        
        
        aRootNodes = (ArrayList<Node>) aGraph.getRootNodes();
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aCompoundCommand = new CompoundCommand();
        

        aGraph.addNode(aNodeA , new Point2D.Double());
        aDeleteNodeCommand = new DeleteNodeCommand(aGraph, aNodeA);  
        
        aCompoundCommand.add(aDeleteNodeCommand);
    }

    @Test
    public void testExecute() {
        aCompoundCommand.execute();
        hasExecuted = true;
        assertTrue(aGraph.contains(aNodeA));
        try {
            assertTrue((boolean)aNeedsLayout.get(aGraph));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } 
    }
    
    @Test
    public void testUndo() {
        if (!hasExecuted)
        {
            aCompoundCommand.execute();
        }
        int numOfNodes = aRootNodes.size();
        aCompoundCommand.undo();
        assertEquals(numOfNodes+1, aRootNodes.size());
        try {
            assertTrue((boolean)aNeedsLayout.get(aGraph));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
