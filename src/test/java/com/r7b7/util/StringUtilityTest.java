package com.r7b7.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StringUtilityTest {
    @Test
    public void testIsNullOrEmptyWithNull() {
        assertEquals( true,StringUtility.isNullOrEmpty(null));
    }

    @Test
    public void testIsNullOrEmptyWithEmptyString() {
        assertEquals( true, StringUtility.isNullOrEmpty(""));
    }

    @Test
    public void testIsNullOrEmptyWithWhitespaceOnlyString() {
        assertEquals( false, StringUtility.isNullOrEmpty(" "));
    }

    @Test
    public void testIsNullOrEmptyWithNonEmptyString() {
        assertEquals(false,StringUtility.isNullOrEmpty("hello"));
    }

    @Test
    public void testIsNullOrEmptyWithSpecialCharacters() {
        assertEquals(false, StringUtility.isNullOrEmpty("!@#$"));
    }

    @Test
    public void testIsUrlValidWithoutHttp() {
        assertEquals(false, StringUtility.isValidHttpOrHttpsUrl("google.com/"));
    }

    @Test
    public void testIsUrlValidWithHttp() {
        assertEquals(true, StringUtility.isValidHttpOrHttpsUrl("http://google.com/"));
    }

    @Test
    public void testIsUrlValidWithHttps() {
        assertEquals(true, StringUtility.isValidHttpOrHttpsUrl("https://google.com/"));
    }

    @Test
    public void testIsUrlValidForNull() {
        assertEquals(false, StringUtility.isValidHttpOrHttpsUrl(null));
    }

    @Test
    public void testIsUrlValidForEmptyString() {
        assertEquals(false, StringUtility.isValidHttpOrHttpsUrl(""));
    }
}
