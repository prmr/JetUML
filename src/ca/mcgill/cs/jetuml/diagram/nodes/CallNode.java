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

package ca.mcgill.cs.jetuml.diagram.nodes;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.PropertyName;

/**
 * A method call node in a sequence diagram. Call nodes are
 * not intended to be manipulated by users. 
 */
public class CallNode extends AbstractNode
{
	public static final int CALL_YGAP = 20;

	private Optional<ImplicitParameterNode> aImplicitParameter = Optional.empty();
	private boolean aOpenBottom;

	/**
	 * Gets the openBottom property.
	 * @return true if this node is the target of a signal edge
	 */
	public boolean isOpenBottom() 
	{ 
		return aOpenBottom; 
	}

	/**
	 * Sets the openBottom property.
	 * @param pNewValue true if this node is the target of a signal edge
	 */      
	public void setOpenBottom(boolean pNewValue)
	{ 
		aOpenBottom = pNewValue; 
	}

	@Override
	public CallNode clone()
	{
		CallNode cloned = (CallNode) super.clone();
		return cloned;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add(PropertyName.OPEN_BOTTOM, () -> aOpenBottom, pOpen -> aOpenBottom = (boolean) pOpen);
	}
	
	/**
     * Gets the parent of this node.
     * @return the parent node
     * @pre hasParent()
	 */
	@Override
	public Node getParent() 
   	{ 
		assert hasParent();
		return aImplicitParameter.get(); 
	}

	/**
     * Sets the parent of this node.
     * @param pNode the parent node
     * @pre pNode != null;
	 */
	@Override
	public void link(Node pNode) 
	{
		assert pNode != null;
		assert pNode instanceof ImplicitParameterNode;
		aImplicitParameter = Optional.of((ImplicitParameterNode) pNode);
	}
	
	@Override
	public void unlink()
	{
		assert hasParent();
		aImplicitParameter = Optional.empty();
	}

	@Override
	public boolean requiresParent()
	{
		return true;
	}

	@Override
	public boolean hasParent()
	{
		return aImplicitParameter.isPresent();
	}
}
