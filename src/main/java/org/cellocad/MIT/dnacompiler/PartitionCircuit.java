package org.cellocad.MIT.dnacompiler;
//jai written on 08/06/2017

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PartitionCircuit {
	
	public PartitionCircuit(LogicCircuit lc){
		//default constructor
	}
	public List<List<Integer>> partitionCircuit(LogicCircuit lc){
		/* Identifies edges to cut returns gate indices in each subgraph
		 * as an arraylist
		 */
		
		List<List<Integer>> subgraph_indices = new ArrayList<List<Integer>>();
		
		//gate.unvisited is a CLASS variable so all instances of variable rewritten
		//simultaneously --> this could break shit
		
		//for(Gate g:lc.get_logic_gates()){
		//	g.set_unvisited(true);
		//}
		
		List<Gate> empty_list = new ArrayList<Gate>();
		List<Gate> lc_gates = lc.get_logic_gates();
		System.out.println("logic gates \n" + lc_gates);

		List<List<Gate>> pathers = FindAllPaths(lc, lc_gates.get(0), lc_gates.get(6), empty_list );
		System.out.println(lc.printGraph());
		System.out.println("success!");
		System.out.println("paths found: ");
		System.out.println(pathers);
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
	 * @return
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
	
	private static List<Gate> Union(List<List<Gate>> list_of_gates_lists){
		List<Gate> union = new ArrayList<Gate>();
		for(List<Gate> gates_list:list_of_gates_lists){
			for(Gate g:gates_list){
				union.add(g);
			}
		}
		return union;
	}
	
	
	
	

}
