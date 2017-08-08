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
		
		List<Gate> empty_list = new ArrayList();
		List<Gate> lc_gates = lc.get_logic_gates();
		System.out.println("logic gates \n" + lc_gates);

		List<List<Gate>> pathers = FindAllPaths(lc, lc_gates.get(0), lc_gates.get(6), empty_list );
		System.out.println(lc.printGraph());
		System.out.println("success!");
		System.out.println("paths found: ");
		System.out.println(pathers);
		return subgraph_indices;
	}
	
	
	public static List<List<Gate>> FindAllPaths(LogicCircuit lc, Gate start_gate, 
											Gate end_gate, List<Gate> path){
		/*Implements a depth first search with backtracking to find all (simple) paths
		 * between 'start_gate' and 'end_gate'. This method employs tree recursion to
		 * perform the search and, therefore, scales disastrously badly. Initially,
		 * 'path' should be given as an empty List<Gate>. Returns a list of lists 
		 * of gates corresponding to paths. 
		 */
		
		//System.out.println("Finding all paths between " + start_gate.Index + " and " + end_gate.Index);
		
		List<List<Gate>> emptyList = Collections.emptyList();
		
		//need to create new object in memory to hold paths so that don't overwrite 
		//path objects from previous recursive calls (as opposed to just doing path.add(start_gate))
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
		
		
		for(Gate child:children){
			List<Gate> input_gates = lc.get_input_gates();
			if(input_gates.contains(child)){ //hit dead end, backtrack
				return emptyList;
			}
		}
		
		/* If ever want to do this with input gates included (i.e. call, lc.get_Gates) then this is stopping condition
		if(children.isEmpty()){
			System.out.println("here");
			return emptyList;
		}
		 */
		
		
		//System.out.println("Current gate: " + start_gate);
		//System.out.println("gate children: " + children);
		
		for(Gate child: children){
			if(!new_path.contains(child)){
				List<List<Gate>> newpaths = FindAllPaths(lc, child, end_gate, new_path);
				for(List<Gate> newpath:newpaths){
					System.out.println("newpath: " + newpath);
					paths.add(newpath);
				}
			}
		}
		//System.out.println("paths: " + paths); 
		return paths;
	}	
	
}
