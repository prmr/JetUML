/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.Clipboard2;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperationProcessor;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * Helper functionality to test various diagram modification
 * scenarios.
 */
public class AbstractTestUsageScenarios 
{
	protected Diagram aDiagram;
	private DiagramOperationProcessor aProcessor;
	protected NoteNode aNoteNode;
	protected NoteEdge aNoteEdge;
	private List<DiagramElement> aSelection;
	private Clipboard2 aClipboard;
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	protected void setup()
	{
		aProcessor = new DiagramOperationProcessor();
		aNoteNode = new NoteNode();
		aNoteEdge = new NoteEdge();
		aSelection = new ArrayList<>();
		aClipboard = Clipboard2.instance();
	}
	
	protected void addNode(Node pNode, Point pRequestedPosition)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createAddNodeOperation(pNode, pRequestedPosition, 1000, 1000));
	}
	
	protected void addEdge(Edge pEdge, Point pStart, Point pEnd)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createAddEdgeOperation(pEdge, pStart, pEnd));
	}
	
	protected void moveNode(Node pNode, int pX, int pY)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createMoveNodeOperation(pNode, pX, pY));
	}
	
	protected void moveSelection(int pX, int pY)
	{
		CompoundOperation operation = new CompoundOperation();
		for( DiagramElement element : aSelection)
		{
			if( element instanceof Node)
			{
				operation.add(aDiagram.builder().createMoveNodeOperation((Node)element, pX, pY));
			}
		}
		aProcessor.executeNewOperation(operation);
	}
	
	protected void setProperty(Property pProperty, Object pValue)
	{
		aProcessor.executeNewOperation(aDiagram.builder().createPropertyChangeOperation(pProperty, pValue));
	}
	
	protected void deleteSelected()
	{
		aProcessor.executeNewOperation(aDiagram.builder().createDeleteElementsOperation(aSelection));
		aSelection.clear();
	}
	
	protected void copy()
	{
		aClipboard.copy(aSelection);
	}
	
	protected void paste()
	{
		aProcessor.executeNewOperation(aDiagram.builder().createAddElementsOperation(aClipboard.getElements()));
	}
	
	protected void cut()
	{
		aClipboard.copy(aSelection);
		aProcessor.executeNewOperation(aDiagram.builder().createDeleteElementsOperation(aSelection));
	}
	
	protected void select(DiagramElement... pElements)
	{
		aSelection.clear();
		aSelection.addAll(Arrays.asList(pElements));
	}
	
	protected void selectAll()
	{
		aSelection.clear();
		aSelection.addAll(aDiagram.getRootNodes());
		aSelection.addAll(aDiagram.getEdges());
	}
	
	protected void undo()
	{
		aProcessor.undoLastExecutedOperation();
	}
}
