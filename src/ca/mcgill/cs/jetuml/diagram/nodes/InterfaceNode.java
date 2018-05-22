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

package ca.mcgill.cs.jetuml.diagram.nodes;

import ca.mcgill.cs.jetuml.views.nodes.InterfaceNodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 * An interface node in a class diagram that can be composed
 * of three compartments: top (for the name), middle (for attributes,
 * normally unused), and bottom (for methods).
 */
public class InterfaceNode extends NamedNode implements ChildNode
{
	private String aMethods = "";   
	private ParentNode aContainer;

	/**
     * Construct an interface node with a default size.
	 */
	public InterfaceNode() 
	{
		setName("\u00ABinterface\u00BB\n");
	}
	
	@Override
	protected NodeView generateView()
	{
		return new InterfaceNodeView(this);
	}

	/**
     * Sets the methods property value.
     * @param pMethods the methods of this interface
	 */
	public void setMethods(String pMethods)
	{
		aMethods = pMethods;
	}
	
	/**
     * Gets the methods property value.
     * @return the methods of this interface
	 */
	public String getMethods()
	{
		return aMethods;
	}

	@Override
	public ParentNode getParent()
	{
		return aContainer;
	}

	@Override
	public void setParent(ParentNode pNode)
	{
		assert pNode instanceof PackageNode || pNode == null;
		aContainer = pNode;
	}
	
	@Override
	public boolean requiresParent()
	{
		return false;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("methods", () -> aMethods, pMethods -> aMethods = (String)pMethods);
	}
}
