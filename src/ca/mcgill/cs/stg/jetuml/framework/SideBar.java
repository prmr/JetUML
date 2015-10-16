/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A JPanel that can expand through a button press. It contains
 * the ToolBar, the OptionalToolBar and the extended versions of each.
 * When the expand button is clicked, the Extended versions of the toolbars are displayed, 
 * when it is contracted the opposite happens.
 * 
 * @author JoelChev
 *
 */
@SuppressWarnings("serial")
public class SideBar extends JPanel
{
	private static final int COMPONENT_GAP = 10;
	private static final int BUTTON_SIZE = 25;
	private ToolBar aToolBar;
	private ExtendedToolBar aExtendedToolBar;
	private OptionalToolBar aOptionalToolBar;
	private ExtendedOptionalToolBar aExtendedOptionalToolBar;

	/**
	 * Contructs a SideBar for the GraphFrame.
	 * @param pGraphFrame the GraphFrame associated with this SideBar. 
	 */
	public SideBar(GraphFrame pGraphFrame)
	{
		aToolBar = new ToolBar(pGraphFrame.getGraph());
		aToolBar.setVisible(true);
		aExtendedToolBar = new ExtendedToolBar(pGraphFrame.getGraph());
		aExtendedToolBar.setVisible(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(aToolBar);
		add(aExtendedToolBar);
		add(Box.createRigidArea(new Dimension(0, COMPONENT_GAP)));
		aOptionalToolBar = new OptionalToolBar(pGraphFrame);
		aOptionalToolBar.setVisible(true);
		aExtendedOptionalToolBar = new ExtendedOptionalToolBar(pGraphFrame);
		aExtendedOptionalToolBar.setVisible(false);
		add(aOptionalToolBar);
		add(aExtendedOptionalToolBar);
		add(Box.createRigidArea(new Dimension(0, COMPONENT_GAP)));
		//Just a dummy button for now.
		final JButton expandButton = new JButton("<<");
		expandButton.setAlignmentX(CENTER_ALIGNMENT);
		expandButton.setToolTipText("Expand");
		expandButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
		expandButton.addActionListener(new ActionListener()
        {
       	 public void actionPerformed(ActionEvent pEvent)
       	 {
       		if(expandButton.getText().equals("<<"))
       		{
       			expandButton.setText(">>");
       			expandButton.setToolTipText("Contract");
       			aToolBar.setVisible(false);
       			if(aToolBar.getSelectedButtonIndex()!=-1)
       			{
       				aExtendedToolBar.setButtonSelected(aToolBar.getSelectedButtonIndex());
       			}
       			aOptionalToolBar.setVisible(false);
       			aExtendedToolBar.setVisible(true);
       			aExtendedOptionalToolBar.setVisible(true);
       		}
       		else
       		{
       			expandButton.setText("<<");
       			expandButton.setToolTipText("Expand");
       			aExtendedToolBar.setVisible(false);
       			if(aExtendedToolBar.getSelectedButtonIndex()!=-1)
       			{
       				aToolBar.setButtonSelected(aExtendedToolBar.getSelectedButtonIndex());
       			}
       			aExtendedOptionalToolBar.setVisible(false);
       			aToolBar.setVisible(true);
       			aOptionalToolBar.setVisible(true);
       		}
       	 }
        });
		add(expandButton);
	}
	
	/**
	 * @return aToolBar the regular ToolBar of this SideBar.
	 */
	public ToolBar getToolBar()
	{
		return aToolBar;
	}
	
	/**
	 * @return aExtendedToolBar the extended ToolBar of this SideBar.
	 */
	public ExtendedToolBar getExtendedToolBar()
	{
		return aExtendedToolBar;
	}
	
	/**
	 * @return a boolean value indicating if the SideBar is currently extended or not.
	 */
	public boolean isExtended()
	{
		if(aExtendedToolBar.isVisible())
		{
			return true;
		}
		return false;
	}
	
	
}
