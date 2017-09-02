package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import org.cellocad.MIT.dnacompiler.Gate.GateType;

public class IntegratedLogicCircuit {
	
	LogicCircuit original_lc;
	List<PartitionCircuit.Subgraph> subgraphs;
	public List<LogicCircuit> sub_lcs = new ArrayList<LogicCircuit>();
	LogicCircuit merged_lc;
	public HashMap<Gate, List<Gate>> terminal_gate_map = new HashMap<Gate, List<Gate>>(); //maps qs gates to parents in other circuit
	public List<Gate> terminal_parents = new ArrayList<Gate>(); //list of terminal gate parents
	public HashSet<Gate> input_gates = new HashSet<Gate>();
	public HashSet<Gate> output_gates = new HashSet<Gate>();
	public List<Gate> qs_gates = new ArrayList<Gate>();
	
	public IntegratedLogicCircuit(LogicCircuit parent_lc, List<PartitionCircuit.Subgraph> subgraphs) {
		this.original_lc = parent_lc;
		this.subgraphs = subgraphs;
		
		for(PartitionCircuit.Subgraph subgraph:subgraphs) {
			System.out.println("this printer: " + subgraph.terminal_gate.getChildren());
			if(subgraph.terminal_gate_parents.size() > 0) {
				System.out.println("term parents " + subgraph.terminal_gate_parents); //print successfully
				this.terminal_parents.addAll(subgraph.terminal_gate_parents);
			
			if(subgraph.terminal_gate.Index != -1) {
				System.out.println("terminal gate: " + subgraph.terminal_gate);
				this.terminal_gate_map.put(subgraph.terminal_gate, subgraph.terminal_gate_parents);
			}
			else {
				System.out.println("else terminal gate: " + subgraph.terminal_gate);
				//this.terminal_gate_map.put(subgraph.terminal_gate, new ArrayList<Gate>());
					
				}
			}
		}
		
	
		for(PartitionCircuit.Subgraph subgraph:subgraphs) {
		
			this.input_gates.addAll(subgraph.sub_lc.get_input_gates());
			this.output_gates.addAll(subgraph.sub_lc.get_output_gates());
			
			//null check, should be able to do better now that have overridden equals()
			//also I think the gate type is what is really 'null' here 
			if(subgraph.terminal_gate.Index != -1) {
				this.qs_gates.add(subgraph.terminal_gate);
			}
		}
		
		set_sub_lcs(this.subgraphs);
		verifyLogicIdentical(); //hard to debug w/ this on
	}
	
	
	public void set_sub_lcs(List<PartitionCircuit.Subgraph> subgraphs) {
		
		List<LogicCircuit> subgraph_lcs_dirty = new ArrayList<LogicCircuit>();
		
		for(PartitionCircuit.Subgraph subgraph:subgraphs) {
			subgraph_lcs_dirty.add(subgraph.sub_lc);
//			for(Gate g: subgraph.sub_lc.get_Gates()) {
//				System.out.println("gate, outgoing wire: " + g + " , "+ g.Outgoing);
//				//System.out.println("gate, outgoing wire: " + g + " , "+ g.Outgoing.Next);
//				
//			}
		//	System.out.println("subgraph wires");
		//	System.out.println(subgraph.sub_lc.get_Wires());
			
		}
		
		
		
		//I think the reason this is working and I don't need to explicitly reset 
		//the QS parent logics is b/c gates already aware who their children are via outgoing wire
		//and I don't touch child logics or visitation state. In other words, 
		//even though qs gate parent isn't in the same circuit, it still knows
		//its child is a qs gate b/c I made copies of gate objects and didn't reset
		//the children anywhere here. Thus, when the logic recursion occurs, since it
		//uses the gate children (and by extension, wires) without checking if the 
		//gate is actually in the logic circuit, the logic recursion succeeds. 
		
		//reset old logics and gate unvisited values for all gates
				//except input and qs_gate parents, which should maintain logic
				//and be marked as visited so recursion doesn't fail
		List<LogicCircuit> reset_lcs = new ArrayList<LogicCircuit>();

		for(LogicCircuit dirty_lc: subgraph_lcs_dirty) {
			for(Gate g:dirty_lc.get_Gates()) {
				if(this.terminal_parents.contains(g)) {
					//this condition line doesn't appear to do anything
					//System.out.println("skipping gate: " + g);
				}
				else if(g.Type.equals(Gate.GateType.INPUT)) { //not resetting input gate logic allows order to be maintained 
					g.set_unvisited(false);
				}
				else {
					g.set_unvisited(true);
					clearGateLogic(g);
				}
			}
			reset_lcs.add(dirty_lc);
		}
		
		//resimulate logics
		for(LogicCircuit reset_lc:reset_lcs) {
			//System.out.println(reset_lc.printGraph());
			Evaluate.simulateLogic(reset_lc);
			//System.out.println("successfully reevaluated logic");
			//System.out.println(reset_lc.printGraph());
			sub_lcs.add(reset_lc);
		}
	}

	
	private void setAllGatesUnvisited(LogicCircuit lc) {
		for(Gate g: lc.get_Gates()) {
			g.set_unvisited(false);
		}
	}
	
