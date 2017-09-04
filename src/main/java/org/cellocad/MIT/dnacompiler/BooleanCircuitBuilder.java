package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.*;
import org.cellocad.BU.netsynth.NetSynth;
import org.cellocad.BU.netsynth.NetSynthSwitch;
import org.cellocad.BU.netsynth.Utilities;
import org.cellocad.MIT.dnacompiler.DNACompiler.CircuitType;
import org.cellocad.MIT.dnacompiler.DNACompiler.ResultStatus;
import org.cellocad.MIT.dnacompiler.Gate.GateType;
import org.cellocad.MIT.figures.*;
import org.cellocad.adaptors.ucfadaptor.UCFAdaptor;
import org.cellocad.adaptors.ucfadaptor.UCFReader;
import org.cellocad.adaptors.ucfadaptor.UCFValidator;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;



/**
 * Builder class to generate a boolean circuit from verilog file. A boolean circuit (aka an abstract circuit or wiring diagram) 
 * is a {@code LogicCircuit} with no gates assignment. It is purely composed of logic gates. Most of the code
 * here was originally in {@code DNACompiler} and moved to here for modularity and simplification purposes. To actually
 * get the circuit, you should construct the class with the appropriate arguments then call {@code get_boolean_lc()}.
 * 
 * @param verilog_filepath filepath to verilog file specifying boolean circuit
 * @param ucf user specified UCF file
 * @param options user specified options
 * 
 * @author jaipadmakumar
 *
 */
public class BooleanCircuitBuilder {
	@Getter private String _verilog_filepath;
	@Getter private Args _options;
	private LogicCircuit _boolean_lc;
	@Getter private ResultStatus _result_status;
	
	@Getter @Setter private String threadDependentLoggername;
	@Getter @Setter private UCF _ucf;
	
	private Logger logger  = Logger.getLogger(getClass());
	
	public BooleanCircuitBuilder(String verilog_filepath, UCF ucf, Args options) {
		this._verilog_filepath = verilog_filepath;
		this._options = options;
        this._ucf = ucf;

	}
	
	public LogicCircuit get_boolean_lc() {
		logger = Logger.getLogger(getThreadDependentLoggername());

		/**
         * NetSynth: convert Verilog to Boolean wiring diagram
         */
		
		logger.info("Building boolean circuit...\n");
        try {
            this._boolean_lc = buildBooleanCircuit();
        } catch(Exception e) {
            throw new IllegalStateException("Error in abstract circuit.  Exiting.");
        }
        
        /**
         * circuit size
         */
        for (GateType gtype : this._boolean_lc.get_gate_types().keySet()) {
            logger.info("Circuit has " + this._boolean_lc.get_gate_types().get(gtype).size() + " " + gtype + " gates.");
        }

        logger.info("N logic gates: " + this._boolean_lc.get_logic_gates().size() + "");
        
        validateCircuit();
        setCircuitLogic();
        
        return this._boolean_lc;
		
	}
	
	//validation can really be moved to LogicCircuit setter itself
	// you should test if this works period, not sure ResultStatus will get passed to main script
	// probably need object to have ResultStatus field and test in DNACompiler if invalid and if so, return
	private void validateCircuit() {
	    /**
	     * A logic circuit must have at least one input gate and one output gate
	     */
	    if (this._boolean_lc.get_input_gates().size() == 0 || this._boolean_lc.get_output_gates().size() == 0) {
	        _result_status = ResultStatus.wiring_diagram_invalid;
	        logger.info("incorrect wiring diagram, no inputs/outputs");
	        return;
	    }
	}
	private void setCircuitLogic() {
		/**
	     *
	     * Set the logic for the input gates.
	     * For combinational logic, this means permuting all input combinations.
	     * For sequential logic, the input 'waveforms' will used as the truth table.
	     *
	     * For a 3-input circuit:
	     *
	     * in1 in2 in3
	     *  0   0   0
	     *  0   0   1
	     *  0   1   0
	     *  0   1   1
	     *  1   0   0
	     *  1   0   1
	     *  1   1   0
	     *  1   1   1
	     */

	    if(_options.get_circuit_type() == CircuitType.sequential) {
	        
	        
	        HashMap<String, List<Integer>> initial_logics = new HashMap<>();
	        int nrows = SequentialHelper.loadInitialLogicsFromTruthtable(initial_logics, get_options().get_fin_sequential_waveform());

	        SequentialHelper.setInitialLogics(this._boolean_lc, initial_logics, nrows);
	        SequentialHelper.printTruthTable(this._boolean_lc);

	        logger.info("Cycle 1");
	        SequentialHelper.updateLogics(this._boolean_lc);
	        SequentialHelper.printTruthTable(this._boolean_lc);

	        logger.info("Cycle 2");
	        SequentialHelper.updateLogics(this._boolean_lc);
	        SequentialHelper.printTruthTable(this._boolean_lc);

	        logger.info("Cycle 3");
	        SequentialHelper.updateLogics(this._boolean_lc);
	        SequentialHelper.printTruthTable(this._boolean_lc);

	        //assert logic is valid
	        if(! SequentialHelper.validLogic(this._boolean_lc)) {
	            throw new IllegalStateException("SequentialHelper: Invalid logic.  Exiting.");
	        }

	    }
	    else {

	        LogicCircuitUtil.setInputLogics(this._boolean_lc);

	        /**
	         *  propagate logic through gates
	         */
	        //initialize logic to all zeroes
	        Integer nrows = this._boolean_lc.get_input_gates().get(0).get_logics().size();
	        for (Gate g : this._boolean_lc.get_Gates()) {
	            if (g.get_logics().isEmpty()) {
	                ArrayList<Integer> logics = new ArrayList<>();
	                for (int i = 0; i < nrows; ++i) {
	                    logics.add(0);
	                }
	                g.set_logics(logics);
	            }
	        }
	        //compute Boolean logic for each gate in the circuit.
	        Evaluate.simulateLogic(this._boolean_lc);
	        
	    }
	}


    


	 
	    
    
	
