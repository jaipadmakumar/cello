package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import static org.cellocad.MIT.dnacompiler.PartitionCircuitUtil.*;
import org.cellocad.MIT.dnacompiler.PartitionCircuit.*;



/**
 * Given an {@code LogicCircuit}, finds the set of all partitions determined by
 * making all possible combinations of up to k edges. For example, if k=2, the 
 * algorithm will find all possible combinations of 2 edges (aka wires) + all 
 * single edges, and cuts the {@code LogicCircuit} into subcircuits based on 
 * those. Thus, each edge (or edge pair) denotes a single set of subcircuits
 * which, taken together, have the same logic as the original logic circuit.
 * 
 * General algorithm:
 * 1. find all paths through graphs (all paths from all sources to all sinks)
 * 2. any path that hits a qs node goes into separate graph --> all nodes to right of qs node, inclusive
 * 3. any remaining paths go in dump graph containing rest of nodes and output nodes
 * 
 * Therefore, each graph either as 1 qs node or all output nodes.
 * 
 * @param lc
 * @return
 */


//Circuit partitioning doesn't change topology of circuit itself so don't need to keep track of wires in this process
//since wires already connected to gates by Gate.Outgoing property

//TODO test case where outdegree = 2 --> structural_iphone2 cutting gate g9 has outdegree = 3 --> works now
//TODO test case where outdegree > 1 AND hit second qs node on path
//list of lists of list<gate> = 
//edge_cut: [[subgraph1path1, subgraph1path2],[subgraph2path1, subgraph2path2]]
//try testing w/ edge cuts:
//[6 NOT g4, 11 NOT g9]
//for iphone blue: [9 NOR g8, 7 NOR g6]

public class PartitionCircuitPathPartition extends PartitionCircuit implements PartitionCircuitAlgorithm{
	
	//public List<IntegratedLogicCircuit> integrated_circuits; 
	//public LogicCircuit parent_lc;
	LogicCircuit lc;
	
	
	PartitionCircuitPathPartition(){
	}
	
