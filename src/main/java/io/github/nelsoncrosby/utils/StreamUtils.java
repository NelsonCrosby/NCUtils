/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Nelson Crosby
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.nelsoncrosby.utils;

import java.io.*;

/**
 * Handy utilities for working with streams.
 *
 * Provides a copyStreams method, and a bunch of wrapper methods for easier usage.
 *
 * Notably useful wrapper methods:
 * <ul>
 *     <li>{@link #readWholeFile(java.io.File)}</li>
 *     <li>{@link #writeToFile(byte[], java.io.File)}</li>
 *     <li>{@link #writeToFile(String, java.io.File)}</li>
 * </ul>
 *
 * @author Nelson Crosby
 */
public class StreamUtils {
    /**
     * The default block size in bytes.
     * Given a method <code>streamAction(...)</code> whose parameter list does
     *  not end with <code>int blockSize</code>, it is a wrapper that calls
     *  <code>streamAction(..., DEFAULT_BLOCK_SIZE)</code>.
     */
    public static final int DEFAULT_BLOCK_SIZE = 1024;

    /**
     * The base for all other methods in this class.
     *
     * It copies all remaining bytes until the EOF from <code>from</code> to
     *  <code>to</code>. This is done in buffers of <code>blockSize</code>
     *  bytes.
     *
     * This method closes both files at the end.
     *
     * @param from The source stream
     * @param to The destination stream
     * @param blockSize The size of the buffer to be used
     * @throws java.io.IOException See {@link java.io.InputStream#read(byte[])} and
     *  {@link java.io.OutputStream#write(byte[], int, int)}.
     */
    public static void copyStreams(InputStream from, OutputStream to, int blockSize) throws IOException {
        byte[] buffer = new byte[blockSize];
        int bytesRead;
        while ((bytesRead = from.read(buffer)) != -1) {
            to.write(buffer, 0, bytesRead);
        }
        from.close();
        to.close();
    }

