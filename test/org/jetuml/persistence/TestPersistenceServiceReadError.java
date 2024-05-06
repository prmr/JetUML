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
	
	// "version" property not found
	private static final String STRUCTURAL_1 = "{}";

	// "version" property value not parseable
	private static final String STRUCTURAL_2 = "{\"diagram\":\"ClassDiagram\",\"edges\":[],\"version\":\"3.x\"}";

	// "diagram" property not a string
	private static final String STRUCTURAL_3 = "{\"version\":\"3.5\",\"diagram\":[]}";
	
	// "diagram" property value not recognized
	private static final String STRUCTURAL_4 = "{\"version\":\"3.5\",\"diagram\":\"FooDiagram\"}";

	// "nodes" property is missing
	private static final String STRUCTURAL_5 = "{\"diagram\":\"ClassDiagram\",\"edges\":[],\"version\":\"3.5\"}";
	
	// "nodes" property is not an array
	private static final String STRUCTURAL_6 = "{\"diagram\":\"ClassDiagram\",\"nodes\":true,\"version\":\"3.5\"}";
	
	// "nodes" array element is not a JSON object
	private static final String STRUCTURAL_7 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[true],\"version\":\"3.5\"}";
	
	// Node missing the "type" property
	private static final String STRUCTURAL_8 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[{}],\"version\":\"3.5\"}";
	
	// Node "type" property is not a string
	private static final String STRUCTURAL_9 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[{\"type\":true}],\"version\":\"3.5\"}";
	
	// Node type is not a real type
	private static final String STRUCTURAL_10 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[{\"type\":\"NotARealNodeType\"}],\"version\":\"3.5\"}";
	
	// Node type missing the "x" property
	private static final String STRUCTURAL_11 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[{\"type\":\"ClassNode\"}],\"version\":\"3.5\"}";
	
	// Node "x" property is not an int
	private static final String STRUCTURAL_12 = "{\"diagram\":\"ClassDiagram\", \"version\":\"3.5\", "
			+ "\"nodes\":[{\"type\":\"ClassNode\", \"x\":true}]}";
	
	// Node type missing the "y" property
	private static final String STRUCTURAL_13 = "{\"diagram\":\"ClassDiagram\", \"version\":\"3.5\", "
			+ "\"nodes\":[{\"type\":\"ClassNode\", \"x\":0}]}";
	
	// Node "y" property is not an int
	private static final String STRUCTURAL_14 = "{\"diagram\":\"ClassDiagram\", \"version\":\"3.5\", "
			+ "\"nodes\":[{\"type\":\"ClassNode\", \"x\":0, \"y\":true}]}";
	
	// Node is missing some properties
	private static final String STRUCTURAL_15 = "{\"diagram\":\"ClassDiagram\", \"version\":\"3.5\", "
			+ "\"nodes\":[{\"type\":\"ClassNode\", \"x\":0, \"y\":2}]}";
	
	// Node missing the "id" property
	private static final String STRUCTURAL_16 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"methods\":\"\",\"name\":\"\",\"x\":600,\"y\":230,\"attributes\":\"\",\"type\":\"ClassNode\"}],"
			+ "\"edges\":[],\"version\":\"3.5\"}";
	
	// Node "id" property is not an int
	private static final String STRUCTURAL_17 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"methods\":\"\",\"name\":\"\",\"x\":600,\"y\":230,\"attributes\":\"\",\"id\":true,\"type\":\"ClassNode\"}],"
			+ "\"edges\":[],\"version\":\"3.5\"}";
	
	// Node "children" property is not an array
	private static final String STRUCTURAL_18 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"children\":true,\"name\":\"\",\"x\":760,\"y\":110,\"id\":0,\"type\":\"PackageNode\"}],"
			+ "\"edges\":[],\"version\":\"3.5\"}";
	
	// Value in "children" array is not an int
	private static final String STRUCTURAL_19 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"children\":[true],\"name\":\"\",\"x\":760,\"y\":110,\"id\":0,\"type\":\"PackageNode\"}],"
			+ "\"edges\":[],\"version\":\"3.5\"}";
	
	// Value in "children" array is not an known node
	private static final String STRUCTURAL_20 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"children\":[1],\"name\":\"\",\"x\":760,\"y\":110,\"id\":0,\"type\":\"PackageNode\"}],"
			+ "\"edges\":[],\"version\":\"3.5\"}";
	
	// "edges" property is missing
	private static final String STRUCTURAL_21 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[],"
			+ "\"version\":\"3.5\"}";
	
	// "edges" property is not an array
	private static final String STRUCTURAL_22 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[], "
			+ "\"edges\":true, \"version\":\"3.5\"}";
	
	// Edge value is not an object
	private static final String STRUCTURAL_23 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[], "
			+ "\"edges\":[true], \"version\":\"3.5\"}";
	
	// Edge type is not a string
	private static final String STRUCTURAL_24 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[], "
			+ "\"edges\":[{\"type\":true}], \"version\":\"3.5\"}";
	
	// Edge type is not a real type
	private static final String STRUCTURAL_25 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[], "
			+ "\"edges\":[{\"type\":\"NotARealType\"}], \"version\":\"3.5\"}";

	// Edge missing some properties
	private static final String STRUCTURAL_26 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[], "
			+ "\"edges\":[{\"type\":\"CallEdge\"}], \"version\":\"3.5\"}";
	
	// Edge missing "start" property
	private static final String STRUCTURAL_27 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[],"
			+ "\"edges\":[{\"startLabel\":\"\",\"middleLabel\":\"\",\"end\":0,"
			+ "\"endLabel\":\"\",\"type\":\"AggregationEdge\",\"Aggregation Type\":\"Aggregation\"}],\"version\":\"3.5\"}";
	
	// Edge start property value is not an int
	private static final String STRUCTURAL_28 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[],"
			+ "\"edges\":[{\"startLabel\":\"\",\"middleLabel\":\"\",\"end\":0, \"start\":true,"
			+ "\"endLabel\":\"\",\"type\":\"AggregationEdge\",\"Aggregation Type\":\"Aggregation\"}],\"version\":\"3.5\"}";
	
	// Edge missing "end" property
	private static final String STRUCTURAL_29 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[],"
			+ "\"edges\":[{\"startLabel\":\"\",\"middleLabel\":\"\",\"start\":1,"
			+ "\"endLabel\":\"\",\"type\":\"AggregationEdge\",\"Aggregation Type\":\"Aggregation\"}],\"version\":\"3.5\"}";
	
	// Edge "end" property is not an int
	private static final String STRUCTURAL_30 = "{\"diagram\":\"ClassDiagram\",\"nodes\":[],"
			+ "\"edges\":[{\"startLabel\":\"\",\"middleLabel\":\"\",\"start\":1,\"end\":true,"
			+ "\"endLabel\":\"\",\"type\":\"AggregationEdge\",\"Aggregation Type\":\"Aggregation\"}],\"version\":\"3.5\"}";
	
	// Edge end node not a real node
	private static final String STRUCTURAL_31 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"methods\":\"\",\"name\":\"\",\"x\":960,\"y\":160,\"attributes\":\"\",\"id\":1,\"type\":\"ClassNode\"},"
			+ "{\"methods\":\"\",\"name\":\"\",\"x\":820,\"y\":160,\"attributes\":\"\",\"id\":0,\"type\":\"ClassNode\"}],"
			+ "\"edges\":[{\"middleLabel\":\"\",\"start\":0,\"directionality\":\"Unidirectional\",\"end\":3,\"type\":\"DependencyEdge\"}],"
			+ "\"version\":\"3.5\"}";
	
	// Structural validation problem: Note node child of class node (which does not allow children)
	private static final String STRUCTURAL_32 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"methods\":\"\",\"name\":\"\",\"x\":660,\"y\":110,\"attributes\":\"\",\"id\":0,\"type\":\"ClassNode\",\"children\":[1]},"
			+ "{\"name\":\"\",\"x\":820,\"y\":110,\"id\":1,\"type\":\"NoteNode\"}],"
			+ "\"edges\":[],\"version\":\"3.5\"}";
	
	// Structural validation problem: Note node child of class node (which does allow children)
	private static final String STRUCTURAL_33 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"name\":\"\",\"x\":860,\"y\":170,\"id\":0,\"type\":\"PackageNode\",\"children\":[1]},"
			+ "{\"name\":\"\",\"x\":1000,\"y\":190,\"id\":1,\"type\":\"NoteNode\"}],"
			+ "\"edges\":[],\"version\":\"3.5\"}";
	
	// Structural validation problem: Note node child of class node (which does allow children)
	private static final String SEMANTIC_1 = "{\"diagram\":\"ClassDiagram\","
			+ "\"nodes\":[{\"methods\":\"\",\"name\":\"\",\"x\":670,\"y\":150,\"attributes\":\"\",\"id\":0,\"type\":\"ClassNode\"}],"
			+ "\"edges\":[{\"middleLabel\":\"\",\"start\":0,\"directionality\":\"Unidirectional\",\"end\":0,\"type\":\"DependencyEdge\"}],"
			+ "\"version\":\"3.5\"}";
		
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
	void testRead_SyntacticErrors(String pInput)
	{
		assertThrowsWithCategory(Category.SYNTACTIC, () -> PersistenceService.read(createFile(pInput)));
	}
	
	@Test
	void testTemp()
	{
		assertThrowsWithCategory(Category.STRUCTURAL, () -> PersistenceService.read(createFile(STRUCTURAL_33)));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {STRUCTURAL_1, STRUCTURAL_2, STRUCTURAL_3, STRUCTURAL_4, STRUCTURAL_5,
			STRUCTURAL_6, STRUCTURAL_7, STRUCTURAL_8, STRUCTURAL_9, STRUCTURAL_10, STRUCTURAL_11,
			STRUCTURAL_12, STRUCTURAL_13, STRUCTURAL_14, STRUCTURAL_15, STRUCTURAL_16, STRUCTURAL_17,
			STRUCTURAL_18, STRUCTURAL_19, STRUCTURAL_20, STRUCTURAL_21, STRUCTURAL_22, STRUCTURAL_23,
			STRUCTURAL_24, STRUCTURAL_25, STRUCTURAL_26, STRUCTURAL_27, STRUCTURAL_28, STRUCTURAL_29,
			STRUCTURAL_30, STRUCTURAL_31, STRUCTURAL_32, STRUCTURAL_33})
	void testRead_StructuralErrors(String pInput)
	{
		assertThrowsWithCategory(Category.STRUCTURAL, () -> PersistenceService.read(createFile(pInput)));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {SEMANTIC_1})
	void testRead_SemanticErrors(String pInput)
	{
		assertThrowsWithCategory(Category.SEMANTIC, () -> PersistenceService.read(createFile(pInput)));
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