	PartitionCircuitPathPartition(LogicCircuit lc_to_split){
		//super(lc);
		this.lc = lc_to_split;
	}
	
	
	@Override
	public List<List<Subgraph>> partitionCircuit(){
		
		List<List<Subgraph>> subgraph_partitions = new ArrayList<List<Subgraph>>();
//		//System.out.println(getParent_lc().printGraph());
//		LogicCircuit lc = getParent_lc();
		
		List<Gate> empty_list = new ArrayList<Gate>();
		List<Gate> lc_gates = lc.get_logic_gates();
		//List<Gate> lc_gates = lc.get_Gates();
		
		List<Gate> test_gate_combos = Arrays.asList(lc_gates.get(7), lc_gates.get(5)); //iphone blue test
		//List<Gate> test_gate_combos = Arrays.asList(lc_gates.get(8));
		List<List<Gate>> gate_combinations = Arrays.asList(test_gate_combos);
		
		
		//'edge' defined as leaving edges of gate, so gate is terminal node in graph
		List<List<Gate>> edge_combinations_to_cut = getValidEdges(2);
		//System.out.println("Total number of edge partitions: " + edge_combinations_to_cut.size());
				
		List<List<Gate>> all_paths = new ArrayList<List<Gate>>();
		for(Gate input_g:lc.get_input_gates()){
			for(Gate output_g:lc.get_output_gates()) {
				//System.out.println("Finding paths to gate: " + g);
				all_paths.addAll(DepthFirstSearch.findAllPaths(lc, output_g, input_g, empty_list));
			}
		}
		
		//System.out.println("all paths:\n");
		//for(List<Gate> p:all_paths) {System.out.println(p);}
		
		
		
		HashMap<List<Gate>, List<Subgraph>> partition_subgraph_map = new HashMap<List<Gate>, List<Subgraph>>();
		
		//for(List<Gate> combos_list : gate_combinations){
		for(List<Gate> combos_list : edge_combinations_to_cut){
			//choose an edge to cut
			//number of subgraphs = length(combos_list)
			List<Subgraph> emptyGraphsList = new ArrayList<Subgraph>();
			partition_subgraph_map.put(combos_list, emptyGraphsList);
			
			//CALL METHOD HERE
			HashMap<Gate, Subgraph> subgraph_path_map = SplitPathEdges(lc, combos_list, all_paths);
			//System.out.println("Subgraph path map: " + subgraph_path_map);
			//collect subgraphs from subgraph_path_map into single list and add to edge_partitions_dict
			for(Gate g: subgraph_path_map.keySet()){
				//System.out.println("key: " + g);
				//System.out.println("value: " + subgraph_path_map.get(g).paths);
				partition_subgraph_map.get(combos_list).add(subgraph_path_map.get(g));
			}	
		}
		
		//use identified subgraphs to find logic subcircuits
		List<List<LogicCircuit>> all_subgraphs_lc = new ArrayList<>();
		//System.out.println("Parent lc: \n" + this.parent_lc.printGraph());
		
		for(List<Gate >k:partition_subgraph_map.keySet()){
			List<Subgraph> subgraphs = partition_subgraph_map.get(k);
			List<LogicCircuit> subgraphs_lc = new ArrayList<>();

			System.out.println("Edge Cut: " + k);
			int sub_count = 1;
			List<Subgraph> subgraph_set = new ArrayList<Subgraph>();
			for (Subgraph subgraph:subgraphs){
				subgraph.buildLogicCircuit();
				subgraphs_lc.add(subgraph.sub_lc);
				
				Subgraph new_subgraph = new Subgraph(subgraph);
				subgraph_set.add(new_subgraph); 
			
				sub_count +=1;
			}
			
			subgraph_partitions.add(subgraph_set);
			all_subgraphs_lc.add(subgraphs_lc);			

		}
		
		//System.out.println("Number of subgraphs matching criteria: " + this.sub_lcs.size());
		System.out.println("Total number of subgraphs found: " + partition_subgraph_map.keySet().size() );
		
		return subgraph_partitions;
	
	
		
	}
	
	
	//Helper method for partitionCircuit()
	//Partitions a graph based on a single edge and returns a subgraph_path_map
	public HashMap<Gate, Subgraph> SplitPathEdges(LogicCircuit lc, List<Gate> edges_to_cut, List<List<Gate>> graph_paths){
		
		//hashmap where Gate key = subgraph w/ terminal node = Gate
		//then data validation aka deduping occurs w/in Subgraph class
		HashMap<Gate, Subgraph> subgraph_path_map = new HashMap<Gate, Subgraph>();
		
		//initialize w/ keys = terminal_gates of subgraphs
		//and Subgraph objects w/ terminal gate and terminal gate parents set
		
		for(Gate qs_gate: edges_to_cut){
			subgraph_path_map.put(qs_gate, new Subgraph(qs_gate, getGateParents(lc, qs_gate))); 
		}
		subgraph_path_map.put(lc.get_output_gates().get(0), new Subgraph());
		
		
		//THIS IS ONLY TRUE FOR CIRCUITS W/ A SINGLE OUTPUT TECHNICALLY
		//may need to explicitly account for other terminal gates
		// but output gates should only be at end of path so shouldn't matter
		//b/c no actual slicing along w/ that gate is necessary? --> pretty sure it doesn't matter after 
		//thinking more but double check in more detail
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
				//start with either an input gate or another quorum sensing gate parent
				
		
				for(List<Gate> full_path:graph_paths){
					System.out.println("full path:\n" + full_path);
					if(full_path.contains(qs_gate)){
						//slice path into subpath that either goes from qs_gate to input
						//or qs_gate to other quorum sensing gate that may be in path
						
						int qs_gate_ind = full_path.indexOf(qs_gate);
						int terminal_ind = LookForward(terminal_gates_excluding_current, qs_gate, full_path);
		
						//System.out.println("qs gate index: " + qs_gate_ind);
						//System.out.println("full path: " + full_path);
						List<Gate> subpath = full_path.subList(qs_gate_ind, terminal_ind);
						List<Gate> subpath_backend = full_path.subList(0, qs_gate_ind); //if node has outdegree>1, won't encounter those remaining nodes in any other way
						System.out.println("path contains qs gate, subpath is:\n" + subpath);
						System.out.println("backend of above path is: " + subpath_backend);
						//System.out.println("subpath: " + subpath + "\n");
						
						subgraph_path_map.get(qs_gate).addPath(subpath);
						subgraph_path_map.get(lc.get_output_gates().get(0)).addPath(subpath_backend);
					}
					else{
						//BUG: THIS PART LOOKS WEIRD --> IF qs_gate isn't on the path shouldn't need to do 
						//any splitting. that being said, need to stop at ANY qs gate even if it's not the one 
						//currently iterating on so maybe need to check if ANY of the edge_cut gates are 
						//on the path
						
						int terminal_ind = LookForward(edges_to_cut, qs_gate, full_path);
						List<Gate> subpath = full_path.subList(0, terminal_ind);
						//keys only go to single output but multiple outputs are possible
						//shouldn't matter b/c complete paths run to same graph and all outputs can only be in one graph (ASSUMPTION)
						
						System.out.println("path does not contain qs gate, subpath is:\n" + subpath);
						subgraph_path_map.get(lc.get_output_gates().get(0)).addPath(subpath);
						//subgraph_path_map.get(lc.get_output_gates().get(0)).addPath(full_path);
						//System.out.println("full path else: " + subpath);
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
				for(List<Gate> combos:Combinations(this.lc.get_logic_gates(), i)){
					combos_list.add(combos);
					//System.out.println("gate combo: " + combos);
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
}
