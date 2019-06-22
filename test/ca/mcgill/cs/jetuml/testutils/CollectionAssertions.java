package ca.mcgill.cs.jetuml.testutils;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A collection of utility methods for testing assertions on 
 * collections.
 */
public enum CollectionAssertions
{	
	isEmpty( CollectionAssertions::isEmpty ),
	hasSize( CollectionAssertions::hasSize ),
	hasNoNullElements( CollectionAssertions::hasNoNullElements),
	contains( CollectionAssertions::contains ),
	doesNotContain( CollectionAssertions::doesNotContain ),
	hasElementsEqualTo( CollectionAssertions::hasElementsEqualTo ),
	hasElementsSameAs( CollectionAssertions::hasElementsSameAs );
	
	private final BiConsumer<Collection<?>, Object[]> aAssertionMethod;
	
	private CollectionAssertions(BiConsumer<Collection<?>, Object[]> pAssertionMethod)
	{
		aAssertionMethod = pAssertionMethod;
	}
	
	public static void assertThat(Collection<?> pCollection, CollectionAssertions 
			pAssertion, Object... pArgument) 
	{
		pAssertion.aAssertionMethod.accept(pCollection, pArgument);
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
	
	public static <T,U> Collection<U> extract(Collection<T> pCollection, Function<T,U> pExtractor)
	{
		return pCollection.stream().map(pExtractor).collect(Collectors.toList());
	}
	
	private static boolean isInteger(Object pObject)
	{
		return pObject instanceof Byte || pObject instanceof Short ||
				pObject instanceof Integer || pObject instanceof Long;
	}
}
