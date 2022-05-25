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

package org.jetuml.diagram.nodes;

import java.util.Optional;

import org.jetuml.diagram.Node;

/**
 * Common data and implementation for the different flavors of 
 * package nodes. Package nodes have at least a name and a parent.
 */
public class AbstractPackageNode extends NamedNode
{
	private Optional<Node> aContainer = Optional.empty();

	@Override
	public Node getParent()
	{
		assert hasParent();
		return aContainer.get();
	}

	@Override
	public void link(Node pNode)
	{
		assert pNode instanceof PackageNode || pNode == null;
		aContainer = Optional.of(pNode);
	}
	
	@Override
	public void unlink()
	{
		assert hasParent();
		aContainer = Optional.empty();
	}

	@Override
	public boolean hasParent()
	{
		return aContainer.isPresent();
	}
}
