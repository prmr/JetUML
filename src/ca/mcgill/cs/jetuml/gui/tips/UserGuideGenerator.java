/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2021 by McGill University.
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
import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.loadTip;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.lines;
import static java.nio.file.Files.write;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import ca.mcgill.cs.jetuml.gui.tips.TipLoader.Tip;

/**
 * Script to generate the user guide by collecting all the tips of the day in a single page.
 * The user guide is created by expanding a template md file with HTML content 
 * to show all the tips in collapsible elements.
 */
public final class UserGuideGenerator
{
	private static final Path INPUT_FILE = Paths.get("docs", "user-guide-template.txt");
	private static final Path OUTPUT_FILE = Paths.get("docs", "user-guide.md");

	private static final String PLACEHOLDER = "$TEXT$";
	private static final String TEMPLATE_BUTTON = "<button class=\"collapsible\">$TEXT$</button>";
	private static final String TEMPLATE_DIV_OPEN = "<div class=\"content\">";
	private static final String TEMPLATE_TEXT = "<p>$TEXT$</p>";
	private static final String TEMPLATE_IMAGE = "<img src=\"../tipdata/tip_images/$TEXT$\">";
	private static final String TEMPLATE_DIV_CLOSE = "</div>";
	
	private UserGuideGenerator() {}
	
	/**
	 * Run without arguments.
	 * 
	 * @param pArgs Not used.
	 */
	public static void main(String[] pArgs) throws IOException
	{
		String template = lines(INPUT_FILE, UTF_8).collect(joining("\n"));
		write(OUTPUT_FILE, template.replace(PLACEHOLDER,  tipsAsHtml()).getBytes(UTF_8));
		System.out.println("The User Guide was generated sucessfully.");
	}
	
	/*
	 * Creates an html representation of all available tips.
	 */
	private static String tipsAsHtml()
	{
		List<String> tips = new ArrayList<>();
		for( int tipNumber = 1; tipNumber <= numberOfTips(); tipNumber++ )
		{
			tips.add(toHtml(loadTip(tipNumber)));
		}
		return tips.stream().collect(joining("\n"));
	}
	
	/* 
	 * Obtains the number of tips for the application's properties.
	 */
	private static int numberOfTips()
	{
		return Integer.parseInt(RESOURCES.getString("tips.quantity"));
	}
	
	/*
	 * Creates an html representation of pTip suitable for display in the user guide.
	 */
	private static String toHtml(Tip pTip)
	{
		StringJoiner html = new StringJoiner("\n");
		html.add(TEMPLATE_BUTTON.replace(PLACEHOLDER, pTip.getTitle()));
		html.add(TEMPLATE_DIV_OPEN);
		for( TipElement element : pTip.getElements() )
		{
			html.add(toHtml(element));
		}
		html.add(TEMPLATE_DIV_CLOSE);
		
		return html.toString();
	}
	
	/*
	 * Creates an html representation of pTipElement suitable for display in the user guide.
	 */
	private static String toHtml(TipElement pTipElement)
	{
		if( pTipElement.getMedia() == Media.TEXT )
		{
			return TEMPLATE_TEXT.replace(PLACEHOLDER, pTipElement.getContent());
		}
		else
		{
			return TEMPLATE_IMAGE.replace(PLACEHOLDER, pTipElement.getContent());
		}
	}
}
