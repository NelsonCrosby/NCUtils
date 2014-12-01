package io.github.nelsoncrosby.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class SysTest {

    // The values checked before assertion are obtained from a forum thread:
    //  http://www.java-gaming.org/topics/possible-values-for-system-property/14110/msg/112286/view.html#msg112286

    @Test
    public void checkLinuxSystem() throws Exception {
        if ((System.getProperty("os.name")).equals("Linux"))
            assertEquals(Sys.LINUX, Sys.SYSTEM);
        else
            System.err.println("Not testing on a Linux system");
    }

    @Test
    public void checkMacSystem() throws Exception {
        if ((System.getProperty("os.name")).equals("Mac OS X"))
            assertEquals(Sys.MAC, Sys.SYSTEM);
        else
            System.err.println("Not testing on a Mac system");
    }

    @Test
    public void checkWindowsSystem() throws Exception {
        if ((System.getProperty("os.name")).startsWith("Windows"))
            assertEquals(Sys.MAC, Sys.SYSTEM);
        else
            System.err.println("Not testing on a Windows system");
    }

    @Test
    public void testUnixy() throws Exception {
        assertFalse(Sys.WINDOWS.isUnix());
        assertTrue(Sys.MAC.isUnix());
        assertTrue(Sys.LINUX.isUnix());
        assertTrue(Sys.OTHER.isUnix());
    }
}
