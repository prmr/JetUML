package ca.mcgill.cs.jetuml.testutils;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * A collection of utility methods for testing assertions on 
 * collections.
 */
public enum CollectionAssertions
{	
	hasSize( CollectionAssertions::hasSize ),
	hasNoNullElements( CollectionAssertions::hasNoNullElements);
	
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
	
	private static void hasSize(Collection<?> pCollection, Object[] pArguments)
	{
		if( pArguments.length ==0 || pArguments[0] == null || !isInteger(pArguments[0]) || 
				((Number)pArguments[0]).longValue() < 0 )
		{
			throw new IllegalArgumentException("Expected a positive integer type, got: " + 
					pArguments[0]);
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
			throw new IllegalArgumentException("Unexpected argument");
		}
		for( Object object : pCollection )
		{
			if( object == null )
			{
				throw new AssertionError(String.format("Null element in collection but none expected"));
			}
		}
	}
	
	private static boolean isInteger(Object pObject)
	{
		return pObject instanceof Byte || pObject instanceof Short ||
				pObject instanceof Integer || pObject instanceof Long;
	}
}
