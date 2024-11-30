package com.r7b7.util;

import java.net.URI;

public final class StringUtility {
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isValidHttpOrHttpsUrl(String url) {
        if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
            return false;
        }
        try {
            URI.create(url).toURL(); 
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
