/*
 * Copyright (c) 2017-2021 Teradata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teradata.jaqy.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.Variable;

/**
 * @author	Heng Yuan
 */
public class S3Utils
{
	public final static String S3BUILDER_VAR = "s3builder";
	public final static String S3CLIENT_VAR = "s3client";
	public final static String S3ACCESS_VAR = "s3access";
	public final static String S3SECRET_VAR = "s3secret";

	public static void setAccess (String access, JaqyInterpreter interpreter)
	{
		interpreter.setVariableValue (S3ACCESS_VAR, access);

		// clear the current s3 client
		interpreter.setVariableValue (S3CLIENT_VAR, null);
	}

	public static void setSecret (String secret, JaqyInterpreter interpreter)
	{
		interpreter.setVariableValue (S3SECRET_VAR, secret);

		// clear the current s3 client
		interpreter.setVariableValue (S3CLIENT_VAR, null);
	}

	public static AmazonS3ClientBuilder getS3Builder (JaqyInterpreter interpreter)
	{
		Object o = interpreter.getVariableValue (S3BUILDER_VAR);
		if (o instanceof AmazonS3ClientBuilder)
			return (AmazonS3ClientBuilder)o;
		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard ();
		builder.withPathStyleAccessEnabled (true);
		interpreter.setVariableValue (S3BUILDER_VAR, builder);
		return builder;
	}

	public static AmazonS3 getS3Client (JaqyInterpreter interpreter)
	{
		{
			Object o = interpreter.getVariableValue (S3CLIENT_VAR);
			if (o instanceof AmazonS3)
				return (AmazonS3)o;
		}

		// now we need to setup a new client.
		AmazonS3ClientBuilder builder = getS3Builder (interpreter);

		// check if we need to set up the access / secret key
		String access = null;
		String secret = null;
		{
			Object o = interpreter.getVariableValue (S3ACCESS_VAR);
			if (o != null)
				access = o.toString ();
		}
		{
			Object o = interpreter.getVariableValue (S3SECRET_VAR);
			if (o != null)
				secret = o.toString ();
		}
		if (access != null &&
			secret != null)
		{
			/*
			 * When both access and secret are null, we are using the default
			 * values (i.e. from credential file or env variables etc).
			 *
			 * When both are set, then we override the default settings (and
			 * subsequent uses).
			 */
			if (access.length () == 0 &&
				secret.length () == 0)
			{
				/*
				 * This is for accessing publicly accessible buckets.
				 */
				builder.withCredentials (new AWSStaticCredentialsProvider (new AnonymousAWSCredentials()));
			}
			else
			{
				builder.withCredentials (new AWSStaticCredentialsProvider (new BasicAWSCredentials (access, secret)));
			}
		}

		AmazonS3 client = builder.build ();

		// now save the client to s3client variable
		Variable clientVar = interpreter.getVariable (S3CLIENT_VAR);
		if (clientVar == null)
		{
			clientVar = new Variable ()
			{
				private AmazonS3 m_client;

				@Override
				public Object get ()
				{
					return m_client;
				}

				@Override
				public boolean set (Object value)
				{
					if (value != null && !(value instanceof AmazonS3))
						return false;
					m_client = (AmazonS3) value;
					return true;
				}

				@Override
				public String getName ()
				{
					return S3CLIENT_VAR;
				}
			};
		}
		clientVar.set (client);
		interpreter.registerVariable (clientVar);
		return client;
	}
}
