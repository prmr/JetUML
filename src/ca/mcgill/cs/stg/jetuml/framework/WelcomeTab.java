package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * @author JoelChev
 * This class instantiates the Welcome Tab that is the default Tab in JetUML.
 *
 */
@SuppressWarnings("serial")
public class WelcomeTab extends JInternalFrame
{
//	private static final Color lightBackground = new Color(72,118,255);
//    private static final Color darkBackground = new Color(67,110,238);
    
	private static final int BORDER_MARGIN = 45;
	private static final int ALTERNATIVE_BORDER_MARGIN = 30;
	private static final int FOOT_BORDER_MARGIN = 10;
	private static final int FONT_SIZE = 30;
	private ResourceBundle aWelcomeResources;
    private JPanel aFootTextPanel;;
    private JPanel aRightTitlePanel;
    private JPanel aLeftTitlePanel;
    private JPanel aLeftPanel;
    private JPanel aRightPanel;
    private JMenu aNewFileMenu;
    private JMenu aRecentFileMenu;
    private ImageIcon aLeftPanelIcon;
    private ImageIcon aRightPanelIcon;
    private String aFootText;
    
	/**
	 * @param pNewFileMenu The NewFileMenu to link to the WelcomeTab.
	 * @param pRecentFileMenu The RecentFileMenu to link to the WelcomeTab.
	 */
	public WelcomeTab(JMenu pNewFileMenu, JMenu pRecentFileMenu)
	{
		
		aWelcomeResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
		aLeftPanelIcon = new ImageIcon(aWelcomeResources.getString("welcome.create.icon"));
		aRightPanelIcon = new ImageIcon(aWelcomeResources.getString("welcome.open.icon")); 
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
	
//		@Override
//	    public void paint(Graphics g)
//	    {
//	    	super.paint(g);
//	        Graphics2D g2 = (Graphics2D) g;
//	        Paint currentPaint = g2.getPaint();
//	        GradientPaint paint = new GradientPaint(getWidth() / 2, -getHeight() / 4, lightBackground,
//	                getWidth() / 2, getHeight() + getHeight() / 4, darkBackground);
//	        g2.setPaint(paint);
//	        g2.fillRect(0, 0, getWidth(), getHeight());
//	        g2.setPaint(currentPaint);
//	    }

	    private JPanel getLeftPanel()
	    {
	        if (this.aLeftPanel == null)
	        {
	            aLeftPanel = new JPanel();
	            aLeftPanel.setOpaque(false);
	            aLeftPanel.setLayout(new BoxLayout(aLeftPanel, BoxLayout.Y_AXIS));
	            aLeftPanel.setBorder(new EmptyBorder(0, 0, 0, BORDER_MARGIN));

	            for (int i = 0; i < aNewFileMenu.getItemCount(); i++)
	            {
	                final JMenuItem item = aNewFileMenu.getItem(i);
	                String label = item.getText();
	                JButton newDiagramShortcut = new JButton(label.toLowerCase());
	                newDiagramShortcut.setUI(new WelcomeButtonUI());
	                newDiagramShortcut.setAlignmentX(Component.RIGHT_ALIGNMENT);
	                newDiagramShortcut.addActionListener(new ActionListener()
	                {
	                    public void actionPerformed(ActionEvent pEvent)
	                    {
	                        item.doClick();
	                    }
	                });
	                aLeftPanel.add(newDiagramShortcut);
	            }

	        }
	        return this.aLeftPanel;
	    }

	    private JPanel getRightPanel()
	    {
	        if (this.aRightPanel == null)
	        {
	            this.aRightPanel = new JPanel();
	            this.aRightPanel.setOpaque(false);
	            this.aRightPanel.setLayout(new BoxLayout(aRightPanel, BoxLayout.Y_AXIS));
	            this.aRightPanel.setBorder(new EmptyBorder(0, BORDER_MARGIN, 0, BORDER_MARGIN));

	            for (int i = 0; i < aRecentFileMenu.getItemCount(); i++)
	            {
	                final JMenuItem item = aRecentFileMenu.getItem(i);
	                String label = item.getText().substring(2);
	                JButton fileShortcut = new JButton(label.toLowerCase());
	                fileShortcut.setUI(new WelcomeButtonUI());
	                fileShortcut.setAlignmentX(Component.LEFT_ALIGNMENT);
	                fileShortcut.addActionListener(new ActionListener()
	                {
	                    public void actionPerformed(ActionEvent pEvent)
	                    {
	                        item.doClick();
	                    }
	                });
	                aRightPanel.add(fileShortcut);
	            }

	        }
	        return this.aRightPanel;
	    }

	    private JPanel getLeftTitlePanel()
	    {
	        if (this.aLeftTitlePanel == null)
	        {
	            JLabel icon = new JLabel();
	            icon.setIcon(this.aLeftPanelIcon);

	            JLabel title = new JLabel(aNewFileMenu.getText().toLowerCase());
	            title.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
	            title.setBorder(new EmptyBorder(0, ALTERNATIVE_BORDER_MARGIN, 0, 0));

	            JPanel panel = new JPanel();
	            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	            panel.add(icon);
	            panel.add(title);
	            panel.setOpaque(false);

	            this.aLeftTitlePanel = new JPanel();
	            this.aLeftTitlePanel.setOpaque(false);
	            this.aLeftTitlePanel.setLayout(new BorderLayout());
	            this.aLeftTitlePanel.add(panel, BorderLayout.EAST);
	            this.aLeftTitlePanel.setBorder(new EmptyBorder(0, 0, ALTERNATIVE_BORDER_MARGIN, BORDER_MARGIN));
	        }
	        return this.aLeftTitlePanel;
	    }

	    private JPanel getRightTitlePanel()
	    {
	        if (this.aRightTitlePanel == null)
	        {
	            JLabel icon = new JLabel();
	            icon.setIcon(this.aRightPanelIcon);
	            icon.setAlignmentX(Component.LEFT_ALIGNMENT);

	            JLabel title = new JLabel(aRecentFileMenu.getText().toLowerCase());
	            title.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
	            title.setBorder(new EmptyBorder(0, 0, 0, ALTERNATIVE_BORDER_MARGIN));

	            JPanel panel = new JPanel();
	            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	            panel.add(title);
	            panel.add(icon);
	            panel.setOpaque(false);

	            this.aRightTitlePanel = new JPanel();
	            this.aRightTitlePanel.setOpaque(false);
	            this.aRightTitlePanel.setLayout(new BorderLayout());
	            this.aRightTitlePanel.add(panel, BorderLayout.WEST);
	            this.aRightTitlePanel.setBorder(new EmptyBorder(0, BORDER_MARGIN, ALTERNATIVE_BORDER_MARGIN, 0));
	        }
	        return this.aRightTitlePanel;
	    }

	    private JPanel getFootTextPanel()
	    {
	        if (this.aFootTextPanel == null)
	        {
	        	this.aFootText = aWelcomeResources.getString("welcome.copyright");
	            this.aFootTextPanel = new JPanel();
	            this.aFootTextPanel.setOpaque(false);
	            this.aFootTextPanel.setBorder(new EmptyBorder(0, 0, FOOT_BORDER_MARGIN, 0));
	            this.aFootTextPanel.setLayout(new BoxLayout(this.aFootTextPanel, BoxLayout.Y_AXIS));
	            this.aFootTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

	            JLabel text = new JLabel(this.aFootText);
	            text.setAlignmentX(Component.CENTER_ALIGNMENT);

	            this.aFootTextPanel.add(text);
	        }

	        return this.aFootTextPanel;
	    }

}	
