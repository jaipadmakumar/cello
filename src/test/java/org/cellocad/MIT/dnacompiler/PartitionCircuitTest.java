//package org.cellocad.MIT.dnacompiler;
//
//import static org.junit.Assert.*;
//
//import java.util.*;
//
//import org.cellocad.MIT.dnacompiler.Gate.GateType;
//import org.cellocad.MIT.dnacompiler.PartitionCircuit.Subgraph;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import static org.mockito.Mockito.*;
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class PartitionCircuitTest {
//	//@Before
//	//PartitionCircuit pc = new PartitionCircuit();
//	
////	 
////	 Gate g1 = mock(Gate.class);
////	 Gate g2 = mock(Gate.class);
////	 Gate g3 = mock(Gate.class);
////	 Gate g4 = mock(Gate.class);
////	 //System.out.println(g1.Name);
//	 
//	 //@Mock
//	
//	
//	@Mock ArrayList<Gate> _Gates;
//	@Mock ArrayList<Wire> _Wires;
//	
//	 Gate g5 = new Gate(1, "NOR"); 
//	 Gate g6 = new Gate(2, "NOR");
//	 Gate g7 = new Gate(3, "NOR");
//	 Gate in1 = new Gate(4, "INPUT");
//	 Gate out = new Gate(0, "OUTPUT");
//	 
//	 Wire w1 = new Wire(0, out, g5);
//	 Wire w2 = new Wire(1, g5, g6);
//	 Wire w3 = new Wire(2, g6, g7);
//	 Wire w4 = new Wire(3, g7, in1);
//	 
//	 
//	 
	 
	 
//	 @InjectMocks LogicCircuit lc1;
//	 @InjectMocks Gate g11;
//	 @InjectMocks Gate g22;
//	 @InjectMocks Gate g33;
//	 @InjectMocks Gate in1;
//	 @Mock(name = "Index") int ind;
//	 @Mock(name = "dType") GateType type;
//	 @InjectMocks Gate out;
	 
	 
	 //@Rule public Mockito mockitoRule = Mockito.rule();
	
//	@Test
//	public void list2CombinationsShouldBeReturned() {
//		List<Gate> array = new ArrayList<Gate>();
//		//array.add(Arrays.asList(1,3,5,6));
//		array.add(g1); array.add(g2); array.add(g3); array.add(g4);
//		List<List<Gate>> combos = PartitionCircuit.Combinations(array, 2);
//		
//		List<List<Gate>> expected = new ArrayList<List<Gate>>();
//		expected.add(Arrays.asList(g1,g2));
//		expected.add(Arrays.asList(g1,g3));
//		expected.add(Arrays.asList(g1,g4));
//		expected.add(Arrays.asList(g2,g3));
//		expected.add(Arrays.asList(g2,g4));
//		expected.add(Arrays.asList(g3,g4));
//		
//		//when(PartitionCircuit.Combinations(array,2)).thenReturn(expected);
//		assertEquals("Test Failed", combos, expected);
//	}
//	
//	//LogicCircuit lc = mock(LogicCircuit.class);
////	----- Logic Circuit #0 -----
////	NOR         01101001          g2                2  (4)         
////	NOR         01000100          g7                7  (2)         
////	NOR         01100000          g5                5  (8)         
////	INPUT       01010101          inC               11             
////	OUTPUT      10010110          out               0  (1)         
////	INPUT       00001111          inA               12             
////	NOR         10011001          g6                6  (10)        
////	NOR         00000110          g4                4  (6)         
////	NOT         10010110          g1                1  (2)         
////	INPUT       00110011          inB               10             
////	NOR         00100010          g8                8  (4) 
//	
////	@Test
////	public void SplitSingleEdgeTest(){
////		
////		g5.Name = "g5"; g5.Outgoing = w2;
////		g6.Name = "g6"; g6.Outgoing = w3;
////		g7.Name = "g7"; g7.Outgoing = w4;
////		in1.Name = "in1"; 
////		out.Name = "out"; out.Outgoing = w1;
////		
////		ArrayList<Gate> gates = new ArrayList<Gate>(Arrays.asList(g5,g6,g7,in1,out));
////		 
////		 
////		ArrayList<Wire> wires = new ArrayList<Wire>(Arrays.asList(w1, w2, w3, w4));
////		LogicCircuit lc =  new LogicCircuit(gates, wires);
////		PartitionCircuit pc = new PartitionCircuit(lc);
////		List<Gate> empty_list = new ArrayList<Gate>();
////		List<Gate> edges =  new ArrayList<Gate>(Arrays.asList(g6));
////		List<List<Gate>> all_paths = new ArrayList<List<Gate>>();
////		for(Gate g:lc.get_input_gates()){
////			//System.out.println("Finding paths to gate: " + g);
////			all_paths.addAll(PartitionCircuit.FindAllPaths(lc, out, g, empty_list));
////		}
////		
////		HashMap<Gate, PartitionCircuit.Subgraph> map = pc.SplitSingleEdge(lc,edges, all_paths);
////		for(Gate g:map.keySet()) {
////			System.out.println(map.get(g).sub_lc);
////
////		}
////	}
//	
//	@Mock LogicCircuit lc = new LogicCircuit( new ArrayList<Gate>(Arrays.asList(g5,g6,g7,in1,out)),new ArrayList<Wire>(Arrays.asList(w1, w2, w3, w4) ));
//	
//	@Test
//	public void getGateParentsTest() {
//		System.out.println(lc.get_Gates());
//		
//		
//	}
//
//}
//
