/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.gui.tips;

import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

import org.jetuml.gui.tips.TipLoader.Tip;

/**
 * Script to generate the user guide by collecting all the tips of the day in a
 * single page.
 */
public final class UserGuideGenerator
{
	private static final int NUMBER_OF_TIPS = Integer.parseInt(RESOURCES.getString("tips.quantity"));
	private static final Path PATH_OUTPUT = Path.of("docs/tips.md");
	private static final String TEMPLATE_TITLE = "\n### %d. %s";
	private static final String TEMPLATE_IMAGE = "\n![Image](../tipdata/tip_images/%s)";
	private static final String HEADER = """
			## Tips for JetUML Users
			
			This section lists all the "Tip of the Day" entries available through JetUML's help menu.
			""";
	
	private UserGuideGenerator() {}
	
	/**
	 * Use without arguments.
	 * 
	 * @param pArgs Not used.
	 */
	public static void main(String[] pArgs) throws IOException
	{
		StringBuilder page = new StringBuilder(HEADER);
		
		for (int tipNumber = 1; tipNumber <= NUMBER_OF_TIPS; tipNumber++)
		{
			page.append(toMarkdown(TipLoader.loadTip(tipNumber))).append("\n");
		}
		Files.write(PATH_OUTPUT, page.toString().getBytes(StandardCharsets.UTF_8));
		System.out.println("User guide generated with %d tips.".formatted(NUMBER_OF_TIPS));
	}
	
	/*
	 * Creates a markdown representation of pTip suitable for display in the user
	 * guide.
	 */
	private static String toMarkdown(Tip pTip)
	{
		StringJoiner markdown = new StringJoiner("\n");
		markdown.add(TEMPLATE_TITLE.formatted(pTip.getId(), pTip.getTitle()));
		for (TipElement element : pTip.getElements())
		{
			markdown.add(toMarkdown(element));
		}

		return markdown.toString();
	}
	
	/*
	 * Creates a markdown representation of pTipElement suitable for display in the
	 * user guide.
	 */
	private static String toMarkdown(TipElement pTipElement)
	{
		if (pTipElement.getMedia() == Media.TEXT)
		{
			return "\n" + pTipElement.getContent().replace(" | ", " > ");
		}
		else
		{
			return TEMPLATE_IMAGE.formatted(pTipElement.getContent());
		}
	}
}
