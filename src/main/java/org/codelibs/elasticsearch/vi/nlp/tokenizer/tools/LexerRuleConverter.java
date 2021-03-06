/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconMarshaller;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jan 11, 2008, 10:11:46 PM
 *         <p>
 *         Convert the text file that contains lexer rules to xml file for OS
 *         independent. The format in use is the lexicon format, each w element
 *         contains a rule.
 */
public class LexerRuleConverter {

    private static final Logger logger = LogManager.getLogger(LexerRuleConverter.class);

    /**
     * Regex for parsing the specification file
     */
    static private final String lxRuleString = "^\\s*(\\S+)\\s+(\\S+)\\s*$";

    private final Map<String, String> lexerMap = new TreeMap<>();

    /**
     * Load lexer specification file. This text file contains lexical rules to
     * tokenize a text
     *
     * @param lexersText
     *            the lexer text filename
     * @return a map
     */
    private Map<String, String> load(final String lexersText) {
        try (final FileInputStream fis = new FileInputStream(lexersText);
                final InputStreamReader isr = new InputStreamReader(fis);
                final LineNumberReader lnr = new LineNumberReader(isr)) {
            // Pattern for parsing each line of specification file
            final Pattern lxRule = Pattern.compile(lxRuleString);
            while (true) {
                final String line = lnr.readLine();
                // read until file is exhausted
                if (line == null) {
                    break;
                }
                final Matcher matcher = lxRule.matcher(line);
                if (matcher.matches()) {
                    // add rules to the list of rules
                    final String name = matcher.group(1);
                    final String regex = matcher.group(2);
                    lexerMap.put(regex, name);
                } else {
                    logger.error("Syntax error in {} at line {}", lexersText, lnr.getLineNumber());
                    System.exit(1);
                }
            }
        } catch (final IOException ioe) {
            logger.error("IOException!");
        }
        return lexerMap;
    }

    /**
     * Convert the lexers text to lexer xml.
     *
     * @param lexersXML
     */
    private void convert(final String lexersXML) {
        new LexiconMarshaller().marshal(lexerMap, lexersXML);
    }

    public static void main(final String[] args) {
        final LexerRuleConverter lexerRuleConverter = new LexerRuleConverter();
        lexerRuleConverter.load("resources/lexers/lexers.txt");
        lexerRuleConverter.convert("resources/lexers/lexers.xml");
        logger.info("Done!");
    }
}
