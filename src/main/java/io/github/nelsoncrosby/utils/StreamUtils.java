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
 *     <li>{@link #readWholeFile(File)}</li>
 *     <li>{@link #writeToFile(byte[], File)}</li>
 *     <li>{@link #writeToFile(String, File)}</li>
 * </ul>
 *
 * @author Nelson Crosby
 */
public class StreamUtils {
    /**
     * The default block size in bytes.
     * Given a method {@code streamAction(...)} whose parameter list does
     *  not end with {@code int blockSize}, wrappers are provided that call
     *  {@code streamAction(..., DEFAULT_BLOCK_SIZE)}.
     */
    public static final int DEFAULT_BLOCK_SIZE = 1024;

    /**
     * The base for all other methods in this class.
     *
     * It copies all remaining bytes until the EOF from {@code from} to
     *  {@code to}. This is done in buffers of {@code blockSize}
     *  bytes.
     *
     * This method closes both files at the end.
     *
     * @param from The source stream
     * @param to The destination stream
     * @param blockSize The size of the buffer to be used
     * @throws IOException See {@link InputStream#read(byte[])} and
     *  {@link OutputStream#write(byte[], int, int)}.
     */
    public static void copyStreams(InputStream from, OutputStream to, int blockSize) throws IOException {
        byte[] buffer = new byte[blockSize];
        int bytesRead;
        while ((bytesRead = from.read(buffer) /* Read bytes into buffer */)
                != -1 /* Check that EOF not reached */) {
            // Write buffer into destination
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
     * @throws IOException
     * @see #copyStreams
     */
    public static void copyStreams(InputStream from, OutputStream to) throws IOException {
        copyStreams(from, to, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Reads all remaining bytes until EOF from {@code in} into a
     *  {@link ByteArrayOutputStream}. Intended use is one of: <blockquote><pre>
     *      readWholeStream(stream, blockSize).toByteArray()
     *      readWholeStream(stream, blockSize).toString()
     *  </pre></blockquote>
     *
     * Shares semantics with {@link #copyStreams}.
     *
     * @param in The source stream
     * @param blockSize The size of the buffer to be used
     * @return A {@link ByteArrayOutputStream} containing the contents of {@code in}
     * @throws IOException
     * @see #copyStreams
     */
    public static ByteArrayOutputStream readWholeStream(InputStream in, int blockSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(in.available());
        copyStreams(in, baos, blockSize);
        return baos;
    }

    /**
     * Wrapper for {@link #readWholeStream} using a {@code blockSize} of
     *  {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param in The source stream
     * @return A {@link ByteArrayOutputStream} containing the contents of {@code in}
     * @throws IOException
     * @see #readWholeStream
     */
    public static ByteArrayOutputStream readWholeStream(InputStream in) throws IOException {
        return readWholeStream(in, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #readWholeStream} that creates a
     *  {@link FileInputStream} from {@code from}.
     *
     * @param from The {@link File} to read from
     * @param blockSize The size of the buffer to be used
     * @return A {@link ByteArrayOutputStream} containing the contents of the file
     * @throws IOException
     * @see #readWholeStream
     */
    public static ByteArrayOutputStream readWholeFile(File from, int blockSize) throws IOException {
        return readWholeStream(new FileInputStream(from), blockSize);
    }

    /**
     * Wrapper for {@link #readWholeFile} using a {@code blockSize} of
     *  {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param from The {@link File} to read from
     * @return A {@link ByteArrayOutputStream} containing the contents of the file
     * @throws IOException
     * @see #readWholeFile
     */
    public static ByteArrayOutputStream readWholeFile(File from) throws IOException {
        return readWholeFile(from, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #readWholeFile} that creates a {@link File}
     *  from {@code fileName}.
     *
     * @param fileName The {@link String} path of the file
     * @param blockSize The size of the buffer to be used
     * @return A {@link ByteArrayOutputStream} containing the contents of the file
     * @throws IOException
     * @see #readWholeFile
     */
    public static ByteArrayOutputStream readWholeFile(String fileName, int blockSize) throws IOException {
        return readWholeFile(new File(fileName), blockSize);
    }

    /**
     * Wrapper for {@link #readWholeFile(String, int)} using a
     *  {@code blockSize} of {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param fileName The {@link String} path of the file
     * @return A {@link ByteArrayOutputStream} containing the contents of the file
     * @throws IOException
     * @see #readWholeFile(String, int)
     */
    public static ByteArrayOutputStream readWholeFile(String fileName) throws IOException {
        return readWholeFile(fileName, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Writes the bytes {@code toWrite} into {@code out} using a
     *  {@link ByteArrayInputStream} and {@link #copyStreams}.
     *
     * @param toWrite The bytes to write
     * @param out The destination stream
     * @param blockSize The size of the buffer to be used
     * @throws IOException
     * @see #copyStreams
     */
    public static void writeToStream(byte[] toWrite, OutputStream out, int blockSize) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(toWrite);
        copyStreams(bais, out, blockSize);
    }

    /**
     * Wrapper for {@link #writeToStream} using a {@code blockSize} of
     *  {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param toWrite The bytes to write
     * @param out The destination stream
     * @throws IOException
     * @see #writeToStream
     */
    public static void writeToStream(byte[] toWrite, OutputStream out) throws IOException {
        writeToStream(toWrite, out, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #writeToStream} that converts {@code toWrite}
     *  using {@link String#getBytes()}
     *
     * @param toWrite The {@link String} to write
     * @param out The destination stream
     * @param blockSize The size of the buffer to be used
     * @throws IOException
     * @see #writeToStream
     */
    public static void writeToStream(String toWrite, OutputStream out, int blockSize) throws IOException {
        writeToStream(toWrite.getBytes(), out, blockSize);
    }

    /**
     * Wrapper for {@link #writeToStream(String, OutputStream, int)}
     *  using a {@code blockSize} of {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param toWrite The {@link String} to write
     * @param out The destination stream
     * @throws IOException
     * @see #writeToStream(String, OutputStream, int)
     */
    public static void writeToStream(String toWrite, OutputStream out) throws IOException {
        writeToStream(toWrite, out, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Writes the bytes {@code toWrite} into a {@link FileOutputStream}
     *  constructed using {@code writeTo} using {@link #copyStreams}
     *
     * @param toWrite The bytes to write
     * @param writeTo The {@link File} to write to
     * @param blockSize The size of the buffer to be used
     * @throws IOException
     * @see #copyStreams
     */
    public static void writeToFile(byte[] toWrite, File writeTo, int blockSize) throws IOException {
        writeToStream(toWrite, new FileOutputStream(writeTo), blockSize);
    }

    /**
     * Wrapper for {@link #writeToFile} using a {@code blockSize} of
     *  {@value #DEFAULT_BLOCK_SIZE}
     *
     * @param toWrite The bytes to write
     * @param writeTo The {@link File} to write to
     * @throws IOException
     * @see #writeToFile
     */
    public static void writeToFile(byte[] toWrite, File writeTo) throws IOException {
        writeToFile(toWrite, writeTo, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #writeToFile} that creates a {@link File} from
     *  {@code fileName}
     *
     * @param toWrite The bytes to write
     * @param fileName The path of the file to write to
     * @param blockSize The size of the buffer to be used
     * @throws IOException
     * @see #writeToFile
     */
    public static void writeToFile(byte[] toWrite, String fileName, int blockSize) throws IOException {
        writeToFile(toWrite, new File(fileName), blockSize);
    }

    /**
     * Wrapper for {@link #writeToFile(byte[], String, int)} using a
     *  {@code blockSize} of {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param toWrite The bytes to write
     * @param fileName The path of the file to write to
     * @throws IOException
     * @see #writeToFile(byte[], String, int)
     */
    public static void writeToFile(byte[] toWrite, String fileName) throws IOException {
        writeToFile(toWrite, fileName, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #writeToFile} that converts {@code toWrite} using
     *  {@link String#getBytes()}
     *
     * @param toWrite The {@link String} to write
     * @param writeTo The {@link File} to write to
     * @param blockSize The size of the buffer to be used
     * @throws IOException
     * @see #writeToFile
     */
    public static void writeToFile(String toWrite, File writeTo, int blockSize) throws IOException {
        writeToFile(toWrite.getBytes(), writeTo, blockSize);
    }

    /**
     * Wrapper for {@link #writeToFile(String, File, int)} using a
     *  {@code blockSize} of {@value #DEFAULT_BLOCK_SIZE}.
     *
     * @param toWrite The {@link String} to write
     * @param writeTo The {@link File} to write to
     * @throws IOException
     * @see #writeToFile(String, File, int)
     */
    public static void writeToFile(String toWrite, File writeTo) throws IOException {
        writeToFile(toWrite, writeTo, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Wrapper for {@link #writeToFile(String, File, int)} that creates a
     *  {@link File} from {@code fileName}
     *
     * @param toWrite The {@link String} to write
     * @param fileName The path of the file to write to
     * @param blockSize The size of the buffer to be used
     * @throws IOException
     * @see #writeToFile(String, File, int)
     */
    public static void writeToFile(String toWrite, String fileName, int blockSize) throws IOException {
        writeToFile(toWrite, new File(fileName), blockSize);
    }

    /**
     * Wrapper for {@link #writeToFile(String, String, int)} using a
     *  {@code blockSize} of {@value #DEFAULT_BLOCK_SIZE}
     *
     * @param toWrite The bytes to write
     * @param fileName The path of the file to write to
     * @throws IOException
     * @see #writeToFile(String, String, int)
     */
    public static void writeToFile(String toWrite, String fileName) throws IOException {
        writeToFile(toWrite, fileName, DEFAULT_BLOCK_SIZE);
    }
}
