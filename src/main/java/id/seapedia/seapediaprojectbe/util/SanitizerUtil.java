package id.seapedia.seapediaprojectbe.util;

public final class SanitizerUtil {

    private SanitizerUtil() {}

    public static String clean(String input) {
        if (input == null) return null;
        return input.trim();
    }
}