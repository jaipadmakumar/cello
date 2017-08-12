package org.cellocad.MIT.dnacompiler;
//jai written on 08/06/2017

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PartitionCircuit {
	
	//Instance Variables
	
	@Getter @Setter private List<List<LogicCircuit>> sub_lcs = new ArrayList<List<LogicCircuit>>(); //list of sub LogicCircuits
	@Getter @Setter private LogicCircuit parent_lc;
	
	public class Subgraph{
		List<Gate> gates = new ArrayList<Gate>();
		List<List<Gate>> paths = new ArrayList<List<Gate>>(); //should be a list of all paths through graph
		LogicCircuit sub_lc;
		
		Subgraph(){
		//default constructor
		}
		
		Subgraph(List<List<Gate>> subgraph_paths){
			//construct subgraph in terms of paths
			paths = subgraph_paths;
		}
		
//		Subgraph(List<Gate> subgraph_path){
//			paths.add(subgraph_path);
//		}
//		
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
				sublcs_gates.addAll(this.paths.get(0));
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
	
	
	public PartitionCircuit(LogicCircuit lc){
		//default constructor
		parent_lc = lc;
		System.out.println("lc wires: " + lc.get_Wires());
		
	}
	//create a nested helper class to hold subgraph paths
	//maybe even create a nested class to hold subgraph data itself
	
	
	
	public List<List<Integer>> partitionCircuit(LogicCircuit lc){
		
		/* Identifies edges to cut returns gate indices in each subgraph
		 * as an arraylist
		 */
		
		//ArrayList<Integer> t = new ArrayList<Integer>();
		List<List<Integer>> subgraph_indices = new ArrayList<List<Integer>>();
		
		List<Gate> empty_list = new ArrayList<Gate>();
		//List<Gate> lc_gates = lc.get_logic_gates();
		//List<Gate> lc_gates = lc.get_Gates();
		//System.out.println("logic gates \n" + lc_gates);
		//Gate start = lc.get_input_gates().get(1);//lc_gates.get(0);
		Gate end = lc.get_output_gates().get(0); //single output gate only currently
		
		//List<List<Gate>> test_paths = FindAllPaths(lc, end, start, empty_list );
		//System.out.println("Start: " + start + " End: " + end);
		//System.out.println("all paths: " + test_paths);
		
		//List<Gate> test_gate_combos = Arrays.asList(lc.get_logic_gates().get(1), lc.get_logic_gates().get(6));
		List<Gate> test_gate_combos = Arrays.asList(lc.get_logic_gates().get(3));
		List<List<Gate>> gate_combinations = Arrays.asList(test_gate_combos);//Combinations(lc.get_logic_gates(), 2);
		//since effectively cutting wires, might work to better explicitly do so
		//for readability
		List<List<Gate>> edge_combinations_to_cut = getValidEdges(2);
		
		//System.out.println("combos: \n");
		//System.out.println(edge_combinations_to_cut);
		
		//System.out.println("combos: \n" + gate_combinations);
		
		List<List<Gate>> all_paths = new ArrayList<List<Gate>>();
		for(Gate g:lc.get_input_gates()){
			all_paths.addAll(FindAllPaths(lc, end, g, empty_list));
		}
		
		
		
		
		
		//general algorithm:
		// 1. find all paths
		// 2. any path that hits a qs node goes into separate graph --> all nodes to right of qs node, inclusive
		// 3. any remaining paths go in dump graph containing rest of nodes
		//Circuit partitioning doesn't change topology of circuit itself so don't need to keep track of wires in this process
		// (since wires aren't aware of anything, can just use gates to determine DAG structure)
		
		//list of lists of list<gate> = 
		//edge_cut: [[subgraph1path1, subgraph1path2],[subgraph2path1, subgraph2path2]]
		
		HashMap<List<Gate>, List<Subgraph>> edge_partition_paths = new HashMap<List<Gate>, List<Subgraph>>();
		for(List<Gate> combos_list : gate_combinations){
			//choose an edge to cut
			//number of subgraphs = length(combos_list)
			List<Subgraph> emptyGraphsList = new ArrayList<Subgraph>();
			//List<List<Gate>> emptySubgraphsList = new ArrayList<List<Gate>>();
			edge_partition_paths.put(combos_list, emptyGraphsList);
			
			HashMap<Integer, Subgraph> subgraph_path_map = new HashMap<Integer, Subgraph>();
			int subgraph_num = 0;
			subgraph_path_map.put(0, new Subgraph());
			
			//Subgraph t = new Subgraph(emptySubgraphsList);
			
			for(Gate qs_gate: combos_list){
				//gives terminal node of subgraph
				if(lc.get_input_gates().contains(qs_gate)){
					//cutting at input gates is pointless
					continue;
				}
				else{
					//now build a subgraph terminating in qs node in terms of paths
					Subgraph subgraph = new Subgraph();
					//put subgraph_num key into dict here
					subgraph_num += 1;
					subgraph_path_map.put(subgraph_num, subgraph);
					for(List<Gate> full_path:all_paths){
						if(full_path.contains(qs_gate)){
							//List<Gate> subpath = new ArrayList<Gate>();
							int qs_gate_ind = full_path.indexOf(qs_gate);
							System.out.println("qs gate index: " + qs_gate_ind);
							System.out.println("full path: " + full_path);
							List<Gate> subpath = full_path.subList(qs_gate_ind, full_path.size());
							System.out.println("subpath: " + subpath);
							subgraph_path_map.get(subgraph_num).addPath(subpath);
						}
						else{
							subgraph_path_map.get(0).addPath(full_path);
						}
						
					}
					//collect subgraphs from subgraph_path_map into single list and add to edge_partitions_dict
					for(int i: subgraph_path_map.keySet()){
						//List<List<Gate>> subgraph_paths = 
						edge_partition_paths.get(combos_list).add(subgraph_path_map.get(i));
					}		
				}
			}
		}
		
		//System.out.println("All paths: " + all_paths);
		for(List<Gate >k:edge_partition_paths.keySet()){
			System.out.println("partition: " + k);
			System.out.println("subgraph paths\n"+edge_partition_paths.get(k).get(0).paths);
			System.out.println("subgraph paths\n"+edge_partition_paths.get(k).get(1).paths);
			//System.out.println("subgraph paths\n"+edge_partition_paths.get(k).get(2).paths);
		}
		
		//use identified subgraphs to find logic subcircuits
		for(List<Gate >k:edge_partition_paths.keySet()){
			List<Subgraph> subgraphs = edge_partition_paths.get(k);
			for (Subgraph subgraph:subgraphs){
				subgraph.buildLogicCircuit();
				System.out.println(subgraph.sub_lc.printGraph());
			}
		}
		
		
		
		
		
		return subgraph_indices;
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
		
		/*
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
	
	/**Finds all 'k' length combinations of items in the list.
	 * 
	 * @param arr list of Gates to find combinations of
	 * @param k length of subsets to find. In other words, it's the 'k' in "n choose k"
	 * @return list of k-item lists of gates representing all possible combinations of gates in {@code arr}
	 */
	public static List<List<Gate>> Combinations(List<Gate> arr, int k){
		//TODO Currently only works properly when k=2!
		//TODO need to fix @ some point
		
		List<List<Gate>> combos = new ArrayList<List<Gate>>();
		for(int i=0;i<arr.size();i++){
			for(int j=i+k-1; j<arr.size(); j+=k-1){
				//System.out.println("i= " + i + " j= " + j);
				//List<Gate> subList = arr.subList(i, j);
				List<Gate> subList = new ArrayList<Gate>();
				//need a list expander to get all integers between i and j and index w/ those
				//List<Integer> inds = Range(i, j);
				//for(int p=0;p<inds.size();p++){subList.add(arr.get(inds.get(p)));};
				subList.add(arr.get(i)); subList.add(arr.get(j));
				combos.add(subList);
			}
		}
		return combos;
	}
	
	//Gets all edges to cut, up to kth combinations of edges inclusive
	//TODO Currently only returns single gates and k gate combinations
	//		NOT ALL K COMBOS
	private List<List<Gate>> getValidEdges(int k){
		List<List<Gate>> combos_list = new ArrayList<List<Gate>>();
		for(Gate g: this.parent_lc.get_logic_gates()){
			List<Gate> single_gate = new ArrayList<Gate>();
			single_gate.add(g);
			combos_list.add(single_gate);
		}
		for(List<Gate> combos:Combinations(this.parent_lc.get_logic_gates(), k)){
			combos_list.add(combos);
		}
		return combos_list;
	}
	
	private static HashSet<Gate> Union(List<List<Gate>> list_of_gates_lists){
		HashSet<Gate> union = new HashSet<Gate>();
		for(List<Gate> gates_list:list_of_gates_lists){
			for(Gate g:gates_list){
				union.add(g);
			}
		}
		return union;
	}
	
	
	
	

}
