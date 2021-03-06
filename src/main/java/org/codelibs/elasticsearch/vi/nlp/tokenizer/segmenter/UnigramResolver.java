/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconUnmarshaller;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.Corpus;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.W;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * org.codelibs.elasticsearch.vi.nlp.segmenter
 * <p>
 * Nov 14, 2007, 9:44:46 PM
 * <p>
 * This is a simple resolver for ambiguities of segmentation.
 * It use a pre-built unigram model to resolve segmentations.
 */
public class UnigramResolver extends AbstractResolver {

    private static final Logger logger = LogManager.getLogger(UnigramResolver.class);

    /**
     * A lexicon unmarshaller to unmarshal the unigram model.
     */
    private LexiconUnmarshaller unmarshaller;
    /**
     * The unigram probabilities.
     */
    private Map<String, Integer> unigram;

    /**
     * Default construtor.
     * @param unigramFilename the unigram filename.
     */
    public UnigramResolver(final String unigramFilename) {
        init();
        // load the unigram model.
        loadUnigram(unigramFilename);
    }

    private void init() {
        // create a lexicon unmarshaller
        unmarshaller = new LexiconUnmarshaller();
        // init the unigram model
        unigram = new HashMap<>();
    }

    /**
     * Load unigram model and calculate frequencies.
     * @param unigramFilename the unigram filename
     */
    private void loadUnigram(final String unigramFilename) {
        logger.info("Loading unigram model...");
        // load unigram model
        final Corpus unigramCorpus = unmarshaller.unmarshal(unigramFilename);
        final List<W> ws = unigramCorpus.getBody().getW();
        for (final W w : ws) {
            final String freq = w.getMsd();
            final String word = w.getContent();
            unigram.put(word, Integer.parseInt(freq));
        }
        logger.info("OK");
    }

    /**
     * Unigram resolver for segmentations. Given a list of segmentations,
     * this method calculates the probabilites of segmentations and
     * choose the one with highest probabilities. Since in the unigram model,
     * the probabilities of words are represented by their frequencies,
     * we can calculate and compare the sum of frequencies of words of segmentations.
     * It is always much more rapid to perform simple operations (like addition) on integers
     * than complex operations (like multiply or division) on doubles.
     * @see org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.AbstractResolver#resolve(java.util.List)
     */
    @Override
    public String[] resolve(final List<String[]> segmentations) {
        String[] choice = null;
        int maxFrequency = 0;
        for (final String[] segmentation : segmentations) {
            int frequency = 0;
            for (final String word : segmentation) {
                int wordFreq = 0;
                if (unigram.containsKey(word)) {
                    wordFreq = unigram.get(word).intValue();
                }
                frequency += wordFreq;
            }
            if (frequency >= maxFrequency) {
                maxFrequency = frequency;
                choice = segmentation;
            }
        }
        return choice;
    }

}
