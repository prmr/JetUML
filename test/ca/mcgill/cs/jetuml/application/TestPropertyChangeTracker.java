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
/**
 * 
 */
package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.application.MultiLineString;
import ca.mcgill.cs.jetuml.application.PropertyChangeTracker;
import ca.mcgill.cs.jetuml.application.PropertyChangeTracker.PropertyChangeCommand;
import ca.mcgill.cs.jetuml.commands.Command;
import ca.mcgill.cs.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.jetuml.graph.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;
import ca.mcgill.cs.jetuml.graph.nodes.FieldNode;

/**
 * Note that the logic of the tests in this class relies
 * of the assumption that Introspector.getBeanInfo(pEdited.getClass()).getPropertyDescriptors();
 * returns the property descriptors in alphabetical order of property name.
 *
 */
public class TestPropertyChangeTracker
{
	private PropertyChangeTracker aTracker;
	private Field aCommandsField; 
	private Field aIndexField;
	private Field aNewValueField;
	private Field aObjectField;
	private Field aOldValueField;
	
	@Before
	public void setUp() throws Exception
	{
		aTracker = new PropertyChangeTracker();
		aCommandsField = CompoundCommand.class.getDeclaredField("aCommands");
		aCommandsField.setAccessible(true);
		aIndexField = PropertyChangeCommand.class.getDeclaredField("aIndex");
		aIndexField.setAccessible(true);
		aNewValueField = PropertyChangeCommand.class.getDeclaredField("aNewPropValue");
		aNewValueField.setAccessible(true);
		aObjectField = PropertyChangeCommand.class.getDeclaredField("aObject");
		aObjectField.setAccessible(true);
		aOldValueField = PropertyChangeCommand.class.getDeclaredField("aPrevPropValue");
		aOldValueField.setAccessible(true);
	}

	@Test
	public void testClassNode()
	{
		ClassNode node = new ClassNode();
		aTracker.startTrackingPropertyChange(node);
		MultiLineString oldName = node.getName().clone();
		String oldAttributes = node.getAttributes();
		node.getName().setText("Foo");
		node.setAttributes("String foo");
		CompoundCommand command = aTracker.stopTrackingPropertyChange(new ClassDiagramGraph());
		Stack<Command> commands = getChildCommands(command);
		assertEquals(2, commands.size());
		PropertyChangeCommand pcc = (PropertyChangeCommand)commands.pop();
		assertEquals("name", getPropertyName(pcc, node));
		assertEquals(node, getFieldValue(aObjectField, pcc));
		assertEquals(oldName, getFieldValue(aOldValueField, pcc));
		assertEquals(node.getName(), getFieldValue(aNewValueField, pcc));
		// Attributes
		pcc = (PropertyChangeCommand)commands.pop();
		assertEquals("attributes", getPropertyName(pcc, node));
		assertEquals(node, getFieldValue(aObjectField, pcc));
		assertEquals(oldAttributes, getFieldValue(aOldValueField, pcc));
		assertEquals(node.getAttributes(), getFieldValue(aNewValueField, pcc));
	}
	
	@Test
	public void testObjectNode()
	{
		FieldNode node = new FieldNode();
		MultiLineString oldName = node.getName().clone();
		String oldValue = node.getValue();
		node.setValue("value");
		oldValue = node.getValue();
	
		aTracker.startTrackingPropertyChange(node);
		node.getName().setText("Foo");
		node.setValue("");
		
		CompoundCommand command = aTracker.stopTrackingPropertyChange(new ObjectDiagramGraph());
		Stack<Command> commands = getChildCommands(command);
		assertEquals(2, commands.size());
		
		PropertyChangeCommand pcc = (PropertyChangeCommand)commands.pop();
		assertEquals("value", getPropertyName(pcc, node));
		assertEquals(node, getFieldValue(aObjectField, pcc));
		assertEquals(oldValue, getFieldValue(aOldValueField, pcc));
		assertEquals(node.getValue(), getFieldValue(aNewValueField, pcc));
		
		pcc = (PropertyChangeCommand)commands.pop();
		assertEquals("name", getPropertyName(pcc, node));
		assertEquals(node, getFieldValue(aObjectField, pcc));
		assertEquals(oldName, getFieldValue(aOldValueField, pcc));
		assertEquals(node.getName(), getFieldValue(aNewValueField, pcc));
	}
	
