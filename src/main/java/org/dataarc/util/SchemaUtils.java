package org.dataarc.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.schema.Field;
import org.dataarc.bean.schema.Schema;
import org.dataarc.core.search.IndexFields;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class SchemaUtils {
    private static final Pattern PATTERN_NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern PATTERN_NONWORD = Pattern.compile("[^\\w\\.\\s-]");
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("[-\\s]+");
    private static final Pattern PATTERN_AFFIX_SLUG = Pattern.compile("(^-)|(-$)");


    protected static final transient Logger logger = LoggerFactory.getLogger(SchemaUtils.class);

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
        if (field == null) {
            return null;
        }

        if (ignorePrefix.contains(field.getName())) {
            return field.getName();
        }
        return String.format("%s_%s", schema.getName(), field.getName());
    }

    public static String toString(Long id2) {
        return String.format("%s", id2.intValue());
    }

    public static String unFormat(String name, Set<Field> fields, String titleTemplate_) {
        String titleTemplate = titleTemplate_;
        for (Field f : fields) {
            logger.debug("  {} {} -> {} ",f, formatId(f) , formatName(name,f));
            titleTemplate = StringUtils.replace(titleTemplate,formatId(f), formatName(name, f));
        }
        logger.debug("{} --> {}", titleTemplate_, titleTemplate);
        return titleTemplate;
    }

    public static String format(String name, Set<Field> fields, String titleTemplate_) {
        String titleTemplate = titleTemplate_;
        for (Field f : fields) {
            logger.debug("  {} {} -> {} ",f, formatName(name,f), formatId(f) );
            titleTemplate = StringUtils.replace(titleTemplate,  formatName(name,f), formatId(f));
        }
        logger.debug("{} --> {}", titleTemplate_, titleTemplate);
        return titleTemplate;
    }

    private static String formatId(Field f) {
        return String.format("{{%s}}", f.getId().intValue());
    }

    private static String formatName(String name, Field f) {
        return String.format("{{%s_%s}}", name, f.getName());
    }

}
