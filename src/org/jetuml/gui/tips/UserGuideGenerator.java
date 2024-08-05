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
package org.jetuml.gui.tips;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.lines;
import static java.nio.file.Files.write;
import static java.util.stream.Collectors.joining;
import static org.jetuml.application.ApplicationResources.RESOURCES;
import static org.jetuml.gui.tips.TipLoader.loadTip;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.jetuml.gui.tips.TipLoader.Tip;

/**
 * Script to generate the user guide by collecting all the tips of the day in a single page.
 * The user guide is created by expanding a template md file with HTML content 
 * to show all the tips in collapsible elements.
 */
public final class UserGuideGenerator
{
	private static final Path INPUT_FILE_TOPICS = Paths.get("docs", "user-guide-topics-template.txt");
	private static final Path INPUT_FILE_LEVELS = Paths.get("docs", "user-guide-levels-template.txt");
	private static final Path INPUT_FILE_DIAGRAMS = Paths.get("docs", "user-guide-diagrams-template.txt");
	private static final Path OUTPUT_FILE_TOPICS = Paths.get("docs", "user-guide-topics.md");
	private static final Path OUTPUT_FILE_LEVELS = Paths.get("docs", "user-guide-levels.md");
	private static final Path OUTPUT_FILE_DIAGRAMS = Paths.get("docs", "user-guide-diagrams.md");

	private static final String PLACEHOLDER = "$TEXT$";
	private static final String TEMPLATE_BUTTON = "<button class=\"collapsible\">$TEXT$</button>";
	private static final String TEMPLATE_CATEGORY_BUTTON = "<button class=\"collapsible category\">$TEXT$</button>";
	private static final String TEMPLATE_DIV_OPEN = "<div class=\"content\">";
	private static final String TEMPLATE_TEXT = "<p>$TEXT$</p>";
	private static final String TEMPLATE_IMAGE = "<img src=\"../tipdata/tip_images/$TEXT$\">";
	private static final String TEMPLATE_DIV_CLOSE = "</div>";
	
	private static final Map<String, List<String>> TOPICS = new HashMap<>();
	private static final Map<String, List<String>> LEVELS = new HashMap<>();
	private static final Map<String, List<String>> DIAGRAMS = new HashMap<>();
	
	/*
	 * The categories in the topic view in the user guide.
	 */
	enum Topic
	{
		CREATING, MODIFYING, SELECTING, COPYING, SEMANTICS, SETTINGS
	}
	
	/*
	 * The categories in the level view in the user guide.
	 */
	enum Level
	{
		BEGINNER, INTERMEDIATE, ADVANCED
	}
	
	/*
	 * The categories in the diagram view in the user guide.
	 */
	enum Diagram
	{
		CLASS, SEQUENCE, OBJECT, STATE, GENERAL
	}
	
	private UserGuideGenerator() {}
	
	/**
	 * Run without arguments.
	 * 
	 * @param pArgs Not used.
	 */
	public static void main(String[] pArgs) throws IOException
	{
		tipsAsHtml();
		generateTopicsAsMd();
		generateLevelsAsMd();
		generateDiagramsAsMd();
		System.out.println("The User Guide was generated sucessfully.");
	}
	
	/*
	 * Generates the user guide organized by topics.
	 */
	private static void generateTopicsAsMd() throws IOException
	{
		String template = lines(INPUT_FILE_TOPICS, UTF_8).collect(joining("\n"));
		List<String> tipCategories = new ArrayList<>();
		for( Topic topic : Topic.values() )
		{
			tipCategories.add(toHtml(topic.toString()));
		}
		template = template.replace(PLACEHOLDER, tipCategories.stream().collect(joining("\n")));
		
		for( Topic topic : Topic.values() )
		{
			template = template.replace("$" + topic.toString() + "$", 
					TOPICS.get(topic.toString().toLowerCase()).stream().collect(joining("\n")));
		}
		write(OUTPUT_FILE_TOPICS, template.getBytes(UTF_8));
	}
	
	/*
	 * Generates the user guide organized by levels.
	 */
	private static void generateLevelsAsMd() throws IOException
	{		
		String template = lines(INPUT_FILE_LEVELS, UTF_8).collect(joining("\n"));
		List<String> tipCategories = new ArrayList<>();
		for( Level level : Level.values() )
		{
			tipCategories.add(toHtml(level.toString()));
		}
		template = template.replace(PLACEHOLDER, tipCategories.stream().collect(joining("\n")));
		
		for( Level level : Level.values() )
		{
			template = template.replace("$" + level.toString() + "$", 
					LEVELS.get(level.toString().toLowerCase()).stream().collect(joining("\n")));
		}
		write(OUTPUT_FILE_LEVELS, template.getBytes(UTF_8));
	}
	
	/*
	 * Generates the user guide organized by diagram type.
	 */
	private static void generateDiagramsAsMd() throws IOException
	{
		String template = lines(INPUT_FILE_DIAGRAMS, UTF_8).collect(joining("\n"));
		List<String> tipCategories = new ArrayList<>();
		for( Diagram diagram : Diagram.values() )
		{
			tipCategories.add(toHtml(diagram.toString()));
		}
		template = template.replace(PLACEHOLDER, tipCategories.stream().collect(joining("\n")));
		
		for( Diagram diagram : Diagram.values() )
		{
			template = template.replace("$" + diagram.toString() + "$", 
					DIAGRAMS.get(diagram.toString().toLowerCase()).stream().collect(joining("\n")));
		}
		write(OUTPUT_FILE_DIAGRAMS, template.getBytes(UTF_8));
	}
	
	/*
	 * Creates an html representation of all available tips, 
	 * grouped by Views, and their respective categories in each View.
	 */
	private static void tipsAsHtml()
	{
		for( int tipNumber = 1; tipNumber <= numberOfTips(); tipNumber++ )
		{
			Tip tip = loadTip(tipNumber);
			for( TipCategory category : tip.getCategories() )
			{
				if( category.getView() == View.TOPIC )
				{
					TOPICS.putIfAbsent(category.getCategory(), new ArrayList<>());
					List<String> html = TOPICS.get(category.getCategory());
					html.add(toHtml(tip));
				}
				else if( category.getView() == View.LEVEL )
				{
					LEVELS.putIfAbsent(category.getCategory(), new ArrayList<>());
					List<String> html = LEVELS.get(category.getCategory());
					html.add(toHtml(tip));
				}
				else
				{
					DIAGRAMS.putIfAbsent(category.getCategory(), new ArrayList<>());
					List<String> html = DIAGRAMS.get(category.getCategory());
					html.add(toHtml(tip));
				}
			}
		}
	}
	
	/* 
	 * Obtains the number of tips for the application's properties.
	 */
	private static int numberOfTips()
	{
		return Integer.parseInt(RESOURCES.getString("tips.quantity"));
	}
	
	/*
	 * Creates an html representation of pCategory suitable for display in the user guide.
	 */
	private static String toHtml(String pCategory)
	{
		StringJoiner html = new StringJoiner("\n");
		html.add(TEMPLATE_CATEGORY_BUTTON.replace(PLACEHOLDER, 
				pCategory.substring(0, 1).toUpperCase() + pCategory.substring(1).toLowerCase()));
		html.add(TEMPLATE_DIV_OPEN);
		html.add("$" + pCategory + "$");
		html.add(TEMPLATE_DIV_CLOSE);
		
		return html.toString();
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
