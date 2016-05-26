package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

public class TestMoveCommand 
{

    private MoveCommand aMoveCommand;
    private Graph aGraph;
    private Node aNode;
    private Field aNeedsLayout;
    
    @Before
    public void setUp() throws Exception 
    {
        aGraph = new ClassDiagramGraph();
        aNeedsLayout = aGraph.getClass().getSuperclass().getDeclaredField("aNeedsLayout");
        aNeedsLayout.setAccessible(true);
        aNode = new ClassNode();
        aMoveCommand = new MoveCommand(aGraph, aNode, 5, 5);
    }

    @Test
    public void testExecute() 
    {
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(0, 0, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        aMoveCommand.execute();
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(5, 5, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        try 
        {
            assertTrue((boolean)aNeedsLayout.get(aGraph));
        } 
        catch (IllegalArgumentException | IllegalAccessException e) 
        {
            fail();
        }
    }

    @Test
    public void testUndo() 
    {
        aMoveCommand.execute();
        try 
        {
            aNeedsLayout.set(aGraph, false);
        } 
        catch (IllegalArgumentException | IllegalAccessException e1) 
        {
            fail();
        }
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(5, 5, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        aMoveCommand.undo();
        assertEquals(aNode.getBounds(), new Rectangle2D.Double(0, 0, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        try 
        {
            assertTrue((boolean)aNeedsLayout.get(aGraph));
        } 
        catch (IllegalArgumentException | IllegalAccessException e) 
        {
            fail();
        }
    } 
}
