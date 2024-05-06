/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.rendering.nodes;

 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.function.Function;

import org.jetuml.diagram.Node;
import org.jetuml.geom.Rectangle;

 /**
  * Stores the bounds of nodes. 
  */
 public class NodeStorage 
 {
 	private Map<Node, Rectangle> aNodeBounds = new IdentityHashMap<>();
 	private boolean aIsActivated = false;

 	/**
 	 * Returns the bounds of the current node either from the storage or from the calculator.
 	 * @param pNode the node of interest.
 	 * @param pBoundCalculator the bound calculator.
 	 * @return the bounds of pNode. 
 	 */
 	public Rectangle getBounds(Node pNode, Function<Node, Rectangle> pBoundCalculator)
 	{
 		if(!aIsActivated)
 		{
 			return pBoundCalculator.apply(pNode);
 		}
 		else if(aIsActivated && aNodeBounds.containsKey(pNode))
 		{
 			return aNodeBounds.get(pNode);
 		}
 		else
 		{
 			Rectangle computedBounds = pBoundCalculator.apply(pNode);
 			aNodeBounds.put(pNode, computedBounds);
 			return computedBounds;
 		}
 	}

 	/**
 	 * Activates the NodeStorage.
 	 */
 	public void activate() 
 	{
 		aIsActivated = true;
 	}

 	/**
 	 * Deactivates and clears the NodeStorage.
 	 */
 	public void deactivateAndClear() 
 	{
 		aIsActivated = false;
 		aNodeBounds.clear();
 	}
 }