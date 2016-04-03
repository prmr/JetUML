/**
 * 
 */
package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.commands.Command;
import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.stg.jetuml.commands.PropertyChangeCommand;
import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;

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
		MultiLineString oldAttributes = node.getAttributes().clone();
		node.getName().setText("Foo");
		node.getAttributes().setText("String foo");
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
