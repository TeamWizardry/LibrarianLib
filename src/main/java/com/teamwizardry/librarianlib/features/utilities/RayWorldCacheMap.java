package com.teamwizardry.librarianlib.features.utilities;

import java.util.Arrays;

public class RayWorldCacheMap
{
	private static final int FREE_KEY = 0;

	/** Keys */
	private int[] m_keys;
	/** Values */
	private Object[] m_values;

	/** Value of 'free' key */
	private Object m_freeValue;

	/** Fill factor, must be between (0 and 1) */
	private final float m_fillFactor;
	/** We will resize a map once it reaches this size */
	private int m_threshold;
	/** Current map size */
	private int m_size;
	/** Mask to calculate the original position */
	private int m_mask;

	public RayWorldCacheMap(final int size, final float fillFactor )
	{
		if ( fillFactor <= 0 || fillFactor >= 1 )
			throw new IllegalArgumentException( "FillFactor must be in (0, 1)" );
		if ( size <= 0 )
			throw new IllegalArgumentException( "Size must be positive!" );
		final int capacity = arraySize( size, fillFactor );
		m_mask = capacity - 1;
		m_fillFactor = fillFactor;

		m_keys = new int[capacity];
		m_values = new Object[capacity];
		m_threshold = (int) (capacity * fillFactor);
	}

	public void clear()
	{
		Arrays.fill(m_keys, 0);
		Arrays.fill(m_values, null);
		m_size = 0;
		m_freeValue = null;
	}

	public Object get( final int key )
	{
		if ( key == FREE_KEY)
			return m_freeValue;

		int idx = phiMix( key ) & m_mask;
		int k = m_keys[ idx ];
		if ( k == key ) //we check FREE prior to this call
			return m_values[ idx ];
		if ( k == FREE_KEY ) //end of chain already
			return null;
		final int startIdx = idx;
		while (( idx = ( idx + 1 ) & m_mask ) != startIdx )
		{
			k = m_keys[ idx ];
			if ( k == FREE_KEY )
				return null;
			if ( k == key )
				return m_values[ idx ];
		}
		return null;
	}

	public Object put( final int key, final Object value )
	{
		if ( key == FREE_KEY )
		{
			final Object ret = m_freeValue;
			if ( m_freeValue == null )
				++m_size;
			m_freeValue = value;
			return ret;
		}

		int ptr = phiMix( key ) & m_mask;
		int k = m_keys[ptr];
		if ( k == FREE_KEY ) //end of chain already
		{
			m_keys[ ptr ] = key;
			m_values[ ptr ] = value;
			if ( m_size >= m_threshold )
				rehash( m_keys.length * 2 ); //size is set inside
			else
				++m_size;
			return null;
		}
		else if ( k == key ) //we check FREE prior to this call
		{
			final Object ret = m_values[ ptr ];
			m_values[ ptr ] = value;
			return ret;
		}

		while ( true )
		{
			ptr = ( ptr + 1 ) & m_mask; //that's next index calculation
			k = m_keys[ ptr ];
			if ( k == FREE_KEY )
			{
				m_keys[ ptr ] = key;
				m_values[ ptr ] = value;
				if ( m_size >= m_threshold )
					rehash( m_keys.length * 2 ); //size is set inside
				else
					++m_size;
				return null;
			}
			else if ( k == key )
			{
				final Object ret = m_values[ ptr ];
				m_values[ ptr ] = value;
				return ret;
			}
		}
	}

	public int size()
	{
		return m_size;
	}

	private void rehash( final int newCapacity )
	{
		m_threshold = (int) (newCapacity * m_fillFactor);
		m_mask = newCapacity - 1;

		final int oldCapacity = m_keys.length;
		final int[] oldKeys = m_keys;
		final Object[] oldValues = m_values;

		m_keys = new int[ newCapacity ];
		m_values = new Object[ newCapacity ];
		m_size = m_freeValue != null ? 1 : 0;

		for ( int i = oldCapacity; i-- > 0; ) {
			if( oldKeys[ i ] != FREE_KEY  )
				put( oldKeys[ i ], oldValues[ i ] );
		}
	}

	/** Taken from FastUtil implementation */

	/** Return the least power of two greater than or equal to the specified value.
	 *
	 * <p>Note that this function will return 1 when the argument is 0.
	 *
	 * @param x a long integer smaller than or equal to 2<sup>62</sup>.
	 * @return the least power of two greater than or equal to the specified value.
	 */
	private static long nextPowerOfTwo( long x ) {
		if ( x == 0 ) return 1;
		x--;
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return ( x | x >> 32 ) + 1;
	}

	/** Returns the least power of two smaller than or equal to 2<sup>30</sup> and larger than or equal to <code>Math.ceil( expected / f )</code>.
	 *
	 * @param expected the expected number of elements in a hash table.
	 * @param f the load factor.
	 * @return the minimum possible size for a backing array.
	 * @throws IllegalArgumentException if the necessary size is larger than 2<sup>30</sup>.
	 */
	private static int arraySize( final int expected, final float f ) {
		final long s = Math.max( 2, nextPowerOfTwo( (long)Math.ceil( expected / f ) ) );
		if ( s > (1 << 30) ) throw new IllegalArgumentException( "Too large (" + expected + " expected elements with load factor " + f + ")" );
		return (int)s;
	}

	//taken from FastUtil
	private static final int INT_PHI = 0x9E3779B9;

	private static int phiMix( final int x ) {
		final int h = x * INT_PHI;
		return h ^ (h >> 16);
	}
}