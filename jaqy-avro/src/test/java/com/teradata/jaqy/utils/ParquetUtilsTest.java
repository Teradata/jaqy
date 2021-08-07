/*
 * Copyright (c) 2021 Teradata
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
package com.teradata.jaqy.utils;

import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.JaqyException;

/**
 * @author  Heng Yuan
 */
public class ParquetUtilsTest
{
    @Test
    public void getByteSizeTest ()
    {
        Assert.assertEquals (1L *1024 * 1024, ParquetUtils.getByteSize ("1MB"));
        Assert.assertEquals (1234L, ParquetUtils.getByteSize ("1234"));
        Assert.assertEquals (1L *1024 * 1024 * 1024, ParquetUtils.getByteSize ("1GB"));
        Assert.assertEquals (-1L, ParquetUtils.getByteSize ("1234a"));
    }

    @Test
    public void getCompressionCodecTest ()
    {
        Assert.assertEquals (CompressionCodecName.GZIP, ParquetUtils.getCompressionCodec ("gz"));
        Assert.assertEquals (CompressionCodecName.GZIP, ParquetUtils.getCompressionCodec ("gzip"));
        Assert.assertEquals (CompressionCodecName.SNAPPY, ParquetUtils.getCompressionCodec ("snappy"));
        Assert.assertEquals (CompressionCodecName.LZO, ParquetUtils.getCompressionCodec ("lzo"));
        Assert.assertEquals (CompressionCodecName.BROTLI, ParquetUtils.getCompressionCodec ("brotli"));
        Assert.assertEquals (CompressionCodecName.LZ4, ParquetUtils.getCompressionCodec ("lz4"));
        Assert.assertEquals (CompressionCodecName.ZSTD, ParquetUtils.getCompressionCodec ("zstd"));
    }

    @Test (expected = JaqyException.class)
    public void getCompressionCodecTest2 ()
    {
        ParquetUtils.getCompressionCodec ("asdf");
    }

    @Test
    public void getCompressionCodecFromExtensionTest ()
    {
        Assert.assertEquals (CompressionCodecName.GZIP, ParquetUtils.getCompressionCodecFromExtension ("abc.pq.gz"));
        Assert.assertEquals (CompressionCodecName.SNAPPY, ParquetUtils.getCompressionCodecFromExtension ("abc.pq.snappy"));
        Assert.assertEquals (CompressionCodecName.LZO, ParquetUtils.getCompressionCodecFromExtension ("abc.pq.lzo"));
        Assert.assertEquals (CompressionCodecName.BROTLI, ParquetUtils.getCompressionCodecFromExtension ("abc.pq.br"));
        Assert.assertEquals (CompressionCodecName.LZ4, ParquetUtils.getCompressionCodecFromExtension ("abc.pq.lz4"));
        Assert.assertEquals (CompressionCodecName.ZSTD, ParquetUtils.getCompressionCodecFromExtension ("abc.pq.zstd"));
        Assert.assertEquals (CompressionCodecName.UNCOMPRESSED, ParquetUtils.getCompressionCodecFromExtension ("abc.pq.asdf"));
    }
}
