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

package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;

public class TestVerticalLayout
{
	private JPanel aPanel;
	private JButton[] aButtons;
	private VerticalLayout aLayout;
	
	@Before
	public void setup()
	{
		aPanel = new JPanel();
		aLayout = new VerticalLayout();
		aButtons = new JButton[] {
				new JButton("A"), 
				new JButton("AA"),
				new JButton("AAA"),
				new JButton("AA"),
				new JButton("AAAA"),
				new JButton("AAA"),
				new JButton("AAAA"),
				new JButton("AAAAA"),
				new JButton("AAAA"),
				new JButton("AAAAA") };
	}
	
	@Test
	public void testZeroContainerNoChildren()
	{
		aLayout.layoutContainer(aPanel); // Test that it does not crash
		assertEquals(new Dimension(), aLayout.minimumLayoutSize(aPanel));
		assertEquals(new Dimension(), aLayout.preferredLayoutSize(aPanel));
	}
	
	@Test
	public void testZeroContainerOneChild()
	{
		aPanel.add(aButtons[0]);
		assertEquals(new Dimension((int)aButtons[0].getPreferredSize().getWidth(), 0),
				aLayout.minimumLayoutSize(aPanel));
		assertEquals(new Dimension((int)aButtons[0].getPreferredSize().getWidth(), 0),
						aLayout.preferredLayoutSize(aPanel));
		aLayout.layoutContainer(aPanel);
		assertEquals(new Rectangle2D.Double(0, 0, aButtons[0].getPreferredSize().getWidth(), aButtons[0].getPreferredSize().getHeight()), aButtons[0].getBounds());
	}
	
	@Test
	public void testZeroContainerTwoChildren()
	{
		aPanel.add(aButtons[0]);
		aPanel.add(aButtons[1]);
		assertEquals(new Dimension((int)aButtons[0].getPreferredSize().getWidth() + (int)aButtons[1].getPreferredSize().getWidth(), 0),
				aLayout.minimumLayoutSize(aPanel));
		assertEquals(new Dimension((int)aButtons[0].getPreferredSize().getWidth() + (int)aButtons[1].getPreferredSize().getWidth(), 0),
				aLayout.preferredLayoutSize(aPanel));
		aLayout.layoutContainer(aPanel);
		assertEquals(new Rectangle2D.Double(0, 0, aButtons[0].getPreferredSize().getWidth(), aButtons[0].getPreferredSize().getHeight()), aButtons[0].getBounds());
		assertEquals(new Rectangle2D.Double(aButtons[0].getPreferredSize().getWidth(), 0, aButtons[1].getPreferredSize().getWidth(), aButtons[1].getPreferredSize().getHeight()), aButtons[1].getBounds());
	}
	
	@Test
	public void testOneChild()
	{
		aPanel.setBounds(new Rectangle(0, 0, 100, 100));
		aPanel.add(aButtons[0]);
		assertEquals(new Dimension((int)aButtons[0].getPreferredSize().getWidth(), 100),
				aLayout.minimumLayoutSize(aPanel));
		assertEquals(new Dimension((int)aButtons[0].getPreferredSize().getWidth(), 100),
						aLayout.preferredLayoutSize(aPanel));
		aLayout.layoutContainer(aPanel);
		assertEquals(new Rectangle2D.Double(0, 0, aButtons[0].getPreferredSize().getWidth(), aButtons[0].getPreferredSize().getHeight()), aButtons[0].getBounds());
	}
	
	@Test
	public void testTwoChildrenOneColumn()
	{
		aPanel.setBounds(new Rectangle(0, 0, 100, 100));
		aPanel.add(aButtons[0]);
		aPanel.add(aButtons[1]);
		assertEquals(new Dimension((int)aButtons[1].getPreferredSize().getWidth(), 100),
				aLayout.minimumLayoutSize(aPanel));
		assertEquals(new Dimension((int)aButtons[1].getPreferredSize().getWidth(), 100),
						aLayout.preferredLayoutSize(aPanel));
		aLayout.layoutContainer(aPanel);
		assertEquals(new Rectangle2D.Double(0, 0, aButtons[0].getPreferredSize().getWidth(), aButtons[0].getPreferredSize().getHeight()), aButtons[0].getBounds());
		assertEquals(new Rectangle2D.Double(0, aButtons[0].getPreferredSize().getHeight(), aButtons[1].getPreferredSize().getWidth(), aButtons[1].getPreferredSize().getHeight()), aButtons[1].getBounds());
	}
	
	@Test
	public void testThreeChildrenOneColumn()
	{
		aPanel.setBounds(new Rectangle(0, 0, 100, 200));
		aPanel.add(aButtons[0]);
		aPanel.add(aButtons[1]);
		aPanel.add(aButtons[2]);
		assertEquals(new Dimension((int)aButtons[2].getPreferredSize().getWidth(), 200),
				aLayout.minimumLayoutSize(aPanel));
		assertEquals(new Dimension((int)aButtons[2].getPreferredSize().getWidth(), 200),
						aLayout.preferredLayoutSize(aPanel));
		aLayout.layoutContainer(aPanel);
		assertEquals(new Rectangle2D.Double(0, 0, aButtons[0].getPreferredSize().getWidth(), aButtons[0].getPreferredSize().getHeight()), aButtons[0].getBounds());
		assertEquals(new Rectangle2D.Double(0, aButtons[0].getPreferredSize().getHeight(), aButtons[1].getPreferredSize().getWidth(), aButtons[1].getPreferredSize().getHeight()), aButtons[1].getBounds());
		assertEquals(new Rectangle2D.Double(0, aButtons[0].getPreferredSize().getHeight() + aButtons[1].getPreferredSize().getHeight(), 
				aButtons[2].getPreferredSize().getWidth(), aButtons[2].getPreferredSize().getHeight()), aButtons[2].getBounds());

	}
	
	@Test
	public void testThreeChildrenTwoColumns()
	{
		aPanel.setBounds(new Rectangle(0, 0, 100, 70));
		aPanel.add(aButtons[0]);
		aPanel.add(aButtons[1]);
		aPanel.add(aButtons[2]);
		assertEquals(new Dimension((int)aButtons[1].getPreferredSize().getWidth() + (int)aButtons[2].getPreferredSize().getWidth(), 70),
				aLayout.minimumLayoutSize(aPanel));
		assertEquals(new Dimension((int)aButtons[1].getPreferredSize().getWidth() + (int)aButtons[2].getPreferredSize().getWidth(), 70),
				aLayout.preferredLayoutSize(aPanel));
		aLayout.layoutContainer(aPanel);
		assertEquals(new Rectangle2D.Double(0, 0, aButtons[0].getPreferredSize().getWidth(), aButtons[0].getPreferredSize().getHeight()), aButtons[0].getBounds());
		assertEquals(new Rectangle2D.Double(0, aButtons[0].getPreferredSize().getHeight(), aButtons[1].getPreferredSize().getWidth(), aButtons[1].getPreferredSize().getHeight()), aButtons[1].getBounds());
		assertEquals(new Rectangle2D.Double(aButtons[1].getPreferredSize().getWidth(), 0, 
				aButtons[2].getPreferredSize().getWidth(), aButtons[2].getPreferredSize().getHeight()), aButtons[2].getBounds());

	}
}
