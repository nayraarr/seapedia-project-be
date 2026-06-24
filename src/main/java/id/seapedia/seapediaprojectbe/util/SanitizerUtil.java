package id.seapedia.seapediaprojectbe.util;

import org.springframework.web.util.HtmlUtils;

public final class SanitizerUtil {

    private SanitizerUtil() {}

    public static String clean(String input) {
        if (input == null) return null;
        return HtmlUtils.htmlEscape(input.trim());
    }
}