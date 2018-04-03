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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph2;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;

public class TestMoveCommand 
{
    private MoveCommand2 aMoveCommand;
    private Graph2 aGraph;
    private Node aNode;
    
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
        aNode = new ClassNode();
        aMoveCommand = new MoveCommand2(aGraph, aNode, 5, 5);
    }

    @Test
    public void testExecute() 
    {
    	assertEquals(aNode.view2().getBounds(), new Rectangle(0, 0, aNode.view2().getBounds().getWidth(), aNode.view2().getBounds().getHeight()));
        aMoveCommand.execute();
        assertEquals(aNode.view2().getBounds(), new Rectangle(5, 5, aNode.view2().getBounds().getWidth(), aNode.view2().getBounds().getHeight()));
    }

    @Test
    public void testUndo() 
    {
    	aMoveCommand.execute();
        assertEquals(aNode.view2().getBounds(), new Rectangle(5, 5, aNode.view2().getBounds().getWidth(), aNode.view2().getBounds().getHeight()));
        aMoveCommand.undo();
        assertEquals(aNode.view2().getBounds(), new Rectangle(0, 0, aNode.view2().getBounds().getWidth(), aNode.view2().getBounds().getHeight()));
    } 
}
