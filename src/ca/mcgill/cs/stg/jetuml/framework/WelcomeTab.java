package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

@SuppressWarnings("serial")
public class WelcomeTab extends JInternalFrame{
	
    private JPanel footTextPanel;;

    private JPanel rightTitlePanel;

    private JPanel leftTitlePanel;

    private JPanel leftPanel;

    private JPanel rightPanel;

    private JMenu aNewFileMenu;
    
    private JMenu aRecentFileMenu;

//    @ResourceBundleBean(key="welcomepanel.new_diagram.icon")
    private ImageIcon leftPanelIcon;
//
//    @ResourceBundleBean(key="welcomepanel.recent_files.icon")
    private ImageIcon rightPanelIcon;
//
//    @ResourceBundleBean(key="welcomepanel.foot_text")
    private String footText;
	
	public WelcomeTab(JMenu pNewFileMenu, JMenu pRecentFileMenu)
	{
	       
	        setOpaque(false);
	        setLayout(new BorderLayout());
	        
	        BasicInternalFrameUI ui = (BasicInternalFrameUI)getUI();
	 	   Container north = (Container)ui.getNorthPane();
	 	   north.remove(0);
	 	   north.validate();
	 	   north.repaint();

	        aNewFileMenu = pNewFileMenu;
	        aRecentFileMenu = pRecentFileMenu;
	        JPanel panel = new JPanel();
	        panel.setLayout(new GridBagLayout());
	        panel.setOpaque(false);

	        JPanel shortcutPanel = new JPanel();
	        shortcutPanel.setOpaque(false);
	        shortcutPanel.setLayout(new GridLayout(2, 2));
	        shortcutPanel.add(getLeftTitlePanel());
	        shortcutPanel.add(getRightTitlePanel());
	        shortcutPanel.add(getLeftPanel());
	        shortcutPanel.add(getRightPanel());
	        GridBagConstraints c = new GridBagConstraints();
	        c.anchor = GridBagConstraints.NORTH;
	        c.weightx = 1;
	        c.gridx = 0;
	        c.gridy = 1;
	        panel.add(shortcutPanel, c);

	        add(panel, BorderLayout.NORTH);
	        add(getFootTextPanel(), BorderLayout.SOUTH);

	    }

