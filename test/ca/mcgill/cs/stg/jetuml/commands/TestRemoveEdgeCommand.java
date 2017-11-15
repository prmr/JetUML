/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.commands;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.edges.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.edges.Edge;

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