	@Test
	public void testAggregationEdge()
	{
		AggregationEdge edge = new AggregationEdge();
	
		aTracker.startTrackingPropertyChange(edge);
		edge.setStartLabel("start");
		edge.setEndLabel("end");
		edge.setType(AggregationEdge.Type.Composition);

		CompoundCommand command = aTracker.stopTrackingPropertyChange(new ClassDiagramGraph());
		Stack<Command> commands = getChildCommands(command);
		assertEquals(3, commands.size());
		
		PropertyChangeCommand pcc = (PropertyChangeCommand)commands.pop();
		assertEquals("type", getPropertyName(pcc, edge));
		assertEquals(edge, getFieldValue(aObjectField, pcc));
		assertEquals(AggregationEdge.Type.Aggregation, getFieldValue(aOldValueField, pcc));
		assertEquals(AggregationEdge.Type.Composition, getFieldValue(aNewValueField, pcc));
		
		pcc = (PropertyChangeCommand)commands.pop();
		assertEquals("startLabel", getPropertyName(pcc, edge));
		assertEquals(edge, getFieldValue(aObjectField, pcc));
		assertEquals("", getFieldValue(aOldValueField, pcc));
		assertEquals("start", getFieldValue(aNewValueField, pcc));
		
		pcc = (PropertyChangeCommand)commands.pop();
		assertEquals("endLabel", getPropertyName(pcc, edge));
		assertEquals(edge, getFieldValue(aObjectField, pcc));
		assertEquals("", getFieldValue(aOldValueField, pcc));
		assertEquals("end", getFieldValue(aNewValueField, pcc));
	}
	
	@Test
	public void testCreatePropertyChangeCommandInvalid()
	{
		assertNull(PropertyChangeTracker.createPropertyChangeCommand(new ClassDiagramGraph(), 
				new ClassNode(), "foo", "fakeOld", "fakeNew"));
	}
	
	@Test
	public void testCreatePropertyChangeCommandValid()
	{
		MultiLineString old = new MultiLineString();
		old.setText("old");
		MultiLineString newValue = new MultiLineString();
		old.setText("new");
		ClassNode node = new ClassNode();
		PropertyChangeCommand command = PropertyChangeTracker.createPropertyChangeCommand(new ClassDiagramGraph(), 
				node, "name", old, newValue);
		assertEquals("name", getPropertyName(command, node));
		assertEquals(node, getFieldValue(aObjectField, command));
		assertEquals(old, getFieldValue(aOldValueField, command));
		assertEquals(newValue, getFieldValue(aNewValueField, command));
	}
	
	@Test
	public void testPropertyChangeCommandExecute()
	{
		MultiLineString old = new MultiLineString();
		old.setText("old");
		MultiLineString newValue = new MultiLineString();
		old.setText("new");
		ClassNode node = new ClassNode();
		PropertyChangeCommand command = PropertyChangeTracker.createPropertyChangeCommand(new ClassDiagramGraph(), 
				node, "name", old, newValue);
		command.execute();
		assertEquals(newValue, node.getName());
	}
	
	@Test
	public void testPropertyChangeCommandUndo()
	{
		MultiLineString old = new MultiLineString();
		old.setText("old");
		MultiLineString newValue = new MultiLineString();
		old.setText("new");
		ClassNode node = new ClassNode();
		PropertyChangeCommand command = PropertyChangeTracker.createPropertyChangeCommand(new ClassDiagramGraph(), 
				node, "name", old, newValue);
		command.execute();
		command.undo();
		assertEquals(old, node.getName());
	}
	
	@SuppressWarnings("unchecked")
	private Stack<Command> getChildCommands(CompoundCommand pCommand)
	{
		try
		{
			return (Stack<Command>)aCommandsField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
	
	private String getPropertyName(PropertyChangeCommand pCommand, Object pEdited)
	{
		try
		{
			PropertyDescriptor[] descriptors = Introspector.getBeanInfo(pEdited.getClass()).getPropertyDescriptors();  
			return descriptors[(Integer)aIndexField.get(pCommand)].getName();
		}
		catch( Exception pException )
		{
			fail();
			return "error";
		}
	}
	
	private Object getFieldValue(Field pField, PropertyChangeCommand pCommand)
	{
		try
		{
			return pField.get(pCommand);
		}
		catch( Exception pException )
		{
			fail();
			return null;
		}
	}
}
