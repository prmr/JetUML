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

package ca.mcgill.cs.stg.jetuml.graph.nodes;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.graph.views.nodes.NodeView;
import ca.mcgill.cs.stg.jetuml.graph.views.nodes.ObjectNodeView;

/**
 *  An object node in an object diagram.
 */
public class ObjectNode extends NamedNode implements ParentNode
{
	private ArrayList<ChildNode> aFields = new ArrayList<>();

	/**
	 * Construct an object node with a default size.
	 */
	public ObjectNode()
	{
		setName(new MultiLineString(true));
		getName().setUnderlined(true);
	}
	
	@Override
	protected NodeView generateView()
	{
		return new ObjectNodeView(this);
	}

	/* 
 	 * Translate the children as well. 
	 * 
	 * @see ca.mcgill.cs.stg.jetuml.graph.nodes.AbstractNode2#translate(int, int)
	 */
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		for(Node child : getChildren())
		{
			child.translate(pDeltaX, pDeltaY);
		}   
	}    

	@Override
	public ObjectNode clone()
	{
		ObjectNode cloned = (ObjectNode) super.clone();
		cloned.aFields = new ArrayList<>();
		
		for( ChildNode child : aFields )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			ChildNode clonedChild = (ChildNode) child.clone();
			clonedChild.setParent(cloned);
			cloned.aFields.add(clonedChild);
		}
		return cloned;
	}

	@Override
	public void addChild(ChildNode pNode)
	{
		addChild(aFields.size(), pNode);
	}
	
	@Override
	public void addChild(int pIndex, ChildNode pNode)
	{
		ParentNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aFields.add(pIndex, pNode);
		pNode.setParent(this);
	}

	@Override
	public List<ChildNode> getChildren()
	{
		return aFields; // TODO there should be a remove operation on ObjectNode
	}

	@Override
	public void removeChild(ChildNode pNode)
	{
		if (pNode.getParent() != this)
		{
			return;
		}
		aFields.remove(pNode);
		pNode.setParent(null);
	}
	
	/**
	 *  Adds a persistence delegate to a given encoder that
	 * encodes the child nodes of this node.
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(ObjectNode.class, new DefaultPersistenceDelegate()
		{
			protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
			{
				super.initialize(pType, pOldInstance, pNewInstance, pOut);
				for(ChildNode node : ((ParentNode) pOldInstance).getChildren())
				{
					pOut.writeStatement( new Statement(pOldInstance, "addChild", new Object[]{ node }) );            
				}
			}
		});
	}
}
