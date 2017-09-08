package org.cellocad.MIT.dnacompiler;
import java.io.*;
import java.util.*;


public class TopologicalSort {
	
	
	private static void topoSortHelper(Gate g, List<Gate> visited, Deque<Gate> stack) {
		
		Gate next_g;
		g.set_unvisited(false); //mark current gate as visited
		System.out.println("Current gate: " + g);
		ListIterator<Gate> child_iter = g.getChildren().listIterator();
		while(child_iter.hasNext()) {
			next_g = child_iter.next();
			System.out.println("next gate: " + next_g);
			if(next_g.is_unvisited()) {
				topoSortHelper(next_g, visited, stack);
			}
		}
		stack.add(g);
		
		
	}
	
	//will need to add wires to input nodes in lc connecting to super source
	// and then remove them at end
	//will also want to clear all g.unvisited()
	public static void topoSort(LogicCircuit lc) {
		//Stack<Gate> stack = new Stack<Gate>();
		//stack.addAll(old_stack);
		System.out.println("Calculating topological sort");
		Deque<Gate> stack = new ArrayDeque<Gate>();
		List<Gate> visited_gates = new ArrayList<Gate>();
		
		
		
		//initialize everything to unvisited
		for(Gate g: lc.get_Gates()) {
			g.set_unvisited(true);
		}
		for(Gate g:lc.get_input_gates()) {g.set_unvisited(false);}
		
		for(Gate g:lc.get_Gates()) {
			System.out.println("current stack: " + stack);
			System.out.println("stack length: " + stack.size());
			if(g.is_unvisited()) {
				topoSortHelper(g, visited_gates, stack);
				
			}
			
		}
		
		while (stack.isEmpty()==false) {
			System.out.println(stack.pop() + " ");
		}
		
		
		
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
