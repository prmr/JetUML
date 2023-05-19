/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package org.jetuml.persistence;

import static org.jetuml.persistence.PersistenceService.read;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetuml.persistence.DeserializationException.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/*
 * This class tests that the different types of errors are correctly detected when 
 * attempting to read a diagram file.
 */
public class TestPersistenceServiceReadError
{
	private static final Path PATH_TEST_FILES = Path.of("testdata");
	private static final Path PATH_TEMPORARY_FILE = PATH_TEST_FILES.resolve("tmp");
	
	// Serialized diagrams with malformed JSON
	private static final String SYNTACTIC_1 = "{";
	private static final String SYNTACTIC_2 = "{\"diagram\":}";
	private static final String SYNTACTIC_3 = "{\"diagram\":\"ClassDiagram\",}";
	
	// Serialized diagrams with malformed diagram structure
	private static final String STRUCTURAL_1 = "{}";
	private static final String STRUCTURAL_2 = "{\"diagram\":\"ClassDiagram\",\"edges\":[],\"version\":\"3.5\"}";
	private static final String STRUCTURAL_3 = "{\"diagram\":\"ClassDiagram\",\"nodes\":\"edges\",\"edges\":[],\"version\":\"3.5\"}";
	
	@AfterEach
	void tearDown()
	{
		try
		{
			Files.delete(PATH_TEMPORARY_FILE);
		}
		catch(IOException exception)
		{
			fail("Manually delete test file: " + PATH_TEMPORARY_FILE.toString());
		}
	}
	
	@Test 
	void testRead_EmptyString()
	{
		assertThrowsWithCategory(Category.SYNTACTIC, () -> read(createFile("")));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {SYNTACTIC_1, SYNTACTIC_2, SYNTACTIC_3})
	void testRead_SyntacticErrors(String pInput) throws Exception
	{
		assertThrowsWithCategory(Category.SYNTACTIC, () -> PersistenceService.read(createFile(pInput)));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {STRUCTURAL_1, STRUCTURAL_2, STRUCTURAL_3})
	void testRead_StructuralErrors(String pInput) throws Exception
	{
		assertThrowsWithCategory(Category.STRUCTURAL, () -> PersistenceService.read(createFile(pInput)));
	}
	
	private static void assertThrowsWithCategory(Category pCategory, Executable pExecutable)
	{
		try
		{
			pExecutable.execute();
		}
		catch( Throwable throwable )
		{
			assertSame(DeserializationException.class, throwable.getClass());
			assertSame(pCategory, ((DeserializationException)throwable).category());
		}
	}
	
	private static final File createFile(String pString)
	{
		try 
		{
			Files.writeString(PATH_TEMPORARY_FILE, pString, StandardCharsets.UTF_8);
			File tmp = PATH_TEMPORARY_FILE.toFile();
			return tmp;
		}
		catch(IOException exception)
		{
			fail();
			return null;
		}
	}
}
