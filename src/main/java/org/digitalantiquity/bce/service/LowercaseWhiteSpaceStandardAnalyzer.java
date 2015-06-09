package org.digitalantiquity.bce.service;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.pattern.PatternTokenizer;
import org.apache.lucene.util.Version;

/**
 * Creates a Lucene Analyzer that tokenizes on spaces and non-word characters, then strips out stop-words and indexes everything.
 * 
 * @author abrin
 *
 */
public final class LowercaseWhiteSpaceStandardAnalyzer extends Analyzer {
    public static final String[] ENGLISH_STOP_WORDS = {
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with"
    };

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        // TOKENIZING ON (punctuation?)(space +) (punctuation?)
        Tokenizer st = new PatternTokenizer(reader, Pattern.compile("((^|\\W|\\_)?(\\s+)(\\W|\\_|$)?)"), -1);
        // FIXME: this still lets things like "carp)" through as well as "carp" - it'd be better if the latter was the only thing

        // http://wiki.apache.org/solr/AnalyzersTokenizersTokenFilters
        // http://wiki.apache.org/solr/SolrRelevancyCookbook#IntraWordDelimiters
        Map<String, String> params = new HashMap<String, String>();
        params.put("generateWordParts", "1");
        params.put("catenateWords", "1");
        params.put("generateNumberParts", "0");
        params.put("catenateNumbers", "0");
        params.put("catenateAll", "1");
        params.put("preserveOriginal", "1");
        params.put("splitOnNumerics", "0");
        params.put("stemEnglishPossessive", "1");
        params.put("splitOnCaseChange", "0");
        params.put(WordDelimiterFilterFactory.LUCENE_MATCH_VERSION_PARAM, Version.LATEST.toString());
        // params.put("types", "wdfftypes.txt"); "% => ALPHA" or "\u002C => DIGIT". Allowable types are: LOWER, UPPER, ALPHA, DIGIT, ALPHANUM,
        // SUBWORD_DELIM.
        // [Solr3.1]
        WordDelimiterFilterFactory wordFilter = new WordDelimiterFilterFactory(params);

        LowerCaseFilter stream = new LowerCaseFilter(wordFilter.create(st));
        TrimFilter trimFilter = new TrimFilter(stream);

        StopFilter stopFilter = new StopFilter(trimFilter, StopFilter.makeStopSet(ENGLISH_STOP_WORDS));
        PorterStemFilter porterStemFilter = new PorterStemFilter(stopFilter);
        ASCIIFoldingFilter filter = new ASCIIFoldingFilter(porterStemFilter);
        return new TokenStreamComponents(st, filter);
    }
}
