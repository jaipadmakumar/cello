package org.cellocad.MIT.dnacompiler;
//jai written on 08/06/2017

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import static org.cellocad.MIT.dnacompiler.PartitionCircuitUtil.*;
import org.cellocad.MIT.figures.*;

/**
 * PartitionCircuit provides a class that enables a LogicCircuit instance to be split into 
 * >1 subcircuits, which collectively constitute the same logic as the parent LogicCircuit 
 * instance. PartitionCircuit is defined by the LogicCircuit it is constructed with.
 * 
 * @param sub_lcs this field contains all possible sets of subgraphs, where each set 
 * mimicks the logic of the parent circuit
 * @param parent_lc the parental {@code LogicCircuit} the instance was constructed with
 * @author jaipadmakumar
 *
 */


public class PartitionCircuit {
	
	//Instance Variables

	@Getter @Setter private List<List<LogicCircuit>> sub_lcs; //list of sub-LogicCircuits
	@Getter @Setter public List<List<Subgraph>> subgraph_sets;
	@Getter @Setter public LogicCircuit parent_lc;
	private Args options;
	@Getter @Setter private List<IntegratedLogicCircuit> integrated_circuits;
	@Getter @Setter private PartitionCircuitAlgorithm algorithm;
	
	//From a code logic perspective, might make more sense to have 
	//subgraph called subcircuit and have that be a subclass of 
	//LogicCircuit
//	public class SplitLogicCircuit{
//		@Getter @Setter private List<List<LogicCircuit>> sub_lcs;
//		@Getter @Setter private LogicCircuit parent_lc;
//		@Getter @Setter private LogicCircuit merged_lc;
//	}
	
	/**
	 * A collection of Subgraph objects taken together constitute the same logic 
	 * as the single parent logic circuit.
	 * 
	 * @param gates list of gates contained in subgraph
	 * @param paths set of paths from source to terminal node present in the subgraph
	 * @param sub_lc LogicCircuit constructed from Subgraph gates
	 * @param terminal_gate final gate in the circuit
	 * @param terminal_gate_parents parent gates of {@code terminal_gate} based on parent logic circuit
	 * 
	 * @author jaipadmakumar
	 */
	
	public class Subgraph{
		
		List<Gate> gates = new ArrayList<Gate>();
		HashSet<List<Gate>> paths = new HashSet<List<Gate>>(); //should be a list of all paths through graph
		LogicCircuit sub_lc; //logic subcircuit
		List<Gate> terminal_gate_parents = new ArrayList<Gate>(); //tells you what qs gate connects to in completed circuit
		Gate terminal_gate = new Gate();
		
		Subgraph(){
		//default constructor
		}
		
		Subgraph(HashSet<List<Gate>> subgraph_paths){
			//construct subgraph in terms of paths
			paths = subgraph_paths;
		}
		
		Subgraph(Subgraph subgr){
			//copy constructor
			this.gates = subgr.gates;
			this.paths = subgr.paths;
			this.sub_lc = subgr.sub_lc;
			this.terminal_gate_parents = subgr.terminal_gate_parents;
			this.terminal_gate = subgr.terminal_gate;
			
		}
		
		Subgraph(Gate term_gate, List<Gate> term_gate_parents){
			//construct w/ auto setting for qs node children
			terminal_gate = new Gate(term_gate);
			terminal_gate_parents = term_gate_parents;
		}
		
		public void addPath(List<Gate> path_to_add){
//			List<Gate> new_path_list = new ArrayList<Gate>();
//			//make copies of gates
//			for(Gate g: path_to_add) {
//				Gate new_g = new Gate(g);
//				new_path_list.add(new_g);
//			}
			paths.add(path_to_add);
		}
		

		public void buildLogicCircuit(){
			
			//calculates subcircuit based on given subgraph paths
			//GATES ALREADY HAVE WIRES ATTACHED FROM PARENT STRUCTURE, JUST USE THOSE IN CONSTRCUTOR 
			//MIGHT NEED TO MAKE A COPY OF GATE BEFORE PUTTING INTO A SUBGRAPH SO THAT 
			// DUPLICATE GATES IN DIFFERENT SUBGRAPHS ARE THEIR OWN OBJECTS IN MEMORY
			
			HashSet<Gate> sublcs_gates = new HashSet<Gate>(); //lc gates set
			HashSet<Wire> sublcs_wires = new HashSet<Wire>(); //lc wires set, contains lots of duplicates b/c wire equality doesn't work
			
			
			if(this.paths.size() == 1){
				//only a single path through list so path is subgraph
				//needs to be cast as ArrayList
				
				for(List<Gate> path:this.paths){
					for(Gate g: path) { //make a gate copy and add
						Gate copy_g = new Gate(g);
						sublcs_gates.add(copy_g);
					}
				}

				for(Gate g:sublcs_gates){
					sublcs_wires.addAll(getGateWires(g));
					}
					
			}
			else{
				//need to find set of gates from list
				
				List<Gate> gate_set = new ArrayList<Gate>(Union(this.paths));
				for(Gate g: gate_set) { //make gate copy
					Gate copy_g = new Gate(g);
					sublcs_gates.add(copy_g);
				}
				
				for(List<Gate> path:this.paths){
					for(Gate g:path){
						sublcs_wires.addAll(getGateWires(g));
						}
					}
				}
			
			//System.out.println("subcircuit gates:\n" + sublcs_gates);
			//System.out.println("subcircuit wires:\n" + sublcs_wires);
			LogicCircuit subcircuit = new LogicCircuit(new ArrayList<Gate>(sublcs_gates), 
				 	new ArrayList<Wire>(sublcs_wires));
			
			this.sub_lc = subcircuit;
		}
		
