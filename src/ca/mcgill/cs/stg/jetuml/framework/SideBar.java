package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import ca.mcgill.cs.stg.jetuml.graph.Graph;

public class SideBar extends JPanel{
	
	private ToolBar aToolBar;
	private ExtendedToolBar aExtendedToolBar;
	private OptionalToolBar aOptionalToolBar;
	private ExtendedOptionalToolBar aExtendedOptionalToolBar;

	public SideBar(GraphFrame pGraphFrame)
	{
		aToolBar = new ToolBar(pGraphFrame.getGraph());
		aToolBar.setVisible(true);
		aExtendedToolBar = new ExtendedToolBar(pGraphFrame.getGraph());
		aExtendedToolBar.setVisible(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(aToolBar);
		add(aExtendedToolBar);
		add(Box.createRigidArea(new Dimension(0, 10)));
		aOptionalToolBar = new OptionalToolBar(pGraphFrame);
		aOptionalToolBar.setVisible(true);
		aExtendedOptionalToolBar = new ExtendedOptionalToolBar(pGraphFrame);
		aExtendedOptionalToolBar.setVisible(false);
		add(aOptionalToolBar);
		add(aExtendedOptionalToolBar);
		add(Box.createRigidArea(new Dimension(0, 10)));
		//Just a dummy button for now.
		final JButton expandButton = new JButton("<<");
		expandButton.setAlignmentX(CENTER_ALIGNMENT);
		expandButton.setToolTipText("Expand");
		expandButton.setPreferredSize(new Dimension(25,25));
		expandButton.addActionListener(new ActionListener()
        {
       	 public void actionPerformed(ActionEvent pEvent)
       	 {
       		if(expandButton.getText().equals("<<")){
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
	
	public ToolBar getToolBar()
	{
		return aToolBar;
	}
	
	public ExtendedToolBar getExtendedToolBar()
	{
		return aExtendedToolBar;
	}
	
	public boolean isExtended()
	{
		if(aExtendedToolBar.isVisible())
		{
			return true;
		}
		return false;
	}
	
	
}
