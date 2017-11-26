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

package ca.mcgill.cs.jetuml.framework;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 *   A layout manager that lays out components along a central axis.
 */
class FormLayout implements LayoutManager
{  
	private static final int GAP = 6;
	
	private int aLeft;
	private int aRight;
	private int aHeight;
	
	@Override
	public Dimension preferredLayoutSize(Container pParent)
	{  
		Component[] components = pParent.getComponents();
		aLeft = 0;
		aRight = 0;
		aHeight = 0;
		for(int i = 0; i < components.length; i += 2)
		{
			Component cleft = components[i];
			Component cright = components[i + 1];
			Dimension dleft = cleft.getPreferredSize();
			Dimension dright = cright.getPreferredSize();
			aLeft = Math.max(aLeft, dleft.width);
			aRight = Math.max(aRight, dright.width);
			aHeight = aHeight + Math.max(dleft.height, dright.height);
		}      
		return new Dimension(aLeft + GAP + aRight, aHeight);
	}
      
	@Override
	public Dimension minimumLayoutSize(Container pParent)
	{  
		return preferredLayoutSize(pParent);
	}

	@Override
	public void layoutContainer(Container pParent)
	{  
		preferredLayoutSize(pParent); // sets left, right
		Component[] components = pParent.getComponents();
		Insets insets = pParent.getInsets();
		int xcenter = insets.left + aLeft;
		int y = insets.top;

		for(int i = 0; i < components.length; i += 2)
		{
			Component cleft = components[i];
			Component cright = components[i + 1];

			Dimension dleft = cleft.getPreferredSize();
			Dimension dright = cright.getPreferredSize();

			int height = Math.max(dleft.height, dright.height);

			cleft.setBounds(xcenter - dleft.width, y + (height - dleft.height) / 2, dleft.width, dleft.height);
			cright.setBounds(xcenter + GAP, y + (height - dright.height) / 2, dright.width, dright.height);
			y += height;
		}
	}

	@Override
	public void addLayoutComponent(String pName, Component pComponent) 
	{}

	@Override
	public void removeLayoutComponent(Component pComponent)
	{}

   
}
