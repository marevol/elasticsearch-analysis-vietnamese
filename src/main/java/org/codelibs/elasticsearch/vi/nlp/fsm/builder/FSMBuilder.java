package org.codelibs.elasticsearch.vi.nlp.fsm.builder;

import java.util.Arrays;
import java.util.List;

import org.codelibs.elasticsearch.vi.nlp.fsm.FSM;
import org.codelibs.elasticsearch.vi.nlp.fsm.IConstants;
import org.codelibs.elasticsearch.vi.nlp.fsm.ISimulator;
import org.codelibs.elasticsearch.vi.nlp.fsm.fsa.DFA;
import org.codelibs.elasticsearch.vi.nlp.fsm.fst.FST;
import org.codelibs.elasticsearch.vi.nlp.fsm.io.FSMMarshaller;
import org.codelibs.elasticsearch.vi.nlp.fsm.util.FSMUtilities;
import org.codelibs.elasticsearch.vi.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jan 25, 2008, 10:28:03 PM
 *         <p>
 *         A builder for state machine. The main task of this class is to build
 *         a machine from a data source.
 */
public abstract class FSMBuilder {

    /**
     * The machine to be built.
     */
    protected FSM machine;

    /**
     * Build a machine depends on its type (FSA or FST)
     * @param machineType the type of the machine.
     */
    public FSMBuilder(final String machineType) {
        // create machine basing on its type
        if (machineType.equalsIgnoreCase(IConstants.FSM_DFA)) {
            machine = new DFA();
        }
        if (machineType.equalsIgnoreCase(IConstants.FSM_FST)) {
            machine = new FST();
        }
    }

    /**
     * Add an input and its corresponding output to the machine. The subclass
     * should override this method to construct the machine.
     *
     * @param input
     *            an input string
     * @param output
     *            the corresponding output of the input.
     */
    protected abstract void addItem(String input, String[] output);

    /**
     * Create a machine from an array of input strings and their corresponding
     * outputs.
     *
     * @param inputs
     *            an array of input strings.
     * @param outputs
     *            output strings
     */
    public void create(final String[] inputs, final String[][] outputs) {
        for (int i = 0; i < inputs.length; i++) {
            addItem(inputs[i], outputs[i]);
        }
        // do the final step of the creation if required
        finalize();
    }

    /**
     * Create a machine from an array of input strings. There is no outputs.
     * This method is used in the case of a DFA.
     *
     * @param inputs
     *            an array of input strings.
     */
    public void create(final String[] inputs) {
        // first sort the input array:
        Arrays.sort(inputs, 0, inputs.length);
        // create an empty output:
        final String[][] outputs = new String[inputs.length][];
        // create the FSM
        create(inputs, outputs);
    }

    /**
     * Create a machine from a list of input items.
     * @param inputs a list of input items.
     */
    public void create(final List<String> inputs) {
        create(inputs.toArray(new String[inputs.size()]));
    }

    /**
     * Create a machine from a list of input items and their outputs.
     * @param inputs a list of input items.
     *
     */
    public void create(final List<String> inputs, final List<String[]> outputs) {
        create(inputs.toArray(new String[inputs.size()]), outputs.toArray(new String[outputs.size()][]));
    }

    /**
     * Create a machine from a plain text file. Each item is in a line.
     * This method is used to facilitate the creation of a DFA. It is
     * not used in the case of FST, because of a need of outputs.
     * @param filename
     */
    public void create(final String filename) {
        final String[] items = UTF8FileUtility.getLines(filename);
        Arrays.sort(items, 0, items.length);
        create(items);
    }

    /**
     * Do some final stuff to completely build the machine. This method
     * is used in the final step of the creation of a minimal machine.
     */
    @Override
    protected abstract void finalize();

    /**
     * Encode the machine to an XML file.
     * @param filename
     */
    public void encode(final String filename) {
        new FSMMarshaller().marshal(machine, filename);
        FSMUtilities.statistic(machine);
    }

    /**
     * Print the machine to standard console to check.
     */
    public void printMachine() {
        new FSMMarshaller().marshal(machine, System.out);
        FSMUtilities.statistic(machine);
    }

    /**
     * Get the machine.
     * @return the machine.
     */
    public FSM getMachine() {
        return machine;
    }

    /**
     * @return the simulator of the underlying machine.
     */
    public ISimulator getSimulator() {
        return getMachine().getSimulator();
    }

    /**
     * Dispose the builder.
     */
    public void dispose() {
        getMachine().dispose();
    }
}
