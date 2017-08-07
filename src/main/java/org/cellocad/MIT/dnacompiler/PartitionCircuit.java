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
	
	
	
	/*python code
	 * path = path + [start]
if start == end:
	return [path]
if not graph.has_key(start):
	return []
paths = []
for node in graph[start]:
	if node not in path:
		newpaths = find_all_paths(graph, node, end, path)
		for newpath in newpaths:
			paths.append(newpath)
return paths

	 */
	
//	 public static void simulateLogic(Gate g){
//
//	        if (g.is_unvisited()) {
//
//	            ArrayList<Gate> children = g.getChildren();
//
//	            for(Gate child: children) {
//	                if(child.is_unvisited()){
//	                    simulateLogic(child); //recursive
//	                }
//	            }
//
//	            //if all children have been visited, visit the current gate 'g'
//	            g.set_unvisited( false );
//	            g.set_logics( GateUtil.computeGateLogics(g) );
//	        }
//	    }

	
	public static List<List<Gate>> FindAllPaths(LogicCircuit lc, Gate start_gate, 
											Gate end_gate, List<Gate> path){

		System.out.println("Finding all paths between " + start_gate.Index + " and " + end_gate.Index);
		List<List<Gate>> emptyList = Collections.emptyList();
		List<Gate> new_path = new ArrayList<Gate>();
		new_path.addAll(path);
		new_path.add(start_gate);
		//path.add(start_gate);
		System.out.println("current path: " + path);
		
		if(start_gate == end_gate){
			System.out.println("Gate found");
			List<List<Gate>> found_path = new ArrayList<List<Gate>>();
			System.out.println("Found newpath: " + found_path);
			found_path.add(new_path);
			return found_path;
		}
		
		
		
		List<List<Gate>> paths = new ArrayList<List<Gate>>();
		//System.out.println(System.identityHashCode(path));
		
		List<Gate> children = start_gate.getChildren();
		
		
		for(Gate child:children){
			List<Gate> input_gates = lc.get_input_gates();
			if(input_gates.contains(child)){ //hit dead end
				System.out.println("Hit dead end at input gate.");
				return emptyList;
			}
		}
//		if(children.isEmpty()){
//			System.out.println("here");
//			return emptyList;
//		}
		
		
		System.out.println("Current gate: " + start_gate);
		System.out.println("gate children: " + children);
		for(Gate child: children){
			if(!path.contains(child)){
				System.out.println("child not in path: " + child);
				List<List<Gate>> newpaths = FindAllPaths(lc, child, end_gate, new_path);
				System.out.println("newpaths: " + newpaths);
				for(List<Gate> newpath:newpaths){
					System.out.println("newpath: " + newpath);
					paths.add(newpath);
				}
			}
		}
		System.out.println("paths: " + paths); 
		return paths;
	}
	
	
}
