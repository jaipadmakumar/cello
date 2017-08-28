package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import org.cellocad.MIT.dnacompiler.Gate.GateType;

public class IntegratedLogicCircuit {
	
	LogicCircuit original_lc;
	List<PartitionCircuit.Subgraph> subgraphs;
	List<LogicCircuit> sub_lcs = new ArrayList<LogicCircuit>();
	LogicCircuit merged_lc;
	//HashMap<Gate, List<Gate>> terminal_gate_map; //maps qs gates to parents in other circuit
	List<Gate> terminal_parents = new ArrayList<Gate>(); //list of terminal gate parents
	
	public IntegratedLogicCircuit(LogicCircuit parent_lc, List<PartitionCircuit.Subgraph> subgraphs) {
		this.original_lc = parent_lc;
		this.subgraphs = subgraphs;
		
		for(PartitionCircuit.Subgraph subgraph:subgraphs) {
			//this.terminal_gate_map.put(subgraph.terminal_gate, subgraph.terminal_gate_parents);
			System.out.println(subgraph.terminal_gate_parents);
			if(subgraph.terminal_gate_parents.size() > 0) {
				this.terminal_parents.addAll(subgraph.terminal_gate_parents);
				//System.out.println(subgraph.terminal_gate_parents);
				//System.out.println("len > 0");
			}
		}
		
		set_sub_lcs(this.subgraphs);
		verifyLogicIdentical();
	}
	
	//BUG potentially comes from fact that terminal gates don't include all output gates for 
	// that graph and when it is an output gate is set to null
	// aka there can be MULTIPLE terminal gates
	
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
		
		//reset old logics and gate unvisited values for all gates
		//except input and qs_gate parents, which should maintain logic
		//and be marked as visited so recursion doesn't fail
		
		//I think the resason this is working and I don't need to explicitly reset 
		//the QS parent logics is b/c gates already aware who their children are
		//and I don't touch child logics or visitation state. In other words, 
		//even though qs gate parent isn't in the same circuit, it still knows
		//its child is a qs gate b/c I made copies of gate objects and didn't reset
		//the children anywhere here. Thus, when the logic recursion occurs, since it
		//uses the gate children (and by extension, wires) without checking if the 
		//gate is actually in the logic circuit, the logic recursion succeeds. 
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
			System.out.println(reset_lc.printGraph());
			Evaluate.simulateLogic(reset_lc);
			System.out.println("successfully reevaluated logic");
			System.out.println(reset_lc.printGraph());
			sub_lcs.add(reset_lc);
		}
	}
//		
//
//        /**
//         *  propagate logic through gates
//         */
//        //initialize logic to all zeroes
//        //Integer nrows = subcircuit.get_input_gates().get(0).get_logics().size();
//        Integer nrows = 16;
//        for (Gate g : subcircuit.get_Gates()) {
//            if (g.get_logics().isEmpty()) {
//                ArrayList<Integer> logics = new ArrayList<>();
//                for (int i = 0; i < nrows; ++i) {
//                    logics.add(0);
//                }
//                g.set_logics(logics);
//            }
//        }
//		//Evaluate.simulateLogic(subcircuit);
//	}
	
	
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
	
	private void verifyLogicIdentical() {
		List<Gate> original_inputs = this.original_lc.get_input_gates();
		List<Gate> original_outputs = this.original_lc.get_output_gates();
		List<Gate> subcircuit_inputs = new ArrayList<Gate>();
		List<Gate> subcircuit_outputs = new ArrayList<Gate>();
		
		for(LogicCircuit sub_lc:this.sub_lcs) {
			subcircuit_inputs.addAll(sub_lc.get_input_gates());
			subcircuit_outputs.addAll(sub_lc.get_output_gates());
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

}

	