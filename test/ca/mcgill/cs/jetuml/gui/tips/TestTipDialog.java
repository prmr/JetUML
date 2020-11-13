package ca.mcgill.cs.jetuml.gui.tips;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.gui.tips.TipLoader.Tip;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TestTipDialog 
{
	private static TipDialog TIP_DIALOG;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		TIP_DIALOG = new TipDialog(null);
	}
	
	@Test
	public void testGetTipElementAsNodeHandlesAllMedia()
	{
		for(Media media : Media.values())
		{
			TipElement tipElement = new TipElement(media, "tip1.png"); //assuming tip1.png exists.
			// When the media is Image, getTipElementAsNode requires the content to be a valid
			// image name, but the other media (Text) don't have such requirements, so we can
			// use tip1.png as the content for all tips.
			
			Node node = getTipElementAsNode(TIP_DIALOG, tipElement);
		}
	}
	
	@Test
	public void testGetTipTitleAsTextNodeGivesExpectedContent()
	{
		Tip tip = TipLoader.loadTip(1);
		String title = tip.getTitle();
		Text titleNode = getTipTitleAsTextNode(tip);
		assertEquals(title, titleNode.getText());
	}
	
	@Test
	public void testGetTipTitleAsTextNodeHasExpectedFontSize()
	{
		double fontSize;
		try
		{
			Field fontSizeField = TipDialog.class.getDeclaredField("TITLE_FONT_SIZE");
			fontSizeField.setAccessible(true);
			fontSize = fontSizeField.getDouble(null);
		}
		catch(Exception e)
		{
			fail();
			return;
		}
		
		Tip tip = TipLoader.loadTip(1);
		Text titleNode = getTipTitleAsTextNode(tip);
		Font titleFont = titleNode.getFont();
		assertEquals(fontSize, titleFont.getSize());
	}
	
	private Node getTipElementAsNode(TipDialog pImplicitTipDialog, TipElement pTipElement)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("getTipElementAsNode", TipElement.class);
			method.setAccessible(true);
			return (Node) method.invoke(pImplicitTipDialog, pTipElement);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Text getTipTitleAsTextNode(Tip pTip)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("getTipTitleAsTextNode", Tip.class);
			method.setAccessible(true);
			return (Text) method.invoke(null, pTip);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
}
