package org.cellocad.MIT.dnacompiler;
import java.io.*;
import java.util.*;



/**
 * Class to compute a topological ordering for a given LogicCircuit. The class holds one public static method, {@code topoSort}
 * which is used to find a topological sort of the gates in a circuit. {@code topoSort} uses a depth first search with backtracking
 * and an extra stack to find a linear ordering of the logic circuit DAG. 
 * @author jaipadmakumar
 *
 */

public class TopologicalSort {
	
	/*
	 * Helper function for topoSort. Basically runs a depth first search through graph (starting at an output node
	 * b/c that's what method is called with initially) and pushes a gate to the stack if it:
	 * 	a) goes down a path where it doesn't encounter any visited nodes and hits an input gate (no children). in this 
	 * 	   case, it backtracks through the path, pushing gates to the stack as it backtracks in same as order as
	 * 	   they're visited in the dfs. or,  
	 *	b) goes down a path where it visits an already seen node, in which case it backtracks at that node and pushes
	 *	   parent of seen gate into stack. This ensures the topological ordering.  
	 */
	private static void topoSortHelper(Gate g, List<Gate> visited, Deque<Gate> stack) {
		
		Gate next_g; //child gate to recurse w/
		visited.add(g); //mark current gate as visited
		
		ListIterator<Gate> child_iter = g.getChildren().listIterator();
		
		//loop is entered as long as a gate has a child aka as long as its not an input gate
		while(child_iter.hasNext()) {
			next_g = child_iter.next(); //recursion tree splits here if gate has >1 child
			if(!visited.contains(next_g)) { //if gate is unvisited, recurse b/c need to keep going down path, else backtrack
				//System.out.println("visiting gate: " + next_g);
				topoSortHelper(next_g, visited, stack);
			}
		}
		
		//get to here when gate has no children aka at end of path
		//OR if encountered an already seen gate, in which case backtrack and push original parent gate
		
		stack.push(g);
		
		
	}
	

	/**
	 * Finds a topological sort of the given LogicCircuit. Employs a depth first search w/ backtracking and an additional 
	 * stack to keep track of the ordering of gates in the linear ordering. Since this is still just a depth first search,
	 * it's runtime should be proportional the size of the input circuit so this should scale well. Assuming I actually
	 * implemented it properly, I think the time complexity should be O(total gates, total wires). 
	 * 
	 * @param lc LogicCircuit to find topological sort of
	 * @return topological_order linear ordering of gates, 1st gate is first entry, last gate is final entry
	 */
	public static List<Gate> topoSort(LogicCircuit lc) {
		System.out.println("Calculating topological sort");
		List<Gate> topological_order = new ArrayList<Gate>();
		Deque<Gate> stack = new ArrayDeque<Gate>();
		List<Gate> visited_gates = new ArrayList<Gate>();
		
		
		//initiate depth first search starting from output gates, only one stack and visited_gates array
		//in memory so will be appropriately filled out by helper function
		for(Gate out_g: lc.get_output_gates()) {
			topoSortHelper(out_g, visited_gates, stack);
		}
		
		System.out.println("toplogical order:");
		while (stack.isEmpty()==false) {
			topological_order.add(stack.pop());
			
		}
		Collections.reverse(topological_order); //circuit is iterated from output --> input so stack needs to be reversed
		for(Gate g:topological_order) {System.out.println(g);}
		
		return topological_order;
		
	}
}

	