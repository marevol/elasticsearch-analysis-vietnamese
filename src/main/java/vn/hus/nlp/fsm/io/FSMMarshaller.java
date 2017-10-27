/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.fsm.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import vn.hus.nlp.fsm.FSM;
import vn.hus.nlp.fsm.IConstants;
import vn.hus.nlp.fsm.State;
import vn.hus.nlp.fsm.Transition;
import vn.hus.nlp.fsm.jaxb.Fsm;
import vn.hus.nlp.fsm.jaxb.ObjectFactory;
import vn.hus.nlp.fsm.jaxb.S;
import vn.hus.nlp.fsm.jaxb.States;
import vn.hus.nlp.fsm.jaxb.T;
import vn.hus.nlp.fsm.jaxb.Transitions;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.fsm
 * <p>
 * Nov 6, 2007, 10:32:24 PM
 * <p>
 */
public class FSMMarshaller {

	private JAXBContext jaxbContext;

	private Marshaller marshaller;

	/**
	 * Default constructor.
	 */
	public FSMMarshaller() {
		// create JAXB context
		//
		createContext();
	}

	private void createContext() {
		jaxbContext = null;
		try {
			final ClassLoader cl = ObjectFactory.class.getClassLoader();
			jaxbContext = JAXBContext.newInstance(IConstants.JAXB_CONTEXT, cl);
		} catch (final JAXBException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Get the marshaller object.
	 * @return
	 */
	private Marshaller getMarshaller() {
		if (marshaller == null) {
			try {
				// create the marshaller
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			} catch (final JAXBException e) {
				e.printStackTrace();
			}
		}
		return marshaller;
	}


	/**
	 * Create a FSM object from a state machine
	 * @param fsm a FSM
	 * @param name the name of FSM
	 * @return a Fsm object.
	 */
	private Fsm createFsm(final FSM fsm, final String name) {
		// create the marshaller
		getMarshaller();
		// build the machine object
		final ObjectFactory of = new ObjectFactory();
		final Fsm _fsm = of.createFsm();
		_fsm.setName(name);
		final States _states= of.createStates();
		_fsm.setStates(_states);
		final Transitions _transitions = of.createTransitions();
		_fsm.setTransitions(_transitions);
		final Map<Integer, State> states = fsm.getStates();
		for (final Integer id : states.keySet()) {
			final State state = states.get(id);
			// create state objects
			final S _s = of.createS();
			_s.setId(id.intValue());
			_s.setType(state.getType());
			_states.getS().add(_s);
			// create transition objects
			final List<Transition> outTransitions = state.getOutTransitions();
			for (final Transition t : outTransitions) {
				final T _t = of.createT();
				_t.setSrc(t.getSource());
				_t.setTar(t.getTarget());
				_t.setInp("" + t.getInput());
				if (t.getOutput() != IConstants.EMPTY_STRING) {
					_t.setOut("" + t.getOutput());
				}
				_transitions.getT().add(_t);
			}
		}
		return _fsm;
	}

	/**
	 * Marshal a fsm to a file.
	 * @param fsm a finite state machine.
	 * @param filename a file.
	 */
	public void marshal(final FSM fsm, final String filename) {
		final Fsm _fsm = createFsm(fsm, filename);
		// marshal the object
		try {
			marshaller.marshal(_fsm, new FileOutputStream(new File(filename)));
		} catch (final JAXBException e) {
			e.printStackTrace();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Marshal a fsm to an output stream. This method is used only to
	 * test the created machine.
	 * @param fsm
	 * @param os
	 */
	public void marshal(final FSM fsm, final OutputStream os) {
		final Fsm _fsm = createFsm(fsm, "sample_fsm");
		// marshal the object
		try {
			marshaller.marshal(_fsm, os);
		} catch (final JAXBException e) {
			e.printStackTrace();
		}
	}

}