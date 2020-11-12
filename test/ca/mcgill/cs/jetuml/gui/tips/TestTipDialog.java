package ca.mcgill.cs.jetuml.gui.tips;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class TestTipDialog 
{
	@Test
	public void testTipDialog_getTipElementAsNodeHandlesAllMedia()
	{
		Pane content = new Pane();
		ScrollPane s = new ScrollPane(content);
		String blankContent = "";
		for(Media media : Media.values())
		{
			TipElement tipElement = new TipElement(media, blankContent);
			Node node = getTipElementAsNode(tipElement);
		}
	}
	
	private Node getTipElementAsNode(TipElement pTipElement)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("getTipElementAsNode", TipElement.class);
			method.setAccessible(true);
			return (Node) method.invoke(null, pTipElement);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
}
