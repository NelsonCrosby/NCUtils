package io.github.nelsoncrosby.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 *
 */
public class StreamUtilsTest {
    final byte[] testContent = (
            "Lorem ipsum dolor sit amet, eu corpora adipiscing deterruisset " +
            "pri. Quo ex fabellas eloquentiam, mel rebum viris nonumes id, " +
            "solet dicant senserit no pri. Vim mucius fabellas in, usu magna " +
            "discere nonumes ut. Ut malorum nostrud consequat est, mei at dico " +
            "quando nostrud, at dolorem officiis abhorreant mei. Doming " +
            "consequuntur vituperatoribus at eum, eu eam graecis consulatu " +
            "gloriatur. Cu modus percipit ius."
    ).getBytes();

    public StreamUtilsTest() {
    }

    /**
     * Test the {@link StreamUtils#copyStreams} method using a block size of a
     *  single byte.
     *
     * @throws Exception
     */
    @Test
    public void testCopyStreamsSingleByteBlockSize() throws Exception {
        try (ByteArrayInputStream source = new ByteArrayInputStream(testContent);
             ByteArrayOutputStream destination = new ByteArrayOutputStream(testContent.length)) {

            StreamUtils.copyStreams(source, destination, 1);

            byte[] finalArray = destination.toByteArray();
            Assert.assertEquals("Resulting size was not the same as the source size",
                    testContent.length, finalArray.length);
            Assert.assertArrayEquals("Destination data did not match source data",
                    testContent, destination.toByteArray());
        }
    }

    /**
     * Test the {@link StreamUtils#copyStreams} method using a probably-uneven
     *  block size (ensure that it won't be an issue).
     *
     * @throws Exception
     */
    @Test
    public void testCopyStreamsUnevenBlockSize() throws Exception {
        try (ByteArrayInputStream source = new ByteArrayInputStream(this.testContent);
             ByteArrayOutputStream destination = new ByteArrayOutputStream(this.testContent.length)) {

            // Generate a block size that is probably not divisible into bytes
            int somewhatEvenBlockSize = this.testContent.length / 8;
            int notEvenBlockSize = somewhatEvenBlockSize - 2;

            StreamUtils.copyStreams(source, destination, notEvenBlockSize);

            byte[] finalArray = destination.toByteArray();
            Assert.assertEquals("Resulting size was not the same as the source size",
                    this.testContent.length, finalArray.length);
            Assert.assertArrayEquals("Destination data did not match source data",
                    testContent, destination.toByteArray());
        }
    }
}
