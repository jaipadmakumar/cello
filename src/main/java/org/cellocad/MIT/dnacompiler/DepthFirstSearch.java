package org.cellocad.MIT.dnacompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepthFirstSearch {
	
	
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
	
	public static List<List<Gate>> findAllPaths(LogicCircuit lc, Gate start_gate, 
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
				List<List<Gate>> newpaths = findAllPaths(lc, child, end_gate, new_path);
				for(List<Gate> newpath:newpaths){
					//System.out.println("newpath: " + newpath);
					paths.add(newpath);
				}
			}
		}
		//System.out.println("paths: " + paths); 
		return paths;
	}

}
