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
import java.util.Stack;

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
	
	private Stack<DiagramElement> aSelected = new Stack<>();
	
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
		clearSelection();
		for(Node node : pDiagram.getRootNodes())
		{
			add(node);
		}
		for(Edge edge : pDiagram.getEdges())
		{
			add(edge);
		}
		aObserver.selectionModelChanged();
	}
	
	public Rectangle getSelectionBounds()
	{
		Optional<Node> lastSelected = getLastSelectedNode();
		assert lastSelected.isPresent();
		Rectangle bounds = lastSelected.get().view().getBounds();
		for(DiagramElement selected : aSelected )
		{
			bounds = bounds.add(selected.view().getBounds());
		}
		return bounds;
	}
	
	public Iterable<Node> getSelectedNodes()
	{
		List<Node> result = new ArrayList<>();
		for( DiagramElement element : aSelected )
		{
			if( element instanceof Node )
			{
				result.add((Node) element);
			}
		}
		return result;
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
				remove(edge);
			}
			else if(pLasso.contains(edge.view().getBounds()))
			{
				if(transitivelyContains(edge.getStart()) && transitivelyContains(edge.getEnd()))
				{
					add(edge);
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
			remove(pNode);
		}
		else if (pLasso.contains(pNode.view().getBounds())) 
		{
			add(pNode);
		}
		if (pNode instanceof ParentNode)
		{
			for (ChildNode child : ((ParentNode) pNode).getChildren())
			{
				selectNode(pCtrl, child, pLasso);
			}
		}
	}
	
	public void resetSelection(List<DiagramElement> pNewSelection)
	{
		clearSelection();
		for( DiagramElement element : pNewSelection )
		{
			aSelected.add(element);
		}
	}
	
	public Optional<Node> getLastSelectedNode()
	{
		Node last = getLastNode();
		if( last == null )
		{
			return Optional.empty();
		}
		else
		{
			return Optional.ofNullable(last);
		}
	}
	
	// SELECTION LIST METHODS
	
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
	public void add(DiagramElement pElement)
	{
		assert pElement != null;
		if( !containsParent( pElement ))
		{
			aSelected.remove(pElement);
			aSelected.push(pElement);
			
			// Remove children in case a parent was added.
			ArrayList<DiagramElement> toRemove = new ArrayList<>();
			for( DiagramElement element : aSelected )
			{
				if( containsParent(element) )
				{
					toRemove.add(element);
				}
			}
			for( DiagramElement element : toRemove )
			{
				remove(element);
			}
		}
	}
	
	/**
	 * Returns true if any of the parents of pElement is contained
	 * (transitively).
	 * @param pElement The element to test
	 * @return true if any of the parents of pElement are included in the 
	 * selection.
	 */
	public boolean containsParent(DiagramElement pElement)
	{
		if( pElement instanceof ChildNode )
		{
			ParentNode parent = ((ChildNode) pElement).getParent();
			if( parent == null )
			{
				return false;
			}
			else if( aSelected.contains(parent))
			{
				return true;
			}
			else
			{
				return containsParent(parent);
			}
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * @param pElement The element to test.
	 * @return True if either this element or one of its parent is contained.
	 */
	public boolean transitivelyContains(DiagramElement pElement)
	{
		return contains(pElement) || containsParent(pElement);
	}
	
	/**
	 * Removes all selections.
	 */
	public void clearSelection()
	{
		aSelected.clear();
	}
	
	/**
	 * @return The last element that was selected, or null
	 * if there are no such elements.
	 */
	public DiagramElement getLastSelected()
	{
		if( aSelected.size() > 0 )
		{
			return aSelected.peek();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Include in the selection list any edge in pEdges whose start and end nodes
	 * are already in the selection list, and replaces the previously last element
	 * in the selection to last place.
	 * @param pEdges The edges to consider adding.
	 */
	public void addEdgesIfContained(Collection<Edge> pEdges)
	{
		if( aSelected.isEmpty() )
		{
			return;
		}
		DiagramElement last = aSelected.pop();
		for( Edge edge : pEdges )
		{
			if( capturesEdge(edge))
			{
				add(edge);
			}
		}
		aSelected.push(last);
	}
	
	/**
	 * @return The last Node that was selected, or null 
	 * if there are no Nodes selected.
	 */
	public Node getLastNode()
	{
		for( int i = aSelected.size()-1; i >=0; i--)
		{
			if( aSelected.get(i) instanceof Node )
			{
				return (Node) aSelected.get(i);
			}
		}
		return null;
	}
	
	/**
	 * @param pElement The element to test.
	 * @return True if pElement is in the list of selected elements.
	 */
	public boolean contains(DiagramElement pElement)
	{
		return aSelected.contains(pElement);
	}
	
	/**
	 * @param pEdge The edge to test.
	 * @return true iif the selection contains both end-points of pEdge, or their parent.
	 */
	public boolean capturesEdge(Edge pEdge)
	{
		return (contains(pEdge.getStart()) || containsParent(pEdge.getStart())) &&
				(contains(pEdge.getEnd()) || containsParent(pEdge.getEnd()));
	}
	
	/**
	 * Removes pElement from the list of selected elements,
	 * or does nothing if pElement is not selected.
	 * @param pElement The element to remove. Cannot be null.
	 */
	public void remove(DiagramElement pElement)
	{
		assert pElement != null;
		aSelected.remove(pElement);
	}
	
	/**
	 * Sets pElement as the single selected element.
	 * @param pElement The element to set as selected. Cannot
	 * be null.
	 */
	public void set(DiagramElement pElement)
	{
		assert pElement != null;
		aSelected.clear();
		aSelected.add(pElement);
	}

	@Override
	public Iterator<DiagramElement> iterator()
	{
		return aSelected.iterator();
	}
	
	/**
	 * @return The number of elements currently selected.
	 */
	public int size()
	{
		return aSelected.size();
	}
	
}
