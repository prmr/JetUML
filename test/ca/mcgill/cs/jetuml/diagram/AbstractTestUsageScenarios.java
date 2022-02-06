/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.diagram;

import static ca.mcgill.cs.jetuml.viewers.FontMetrics.DEFAULT_FONT_SIZE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.Clipboard;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperationProcessor;
import ca.mcgill.cs.jetuml.diagram.builder.SimpleOperation;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * Helper functionality to test various diagram modification
 * scenarios.
 */
public class AbstractTestUsageScenarios 
{
	private static int userDefinedFontSize;
	protected Diagram aDiagram;
	protected DiagramBuilder aBuilder;
	private DiagramOperationProcessor aProcessor;
	protected NoteNode aNoteNode;
	protected NoteEdge aNoteEdge;
	private List<DiagramElement> aSelection;
	private Clipboard aClipboard;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	protected void setup()
	{
		aProcessor = new DiagramOperationProcessor();
		aNoteNode = new NoteNode();
		aNoteEdge = new NoteEdge();
		aSelection = new ArrayList<>();
		aClipboard = Clipboard.instance();
	}
	
	protected Iterable<DiagramElement> getClipboardContent()
	{
		return aClipboard.getElements();
	}
	
	protected void addNode(Node pNode, Point pRequestedPosition)
	{
		aProcessor.executeNewOperation(aBuilder.createAddNodeOperation(pNode, pRequestedPosition));
	}
	
	protected void addEdge(Edge pEdge, Point pStart, Point pEnd)
	{
		aProcessor.executeNewOperation(aBuilder.createAddEdgeOperation(pEdge, pStart, pEnd));
	}
	
	protected void moveNode(Node pNode, int pX, int pY)
	{
		aProcessor.executeNewOperation(DiagramBuilder.createMoveNodeOperation(pNode, pX, pY));
	}
	
	protected void moveSelection(int pX, int pY)
	{
		CompoundOperation operation = new CompoundOperation();
		for( DiagramElement element : aSelection)
		{
			if( element instanceof Node)
			{
				operation.add(DiagramBuilder.createMoveNodeOperation((Node)element, pX, pY));
			}
		}
		aProcessor.executeNewOperation(operation);
	}
	
	protected void setProperty(Property pProperty, Object pValue)
	{
		Object oldValue = pProperty.get();
		aProcessor.executeNewOperation(new SimpleOperation(()-> pProperty.set(pValue), ()-> pProperty.set(oldValue)));
	}
	
	protected void deleteSelected()
	{
		aProcessor.executeNewOperation(aBuilder.createRemoveElementsOperation(aSelection));
		aSelection.clear();
	}
	
	protected void copy()
	{
		aClipboard.copy(aSelection);
	}
	
	protected void paste()
	{
		aProcessor.executeNewOperation(aBuilder.createAddElementsOperation(aClipboard.getElements()));
	}
	
	protected void cut()
	{
		aClipboard.copy(aSelection);
		aProcessor.executeNewOperation(aBuilder.createRemoveElementsOperation(aSelection));
	}
	
	protected void select(DiagramElement... pElements)
	{
		aSelection.clear();
		aSelection.addAll(Arrays.asList(pElements));
	}
	
	protected void selectAll()
	{
		aSelection.clear();
		aDiagram.rootNodes().forEach(aSelection::add);
		aDiagram.edges().forEach(aSelection::add);
	}
	
	protected void undo()
	{
		aProcessor.undoLastExecutedOperation();
	}
	
	protected int numberOfRootNodes()
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Node node : aDiagram.rootNodes() )
		{
			sum++;
		}
		return sum;
	}
	
	protected int numberOfEdges()
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Edge edge : aDiagram.edges() )
		{
			sum++;
		}
		return sum;
	}
	
	protected Node getRootNode(int pIndex)
	{
		Iterator<Node> iterator = aDiagram.rootNodes().iterator();
		int i = 0;
		Node node = iterator.next();
		while( i < pIndex )
		{
			i++;
			node = iterator.next();
		}
		return node;
	}
}
