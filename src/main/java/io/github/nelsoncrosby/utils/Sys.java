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

import java.io.File;

/**
 * Provides an easy way to do OS-specific stuff
 *
 * @author Nelson Crosby
 */
public enum Sys {
    /** Represents a JVM running on Microsoft Windows */
    WINDOWS,
    /** Represents a JVM running on Apple Mac OS */
    MAC,
    /** Represents a JVM running on a Linux distribution */
    LINUX,
    /** Represents a JVM running on an OS not currently detected */
    OTHER;

    /**
     * Checks if this system is UNIX-based
     *
     * @return {@code true} if {@code this} is a UNIX-based system (one of
     *         {@link #MAC}, {@link #LINUX} or {@link #OTHER}).
     */
    public boolean isUnix() {
        return this == MAC || this == LINUX
                // Most other systems will likely be UNIX-based
                || this == OTHER;
    }

    public static final Sys SYSTEM;
    private static final File PRIVATE_DIRECTORY_PREFIX;

    static {
        // Define the running system
        String os = System.getProperty("os.name").toLowerCase();
        SYSTEM = os.contains("windows") ? WINDOWS :
                os.contains("mac") ? MAC :
                os.contains("linux") ? LINUX :
                OTHER;

        // Configure PRIVATE_DIRECTORY_PREFIX for current system
        File userHome = new File(System.getProperty("user.home"));
        switch (SYSTEM) {
            case WINDOWS:
                PRIVATE_DIRECTORY_PREFIX = new File(userHome, "AppData/Roaming");
                break;
            case MAC:
                PRIVATE_DIRECTORY_PREFIX = new File(userHome, "Library/Application Support");
                break;
            case LINUX:
                // Linux has a new place for config stuff to go
                PRIVATE_DIRECTORY_PREFIX = new File(userHome, ".local/share");
                break;
            default:
                // Prepend a dot and place it in user home
                PRIVATE_DIRECTORY_PREFIX = userHome;
                break;
        }
    }


    /**
     * Generates a private directory for this app.
     *
     * If this method does not return null, then this directory is guaranteed to
     *  be created.
     *
     * @param appName The name for the private directory (should be an app name)
     * @return The {@link java.io.File} object for this directory, or null if this method cannot create the directory.
     */
    public static File generatePrivateDirectory(String appName) {
        if (SYSTEM.isUnix() &&
                !(SYSTEM == MAC /* Mac is UNIX-based, but private directories
                                   have another place to go */
                || SYSTEM == LINUX /* Linux has a new place which is tidier */)) {
            // Best way to make something private in these systems is by prepending a dot
            appName = '.' + appName;
        }
        // $PRIVATE_DIRECTORY_PREFIX/$appName
        File privateDir = new File(PRIVATE_DIRECTORY_PREFIX, appName);
        if (!privateDir.isDirectory()) {
            if (privateDir.exists())
                return null;
            else if (!privateDir.mkdirs())
                return null;
        }
        return privateDir;
    }

    /**
     * Generate a private directory for this app, and return a file.
     *
     * Calls {@link #generatePrivateDirectory} on {@code appName}. If that method
     *  returns null, this method will too.
     *
     * @param appName The name of the parent directory for the file (should be the name of the app)
     * @param file The path for the file
     * @return The {@link java.io.File} object representing {@code PRIVATE_DIRECTORY_PREFIX/appName/file}
     */
    public static File getPrivateFile(String appName, String file) {
        File privateDirectory = generatePrivateDirectory(appName);
        if (privateDirectory == null) return null;
        // $privateDirectory/$file
        return new File(privateDirectory, file);
    }
}
