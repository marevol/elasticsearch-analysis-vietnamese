/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.test;

import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Created: Feb 17, 2008, 2:52:59 PM
 * <p>
 */
public class FrenchLexiconExtractor {

    private static final Logger logger = LogManager.getLogger(FrenchLexiconExtractor.class);

    static String INPUT_FILE = "samples/dicts/fr/morph.txt";

    static String OUTPUT_FILE = "samples/dicts/fr/fr.txt";

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Set<String> lexicon = new TreeSet<>();
        final String[] lines = UTF8FileUtility.getLines(INPUT_FILE);
        for (final String line : lines) {
            lexicon.add(line);
        }
        UTF8FileUtility.createWriter(OUTPUT_FILE);
        for (final String word : lexicon) {
            UTF8FileUtility.write(word + "\n");

        }
        UTF8FileUtility.closeWriter();
        logger.info("Done");
    }

}
