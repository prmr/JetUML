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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.nodes.Node;

public class TestMoveCommand 
{
    private MoveCommand aMoveCommand;
    private Graph aGraph;
    private Node aNode;
    
    @Before
    public void setUp() throws Exception 
    {
        aGraph = new ClassDiagramGraph();
        aNode = new ClassNode();
        aMoveCommand = new MoveCommand(aGraph, aNode, 5, 5);
    }

    @Test
    public void testExecute() 
    {
    	assertEquals(aNode.getBounds(), new Rectangle(0, 0, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        aMoveCommand.execute();
        assertEquals(aNode.getBounds(), new Rectangle(5, 5, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
    }

    @Test
    public void testUndo() 
    {
    	aMoveCommand.execute();
        assertEquals(aNode.getBounds(), new Rectangle(5, 5, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
        aMoveCommand.undo();
        assertEquals(aNode.getBounds(), new Rectangle(0, 0, aNode.getBounds().getWidth(), aNode.getBounds().getHeight()));
    } 
}
