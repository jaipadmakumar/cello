package org.cellocad.MIT.dnacompiler;
//jai written on 08/06/2017

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import static org.cellocad.MIT.dnacompiler.PartitionCircuitUtil.*;


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

	@Getter @Setter private List<List<LogicCircuit>> sub_lcs; //list of sub LogicCircuits
	@Getter @Setter private LogicCircuit parent_lc; 
	
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
			
		}
		
		Subgraph(Gate term_gate, List<Gate> term_gate_parents){
			//construct w/ auto setting for qs node children
			terminal_gate = term_gate;
			terminal_gate_parents = term_gate_parents;
		}
		
		private void addPath(List<Gate> path_to_add){
			paths.add(path_to_add);
		}
		
		private void buildLogicCircuit(){
			
			//calculates subcircuit based on given subgraph paths
			HashSet<Gate> sublcs_gates = new HashSet<Gate>(); //lc gates set
			HashSet<Wire> sublcs_wires = new HashSet<Wire>(); //lc wires set
			int wire_count = 0;
			
			if(this.paths.size() == 1){
				//only a single path through list so path is subgraph
				//needs to be cast as ArrayList
				//ArrayList<Gate> sublcs_gates = new ArrayList<Gate>(subgraph_paths.get(0));
				
				for(List<Gate> path:this.paths){
					sublcs_gates.addAll(path);
				}
				//sublcs_gates.addAll(this.paths.get(0));
				for(Gate g:sublcs_gates){
					//int gate_ind = g.Index;
					//List<Integer> child_inds = new ArrayList<Integer>();
					for(Gate child:g.getChildren()){
						//child_inds.add(child.Index);
						
						Wire wire = new Wire(wire_count, g, child);
						sublcs_wires.add(wire);
						wire_count +=1;
					}
					
				}
			}
			else{
				//need to find set of gates from list
				
				List<Gate> gate_set = new ArrayList<Gate>(Union(this.paths));
				sublcs_gates.addAll(gate_set);
				
				for(List<Gate> path:this.paths){
					for(Gate g:path){
						for(Gate child:g.getChildren()){
							//child_inds.add(child.Index);
							
							Wire wire = new Wire(wire_count, g, child);
							sublcs_wires.add(wire);
							wire_count +=1;
						}
					}
				}
			}
			LogicCircuit subcircuit = new LogicCircuit(new ArrayList<Gate>(sublcs_gates), 
				 	new ArrayList<Wire>(sublcs_wires));
			this.sub_lc = subcircuit;
		}
	}
	
	
	public PartitionCircuit() {
		//default constructor
	}
	
	public PartitionCircuit(LogicCircuit lc){
		
		parent_lc = lc;
		sub_lcs = new ArrayList<List<LogicCircuit>>();
		
	}

	/**
	 * Given an {@code LogicCircuit}, finds the set of all partitions determined by
	 * making all possible combinations of up to k edges. For example, if k=2, the 
	 * algorithm will find all possible combinations of 2 edges (aka wires) + all 
	 * single edges, and cuts the {@code LogicCircuit} into subcircuits based on 
	 * those. Thus, each edge (or edge pair) denotes a single set of subcircuits
	 * which, taken together, have the same logic as the original logic circuit.
	 * 
	 * @param lc
	 * @return
	 */
	
	//try testing w/ edge cuts:
	//[6 NOT g4, 11 NOT g9]
	
	public void partitionCircuit(LogicCircuit lc){
		
		List<Gate> empty_list = new ArrayList<Gate>();
		List<Gate> lc_gates = lc.get_logic_gates();
		//List<Gate> lc_gates = lc.get_Gates();
		//System.out.println("logic gates \n" + lc_gates);
		//Gate start = lc.get_input_gates().get(1);//lc_gates.get(0);
		Gate end = lc.get_output_gates().get(0); //single output gate only currently
		
		//List<List<Gate>> test_paths = FindAllPaths(lc, end, start, empty_list );
		//System.out.println("Start: " + start + " End: " + end);
		//System.out.println("all paths: " + test_paths);
		
		//List<Gate> test_gate_combos = Arrays.asList(lc.get_logic_gates().get(1), lc.get_logic_gates().get(6),  lc.get_logic_gates().get(3), lc.get_logic_gates().get(2));
		//List<Gate> test_gate_combos = Arrays.asList(lc_gates.get(3));
		//List<Gate> test_gate_combos = Arrays.asList(lc_gates.get(2), lc_gates.get(8));
		List<Gate> test_gate_combos = Arrays.asList(lc_gates.get(10), lc_gates.get(11));
		List<List<Gate>> gate_combinations = Arrays.asList(test_gate_combos);
		
		//since effectively cutting wires, might work to better explicitly do so
		//for readability
		//'edge' defined as leaving edges of gate, so gate is terminal node in graph
		List<List<Gate>> edge_combinations_to_cut = getValidEdges(2);
		
		//List<List<Gate>> test_combo_func = CombosTest(test_gate_combos, 3);
		
		//System.out.println("COMBOS of: " + "\n");
		//System.out.println(edge_combinations_to_cut);
		
		//System.out.println("combos: \n" + gate_combinations);
		
		List<List<Gate>> all_paths = new ArrayList<List<Gate>>();
		for(Gate input_g:lc.get_input_gates()){
			for(Gate output_g:lc.get_output_gates())
				//System.out.println("Finding paths to gate: " + g);
				all_paths.addAll(DepthFirstSearch.findAllPaths(lc, output_g, input_g, empty_list));
		}
		
		//general algorithm:
		// 1. find all paths
		// 2. any path that hits a qs node goes into separate graph --> all nodes to right of qs node, inclusive
		// 3. any remaining paths go in dump graph containing rest of nodes
		//Circuit partitioning doesn't change topology of circuit itself so don't need to keep track of wires in this process
		// (since wires aren't aware of anything, can just use gates to determine DAG structure)
		
		//TODO test case where outdegree = 2
		//list of lists of list<gate> = 
		//edge_cut: [[subgraph1path1, subgraph1path2],[subgraph2path1, subgraph2path2]]
		
		HashMap<List<Gate>, List<Subgraph>> partition_subgraph_map = new HashMap<List<Gate>, List<Subgraph>>();
		
		for(List<Gate> combos_list : gate_combinations){
		//for(List<Gate> combos_list : edge_combinations_to_cut){
			//choose an edge to cut
			//number of subgraphs = length(combos_list)
			List<Subgraph> emptyGraphsList = new ArrayList<Subgraph>();
			//List<List<Gate>> emptySubgraphsList = new ArrayList<List<Gate>>();
			partition_subgraph_map.put(combos_list, emptyGraphsList);
			
			//CALL METHOD HERE
			HashMap<Gate, Subgraph> subgraph_path_map = SplitSingleEdge(lc, combos_list, all_paths);
			//System.out.println("Subgraph path map: " + subgraph_path_map);
			//collect subgraphs from subgraph_path_map into single list and add to edge_partitions_dict
			for(Gate g: subgraph_path_map.keySet()){
				System.out.println("key: " + g);
				System.out.println("value: " + subgraph_path_map.get(g).paths);
				partition_subgraph_map.get(combos_list).add(subgraph_path_map.get(g));
				}	
		}
		
		//use identified subgraphs to find logic subcircuits
		List<List<LogicCircuit>> all_subgraphs_lc = new ArrayList<>();
		System.out.println("Parent lc: \n" + this.parent_lc.printGraph());
		for(List<Gate >k:partition_subgraph_map.keySet()){
			List<Subgraph> subgraphs = partition_subgraph_map.get(k);
			List<LogicCircuit> subgraphs_lc = new ArrayList<>();
			
			
			//following is just print statements for debugging except for one line that's important
			System.out.println("Edge Cut: " + k);
			int sub_count = 1;
			for (Subgraph subgraph:subgraphs){
				subgraph.buildLogicCircuit();
				
				subgraphs_lc.add(subgraph.sub_lc);
				//System.out.println("output gates" + this.parent_lc.get_output_gates());
				System.out.println("output gate(s): " + subgraph.sub_lc.get_output_gates());
				System.out.println("terminal gate: " + subgraph.terminal_gate);
				System.out.println("terminal gate parents: " + subgraph.terminal_gate_parents);
				System.out.println("Subgraph sub_lc " + sub_count + ":\n");
				System.out.println("number of gates in sublc: " + subgraph.sub_lc.get_Gates().size());
				System.out.println(subgraph.sub_lc.printGraph());
				sub_count +=1;
			}
			
			all_subgraphs_lc.add(subgraphs_lc);
			

		}
		
		for(List<LogicCircuit> subgraphs:all_subgraphs_lc){
			if(!isLogicCircuitTooBig(subgraphs, 5)){
				this.sub_lcs.add(subgraphs); //populate PartitionCircuit object w/ identified subgraphs

			}
		}
		
		System.out.println("Number of subgraphs matching criteria: " + this.sub_lcs.size());
		System.out.println("Total number of subgraphs found: " + partition_subgraph_map.keySet().size() );
		
	}
	
	
	//Helper method for partitionCircuit()
	//Partitions a graph based on a single edge and returns a subgraph_path_map
	public HashMap<Gate, Subgraph> SplitSingleEdge(LogicCircuit lc, List<Gate> edges_to_cut, List<List<Gate>> graph_paths){
		
		//hashmap where Gate key = subgraph w/ terminal node = Gate
		//then data validation aka deduping occurs w/in Subgraph class
		HashMap<Gate, Subgraph> subgraph_path_map = new HashMap<Gate, Subgraph>();
		//int subgraph_num = 0;
		
		//initialize w/ keys = terminal_gates of subgraphs
		//and Subgraph objects w/ terminal gate and terminal gate parents set
		
		for(Gate qs_gate: edges_to_cut){
			subgraph_path_map.put(qs_gate, new Subgraph(qs_gate, getGateParents(this.parent_lc, qs_gate))); 
		}
		subgraph_path_map.put(lc.get_output_gates().get(0), new Subgraph());
					
		for(Gate qs_gate: edges_to_cut){
			//have terminal node of current subgraph
			
			List<Gate> terminal_gates_excluding_current = new ArrayList<Gate>();
			for(Gate g:edges_to_cut){
				if(g != qs_gate){terminal_gates_excluding_current.add(g);}
			}	
			
			if(lc.get_input_gates().contains(qs_gate)){
				//cutting at input gates is pointless
				//TODO also don't need include output gate b/c has no incoming edge
				continue;
			}
			else{
				//now build a subgraph terminating in qs node in terms of paths
				//start with either an input gate or another quorum sensing gate
		
				for(List<Gate> full_path:graph_paths){
					if(full_path.contains(qs_gate)){
						//slice path into subpath that either goes from qs_gate to input
						//or qs_gate to other quorum sensing gate that may be in path
						
						int qs_gate_ind = full_path.indexOf(qs_gate);
						int terminal_ind = LookForward(terminal_gates_excluding_current, qs_gate, full_path);
		
						//System.out.println("qs gate index: " + qs_gate_ind);
						//System.out.println("full path: " + full_path);
						List<Gate> subpath = full_path.subList(qs_gate_ind, terminal_ind);
						System.out.println("subpath: " + subpath + "\n");
						subgraph_path_map.get(qs_gate).addPath(subpath);
					}
					else{
						int terminal_ind = LookForward(edges_to_cut, qs_gate, full_path);
						List<Gate> subpath = full_path.subList(0, terminal_ind);
						subgraph_path_map.get(lc.get_output_gates().get(0)).addPath(subpath);
						System.out.println("full path else: " + full_path);
					}
					
				}
			
			}
		}
		return subgraph_path_map;
	}
	