	/**
    *
    * Prashant Vaidyanathan's NetlistSynthesizer generates the wiring diagram from Verilog input
    *
    * Flow 1: ABC to AND-Inverter Graph to NOR/NOT
    * Flow 2: Espresso to POS to NOR/NOT
    * Other: precomputed netlists for 3-input 1-output (by Swapnil Bhatia)
    *
    */
   private LogicCircuit buildBooleanCircuit() throws IOException, ParseException {

       if(_options.get_circuit_type() == CircuitType.sequential) {

           if( ! _options.get_synthesis().equals("originalstructural")) {
               throw new IllegalStateException("ARGUMENTS: sequential logic requires originalstructural logic synthesis.");
           }

           if(get_options().get_fin_sequential_waveform() == "") {
               throw new IllegalStateException("ARGUMENTS: missing sequential waveform");
           }

           LogicCircuit abstract_lc = StructuralVerilogToDAG.createDAG(get_options().get_fin_verilog());

           return abstract_lc;
       }
       
       
       LogicCircuit abstract_logic_circuit = new LogicCircuit();

       ////////////////// Create LogicCircuit from NetSynth //////////////


       //get Abstract Circuit with options
       org.cellocad.BU.dom.DAGW GW = new org.cellocad.BU.dom.DAGW();

       String verilog_string = "";
       ArrayList<String> verilog_lines = Util.fileLines(this._verilog_filepath);
       for(String s: verilog_lines) {
           verilog_string += s + "\n";
       }

       List<NetSynthSwitch> switches = new ArrayList<>();
       org.json.JSONArray motifLibrary = new org.json.JSONArray();
       switches.add(NetSynthSwitch.originalstructural); //jai did this, tells netsynth to only run structural verilog
       //w/ no logic minimization

       //convert org.simple.json to org.json
       for(int i=0; i<this._ucf.get_motif_library().size(); ++i) {
           String objString = this._ucf.get_motif_library().get(i).toString();
           try {
               motifLibrary.put(new org.json.JSONObject(objString));
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       
       NetSynth netsynth = new NetSynth("netSynth", Utilities.getNetSynthResourcesFilepath() ,_options.get_output_directory());

       
       GW = netsynth.runNetSynth(
               this._verilog_filepath,
              // new ArrayList<NetSynthSwitch>(),
               switches, //and jai did this for the structural verilog stuff
               motifLibrary
       );

       netsynth.cleanDirectory();

       
       abstract_logic_circuit = new LogicCircuit(GW.Gates, GW.Wires);

       //Prashant needs to fix bug with extra output wire.
       LogicCircuit lc = abstract_logic_circuit;
       for(int i=0; i<lc.get_Wires().size(); ++i) {
           Wire w = lc.get_Wires().get(i);
           if(w.To.Index == w.From.Index) {
               lc.get_Wires().remove(i);
               i--;
           }
       }


       for(Gate g: abstract_logic_circuit.get_Gates()) {
           if(g.Outgoing != null) {
               g.Outgoing_wire_index = g.Outgoing.Index;
           }
       }
       for(Wire w: abstract_logic_circuit.get_Wires()) {
           if(w.From != null) {
               w.From_index = w.From.Index;
           }
           if(w.To != null) {
               w.To_index = w.To.Index;
           }
           if(w.Next != null) {
               w.Next_index = w.Next.Index;
           }
       }

       LogicCircuitUtil.renameGatesWires(abstract_logic_circuit);

       logger.info(Netlist.getNetlist(abstract_logic_circuit));
       
       return abstract_logic_circuit;
   }

}
