/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *  A property editor for the MultiLineString type.
 */
public class MultiLineStringEditor extends PropertyEditorSupport
{
	private static final int ROWS = 5;
	private static final int COLUMNS = 30;
	   
	private static Set tab = new HashSet(1);
	private static Set shiftTab = new HashSet(1);
	
	static 
	{
		tab.add(KeyStroke.getKeyStroke("TAB" ));
		shiftTab.add(KeyStroke.getKeyStroke( "shift TAB" ));
	} 
	
	@Override
	public boolean supportsCustomEditor()
	{
		return true;
	}

	@Override
	public Component getCustomEditor()
	{
		final MultiLineString value = (MultiLineString)getValue();
		final JTextArea textArea = new JTextArea(ROWS, COLUMNS);

		textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
		textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shiftTab);
      
		textArea.setText(value.getText());
		textArea.getDocument().addDocumentListener(new DocumentListener()
		{
            public void insertUpdate(DocumentEvent pEvent) 
            {
               value.setText(textArea.getText());
               firePropertyChange();
            }
            public void removeUpdate(DocumentEvent pEvent) 
            {
               value.setText(textArea.getText());
               firePropertyChange();
            }
            public void changedUpdate(DocumentEvent pEvent) 
            {}
         });
      return new JScrollPane(textArea);
   }
   
	// The actions
   private static Action nextFocusAction = new AbstractAction("Move Focus Forward") 
   {
      public void actionPerformed(ActionEvent evt) 
      {
         ((Component)evt.getSource()).transferFocus();
      }
   };
   
   private static Action prevFocusAction = new AbstractAction("Move Focus Backwards") 
   {
      public void actionPerformed(ActionEvent evt) 
      {
         ((Component)evt.getSource()).transferFocusBackward();
      }
   };
   
 
}