	private void clearAllLogics(LogicCircuit lc) {
		for(Gate g:lc.get_Gates()) {
			ArrayList<Integer> zero_logics = new ArrayList<Integer>(Collections.nCopies(g.get_logics().size(), 0));
			g.set_logics(zero_logics);
		}
	}
	
	private void clearGateLogic(Gate g) {
		ArrayList<Integer> zero_logics = new ArrayList<Integer>(Collections.nCopies(g.get_logics().size(), 0));
		g.set_logics(zero_logics);
		}
	
	private List<Gate> dedupGatesByName(List<Gate> gates_list){
		List<String> seen_names = new ArrayList<String>();
		List<Gate> dedup = new ArrayList<Gate>();
		
		for(Gate g:gates_list) {
			System.out.println("on gate: " + g);
			System.out.println("gate hash: " + g.hashCode());
			if(!seen_names.contains(g.Name)) {
				dedup.add(g);
				seen_names.add(g.Name);
			}
		}
		return dedup;
	}
	
	private void verifyLogicIdentical() {
		List<Gate> original_inputs = this.original_lc.get_input_gates();
		List<Gate> original_outputs = this.original_lc.get_output_gates();
		HashSet<Gate> subcircuit_inputs = this.input_gates; //new ArrayList<Gate>();
		HashSet<Gate> subcircuit_outputs = this.output_gates; //new ArrayList<Gate>();
		
//		for(LogicCircuit sub_lc:this.sub_lcs) {
//			subcircuit_inputs.addAll(sub_lc.get_input_gates());
//			subcircuit_outputs.addAll(sub_lc.get_output_gates());
//		}
		
		if(original_inputs.size() != subcircuit_inputs.size()) {
			throw new IllegalStateException("Parent and integrated circuit don't have same number of inputs.");
		}
		if(original_outputs.size() != subcircuit_outputs.size()) {
			throw new IllegalStateException("Parent and integrated circuit don't have same number of output gates.");
		}
		
		for(Gate lc_out: original_outputs) {
			for(Gate sublc_out:subcircuit_outputs) {
				if(lc_out.Name.equals(sublc_out.Name)){
					if(!lc_out.get_logics().equals(sublc_out.get_logics())) {
						 throw new IllegalStateException("Outputs logics not identical. Circuit not functionally identical.");
					}
				}
			}
		}
		
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
		
		private double pairwiseGateDiff() {
			//calculate sum of all pairwise differences in gate counts
			System.out.println("number of subgraphs: " + this.sub_lcs.size());
			System.out.println("calculating pairwise differences");
			double x = 0;
			for(int i=0;i<this.sub_lcs.size();++i) {
				for(int j=i+1;j<this.sub_lcs.size(); ++j) {
					x+= Math.abs(this.sub_lcs.get(i).get_logic_gates().size() - this.sub_lcs.get(j).get_logic_gates().size());
				}
			}
			
			//System.out.println("Score: " + x);
			
			return x;
		}



}

	