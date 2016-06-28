package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Aligns components top down and moves to a second row 
 * if there isn't sufficient space.
 */
public class VerticalLayout implements LayoutManager
{
	@Override
	public void addLayoutComponent(String pString, Component pComponent)
	{}
	
	@Override
	public void removeLayoutComponent(Component pComponent)
	{}

	@Override
	public void layoutContainer(Container pParent)
	{
		int accumulatedHeight = 0;
		int xOffset = 0;
		int maxWidth = 0;
		
		for( Component child : pParent.getComponents() )
		{
			if( accumulatedHeight + child.getPreferredSize().getHeight() > pParent.getHeight() )
			{
				accumulatedHeight = 0;
				xOffset = xOffset + maxWidth;
				maxWidth = 0;
			}
			child.setBounds(xOffset, accumulatedHeight, 
					(int)child.getPreferredSize().getWidth(), (int)child.getPreferredSize().getHeight());
			accumulatedHeight += child.getPreferredSize().getHeight();
			maxWidth = Math.max(maxWidth, (int) child.getPreferredSize().getWidth());
		}
	}
	
	@Override
	public Dimension minimumLayoutSize(Container pParent)
	{
		int accumulatedHeight = 0;
		int xOffset = 0;
		int maxWidth = 0;
		
		for( Component child : pParent.getComponents() )
		{
			if( accumulatedHeight + child.getPreferredSize().getHeight() > pParent.getHeight() )
			{
				accumulatedHeight = 0;
				xOffset = xOffset + maxWidth;
				maxWidth = 0;
			}
			accumulatedHeight += child.getPreferredSize().getHeight();
			maxWidth = Math.max(maxWidth, (int) child.getPreferredSize().getWidth());
		}
		return new Dimension(xOffset + maxWidth, pParent.getHeight());
	}

	@Override
	public Dimension preferredLayoutSize(Container pParent)
	{
		return minimumLayoutSize(pParent);
	}
}