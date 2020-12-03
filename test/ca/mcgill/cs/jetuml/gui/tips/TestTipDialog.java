/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javafx.event.ActionEvent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.gui.tips.TipLoader.Tip;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TestTipDialog 
{
	public static final int NUM_TIPS = Integer.parseInt(RESOURCES.getString("tips.quantity"));
	public static double TIP_ELEMENT_IMAGE_NODES_MAX_WIDTH;
	
	private TipDialog aTipDialog;
	private Tip aTip1;
	private Tip aTip2;
	
	@BeforeAll
	public static void setupClass() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		JavaFXLoader.load();
		
		Field paddingField = TipDialog.class.getDeclaredField("PADDING");
		paddingField.setAccessible(true);
		double padding = paddingField.getDouble(null);
		
		Field defaultNodeSpacingField = TipDialog.class.getDeclaredField("DEFAULT_NODE_SPACING");
		defaultNodeSpacingField.setAccessible(true);
		double defaultNodeSpacing = defaultNodeSpacingField.getDouble(null);
		
		Field windowPrefWidthField = TipDialog.class.getDeclaredField("WINDOW_PREF_WIDTH");
		windowPrefWidthField.setAccessible(true);
		double windowPredWidth = windowPrefWidthField.getDouble(null);
		
		TIP_ELEMENT_IMAGE_NODES_MAX_WIDTH = windowPredWidth - 2 * padding - 4 * defaultNodeSpacing;
	}
	
	@BeforeEach
	public void setup()
	{
		aTipDialog = new TipDialog(null);
		aTip1 = TipLoader.loadTip(1);
		aTip2 = TipLoader.loadTip(2);
	}
	
	@Test
	public void testGetTipElementAsNodeHandlesAllMedia()
	{
		VBox parent = new VBox();
		for(Media media : Media.values())
		{
			TipElement tipElement = new TipElement(media, "tip1.png"); //assuming tip1.png exists.
			// When the media is Image, getTipElementAsNode requires the content to be a valid
			// image name, but the other media (Text) don't have such requirements, so we can
			// use tip1.png as the content for all tips.
			
			// This is a crash test for this method, the return value does not matter
			getTipElementAsNode(aTipDialog, tipElement, parent);
		}
	}
	
	@Test
	public void testGetTipTitleAsTextNodeGivesExpectedContent()
	{
		String title = aTip1.getTitle();
		Text titleNode = getTipTitleAsTextNode(aTip1);
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
	
	@Test
	public void testGetTipAsVBoxReturnsValidVBox()
	{
		assertNotNull(getTipAsVBox(aTipDialog, aTip1));
	}
	
	@Test
	public void testTipDialogCheckBoxIsChecked() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		Field checkBoxField = TipDialog.class.getDeclaredField("aShowTipsOnStartupCheckBox");
		checkBoxField.setAccessible(true);
		CheckBox checkBox = (CheckBox)(checkBoxField.get(aTipDialog));
		assertTrue(checkBox.isSelected());
	}
	
	@Test
	public void testCreateEmptyTipMenuReturnsValidHBox()
	{
		assertNotNull(createEmptyTipMenu(aTipDialog));
	}
	
	@Test
	public void testCreateTipMenuButtonsReturnsValidHBox()
	{
		assertNotNull(createTipMenuButtons(aTipDialog));
	}
	
	@Test
	public void testCreateTipMenuReturnsValidHBox()
	{
		assertNotNull(createTipMenu(aTipDialog));
	}
	
	@Test
	public void testCreateTipMenuReturnsHBoxWithChildren()
	{
		HBox tipMenu = createTipMenu(aTipDialog);
		assertTrue(tipMenu.getChildren().size() > 0);
	}
	
	@Test
	public void testSetupNewTipPutsTipInTipDisplayPane() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field tipDisplayField = TipDialog.class.getDeclaredField("aTipDisplay");
		tipDisplayField.setAccessible(true);
		ScrollPane tipDisplay = (ScrollPane) tipDisplayField.get(aTipDialog);
		
		assertEquals(tipDisplay.getContent(), null);
		
		setupNewTip(aTipDialog, aTip1);
		
		assertNotEquals(tipDisplay.getContent(), null);
	}
	
	@Test
	public void testSetupNewTipReplacesPreviousTip() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field tipDisplayField = TipDialog.class.getDeclaredField("aTipDisplay");
		tipDisplayField.setAccessible(true);
		ScrollPane tipDisplay = (ScrollPane) tipDisplayField.get(aTipDialog);
		
		setupNewTip(aTipDialog, aTip1);
		
		Node firstDisplayedTip = tipDisplay.getContent();
		
		setupNewTip(aTipDialog, aTip2);
		
		assertNotEquals(tipDisplay.getContent(), firstDisplayedTip);
	}
	
	@Test
	public void testGetTextTipElementAsTextNodeReturnsValidTextNode()
	{
		String content = "sample content";
		TipElement tipElement = new TipElement(Media.TEXT, content);
		
		Text node = getTextTipElementAsTextNode(aTipDialog, tipElement);
		
		assertNotNull(node);
	}
	
	@Test
	public void testGetTextTipElementAsTextNodeReturnsNodeWithTextWrapAround()
	{
		String content = "sample content";
		TipElement tipElement = new TipElement(Media.TEXT, content);
		
		Text node = getTextTipElementAsTextNode(aTipDialog, tipElement);
		
		assertTrue(node.wrappingWidthProperty().isBound());
	}
	
	@Test
	public void testGetTextTipElementAsTextNodeHasRightContent()
	{
		String content = "sample content";
		TipElement tipElement = new TipElement(Media.TEXT, content);
		
		Text node = getTextTipElementAsTextNode(aTipDialog, tipElement);
		
		assertEquals(node.getText(), content);
	}
	
	@Test
	public void testGetImageTipElementAsImageViewLoadsImageProperly()
	{
		String imageName = "tip1.png";
		TipElement tipElement = new TipElement(Media.IMAGE, imageName);
		
		ImageView node = getImageTipElementAsImageView(aTipDialog, tipElement);
		
		assertNotNull(node);
		assertFalse(node.getImage().isError());
	}
	
	@Test
	public void testGetImageTipElementAsImageViewReturnsImageViewsWithValidWidth()
	{
		for(int i = 1; i <= NUM_TIPS; i++)
		{
			Tip tip = TipLoader.loadTip(i);
			List<TipElement> tipElements = tip.getElements();
			
			for(TipElement tipElement : tipElements)
			{
				if(tipElement.getMedia() != Media.IMAGE)
				{
					continue;
				}
				ImageView node = getImageTipElementAsImageView(aTipDialog, tipElement);
				double imageWidth = node.getImage().getWidth();
				boolean imageIsSmallEnough = (imageWidth < TIP_ELEMENT_IMAGE_NODES_MAX_WIDTH);
				boolean nodeIsFitToValidWidth = (node.getFitWidth() == TIP_ELEMENT_IMAGE_NODES_MAX_WIDTH);
				
				assertTrue( imageIsSmallEnough || nodeIsFitToValidWidth);
			}
		}
	}
	
	@Test
	public void testCheckboxInitiallySelectedProperly() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field checkBoxField = TipDialog.class.getDeclaredField("aShowTipsOnStartupCheckBox");
		checkBoxField.setAccessible(true);
		CheckBox checkBox = (CheckBox)(checkBoxField.get(aTipDialog));
		
		boolean userPrefCheckBoxSelected = UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips);
		
		boolean checkBoxIsSelected = checkBox.isSelected();
		
		assertEquals(checkBoxIsSelected, userPrefCheckBoxSelected);
	}
	
	@Test
	public void testUncheckingCheckboxTurnsUserPrefFalse() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field checkBoxField = TipDialog.class.getDeclaredField("aShowTipsOnStartupCheckBox");
		checkBoxField.setAccessible(true);
		CheckBox checkBox = (CheckBox)(checkBoxField.get(aTipDialog));
		
		boolean checkBoxInitialValue = UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips);
		UserPreferences.instance().setBoolean(UserPreferences.BooleanPreference.showTips, true);
		
		checkBox.setSelected(false);
		checkBox.getOnAction().handle(new ActionEvent());
		
		boolean newUserPref = UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips);
		UserPreferences.instance().setBoolean(UserPreferences.BooleanPreference.showTips, checkBoxInitialValue);
		
		assertFalse(newUserPref);
	}
	
	@Test
	public void testCheckingCheckboxTurnsUserPrefTrue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field checkBoxField = TipDialog.class.getDeclaredField("aShowTipsOnStartupCheckBox");
		checkBoxField.setAccessible(true);
		CheckBox checkBox = (CheckBox)(checkBoxField.get(aTipDialog));
		
		boolean checkBoxInitialValue = UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips);
		UserPreferences.instance().setBoolean(UserPreferences.BooleanPreference.showTips, false);
		
		checkBox.setSelected(true);
		checkBox.getOnAction().handle(new ActionEvent());
		
		boolean newUserPref = UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips);
		UserPreferences.instance().setBoolean(UserPreferences.BooleanPreference.showTips, checkBoxInitialValue);
		
		assertTrue(newUserPref);
	}
	
	private static Node getTipElementAsNode(TipDialog pImplicitTipDialog, TipElement pTipElement, VBox pParent)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("getTipElementAsNode", TipElement.class, VBox.class);
			method.setAccessible(true);
			return (Node) method.invoke(pImplicitTipDialog, pTipElement, pParent);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static Text getTipTitleAsTextNode(Tip pTip)
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
	
	private static VBox getTipAsVBox(TipDialog pImplicitTipDialog, Tip pTip)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("getTipAsVBox", Tip.class);
			method.setAccessible(true);
			return (VBox) method.invoke(pImplicitTipDialog, pTip);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static HBox createTipMenu(TipDialog pImplicitTipDialog)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("createTipMenu");
			method.setAccessible(true);
			return (HBox) method.invoke(pImplicitTipDialog);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static HBox createEmptyTipMenu(TipDialog pImplicitTipDialog)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("createEmptyTipMenu");
			method.setAccessible(true);
			return (HBox) method.invoke(pImplicitTipDialog);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static HBox createTipMenuButtons(TipDialog pImplicitTipDialog)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("createTipMenuButtons");
			method.setAccessible(true);
			return (HBox) method.invoke(pImplicitTipDialog);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static void setupNewTip(TipDialog pImplicitTipDialog, Tip pTip)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("setupNewTip", Tip.class);
			method.setAccessible(true);
			method.invoke(pImplicitTipDialog, pTip);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	private static Text getTextTipElementAsTextNode(TipDialog pImplicitTipDialog, TipElement pTipElement)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("getTextTipElementAsTextNode", TipElement.class);
			method.setAccessible(true);
			return (Text) method.invoke(pImplicitTipDialog, pTipElement);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private static ImageView getImageTipElementAsImageView(TipDialog pImplicitTipDialog, TipElement pTipElement)
	{
		try
		{
			Method method = TipDialog.class.getDeclaredMethod("getImageTipElementAsImageView", TipElement.class);
			method.setAccessible(true);
			return (ImageView) method.invoke(pImplicitTipDialog, pTipElement);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
}
