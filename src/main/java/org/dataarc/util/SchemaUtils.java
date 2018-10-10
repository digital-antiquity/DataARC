package org.dataarc.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.SchemaField;
import org.dataarc.core.search.IndexFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utilities for work with a schema
 * 
 * @author abrin
 *
 */
public class SchemaUtils {
    private static final String HANDLEBAR_FIELD_NAME = "fieldName ";
    private static final Pattern PATTERN_NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern PATTERN_NONWORD = Pattern.compile("[^\\w\\.\\s-]");
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("[-\\s%<>!=]");
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
        return StringUtils.replaceAll(input.toLowerCase(), " ", "_");
    }

    private static final List<String> ignorePrefix = Arrays.asList(IndexFields.SOURCE, IndexFields.START, IndexFields.END, IndexFields.TITLE);

    /**
     * Reformat a field name
     * @param schema
     * @param field
     * @return
     */
    public static String formatForSolr(Schema schema, org.dataarc.bean.schema.SchemaField field) {
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

    /**
     * Hooks to un-format a schema name/field
     * @param name
     * @param fields
     * @param titleTemplate_
     * @return
     */
    public static String unFormat(String name, Set<SchemaField> fields, String titleTemplate_) {
        String titleTemplate = titleTemplate_;
        return titleTemplate;
        /*
         * for (Field f : fields) {
         * 
         * //FIXME: need a regex that replaces " field}}" " field "
         * titleTemplate = StringUtils.replace(titleTemplate,formatId("",f), formatName(name, f));
         * titleTemplate = StringUtils.replace(titleTemplate,formatId("fieldName ",f), formatName(HANDLEBAR_FIELD_NAME + name, f));
         * }
         * logger.debug("{} --> {}", titleTemplate_, titleTemplate);
         * return titleTemplate;
         * 
         */
    }

    /**
     * Hooks to format a schema name/field 
     * @param name
     * @param fields
     * @param titleTemplate_
     * @return
     */
    public static String format(String name, Set<SchemaField> fields, String titleTemplate_) {
        String titleTemplate = titleTemplate_;
        return titleTemplate;
        /*
         * for (Field f : fields) {
         * titleTemplate = StringUtils.replace(titleTemplate, formatName(name,f), formatId("",f));
         * titleTemplate = StringUtils.replace(titleTemplate, formatName(HANDLEBAR_FIELD_NAME + name,f), formatId("fieldName ",f));
         * }
         * logger.debug("{} --> {}", titleTemplate_, titleTemplate);
         * return titleTemplate;
         */
    }

    private static String formatId(String prefix, SchemaField f) {
        return String.format("{{%s%s}}", prefix, f.getId().intValue());
    }

    private static String formatName(String name, SchemaField f) {
        return String.format("{{%s_%s}}", name, f.getName());
    }

}
