package org.cellocad.MIT.dnacompiler;
//jai written on 08/06/2017

import lombok.Getter;
import lombok.Setter;

import java.util.*;


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
		List<Gate> terminal_gate_parents = new ArrayList<Gate>(); //tells you what qs_gate connects to in completed circuit
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
		//List<List<Gate>> gate_combinations = Arrays.asList(test_gate_combos);
		
		//since effectively cutting wires, might work to better explicitly do so
		//for readability
		//'edge' defined as leaving edges of gate, so gate is terminal node in graph
		List<List<Gate>> edge_combinations_to_cut = getValidEdges(2);
		
		//List<List<Gate>> test_combo_func = CombosTest(test_gate_combos, 3);
		
		//System.out.println("COMBOS of: " + "\n");
		//System.out.println(edge_combinations_to_cut);
		
		//System.out.println("combos: \n" + gate_combinations);
		
		List<List<Gate>> all_paths = new ArrayList<List<Gate>>();
		for(Gate g:lc.get_input_gates()){
			//System.out.println("Finding paths to gate: " + g);
			all_paths.addAll(FindAllPaths(lc, end, g, empty_list));
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
		
		//for(List<Gate> combos_list : gate_combinations){
		for(List<Gate> combos_list : edge_combinations_to_cut){
			//choose an edge to cut
			//number of subgraphs = length(combos_list)
			List<Subgraph> emptyGraphsList = new ArrayList<Subgraph>();
			//List<List<Gate>> emptySubgraphsList = new ArrayList<List<Gate>>();
			partition_subgraph_map.put(combos_list, emptyGraphsList);
			
			//CALL METHOD HERE
			HashMap<Gate, Subgraph> subgraph_path_map = SplitSingleEdge(lc, combos_list, all_paths);
			
			//collect subgraphs from subgraph_path_map into single list and add to edge_partitions_dict
			for(Gate g: subgraph_path_map.keySet()){
				//System.out.println("key: " + g);
				//System.out.println("value: " + subgraph_path_map.get(g).paths);
				partition_subgraph_map.get(combos_list).add(subgraph_path_map.get(g));
				}	
		}
		
		//use identified subgraphs to find logic subcircuits
		List<List<LogicCircuit>> all_subgraphs_lc = new ArrayList<>();
		System.out.println("Parent lc: \n" + this.parent_lc.printGraph());
		for(List<Gate >k:partition_subgraph_map.keySet()){
			List<Subgraph> subgraphs = partition_subgraph_map.get(k);
			List<LogicCircuit> subgraphs_lc = new ArrayList<>();
			
			
			//following is just print statements for debugging
			System.out.println("Edge Cut: " + k);
			int sub_count = 1;
			for (Subgraph subgraph:subgraphs){
				subgraph.buildLogicCircuit();
				
				subgraphs_lc.add(subgraph.sub_lc);
				System.out.println("terminal gate parents: " + subgraph.terminal_gate_parents);
				System.out.println("Subgraph sub_lc " + sub_count + ":\n");
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
		
		System.out.println("Number of subgraphs: " + this.sub_lcs.size());
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
			subgraph_path_map.put(qs_gate, new Subgraph(qs_gate, getGateParents(qs_gate))); 
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
						//System.out.println("subpath: " + subpath + "\n");
						subgraph_path_map.get(qs_gate).addPath(subpath);
					}
					else{
						int terminal_ind = LookForward(edges_to_cut, qs_gate, full_path);
						List<Gate> subpath = full_path.subList(0, terminal_ind);
						subgraph_path_map.get(lc.get_output_gates().get(0)).addPath(subpath);
						//System.out.println("full path else: " + full_path);
					}
					
				}
			
			}
		}
		return subgraph_path_map;
	}
	
	
	/**Implements a depth first search with backtracking to find all (simple) paths
	 * between <code>start_gate</code> and <code>end_gate</code>. This method employs tree recursion to
	 * perform the search and, therefore, scales disastrously badly. Initially,
	 * 'path' should be given as an empty <code>List&ltGate&gt</code>. Returns a list of lists 
	 * of gates corresponding to paths.
	 * <p>
	 * Note that the graph is traversed in reverse here and the paths returned are given
	 * in the order in which they are traversed. Thus the paths returned are run in the
	 * opposite direction you would normally read a graph. Therefore, the {@code start_gate}
	 * should be the gate with the lower index (which corresponds to the gate 
	 * further 'downstream' if looking at a graph visually). The indices correspond to the 
	 * indices given in {@code LogicCircuit}. 
	 * 
	 * 
	 * For example, to return all paths between an output and input gate
	 * run with the following arguments:<br>
	 * &nbsp {@code FindAllPaths(lc, output_gate, input_gate, emptyList)} <br>
	 * which returns {@code [output_gate, ..., input_gate]}
	 * 
	 * @param lc a LogicCircuit instance
	 * @param start_gate start gate of path
	 * @param end_gate final gate of path
	 * @param path pass as an empty list w/ type <code>List&ltGate&gt</code> (required for recursive call)
	 * 
	 * @see org.cellocad.MIT.dnacompiler.LogicCircuit
	 */
	public static List<List<Gate>> FindAllPaths(LogicCircuit lc, Gate start_gate, 
											Gate end_gate, List<Gate> path){

		
		//System.out.println("Finding all paths between " + start_gate.Index + " and " + end_gate.Index);
		
		List<List<Gate>> emptyList = Collections.emptyList();
		
		//need to create new Gate in memory to hold paths so that don't overwrite 
		//path Gates from previous recursive calls (as opposed to just doing path.add(start_gate))
		List<Gate> new_path = new ArrayList<Gate>();
		new_path.addAll(path);
		new_path.add(start_gate);
		
		//System.out.println("current path: " + path);
		
		if(start_gate == end_gate){ //found a path, return it so it gets passed up the tree
			List<List<Gate>> found_path = new ArrayList<List<Gate>>();
			found_path.add(new_path);
			return found_path;
		}
		
		List<List<Gate>> paths = new ArrayList<List<Gate>>();
		//System.out.println(System.identityHashCode(path));
		
		List<Gate> children = start_gate.getChildren();
		
		/**
		for(Gate child:children){
			List<Gate> input_gates = lc.get_input_gates();
			if(input_gates.contains(child)){ //hit dead end, backtrack
				return emptyList;
			}
		}
		*/
		
		
		//If ever want to do this with input gates included (i.e. call, lc.get_Gates) then this is stopping condition
		if(children.isEmpty()){
			//System.out.println("here");
			return emptyList;
		}
		
		
		//System.out.println("Current gate: " + start_gate);
		//System.out.println("gate children: " + children);
		
		for(Gate child: children){
			if(!new_path.contains(child)){
				List<List<Gate>> newpaths = FindAllPaths(lc, child, end_gate, new_path);
				for(List<Gate> newpath:newpaths){
					//System.out.println("newpath: " + newpath);
					paths.add(newpath);
				}
			}
		}
		//System.out.println("paths: " + paths); 
		return paths;
	}
	
	
	/**
	 * Iteratively finds all 'k' length combinations of gates in list. Algorithm uses the indexes 
	 * of a list to generate all desired subsets of the list and then grabs the item from the 
	 * original list using those indices. Code was copied (with slight modification) from the 
	 * following answer on stackoverflow:
	 * https://stackoverflow.com/a/29914908/3919605
	 * 
	 * @param arr list of gates to find combinations of
	 * @param k length of subsets to find. the 'k' in "n choose k"
	 * @return list of k length sublists containing all possible combinations of k gates. 
	 */
	public static List<List<Gate>> Combinations(List<Gate> arr, int k){
		List<Gate> input = arr;    // input array

		List<List<Gate>> subsets = new ArrayList<>();

		int[] s = new int[k];                  // here we'll keep indices 
		                                       // pointing to elements in input array

		if (k <= input.size()) {
		    // first index sequence: 0, 1, 2, ...
		    for (int i = 0; (s[i] = i) < k - 1; i++);  
		    	subsets.add(getSubset(input, s));
		    for(;;) {
		        int i;
		        // find position of item that can be incremented
		        for (i = k - 1; i >= 0 && s[i] == input.size() - k + i; i--); 
		        if (i < 0) {
		            break;
		        }
		        s[i]++;                    // increment this item
		        for (++i; i < k; i++) {    // fill up remaining items
		            s[i] = s[i - 1] + 1; 
		        }
		        subsets.add(getSubset(input, s));
		    }
		}
		return subsets;
		
	}
	
	
	/** Helper function for findCombinations(). Generate actual subset by index sequence.
	 * Original code copied from following answer on stackoverflow:
	 * https://stackoverflow.com/a/29914908/3919605
	 */
	private static List<Gate> getSubset(List<Gate> input, int[] subset) {
	    List<Gate> result = new ArrayList<Gate>(subset.length); 
	    for(int i = 0; i < subset.length; i++) 
	    	result.add(input.get(subset[i]));
	    return result;
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
	
	public static List<Integer> getRange(int n){
		// Returns List of integers ranging from 1, n
		List<Integer> range = new ArrayList<>();
		for(int i=1; i<=n ; i++){
			range.add(i);
		}
		return range;
	}
	
	private static HashSet<Gate> Union(HashSet<List<Gate>> list_of_gates_lists){
		//function is basically pointless at this point I think b/c it's already a hashset
		//coming in...
		HashSet<Gate> union = new HashSet<Gate>();
		for(List<Gate> gates_list:list_of_gates_lists){
			for(Gate g:gates_list){
				union.add(g);
			}
		}
		return union;
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
	private static Integer LookForward(List<Gate> terminal_gates, Gate current_gate, List<Gate> path){

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
	
	//Finds 'parents' of given gate, all gates in LC that contain 'gate' as a child
	public List<Gate> getGateParents(Gate gate){
		List<Gate> parents = new ArrayList<Gate>();
		for(Gate g:this.parent_lc.get_Gates()){
			if(g.getChildren().contains(gate)){
				parents.add(g);
			}
		}
		return parents;
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
