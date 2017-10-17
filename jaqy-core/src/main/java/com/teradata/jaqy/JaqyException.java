package com.teradata.jaqy;

public class JaqyException extends RuntimeException
{
	private static final long serialVersionUID = 5138632292108189946L;

	public JaqyException (String msg)
	{
		super (msg);
	}
}
