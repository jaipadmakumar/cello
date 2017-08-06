package org.cellocad.MIT.dnacompiler;
//jai written on 08/06/2017

import lombok.Getter;
import lombok.Setter;
import java.util.*;

public class PartitionCircuit {
	
	public PartitionCircuit(LogicCircuit lc){
		//default constructor
	}
	public static List<List<Integer>> partitionCircuit(LogicCircuit lc){
		/* Identifies edges to cut returns gate indices in each subgraph
		 * as an arraylist
		 */
		
		List<List<Integer>> subgraph_indices = new ArrayList<List<Integer>>();
		
		lc.printGraph();
		System.out.println("success!");
		return subgraph_indices;
	}
	
	
}
