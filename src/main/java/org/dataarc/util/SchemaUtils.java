package org.dataarc.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.legacy.search.IndexFields;

public class SchemaUtils {
    private static final Pattern PATTERN_NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern PATTERN_NONWORD = Pattern.compile("[^\\w\\.\\s-]");
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("[-\\s]+");
    private static final Pattern PATTERN_AFFIX_SLUG = Pattern.compile("(^-)|(-$)");

    /**
     * Slightly faster version of String.replaceAll
     * 
     * @param str
     * @param pattern
     * @param replacement
     * @return
     */
    static String replaceAll(String str, Pattern pattern, String replacement) {
        if (StringUtils.isBlank(str))
            return str;
        return pattern.matcher(str).replaceAll(replacement);
    }

    /**
     * Convert unicode string into approximated ascii (NFKD normalization)
     * 
     * @param utfString
     * @return
     */
    public static String normalize(String utfString) {
        if (StringUtils.isBlank(utfString)) {
            return utfString;
        }
        String decomp = Normalizer.normalize(utfString, Normalizer.Form.NFKD);
        String input = replaceAll(decomp, PATTERN_NON_ASCII, "");
        input = replaceAll(input, PATTERN_NONWORD, "");
        input = replaceAll(input, PATTERN_WHITESPACE, "_");
        input = replaceAll(input, PATTERN_AFFIX_SLUG, "");
        return input.toLowerCase();
    }

    private static final List<String> ignorePrefix = Arrays.asList(IndexFields.SOURCE, IndexFields.START, IndexFields.END, IndexFields.TITLE);

    public static String formatForSolr(Schema schema, org.dataarc.bean.schema.Field field) {
        if (ignorePrefix.contains(field.getName())) {
            return field.getName();
        }
        return String.format("%s_%s", schema.getName(), field.getName());
    }

}