	    public void paint(Graphics g)
	    {
	        //Graphics2D g2 = (Graphics2D) g;
	        //Paint currentPaint = g2.getPaint();
	        ///ITheme cLAF = ThemeManager.getInstance().getTheme();
	        try
			{
				for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
				{
					if("Nimbus".equals(info.getName())) 
					{
			            UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
			} 
			catch(UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) 
			{
			    // Nothing: We revert to the default LAF
			}
//	        GradientPaint paint = new GradientPaint(getWidth() / 2, -getHeight() / 4, cLAF.getWelcomeBackgroundStartColor(),
//	                getWidth() / 2, getHeight() + getHeight() / 4, cLAF.getWelcomeBackgroundEndColor());
//	        g2.setPaint(paint);
//	        g2.fillRect(0, 0, getWidth(), getHeight());
//	        g2.setPaint(currentPaint);
	        super.paint(g);
	    }

	    private JPanel getLeftPanel()
	    {
	        if (this.leftPanel == null)
	        {
	            leftPanel = new JPanel();
	            leftPanel.setOpaque(false);
	            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
	            leftPanel.setBorder(new EmptyBorder(0, 0, 0, 45));

	            for (int i = 0; i < aNewFileMenu.getItemCount(); i++)
	            {
	                final JMenuItem item = aNewFileMenu.getItem(i);
	                String label = item.getText();
	                JButton newDiagramShortcut = new JButton(label.toLowerCase());
	                newDiagramShortcut.setUI(new BasicButtonUI());
	                newDiagramShortcut.setAlignmentX(Component.RIGHT_ALIGNMENT);
	                newDiagramShortcut.addActionListener(new ActionListener()
	                {
	                    public void actionPerformed(ActionEvent e)
	                    {
	                        item.doClick();
	                    }
	                });
	                leftPanel.add(newDiagramShortcut);
	            }

	        }
	        return this.leftPanel;
	    }

	    private JPanel getRightPanel()
	    {
	        if (this.rightPanel == null)
	        {
	            this.rightPanel = new JPanel();
	            this.rightPanel.setOpaque(false);
	            this.rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
	            this.rightPanel.setBorder(new EmptyBorder(0, 45, 0, 45));

	            for (int i = 0; i < aRecentFileMenu.getItemCount(); i++)
	            {
	                final JMenuItem item = aRecentFileMenu.getItem(i);
	                String label = item.getText();
	                JButton fileShortcut = new JButton(label.toLowerCase());
	                fileShortcut.setUI(new BasicButtonUI());
	                fileShortcut.setAlignmentX(Component.LEFT_ALIGNMENT);
	                fileShortcut.addActionListener(new ActionListener()
	                {
	                    public void actionPerformed(ActionEvent e)
	                    {
	                        item.doClick();
	                    }
	                });
	                rightPanel.add(fileShortcut);
	            }

	        }
	        return this.rightPanel;
	    }

	    private JPanel getLeftTitlePanel()
	    {
	        if (this.leftTitlePanel == null)
	        {
	            JLabel icon = new JLabel();
	            icon.setIcon(this.leftPanelIcon);

	            JLabel title = new JLabel(aNewFileMenu.getText().toLowerCase());
//	            ITheme cLAF = ThemeManager.getInstance().getTheme();
//	            title.setFont(cLAF.getWelcomeBigFont());
//	            title.setForeground(cLAF.getWelcomeBigForegroundColor());
	            title.setBorder(new EmptyBorder(0, 30, 0, 0));

	            JPanel panel = new JPanel();
	            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	            panel.add(icon);
	            panel.add(title);
	            panel.setOpaque(false);

	            this.leftTitlePanel = new JPanel();
	            this.leftTitlePanel.setOpaque(false);
	            this.leftTitlePanel.setLayout(new BorderLayout());
	            this.leftTitlePanel.add(panel, BorderLayout.EAST);
	            this.leftTitlePanel.setBorder(new EmptyBorder(0, 0, 30, 45));
	        }
	        return this.leftTitlePanel;
	    }

	    private JPanel getRightTitlePanel()
	    {
	        if (this.rightTitlePanel == null)
	        {
	            JLabel icon = new JLabel();
	            icon.setIcon(this.rightPanelIcon);
	            icon.setAlignmentX(Component.LEFT_ALIGNMENT);

	            JLabel title = new JLabel(aRecentFileMenu.getText().toLowerCase());
//	            ITheme cLAF = ThemeManager.getInstance().getTheme();
//	            title.setFont(cLAF.getWelcomeBigFont());
//	            title.setForeground(cLAF.getWelcomeBigForegroundColor());
	            title.setBorder(new EmptyBorder(0, 0, 0, 30));

	            JPanel panel = new JPanel();
	            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	            panel.add(title);
	            panel.add(icon);
	            panel.setOpaque(false);

	            this.rightTitlePanel = new JPanel();
	            this.rightTitlePanel.setOpaque(false);
	            this.rightTitlePanel.setLayout(new BorderLayout());
	            this.rightTitlePanel.add(panel, BorderLayout.WEST);
	            this.rightTitlePanel.setBorder(new EmptyBorder(0, 45, 30, 0));
	        }
	        return this.rightTitlePanel;
	    }

	    private JPanel getFootTextPanel()
	    {
	        if (this.footTextPanel == null)
	        {
	            this.footTextPanel = new JPanel();
	            this.footTextPanel.setOpaque(false);
	            this.footTextPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
	            this.footTextPanel.setLayout(new BoxLayout(this.footTextPanel, BoxLayout.Y_AXIS));
	            this.footTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

	            JLabel text = new JLabel(this.footText);
//	            ITheme cLAF = ThemeManager.getInstance().getTheme();
//	            text.setFont(cLAF.getWelcomeSmallFont());
//	            text.setForeground(cLAF.getWelcomeBigForegroundColor());
	            text.setAlignmentX(Component.CENTER_ALIGNMENT);

	            this.footTextPanel.add(text);
	        }

	        return this.footTextPanel;
	    }

}	
