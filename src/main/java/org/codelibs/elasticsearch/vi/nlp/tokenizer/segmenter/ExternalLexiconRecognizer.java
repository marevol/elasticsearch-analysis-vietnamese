/**
 * Phuong LE HONG, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconUnmarshaller;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.Corpus;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.W;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Dec 28, 2009, 3:23:53 PM
 * <p>
 * An additional lexicon recognizer which recognises an external lexicon provided
 * by users in case they want to use a custom lexicon (in addition with the internal
 * lexicon of the tokenizer).
 */
public class ExternalLexiconRecognizer extends AbstractLexiconRecognizer {

    private static final Logger logger = LogManager.getLogger(ExternalLexiconRecognizer.class);

    private Set<String> externalLexicon;

    /**
     * Default constructor.
     */
    public ExternalLexiconRecognizer() {
        this(IConstants.EXTERNAL_LEXICON);
    }

    /**
     * Creates an external lexicon recognizer given a lexicon.
     * @param externalLexiconFilename a lexicon filename
     */
    public ExternalLexiconRecognizer(final String externalLexiconFilename) {
        // load the prefix lexicon
        //
        final LexiconUnmarshaller lexiconUnmarshaller = new LexiconUnmarshaller();
        final Corpus lexicon = lexiconUnmarshaller.unmarshal(externalLexiconFilename);
        final List<W> ws = lexicon.getBody().getW();
        externalLexicon = new HashSet<>();
        // add all prefixes to the set after converting them to lowercase
        for (final W w : ws) {
            externalLexicon.add(w.getContent().toLowerCase());
        }
        logger.info("External lexicon loaded.");
    }

    public ExternalLexiconRecognizer(final Properties properties) {
        this(properties.getProperty("externalLexicon"));
    }

    /* (non-Javadoc)
     * @see org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.AbstractLexiconRecognizer#accept(java.lang.String)
     */
    @Override
    public boolean accept(final String token) {
        return externalLexicon.contains(token);
    }

    /* (non-Javadoc)
     * @see org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.AbstractLexiconRecognizer#dispose()
     */
    @Override
    public void dispose() {
        externalLexicon.clear();
        externalLexicon = null;
    }

    /**
     * Gets the external lexicon.
     * @return the external lexicon.
     */
    public Set<String> getExternalLexicon() {
        return externalLexicon;
    }
}
