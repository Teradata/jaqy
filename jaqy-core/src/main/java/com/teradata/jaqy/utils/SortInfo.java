package com.teradata.jaqy.utils;

public class SortInfo
{
	/**
	 * The column id.  If the id is negative, it is not set.
	 */
	public int column;
	/**
	 * The column name.  If it is null, it is not set.
	 */
	public String name;
	/**
	 * Ascending sort?
	 */
	public boolean asc;
	/**
	 * Is null sorted low?  If not, it is sorted as high.
	 */
	public boolean nullLow;
}
