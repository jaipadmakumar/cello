package org.cellocad.MIT.dnacompiler;
import java.io.*;
import java.util.*;





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
	

	public static void topoSort(LogicCircuit lc) {
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
	
	private static void addSuperSource(LogicCircuit lc) {
		
		Gate super_source = new Gate();
		super_source.Name = "SUPER_SOURCE";
		//super_source.Index = -100;
		super_source.Outgoing = null;

		ArrayList<Wire> corrected_wires = new ArrayList<Wire>(lc.get_Wires());
		ArrayList<Gate> corrected_gate = new ArrayList<Gate>();
		corrected_gate.add(super_source);
		corrected_gate.addAll(lc.get_logic_gates());
		corrected_gate.addAll(lc.get_output_gates());
		
		
		int c=100;
		System.out.println("input outgoing pre: " + lc.get_input_gates().get(0).Outgoing);
		for(Gate g: lc.get_input_gates()) {
			g.Outgoing = new Wire(c, g, super_source );
			corrected_wires.add(new Wire(c, g, super_source ));
			corrected_gate.add(g);
			c+=1;
		}
		System.out.println("input outgoing post: " + lc.get_input_gates().get(0).Outgoing);
		LogicCircuit corrected_lc = new LogicCircuit(corrected_gate, corrected_wires);
		System.out.println(corrected_lc.printGraph());
		
		
	}
	

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
