/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.commands;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph2;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.edges.CallEdge;

public class TestAddEdgeCommandTest 
{
    private Graph2 aGraph;
    private Field aEdgesToBeRemoved;
    private Edge aEdge;
    private AddEdgeCommand2 aAddEdgeCommand;

    /**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
    
    @Before
    public void setup() throws Exception 
    {
        aGraph = new ClassDiagramGraph2();
        aEdgesToBeRemoved = aGraph.getClass().getSuperclass().getDeclaredField("aEdgesToBeRemoved");
        aEdgesToBeRemoved.setAccessible(true);
        aEdge = new CallEdge();
        aAddEdgeCommand = new AddEdgeCommand2(aGraph, aEdge);  
    }

    @Test
    public void testExecute() 
    {
        aAddEdgeCommand.execute();
        assertTrue(aGraph.getEdges().contains(aEdge));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUndo()
    {
        aAddEdgeCommand.execute();
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
    }
}