    /**
     * Wrapper for {@link #copyStreams} using {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param from The source stream
     * @param to The destination stream
     * @throws java.io.IOException
     * @see #copyStreams
     */
    public static void copyStreams(InputStream from, OutputStream to) throws IOException {
        copyStreams(from, to, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Reads all remaining bytes until EOF from <code>in</code> into a
     *  {@link java.io.ByteArrayOutputStream}. Intended use is one of: <blockquote><pre>
     *      readWholeStream(stream, blockSize).toByteArray()
     *      readWholeStream(stream, blockSize).toString()
     *  </pre></blockquote>
     *
     * Shares semantics with {@link #copyStreams}.
     *
     * @param in The source stream
     * @param blockSize The size of the buffer to be used
     * @return A {@link java.io.ByteArrayOutputStream} containing the contents of <code>in</code>
     * @throws java.io.IOException
     * @see #copyStreams
     */
    public static ByteArrayOutputStream readWholeStream(InputStream in, int blockSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(in.available());
        copyStreams(in, baos, blockSize);
        return baos;
    }

    /**
     * Wrapper for {@link #readWholeStream} using a <code>blockSize</code> of
     *  {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param in The source stream
     * @return A {@link java.io.ByteArrayOutputStream} containing the contents of <code>in</code>
     * @throws java.io.IOException
     * @see #readWholeStream
     */
    public static ByteArrayOutputStream readWholeStream(InputStream in) throws IOException {
        return readWholeStream(in, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #readWholeStream} that creates a
     *  {@link java.io.FileInputStream} from <code>from</code>.
     *
     * @param from The {@link java.io.File} to read from
     * @param blockSize The size of the buffer to be used
     * @return A {@link java.io.ByteArrayOutputStream} containing the contents of the file
     * @throws java.io.IOException
     * @see #readWholeStream
     */
    public static ByteArrayOutputStream readWholeFile(File from, int blockSize) throws IOException {
        return readWholeStream(new FileInputStream(from), blockSize);
    }

    /**
     * Wrapper for {@link #readWholeFile} using a <code>blockSize</code> of
     *  {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param from The {@link java.io.File} to read from
     * @return A {@link java.io.ByteArrayOutputStream} containing the contents of the file
     * @throws java.io.IOException
     * @see #readWholeFile
     */
    public static ByteArrayOutputStream readWholeFile(File from) throws IOException {
        return readWholeFile(from, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #readWholeFile} that creates a {@link java.io.File}
     *  from <code>fileName</code>.
     *
     * @param fileName The {@link String} path of the file
     * @param blockSize The size of the buffer to be used
     * @return A {@link java.io.ByteArrayOutputStream} containing the contents of the file
     * @throws java.io.IOException
     * @see #readWholeFile
     */
    public static ByteArrayOutputStream readWholeFile(String fileName, int blockSize) throws IOException {
        return readWholeFile(new File(fileName), blockSize);
    }

    /**
     * Wrapper for {@link #readWholeFile(String, int)} using a
     *  <code>blockSize</code> of {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param fileName The {@link String} path of the file
     * @return A {@link java.io.ByteArrayOutputStream} containing the contents of the file
     * @throws java.io.IOException
     * @see #readWholeFile(String, int)
     */
    public static ByteArrayOutputStream readWholeFile(String fileName) throws IOException {
        return readWholeFile(fileName, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Writes the bytes <code>toWrite</code> into <code>out</code> using a
     *  {@link java.io.ByteArrayInputStream} and {@link #copyStreams}.
     *
     * @param toWrite The bytes to write
     * @param out The destination stream
     * @param blockSize The size of the buffer to be used
     * @throws java.io.IOException
     * @see #copyStreams
     */
    public static void writeToStream(byte[] toWrite, OutputStream out, int blockSize) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(toWrite);
        copyStreams(bais, out, blockSize);
    }

    /**
     * Wrapper for {@link #writeToStream} using a <code>blockSize</code> of
     *  {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param toWrite The bytes to write
     * @param out The destination stream
     * @throws java.io.IOException
     * @see #writeToStream
     */
    public static void writeToStream(byte[] toWrite, OutputStream out) throws IOException {
        writeToStream(toWrite, out, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #writeToStream} that converts <code>toWrite</code>
     *  using {@link String#getBytes()}
     *
     * @param toWrite The {@link String} to write
     * @param out The destination stream
     * @param blockSize The size of the buffer to be used
     * @throws java.io.IOException
     * @see #writeToStream
     */
    public static void writeToStream(String toWrite, OutputStream out, int blockSize) throws IOException {
        writeToStream(toWrite.getBytes(), out, blockSize);
    }

    /**
     * Wrapper for {@link #writeToStream(String, java.io.OutputStream, int)}
     *  using a <code>blockSize</code> of {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param toWrite The {@link String} to write
     * @param out The destination stream
     * @throws java.io.IOException
     * @see #writeToStream(String, java.io.OutputStream, int)
     */
    public static void writeToStream(String toWrite, OutputStream out) throws IOException {
        writeToStream(toWrite, out, DEFAULT_BLOCK_SIZE);
    }

    public static void writeToFile(byte[] toWrite, File writeTo, int blockSize) throws IOException {
        writeToStream(toWrite, new FileOutputStream(writeTo), blockSize);
    }

    public static void writeToFile(byte[] toWrite, File writeTo) throws IOException {
        writeToFile(toWrite, writeTo, DEFAULT_BLOCK_SIZE);
    }

    public static void writeToFile(byte[] toWrite, String fileName, int blockSize) throws IOException {
        writeToFile(toWrite, new File(fileName), blockSize);
    }

    public static void writeToFile(byte[] toWrite, String fileName) throws IOException {
        writeToFile(toWrite, fileName, DEFAULT_BLOCK_SIZE);
    }

    public static void writeToFile(String toWrite, File writeTo, int blockSize) throws IOException {
        writeToFile(toWrite.getBytes(), writeTo, blockSize);
    }

    public static void writeToFile(String toWrite, File writeTo) throws IOException {
        writeToFile(toWrite, writeTo, DEFAULT_BLOCK_SIZE);
    }

    public static void writeToFile(String toWrite, String fileName, int blockSize) throws IOException {
        writeToFile(toWrite, new File(fileName), blockSize);
    }

    public static void writeToFile(String toWrite, String fileName) throws IOException {
        writeToFile(toWrite, fileName, DEFAULT_BLOCK_SIZE);
    }
}
