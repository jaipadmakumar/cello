package org.cellocad.MIT.dnacompiler;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Rule;
import org.junit.Test;


import static org.mockito.Mockito.*;


public class PartitionCircuitTest {
	//@Before
	//PartitionCircuit pc = new PartitionCircuit();
	
	 
	 Gate g1 = mock(Gate.class);
	 Gate g2 = mock(Gate.class);
	 Gate g3 = mock(Gate.class);
	 Gate g4 = mock(Gate.class);
	 
	 
	 //@Rule public Mockito mockitoRule = Mockito.rule();
	
	@Test
	public void list2CombinationsShouldBeReturned() {
		List<Gate> array = new ArrayList<Gate>();
		//array.add(Arrays.asList(1,3,5,6));
		array.add(g1); array.add(g2); array.add(g3); array.add(g4);
		List<List<Gate>> combos = PartitionCircuit.Combinations(array, 2);
		
		List<List<Gate>> expected = new ArrayList<List<Gate>>();
		expected.add(Arrays.asList(g1,g2));
		expected.add(Arrays.asList(g1,g3));
		expected.add(Arrays.asList(g1,g4));
		expected.add(Arrays.asList(g2,g3));
		expected.add(Arrays.asList(g2,g4));
		expected.add(Arrays.asList(g3,g4));
		
		//when(PartitionCircuit.Combinations(array,2)).thenReturn(expected);
		assertEquals("Test Failed", combos, expected);
	}

}

