/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.application.SelectionList;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Rectangle;

/**
 * Encapsulates all state related to the selection feature of a diagram canvas.
 * Conceptually, a selection model comprises three components:
 * 
 * 1. A list of selected elements.
 * 2. Optionally, a rubberband tool used to select two nodes for drawing an edge
 * 3. Optionally, a lasso tool used to select any element within a region on a canvas.
 */
public class SelectionModel implements Iterable<DiagramElement>
{
	private final SelectionObserver aObserver;
	private final SelectionList aSelectionList = new SelectionList();
	private Optional<Line> aRubberband = Optional.empty();
	private Optional<Rectangle> aLasso = Optional.empty();
	
	/**
	 * Creates a new selection model with a single observer.
	 * 
	 * @param pObserver The observer for this model.
	 */
	public SelectionModel(SelectionObserver pObserver)
	{
		aObserver = pObserver;
	}
	
	public void selectAll(Diagram pDiagram)
	{
		aSelectionList.clearSelection();
		for(Node node : pDiagram.getRootNodes())
		{
			aSelectionList.add(node);
		}
		for(Edge edge : pDiagram.getEdges())
		{
			aSelectionList.add(edge);
		}
		aObserver.selectionModelChanged();
	}
	
	public Rectangle getSelectionBounds()
	{
		Optional<Node> lastSelected = getLastSelectedNode();
		assert lastSelected.isPresent();
		Rectangle bounds = lastSelected.get().view().getBounds();
		for(DiagramElement selected : aSelectionList )
		{
			bounds = bounds.add(selected.view().getBounds());
		}
		return bounds;
	}
	
	public Iterable<Node> getSelectedNodes()
	{
		List<Node> result = new ArrayList<>();
		for( DiagramElement element : aSelectionList )
		{
			if( element instanceof Node )
			{
				result.add((Node) element);
			}
		}
		return result;
	}
	
	public SelectionList getSelectionList()
	{
		return aSelectionList;
	}
	
	public void fireNotification()
	{
		aObserver.selectionModelChanged();
	}
	
	public void activateLasso(Rectangle pLasso, Diagram pDiagram, boolean pAddMode)
	{
		aLasso = Optional.of(pLasso);
		for(Node node : pDiagram.getRootNodes())
		{
			selectNode(pAddMode, node, pLasso);
		}
		//Edges need to be added too when highlighted, but only if both their endpoints have been highlighted.
		for(Edge edge: pDiagram.getEdges())
		{
			if(!pAddMode && !pLasso.contains(edge.view().getBounds()))
			{
				removeFromSelection(edge);
			}
			else if(pLasso.contains(edge.view().getBounds()))
			{
				if(aSelectionList.transitivelyContains(edge.getStart()) && aSelectionList.transitivelyContains(edge.getEnd()))
				{
					addToSelection(edge);
				}
			}
		}
		aObserver.selectionModelChanged();
	}
	
	public Optional<Rectangle> getLasso()
	{
		return aLasso;
	}
	
	public Optional<Line> getRubberband()
	{
		return aRubberband;
	}
	
	public void activateRubberband(Line pLine)
	{
		aRubberband = Optional.of(pLine);
		aObserver.selectionModelChanged();
	}
	
	public void deactivateLasso()
	{
		aLasso = Optional.empty();
		aObserver.selectionModelChanged();
	}
	
	public void deactivateRubberband()
	{
		aRubberband = Optional.empty();
		aObserver.selectionModelChanged();
	}
	
	private void selectNode(boolean pCtrl, Node pNode, Rectangle pLasso)
	{
		if (!pCtrl && !pLasso.contains(pNode.view().getBounds())) 
		{
			removeFromSelection(pNode);
		}
		else if (pLasso.contains(pNode.view().getBounds())) 
		{
			addToSelection(pNode);
		}
		if (pNode instanceof ParentNode)
		{
			for (ChildNode child : ((ParentNode) pNode).getChildren())
			{
				selectNode(pCtrl, child, pLasso);
			}
		}
	}
	
	public void resetSelection(SelectionList pNewSelection)
	{
		aSelectionList.clearSelection();
		for( DiagramElement element : pNewSelection )
		{
			aSelectionList.add(element);
		}
	}
	
	/**
	 * Sets pElement as the single element in the selection list.
	 * 
	 * @param pElement The element to set.
	 */
	public void setSelection(DiagramElement pElement)
	{
		assert pElement != null;
		aSelectionList.set(pElement);
	}
	
	/**
	 * Removes pElement from the list of selected elements,
	 * or does nothing if pElement is not selected.
	 * @param pElement The element to remove. Cannot be null.
	 */
	public void removeFromSelection(DiagramElement pElement)
	{
		assert pElement != null;
		aSelectionList.remove(pElement);
	}
	
	/**
	 * Adds an element to the selection set and sets
	 * it as the last selected element. If the element 
	 * is already in the list, it is added to the end 
	 * of the list. If the node is transitively a child of 
	 * any node in the list, it is not added.
	 * 
	 * @param pElement The element to add to the list.
	 * Cannot be null.
	 */
	public void addToSelection(DiagramElement pElement)
	{
		assert pElement != null;
		aSelectionList.add(pElement);
	}
	
	public Optional<Node> getLastSelectedNode()
	{
		Node last = aSelectionList.getLastNode();
		if( last == null )
		{
			return Optional.empty();
		}
		else
		{
			return Optional.ofNullable(last);
		}
	}
	
	public void clearSelection()
	{
		aSelectionList.clearSelection();
	}
	
	public void addEdgesIfContained(Collection<Edge> pEdges)
	{
		aSelectionList.addEdgesIfContained(pEdges);
	}
	
	public boolean contains(DiagramElement pElement)
	{
		assert pElement != null;
		return aSelectionList.contains(pElement);
	}

	@Override
	public Iterator<DiagramElement> iterator()
	{
		return aSelectionList.iterator();
	}
	
}
