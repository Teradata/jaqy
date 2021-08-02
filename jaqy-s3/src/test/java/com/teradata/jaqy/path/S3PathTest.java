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
package com.teradata.jaqy.path;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.s3.S3Utils;
import com.teradata.jaqy.utils.FileUtils;

import akka.actor.ActorSystem;
import akka.actor.CoordinatedShutdown;
import io.findify.s3mock.S3Mock;
import io.findify.s3mock.provider.InMemoryProvider;

/**
 * @author	Heng Yuan
 */
public class S3PathTest
{
	@Test
	public void test1 () throws Exception
	{
		/*
		 * A workaround for S3Mock generating an output to stdout.
		 *     https://github.com/findify/s3mock/issues/67
		 * It generates an output in Eclipse, but not so when running
		 * mvn clean test.
		 */
		InMemoryProvider provider = new InMemoryProvider ();
		ActorSystem actor = S3Mock.$lessinit$greater$default$3(8001, provider);
		S3Mock api = new S3Mock (8001, provider, actor);
		api.start ();

		// setup
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		AmazonS3ClientBuilder builder = S3Utils.getS3Builder (interpreter);
		builder.setEndpointConfiguration (new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", "us-west-2"));

		// setup some files in the bucket
		AmazonS3 s3client = builder.build ();
		s3client.createBucket("tests");
		File dir = new File ("../tests/unittests/csv/lib");
		s3client.putObject ("tests", "unittests/csv/lib/sin.csv", new File (dir, "sin.csv"));
		s3client.putObject ("tests", "unittests/csv/lib/sin2.csv", new File (dir, "sin2.csv"));
		s3client.shutdown ();

		S3PathHandler handler = new S3PathHandler ();

		String url = "s3://tests/unittests/csv/lib/sin.csv";
		String parent = "s3://tests/unittests/csv/lib";

		S3Path path = (S3Path)handler.getPath (url, interpreter);
		Assert.assertNotNull (path);
		Assert.assertEquals ("tests", path.getBucket ());
		Assert.assertEquals ("unittests/csv/lib/sin.csv", path.getFile ());
		Assert.assertEquals (url, path.getPath ());
		Assert.assertEquals (url, path.getCanonicalPath ());
		Assert.assertTrue (path.exists ());
		Assert.assertEquals (31443, path.length ());
		Assert.assertEquals (31443, path.length ());
		Assert.assertTrue (path.isFile ());
		Assert.assertEquals (0, FileUtils.compare (path.getInputStream (), new FileInputStream (new File (dir, "sin.csv"))));

		path = (S3Path)path.getParent ();
		Assert.assertEquals (parent, path.getPath ());
		path = (S3Path)path.getRelativePath ("sin2.csv");
		Assert.assertEquals ("s3://tests/unittests/csv/lib/sin2.csv", path.getPath ());
		Assert.assertTrue (path.isFile ());

		path = (S3Path)path.getParent ();
		path = (S3Path)path.getRelativePath ("/unittests/csv/lib/import1.csv");
		Assert.assertEquals ("s3://tests/unittests/csv/lib/import1.csv", path.getPath ());
		FileUtils.copy (path.getOutputStream (), new FileInputStream (new File (dir, "import1.csv")), new byte[4096]);
		Assert.assertEquals (25, path.length ());
		Assert.assertEquals (25, path.length ());
		Assert.assertEquals (0, FileUtils.compare (path.getInputStream (), new FileInputStream (new File (dir, "import1.csv"))));

		path = (S3Path)path.getParent ();
		path = (S3Path)path.getRelativePath ("../../csv/lib/sin.csv");
		Assert.assertEquals (url, path.getPath ());

		path = (S3Path)path.getParent ();
		path = (S3Path)path.getRelativePath ("../test/abc.csv");
		Assert.assertEquals (0, path.length ());
		Assert.assertEquals (0, path.length ());
		Assert.assertFalse (path.exists ());
		Assert.assertFalse (path.exists ());

		s3client = S3Utils.getS3Client (interpreter);
		if (s3client != null)
			s3client.shutdown ();
		CoordinatedShutdown.get(actor).runAll ();
		api.stop ();
	}
}
