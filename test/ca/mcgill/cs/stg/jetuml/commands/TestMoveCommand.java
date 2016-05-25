package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestMoveCommand {

    private MoveCommand aMoveCommand;
    private Graph aGraph;
    private Node aNode;
    boolean hasExecuted = false;
    
    @Before
    public void setUp() throws Exception {
        aGraph = new ClassDiagramGraph();
        aNode = new ClassNode();
        aMoveCommand = new MoveCommand(aGraph, aNode, 5.0, 5.0);
    }

    @Test
    public void testExecute() {
        Rectangle2D aBounds = aNode.getBounds();
        aMoveCommand.execute();
        hasExecuted = true;
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(aBounds.getX()+5.0, aBounds.getY()+5.0, aBounds.getWidth(), aBounds.getHeight()));
    }

    @Test
    public void testUndo() {
        if (!hasExecuted)
        {
            aMoveCommand.execute();
        }
        Rectangle2D aBounds = aNode.getBounds();
        aMoveCommand.undo();
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(aBounds.getX()-5.0, aBounds.getY()-5.0, aBounds.getWidth(), aBounds.getHeight()));
    }
}
