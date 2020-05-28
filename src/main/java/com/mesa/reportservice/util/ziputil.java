package com.mesa.reportservice.util;


import net.jpountz.lz4.*;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by wk1 on 2020/5/15.
 */
public class ziputil {


    public static byte[] commonGzipCompress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(baos);
        gcos.write(data);
        gcos.close();
        return baos.toByteArray();
    }

    public static byte[] commonGzipDecompress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        GzipCompressorInputStream gcis = new GzipCompressorInputStream(new ByteArrayInputStream(data));
        int count;
        byte[] buffer = new byte[8192];
        while ((count = gcis.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }
        gcis.close();
        return baos.toByteArray();
    }



    public static byte[] snappyCompress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }

    public static byte[] snappyDecompress(byte[] data) throws IOException {
        return Snappy.uncompress(data);
    }


    public static byte[] commonXzCompress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        XZCompressorOutputStream pcos = new XZCompressorOutputStream(baos);
        pcos.write(data);
        pcos.close();
        return baos.toByteArray();
    }

    public static byte[] commonXzDecompress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        XZCompressorInputStream xzis = new XZCompressorInputStream(new ByteArrayInputStream(data));
        int count;
        byte[] buffer = new byte[8192];
        while ((count = xzis.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }
        xzis.close();
        return baos.toByteArray();
    }


    public static byte[] lz4Compress(byte[] data) throws IOException {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        LZ4Compressor compressor = factory.fastCompressor();
        LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(byteOutput, 8192, compressor);
        compressedOutput.write(data);
        compressedOutput.close();
        return byteOutput.toByteArray();
    }

    public static byte[] lz4Decompress(byte[] data) throws IOException {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        LZ4FastDecompressor decompresser = factory.fastDecompressor();
        LZ4BlockInputStream lzis = new LZ4BlockInputStream(new ByteArrayInputStream(data), decompresser);
        int count;
        byte[] buffer = new byte[8192];
        while ((count = lzis.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }
        lzis.close();
        return baos.toByteArray();
    }
}
