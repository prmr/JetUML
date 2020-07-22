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
package ca.mcgill.cs.jetuml.testutils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A collection of utility methods for testing assertions on 
 * collections.
 */
public enum CollectionAssertions
{	
	
	/** True is the collection is empty. Does not require any argument. */
	isEmpty( CollectionAssertions::isEmpty ),
	
	/** True if the collection has the size expressed in the single argument. */
	hasSize( CollectionAssertions::hasSize ),
	
	/** True if there are no null elements in the collection. */
	hasNoNullElements( CollectionAssertions::hasNoNullElements),
	
	/** True if the collection contains the single argument. */
	contains( CollectionAssertions::contains ),
	
	/** True if the collection does not contain the single argument. */
	doesNotContain( CollectionAssertions::doesNotContain ),
	
	/** True if the elements in the collection are equal to the elements provided 
	 * as arguments, in the same sequence */
	hasElementsEqualTo( CollectionAssertions::hasElementsEqualTo ),
	
	/** True if the elements in the collection are the same as the elements provided 
	 * as arguments, in the same sequence */
	hasElementsSameAs( CollectionAssertions::hasElementsSameAs ),
	
	/**
	 * True if the set of elements in the collection is exactly the same as the 
	 * set of elements provided as arguments (i.e., irrespective of order), using
	 * object equality to test membership.
	 */
	hasSetOfElementsEqualsTo( CollectionAssertions::hasSetOfElementsEqualTo );
	
	private final BiConsumer<Collection<?>, Object[]> aAssertionMethod;
	
	private CollectionAssertions(BiConsumer<Collection<?>, Object[]> pAssertionMethod)
	{
		aAssertionMethod = pAssertionMethod;
	}
	
	/**
	 * Checks that pAssertion holds for pArguments. If it does, nothing happends
	 * If the assertion does not hold, throws an AssertionError.
	 * 
	 * @param pCollection The collection to test.
	 * @param pAssertion A descriptor for the assertion
	 * @param pArguments The argument needed to check the assertion.
	 */
	public static void assertThat(Collection<?> pCollection, CollectionAssertions 
			pAssertion, Object... pArguments) 
	{
		pAssertion.aAssertionMethod.accept(pCollection, pArguments);
	}
	
	private static void isEmpty(Collection<?> pCollection, Object[] pArguments)
	{
		if( pArguments.length != 0 )
		{
			throw new IllegalArgumentException("Invalid arguments");
		}
		if( !pCollection.isEmpty() )
		{
			throw new AssertionError(String.format("Collection not empty as expected"));
		}
	}
	
	private static void hasSize(Collection<?> pCollection, Object[] pArguments)
	{
		if( pArguments.length != 1 || pArguments[0] == null || !isInteger(pArguments[0]) || 
				((Number)pArguments[0]).longValue() < 0 )
		{
			throw new IllegalArgumentException("Invalid arguments");
		}
		if( pCollection.size() != ((Number)pArguments[0]).longValue() )
		{
			throw new AssertionError(String.format("Collection has size <%d> but expected <%d>", pCollection.size(), ((Number)pArguments[0]).longValue()));
		}
	}
	
	private static void hasNoNullElements(Collection<?> pCollection, Object[] pArguments)
	{
		if( pArguments.length != 0 )
		{
			throw new IllegalArgumentException("Invalid arguments");
		}
		for( Object object : pCollection )
		{
			if( object == null )
			{
				throw new AssertionError(String.format("Null element in collection but none expected"));
			}
		}
	}
	
	private static void doesNotContain(Collection<?> pCollection, Object[] pArguments)
	{
		if( pArguments.length != 1 || pArguments[0] == null )
		{
			throw new IllegalArgumentException("Invalid arguments");
		}
		if( pCollection.contains(pArguments[0]) )
		{
			throw new AssertionError(String.format("Collection contains unexpected object: " + pArguments[0]));
		}
	}
	
	private static void contains(Collection<?> pCollection, Object[] pArguments)
	{
		if( pArguments.length != 1 || pArguments[0] == null )
		{
			throw new IllegalArgumentException("Invalid arguments");
		}
		if( !pCollection.contains(pArguments[0]) )
		{
			throw new AssertionError("Collection does not contain expected object: " + pArguments[0]);
		}
	}
	
	private static void hasElementsEqualTo(Collection<?> pCollection, Object[] pArguments)
	{
		if( pCollection.size() != pArguments.length )
		{
			throw new AssertionError(String.format("Collection expected to have <%d> elements but had <%d>",
					pArguments.length, pCollection.size()));
		}
		Object[] elements = pCollection.toArray();
		for( int i = 0; i < elements.length; i++ )
		{
			if( !safeEquals(elements[i], pArguments[i]) )
			{
				throw new AssertionError(String.format("Mismatch at position %d: expected %s but got %s ",
						i, pArguments[i], elements[i]));
			}
		}
	}
	
	private static void hasSetOfElementsEqualTo(Collection<?> pCollection, Object[] pArguments)
	{
		HashSet<Object> target = new HashSet<>(Arrays.asList(pArguments));
		for( Object element : pCollection )
		{
			if( !target.contains(element) )
			{
				throw new AssertionError(String.format("Element not expected: %s", element));
			}
		}
		for( Object element : target )
		{
			if( !pCollection.contains(element))
			{
				throw new AssertionError(String.format("Element missing: %s", element));
			}
		}
	}
	
	private static void hasElementsSameAs(Collection<?> pCollection, Object[] pArguments)
	{
		if( pCollection.size() != pArguments.length )
		{
			throw new AssertionError(String.format("Collection expected to have <%d> elements but had <%d>",
					pArguments.length, pCollection.size()));
		}
		Object[] elements = pCollection.toArray();
		for( int i = 0; i < elements.length; i++ )
		{
			if( elements[i] != pArguments[i] )
			{
				throw new AssertionError(String.format("Mismatch at position %d: not the same object as in target sequence",
						i, pArguments[i], elements[i]));
			}
		}
	}
	
	private static boolean safeEquals(Object pObject1, Object pObject2)
	{
		if( pObject1 == null && pObject2 == null )
		{
			return true;
		}
		if( pObject1 != null && pObject2 == null )
		{
			return false;
		}
		if( pObject1 == null && pObject2 != null )
		{
			return false;
		}
		return pObject1.equals(pObject2);
	}
	
	/**
	 * A convenience method to map the elements in the collection to a value 
	 * and create a new collection from the extracted values.
	 * 
	 * @param <T> The type of the elements in the original collection.
	 * @param <U> The type of the elements to extract.
	 * @param pCollection The input collection/
	 * @param pExtractor A function to extract the desired elements from the collection elements.
	 * @return A collection of elements mapped from the elements in the input collection.
	 */
	public static <T,U> Collection<U> extract(Collection<T> pCollection, Function<T,U> pExtractor)
	{
		assert pCollection != null && pExtractor != null;
		return pCollection.stream().map(pExtractor).collect(Collectors.toList());
	}
	
	private static boolean isInteger(Object pObject)
	{
		return pObject instanceof Byte || pObject instanceof Short ||
				pObject instanceof Integer || pObject instanceof Long;
	}
}