		private List<Wire> getGateWires(Gate g){
			// assumes <3 wires exist
			List<Wire> wires = new ArrayList<Wire>();
			if(g.Type != Gate.GateType.INPUT) {
				if(g.Outgoing != null) {
					Wire wire = new Wire(g.Outgoing); //make copies
					wires.add(wire);
				}
				if(g.Outgoing.Next != null) {
					Wire wire = new Wire(g.Outgoing.Next);
					wires.add(wire);
					}
				}
			return wires;
		}
		
		private List<Wire> getWires(Gate g){
			 //doesn't make copies but will actually catch all wires if have greater than 2
	        List<Wire> children = new ArrayList<Wire>();

	        if ( (g.Outgoing != null) && (g.Outgoing.To != null)){
	            children.add(g.Outgoing);

	            Wire w = g.Outgoing;
	            while(w.Next != null && w.Next.To != null) {
	                children.add(w.Next);
	                w = w.Next;
	            }
	        }

	        return children;
	    }
	}
	
	
	public PartitionCircuit() {
		//default constructor
	}
	
	public PartitionCircuit(LogicCircuit lc){
		
		parent_lc = lc;
		sub_lcs = new ArrayList<List<LogicCircuit>>();
		this.integrated_circuits = new ArrayList<IntegratedLogicCircuit>();
		this.subgraph_sets = new ArrayList<List<Subgraph>>();
		this.integrated_circuits = new ArrayList<IntegratedLogicCircuit>();
		this.subgraph_sets = new ArrayList<List<Subgraph>>();
		
		
	}
	
	public PartitionCircuit(LogicCircuit lc, Args options){
			
			parent_lc = lc;
			sub_lcs = new ArrayList<List<LogicCircuit>>();
			this.options = options;
			this.integrated_circuits = new ArrayList<IntegratedLogicCircuit>();
			this.subgraph_sets = new ArrayList<List<Subgraph>>();

			
		}
	
	public PartitionCircuit(LogicCircuit lc, Args options, PartitionCircuitAlgorithm algo){
		
		parent_lc = lc;
		sub_lcs = new ArrayList<List<LogicCircuit>>();
		this.options = options;
		this.integrated_circuits = new ArrayList<IntegratedLogicCircuit>();
		this.subgraph_sets = new ArrayList<List<Subgraph>>();
		this.algorithm = algo;

		
	}
	
	public void setIntegratedCircuits() {
		
		System.out.println("Determining integrated lc");
		
		int count = 0;
		for(List<Subgraph> subgraphs:this.subgraph_sets) {
			//System.out.println("terminal gate in pc ic call: " + test_built_subgraphs.get(1).terminal_gate);
			IntegratedLogicCircuit ic = new IntegratedLogicCircuit(this.parent_lc, subgraphs);
			this.integrated_circuits.add(ic);
			System.out.println("Score: " + ic.score);
			
			Graphviz graphviz = new Graphviz(options.get_home(), options.get_output_directory(), options.get_jobID());
			ScriptCommands script_commands = new ScriptCommands(options.get_home(), options.get_output_directory(), options.get_jobID());
			String outfile_name = "IC_WIRING_DIAGRAM_" + count;
			for(Gate combo:ic.qs_gates) {outfile_name += "_" + combo.Name;}
			outfile_name += ".dot";
			graphviz.printGraphvizDotText(ic, outfile_name);
			script_commands.makeDot2Png(outfile_name);
		
		}
		count+=1;
		Collections.sort(this.integrated_circuits);
		System.out.println("Integrated Circuit Scores:\n");
		for(IntegratedLogicCircuit ic:this.integrated_circuits) {System.out.println(ic.score);}
	}

	public void set_subgraph_sets() {
		//System.out.println(this.parent_lc.printGraph());
		subgraph_sets = this.algorithm.partitionCircuit();
	}
	
	
	
	
	//Serves as a scoring function for determining which subgraphs should be kept
	private boolean isLogicCircuitTooBig(List<LogicCircuit> subgraphs, int max){
		//returns true if any subgraph has number of gates larger than max
		boolean bool = false;
		for(LogicCircuit subgr: subgraphs){
			if(subgr.get_logic_gates().size() > max){
				bool = true;
				}
		}
		return bool;
	}
	
	private int pairwiseGateDiff(List<LogicCircuit> sublcs) {
		//calculate sum of all pairwise differences in gate counts
		System.out.println("number of subgraphs: " + sublcs.size());
		System.out.println("calculating pairwise differences");
		int x = 0;
		for(int i=0;i<sublcs.size();++i) {
			for(int j=i+1;j<sublcs.size(); ++j) {
				x+= Math.abs(sublcs.get(i).get_logic_gates().size() - sublcs.get(j).get_logic_gates().size());
			}
		}
		
		System.out.println("Score: " + x);
		
		return x;
	}
	
	private void printLogics(LogicCircuit lc) {
		for(Gate g:lc.get_Gates()) {
			System.out.println(g + " logic: " + g.get_logics());
		}
	}
	private List<List<Integer>> getInputLogics(LogicCircuit lc) {
		List<List<Integer>> input_logics = new ArrayList<List<Integer>>();
		for(Gate in: lc.get_input_gates()) {
			input_logics.add(in.get_logics());
		}
		return input_logics;
	}
	
	private List<List<Integer>> getOutputLogics(LogicCircuit lc) {
		List<List<Integer>> output_logics = new ArrayList<List<Integer>>();
		for(Gate out: lc.get_output_gates()) {
			output_logics.add(out.get_logics());
		}
		return output_logics;
	}
	
	 
	
	

	
	
	
	
}