//Gets all edges to cut, up to kth combinations of edges inclusive
	private List<List<Gate>> getValidEdges(int k){
		List<List<Gate>> combos_list = new ArrayList<List<Gate>>();
		List<Integer> k_range = getRange(k);
		
		for(int i:k_range){
			for(List<Gate> combos:Combinations(this.parent_lc.get_logic_gates(), i)){
				combos_list.add(combos);
			}
		}
		return combos_list;
	}
	
	
	
	
	/**
	 * Looks forward in {@code path} from {@code current_gate} to the first gate in {@code terminal_gates}
	 * encountered and returns the index in {@code path} of that gate. If no gate in {@code terminal_gates}
	 * was found, returns the length of the {@code path}. *Note again that when visualized on a graph, 
	 * this is actually 'looking back' in a path but since paths here run from output --> input, it is 
	 * implemented as lookforward. 
	 * 
	 * @param terminal_gates list of terminal gates that should be at the end of a path. "Look forward" to this gate.
	 * @param current_gate gate to start looking "from", in other words, the start of a path
	 * @param path path to search in
	 * @return index in path of first terminal gate found (assuming it occurs after start_gate) or size of 
	 * path if no terminal gates were in the path. 
	 */
	private Integer LookForward(List<Gate> terminal_gates, Gate current_gate, List<Gate> path){

		Integer index = path.size();
		Integer current_gate_ind = path.indexOf(current_gate);
		
		for(Gate g: terminal_gates){
			if(path.contains(g)){
				//want to stop at first index found
				int ind = path.indexOf(g);
				if(ind < index && ind > current_gate_ind){
					index = ind;
				}
			}
		}
		
		return index;
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
	

}
