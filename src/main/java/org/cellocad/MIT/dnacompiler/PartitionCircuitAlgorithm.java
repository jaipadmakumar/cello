package org.cellocad.MIT.dnacompiler;

import java.util.List;

import org.cellocad.MIT.dnacompiler.PartitionCircuit.Subgraph;

//interface to implement 'strategy' design pattern for circuit partitioning algorithms
//I think a way to do this is to have different algorithms which implement this interface, 
// then have a base class which hold the Subgraph class which is extended by various algorithms
//implementing this interface. 
//Then partition circuit serves as the 'context' class and algorithms would return lists of 
//Subgraph objects. Then use IntegratedLogicCircuit with polymorphism (via constructor overloading)
// to get a list of IntegratedLogicCircuits if lists of subgraphs are passed in or a single 
//circuit if a single list of subgraphs is passed in, where IntegratedLogicCircuit will take a 
// PartitionCircuit (which was already called with whatever strategy we want to partition circuit 
// and holds the partitioned circuit in its 'subgraph_sets' field) object.


public interface PartitionCircuitAlgorithm {
	public List<List<Subgraph>> partitionCircuit();
}
