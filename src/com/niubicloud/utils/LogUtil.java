package com.niubicloud.utils;

import java.io.PrintStream;

public class LogUtil {
    private static PrintStream outStream = System.out;
    private static PrintStream errStream = System.err;

    public static void warn(String str) {
        outStream.println("Warn: " + str);
    }

    public static void error(String str) {
        errStream.println("Error: " + str);
    }
}
