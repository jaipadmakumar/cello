package org.cellocad.MIT.dnacompiler;
/**
 * written by jai on 7/16/2017.

 * Uses simulated annealing assignment algorithm but incorporating fixed gates. 
 * Class is an exact copy of simulated annealing classes but with some modifications for fixed gates
 */


import org.apache.log4j.Logger;

import java.util.*;

import lombok.Getter;
import lombok.Setter;



/* Modifications made:
 * - declared number of hill trajectories and iteration as class variables 
 * - fixed bug in getNextGate that prevented algorithm from performing swaps between gates
 */
public class BuildCircuitsFixedGates extends BuildCircuits {


    public BuildCircuitsFixedGates(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }


   

    @Override
    public void buildCircuits(){
    	 /*Best way to incorporate fixed gates and allow swaps only at subsets of gates (individual cells) is
    	  * probably to have my script spit out a list of nodes in graph to be fixed and nodes that are in 
    	  * individual cells. Then run simulated annealing on each set of nodes but loosening conditions so
    	  * that duplicate groups are allowed BETWEEN node subsets but not W/IN a subset. 
    	  * 
    	  * Will also need to carry over information as to which gates the QS gates connect to. Thus, it's
    	  * probably going to simplest overall for python script to spit out a list of indices (in proper order)
    	  * in abstract_LogicCircuit that define each subset as well as the quorum sensing gates. 
    	  * Note the QS gates don't actually need to be explicitly passed, since they will terminal node of any
    	  * subset that doesn't include the OUTPUT gate but do it anyways b/c in future that might not be true (for e.g.
    	  * might have subset that has multiple QS gates b/c that reduces complexity)
         */
    	
        logger = Logger.getLogger(getThreadDependentLoggername());
        System.out.println("Enumerating logic circuits using simulated annealing and fixed gates... (jai code for quorum sensing)");

        Random generator = new Random();


        double max_score = 0.0;
        ArrayList<Integer> fixed_gate_indices = new ArrayList<Integer>();
        fixed_gate_indices.add(3);
        fixed_gate_indices.add(7);
        List<Gate> fixed_gates = new ArrayList<Gate>();
        
        for(String k:get_gate_library().get_GATES_BY_NAME().keySet()){
        	if(fixed_gate_names.contains(k)){
        		fixed_gates.add(get_gate_library().get_GATES_BY_NAME().get(k));
        	}
        }
        System.out.println("FIXED QUORUM SENSING GATES: " + fixed_gates);
        
        LogicCircuit lc = new LogicCircuit(get_unassigned_lc());

        //System.out.println("\nlogic circuit initial state\n");
        //System.out.println(lc.printGraph());
        
        Integer num_swaps = 0; 
        Integer num_subs = 0;
        Integer num_fix_swaps = 0;
        Integer num_fix_subs = 0;
        
        for(int traj=0; traj < NumTrajectories ; ++traj) {

            set_best_score( 0.0 );

            //initial random
            List<String> used_fixed_groups = new ArrayList<String>();
            for (int i = 0; i < lc.get_logic_gates().size(); ++i) {
                Gate g = lc.get_logic_gates().get(i);

                if(fixed_gate_indices.contains(g.Index)) {
                	for(Gate fix_g:fixed_gates){
                		if(!used_fixed_groups.contains(fix_g.Group)){
                			System.out.println("gate" + g);
                			g.Group = fix_g.Group;
                			g.Name = fix_g.Name;
                			used_fixed_groups.add(fix_g.Group);
                			break;
                		}
                	}
                }
                else{
                	g.Name = "null";
                	g.Group = "null";
                }
            }

            for (int i = 0; i < lc.get_logic_gates().size(); ++i) {

                Gate g = lc.get_logic_gates().get(i);
                //System.out.println("current gate: " + g.Index); //jai added
                
                LinkedHashMap<String, ArrayList<Gate>> groups_of_type = get_gate_library().get_GATES_BY_GROUP().get(g.Type);
                
                ArrayList<String> group_names = new ArrayList<String>(groups_of_type.keySet());

                
                //jai
                //System.out.println("\nlogic circuit fixed?\n");
                //System.out.println(lc.printAssignment());
                //end of jai
                
                Collections.shuffle(group_names);

                for (String group_name : group_names) {
                	//System.out.println("current group name iter " + group_name);
                	//added extra condition g.Name == "null" to skip over gates already assigned -jai
                    if (!currentlyAssignedGroup(lc, group_name) && g.Group.equals("null")) {
                    	//System.out.println("group_name: " + group_name);
                    	//System.out.println();
                        ArrayList<Gate> gates_of_group = new ArrayList<Gate>(groups_of_type.get(group_name));

                        Collections.shuffle(gates_of_group);

                        g.Name = gates_of_group.get(0).Name;
                        g.Group = group_name;
                        //loop effectively just sets to final item in gates_of_group not in circuit
                        //--> odd behavior b/c really just want loop to stop when it hits an unused group_name 
                        //break fixes this problem and prevents pointless iterations
                        
                        break;
                    }
                }

            }
            
            //System.out.println("Random assignment ");
            //System.out.println(lc.printAssignment());
            
            //jai
            //System.out.println("\nlogic circuit initial state before hill climbing (fixed gates)\n");
            //System.out.println(lc.printAssignment());
            //end of jai

            Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
            for (Gate g : lc.get_logic_gates()) {
                Evaluate.evaluateGate(g, get_options());
            }
            Toxicity.evaluateCircuitToxicity(lc, get_gate_library());

            //System.out.println(lc.printGraph());


            //next will be changed.  if rejected, LC next will be reset back to LC curr.

            //String follow_best = "";

            Double MAXTEMP = 100.0;
            Double MINTEMP = 0.001;

            Integer STEPS = NumIterations; 

            Double LOGMAX = Math.log10(MAXTEMP);
            Double LOGMIN = Math.log10(MINTEMP);

            Double LOGINC = (LOGMAX - LOGMIN) / STEPS;


            Integer T0_STEPS = 100; //Purpose of arbitrarily adding a 100 steps to #of hill iterations? -jai

            //String b = get_options().get_output_directory() + "/b" + String.format("%02d", traj) + ".txt";
            
            /* outline for how code should work more logically:
        	 * for(int i = 0; i < STEPS + T0_STEPS; ++i) {
        	 * 		//current_gates lists need to be either populated here each time based on 
        	 * 		//indices of subgraph or this needs to be put outside loop
        	 * 		//and modified/updated as swaps and subs are performed
        	 * 		subgraph1_current_gates = [];
        	 * 		subgraph2_current_gates = [];
        	 * 		subgraph1_indices = [];
        	 * 		subgraph2_indices = [];
        	 * 		A_gate = lc.drawRandomGate();
        	 * 		
        	 * 
        	 * 		if(fixed_gates.contains(A_gate.Name)){
        	 * 		//since QS gates leave cell, they should follow default assignment
        	 * 		//steps b/c it doesn't matter which particular subgraph in
        	 * 			//draw a fixed gate from gate lib., subject to constraints
        	 * 			B_gate = drawFixedGateFromLibrary(lc, A_gate, ); //either in lc or not
        	 * 			if(isCurrentlyAssigned(lc, B_gate)){
        	 * 				//do swap
        	 * 			}
        	 * 			else{
        	 * 				//do sub
        	 * 			}}
        	 * 		else{
        	 * 			//now it matter which subgraph a gate is in so just choose one
        	 * 			subgraph_num = pick random subgraph to do stuff on
        	 * 		//get next gate given subgraph_num, this should probably be a
        	 * 		//hashmap with keys as subgraph numbers connecting to current_gates
        	 * 		//for that subgraph
        	 * 			//choose gate from currently assigned gates or from gatelibrary
        	 * 			B_gate = get_NextGateGivenSubgraph(subgraph_gates.get(subgraph_num), A_gate)
        	 * 			if(subgraph_gates.get(subgraph_num).contains(B_gate)){
        	 * 				//do swap
        	 * 			}
        	 * 			else{
        	 * 				//do sub
        	 * 			}
        	 * 		}
        	 * 		//update gate lists with changes
        	 * 			
        	 * 			
        	 */
            
            
            for (int i = 0; i < STEPS + T0_STEPS; ++i) {
            	//Do hill climbing for current trajectory, entire loop runs  _hill_trajectories times
            	// and each loop has _hill_iterations + TO_STEPS iterations
            	

                //Util.fileWriter(b, follow_best, true);

                Double log_temperature = LOGMAX - i * LOGINC;
                Double temperature = Math.pow(10, log_temperature);


                if (i >= STEPS) {
                    temperature = 0.0;
                }

                LogicCircuit save_lc = new LogicCircuit(lc);


                //double score = lc.get_scores().get_score();
                //double growth = Toxicity.mostToxicRow(lc);
                //int rb = get_roadblock().numberRoadblocking(lc);

               
                //note that "A_gate_index" is index in _logic_gates() ArrayList NOT the actual index
                //b/c _logic_gates() returns an ArrayList<Gate> that is not sorted by gate index
                //very confusing... fix at some point --> A_gate = lc.get(gate @ A_gate_index)
                //also could cause huge problems if lc.get_logic_gates() didn't consistently return 
                //gates in same order (non-issue for now)
                int A_gate_index = generator.nextInt(lc.get_logic_gates().size());
                Gate A_gate = lc.get_logic_gates().get( A_gate_index );
                Gate B_gate;

                String A_gate_name = new String(A_gate.Name);
                String A_gate_group = new String(A_gate.Group);
              
                
                //certain gates in lc are fixed and can only be particular gates aka qs gates
                //subgraph in is irrelevant b/c only one qs group per lc 
                if(fixed_gate_indices.contains(A_gate.Index)){
                	//must try swapping or substitution w/ allowed gate
                	B_gate = getNextFixedGate(lc, A_gate, fixed_groups);
                	String B_gate_name = new String(B_gate.Name);
                    String B_gate_group = new String(B_gate.Group);

                    //1. if second gate is used, swap
                    if(isNextGateCurrentlyUsed(lc, B_gate)) {
                    	num_fix_swaps += 1;
                        
                        int B_gate_index = FindGateIndexFromName(lc, B_gate_name);
                        //System.out.println("F gate index: " + F_gate_index);
                       
                        lc.get_logic_gates().get(A_gate_index).Name  = B_gate_name;
                        lc.get_logic_gates().get(B_gate_index).Name  = A_gate_name;
                        lc.get_logic_gates().get(A_gate_index).Group = B_gate_group;
                        lc.get_logic_gates().get(B_gate_index).Group = A_gate_group;

                    }
                    //2. if second gate is unused, substitute
                    else {
                    	num_fix_subs += 1;
                        lc.get_logic_gates().get(A_gate_index).Name  = B_gate_name;
                        lc.get_logic_gates().get(A_gate_index).Group = B_gate_group;
                    }

                }
                
                
                else{
                	//can't use getNextGate here b/c need to fix gate library to exclude gates in
                	//particular subgraph that A_gate is in, not across the entire logic circuit
                     
                	//find subgraph indexes and current subgraph gates
                     List<Integer> subgraph_indices = getSubgraphFromGate(subgraphs_indices, A_gate);
                     
                     
                     ArrayList<Gate> current_subgraph_gates = new ArrayList<Gate>();
                     //ArrayList<String> subgraph_group_names = new ArrayList<String>();
                     for(Gate g:lc.get_logic_gates()){
                    	 //get gates currently at those indices, note working with lc gate indexes here
                    	 if(subgraph_indices.contains(g.Index)){
                    		 current_subgraph_gates.add(g);
                    		 //subgraph_group_names.add(g.Group);
                    	 }
                     }
                     
                    // System.out.println("Current subgraph gates: " + current_subgraph_gates);
                     B_gate = getNextSubgraphGate(lc, subgraph_indices, A_gate);
                     String B_gate_name = new String(B_gate.Name);
                     String B_gate_group = new String(B_gate.Group);	 
                    		 
                    		 
	                //1. if second gate is used in subgraph, swap
	               // if(isNextGateCurrentlyUsed(lc, B_gate)) {
                     if(isNextGateInArray(current_subgraph_gates, B_gate)){
	                	//System.out.println("Swapping");
	                	num_swaps += 1; 
	                	
	                	//need to make sure find correct gate index b/c can be duplicate
	                	//gates across lc, just not in subgraph
	                    int B_gate_index = FindSubgraphGateIndexFromName(lc, subgraph_indices, B_gate.Name); 
	                    //System.out.println("B GATE INDEX: " + B_gate_index);
	                    lc.get_logic_gates().get(A_gate_index).Name  = B_gate_name;
	                    lc.get_logic_gates().get(B_gate_index).Name  = A_gate_name;
	                    lc.get_logic_gates().get(A_gate_index).Group = B_gate_group;
	                    lc.get_logic_gates().get(B_gate_index).Group = A_gate_group;
	
	                }
	                //2. if second gate is unused, substitute
	                else {
	                	//System.out.println("Substituting");
	                	num_subs +=1;
	                    lc.get_logic_gates().get(A_gate_index).Name  = B_gate_name;
	                    lc.get_logic_gates().get(A_gate_index).Group = B_gate_group;
	                }
                }


                set_n_total_assignments( get_n_total_assignments() + 1 );

                Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
                Toxicity.evaluateCircuitToxicity(lc, get_gate_library());

                int B_rb = get_roadblock().numberRoadblocking(lc, get_gate_library());
                int A_rb = get_roadblock().numberRoadblocking(save_lc, get_gate_library());

                Double B_score = lc.get_scores().get_score();
                double A_score = save_lc.get_scores().get_score();

                double B_growth = Toxicity.mostToxicRow(lc);
                double A_growth = Toxicity.mostToxicRow(save_lc);


                /*System.out.println("out:"+ A_gate_name + " in:"+B_gate_name +
                                " prev_sc:" + String.format("%-6.2f", A_score)  + " B_sc:" + String.format("%-6.2f", B_score) +
                                " prev_rb:" + A_rb     + " B_rb:" + B_rb +
                                " prev_tx:" + String.format("%-5.4f", A_growth) + " B_tx:" + String.format("%-5.4f", B_growth)
                );*/


                //follow_best = i + " " + get_best_score() + "\n";

                if(B_rb > A_rb) {
                    //System.out.println("reject added roadblock");

                    revert(lc, save_lc);
                    continue;
                }
                else if(B_rb < A_rb) {
                    //System.out.println("accept removed roadblock");

                    continue; //accept, but don't proceed to evaluate based on score
                }

                if(get_options().is_toxicity()) {

                    if(A_growth < get_options().get_toxicity_threshold()) {
                        if( B_growth > A_growth) {
                            //System.out.println("accept, curr fails growth threshold, next improved growth.");
                            continue;
                        }
                        else {
                            //System.out.println("reject, curr fails growth threshold, next did not improve growth.");
                            revert(lc, save_lc);
                            continue;
                        }
                    }
                    else {
                        if(B_growth < get_options().get_toxicity_threshold()) {
                            //System.out.println("reject, next went below growth threshold.");
                            revert(lc, save_lc);
                            continue;
                        }
                    }
                }

                Double probability = Math.exp( (B_score - A_score)/temperature ); //e^b
                Double rand = Math.random();

                if ( rand < probability) {
                    //if (B_score > get_best_score()) {

                    /////////////// Noise Margin filter //////////////
                    Evaluate.evaluateCircuitNoiseMargin(lc, get_options());
                    if(lc.get_scores().is_noise_margin_contract() == false) {
                        //System.out.println("failed nm");
                        //revert(lc, save_lc);
                        continue;
                    }
                    else {
                        //System.out.println("passed nm");
                    }
                    /////////////// End Noise Margin filter //////////////


                    //System.out.println("PASSED FILTERS, CHECKING IF > BEST");

                    if(B_score > get_best_score()) {
                        set_best_score( B_score );

                        if(get_best_score() > max_score) {

                            //recheck roadblock and toxicity... improvements will pass to avoid getting stuck,
                            //but we don't want to actually save an assignment that doesn't pass all filters.
                            if(get_roadblock().numberRoadblocking(lc, get_gate_library()) == 0 && Toxicity.mostToxicRow(lc) > get_options().get_toxicity_threshold()) {
                                get_logic_circuits().add(new LogicCircuit(lc));
                                max_score = get_best_score();
                                logger.info("  iteration " + String.format("%4s", i) + ": score = " + String.format("%6.2f", get_best_score()));
                            }
                        }
                    }

                }
                else {
//                    if(probability > 0.0001) {
//                        System.out.println(Util.sc(rand) + " greater than " + Util.sc(probability));
//                    }

                    revert(lc, save_lc);
                }
            }

            logger.info("Trajectory " + (traj+1) + " of " + get_options().get_hill_trajectories());
            set_best_score(0.0);
            max_score = 0.0;
            checkSubgraphReuseError( lc, subgraphs_indices);


        }
        //jai written
        //for(Gate g:lc.get_Gates()){
        	//System.out.println("LOGIC GATE AFTER SIM ANNELAING");
        	//System.out.println("Gate: Index, Name = " + g.Index + "," +   g.Name);
        //}
        System.out.println("NUMBER OF SWAPS: " + num_swaps);
        System.out.println("NUMBER OF SUBS: " + num_subs);
        System.out.println("NUMBER OF FIX SWAPS: " + num_fix_swaps);
        System.out.println("NUMBER OF FIX SUBS: " + num_fix_subs);
        System.out.println("EXPECTED NUMBER OF TOTAL ITERATIONS: " + (NumTrajectories * (NumIterations+100)));
        //System.out.println("LC AT END");
        //System.out.println(lc.printGraph());
        //END OF JAI 
        
        //System.out.println("lc graph");
        //System.out.println(lc.printGraph());
        //checkReuseError(lc);
    }



    private boolean currentlyAssignedGroup(LogicCircuit lc, String group_name) {
    	//checks if 'group_name' is already to a gate in a logic circuit
        for(Gate g: lc.get_logic_gates()) {
            if(g.Group.equals(group_name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNextGateCurrentlyUsed(LogicCircuit A_lc, Gate B_gate) {
        for(int i=0; i<A_lc.get_logic_gates().size(); ++i) {
            String gate_name = A_lc.get_logic_gates().get(i).Name;
            if(B_gate.Name.equals(gate_name)) {
            	return true;
                }
        }
        return false;
    }
    
    private boolean isNextGateInArray(ArrayList<Gate> gate_list, Gate B_gate){
    	for(Gate g:gate_list){
    		if(g.Name.equals(B_gate.Name)){
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean currentlyAssignedSubgraphGroup(LogicCircuit lc, List<Integer> subgraph_indices, String group_name){
    	for(Gate g: lc.get_logic_gates()) {
    		//System.out.println("gate, index: " + g + ", " + g.Index);
			//System.out.println("group passed: " + group_name);
    		if(subgraph_indices.contains(g.Index)){
    			if(g.Group.equals(group_name)) {
    				//System.out.println("returning true");
    				return true;
    			}	
            }
        }
        return false;
    }
    
    private List<Integer> getSubgraphFromGate(List<List<Integer>> subgraph_inds_list, Gate A_gate){
    	 List<Integer> subgraph_indices = new ArrayList<Integer>(); 
         for(List<Integer> subgraph_inds:subgraph_inds_list){
        	 if(subgraph_inds.contains(A_gate.Index)){
        		 subgraph_indices = subgraph_inds;
            	 break;
        	 }
         }
         return subgraph_indices;
    }
    
    private Gate getNextSubgraphGate(LogicCircuit lc, List<Integer> subgraph_indices, Gate A_gate){
    	//get allowed subgraph gates
    	
    	ArrayList<Gate> gates_of_type = new ArrayList<Gate>(get_gate_library().get_GATES_BY_TYPE().get(A_gate.Type).values());
    	HashMap<String, Gate> allowed_B_gates = new HashMap<String, Gate>();
    	ArrayList<Gate> current_subgraph_gates = new ArrayList<Gate>();
    	
    	for(Gate g:lc.get_logic_gates()){
    		if(subgraph_indices.contains(g.Index)){
    			current_subgraph_gates.add(g);
    		}
    	}
    
        for(Gate g: gates_of_type) {

             //disallow same gate or
        	 //disallow swapping w/ a fixed gate --> also, currently causes crash for some reason if condition removed
        	if(g.Name.equals(A_gate.Name) || fixed_gate_names.contains(g.Name)) {
                continue;
             }
             
             //allow RBS variant
            else if(g.Group.equals(A_gate.Group)){
            	//System.out.println("allowing RBS variant " + A_gate.Name + ": " + g.Name);
                allowed_B_gates.put(g.Name, g);
            }

            //allow non-duplicate subgraph groups OR currently used gates
            else if (!currentlyAssignedSubgraphGroup(lc, subgraph_indices, g.Group) || isNextGateInArray(current_subgraph_gates, g)) {
            	
                allowed_B_gates.put(g.Name, g);
            }
        }


         ArrayList<String> allowed_B_gate_names = new ArrayList<String>( allowed_B_gates.keySet());
         Collections.shuffle(allowed_B_gate_names);
         String B_gate_name = allowed_B_gate_names.get(0);
         
         
         /*
         System.out.println("subgraph inds: " + subgraph_indices);
         System.out.println("Current subgraph gates: " + current_subgraph_gates);
         System.out.println("allowed B: " + allowed_B_gate_names.toString());
         System.out.println("A_gate " + A_gate.Name);
         System.out.println("B_gate " + B_gate_name);
         System.out.println("Current assignment " + lc.printAssignment());
         */
         

         return get_gate_library().get_GATES_BY_NAME().get(B_gate_name);
    }
    	


    private Gate getNextGate(LogicCircuit lc, Gate A_gate) {
    	//gets another gate that is 1) different than A.gate 2) is not of the same group as something else in circuit
    	//gates allowed should be: 
    	// 1) an RBS variant of the current gate (substitute)
    	// 2) of a different group and currently unassigned (substitute)
    	// 3) an already assigned gate (performs swapping)
    	// 4) not from fixed gates group (reserved for fixed gates), shouldn't be strictly necessary but for do for now...
    	// Original implementation prevents 3 from ever occurring b/c blocks any duplicate groups --> fixed now
    	
        ArrayList<Gate> gates_of_type = new ArrayList<Gate>(get_gate_library().get_GATES_BY_TYPE().get(A_gate.Type).values());

        HashMap<String, Gate> allowed_B_gates = new HashMap<String, Gate>();

        for(Gate g: gates_of_type) {

            //disallow same gate
            if(g.Name.equals(A_gate.Name)) {
                continue;
            }
            
            //disallow swapping w/ a fixed gate --> also, currently causes crash for some reason if condition removed
            else if(fixed_gate_names.contains(g.Name)){
            	continue;
            }
            
            //allow RBS variant
            else if(g.Group.equals(A_gate.Group)){
            	//System.out.println("allowing RBS variant " + A_gate.Name + ": " + g.Name);
                allowed_B_gates.put(g.Name, g);
            }

            //allow non-duplicate groups OR currently used gates
            else if (!currentlyAssignedGroup(lc, g.Group) || isNextGateCurrentlyUsed(lc, g)) {
                allowed_B_gates.put(g.Name, g);
            }
        }


        ArrayList<String> allowed_B_gate_names = new ArrayList<String>( allowed_B_gates.keySet());
        Collections.shuffle(allowed_B_gate_names);
        String B_gate_name = allowed_B_gate_names.get(0);
        
        /*
        System.out.println("allowed B: " + allowed_B_gate_names.toString());
        System.out.println("Current assignment " + lc.printAssignment());
        System.out.println("A_gate " + A_gate.Name);
        System.out.println("B_gate " + B_gate_name);
        */

        return get_gate_library().get_GATES_BY_NAME().get(B_gate_name);

    }
    
    private Gate getNextFixedGate(LogicCircuit lc, Gate A_gate, ArrayList<String> allowed_groups) {
    	//gets another gate that is 1) different than A.gate 2) is not of the same group as something else in circuit
    	// 3) in specified set of allowed groups --> use for getting quorum sensing gates
    	
        ArrayList<Gate> gates_of_type = new ArrayList<Gate>(get_gate_library().get_GATES_BY_TYPE().get(A_gate.Type).values());
        HashMap<String, Gate> allowed_B_gates = new HashMap<String, Gate>();

        for(Gate g: gates_of_type) {
        	
            //disallow same gate added or condition to disallow gates not in allowed groups
            if(g.Name.equals(A_gate.Name) || !allowed_groups.contains(g.Group)) {
            	//System.out.println("current gate: " + g.Name);
                continue;
            }
            
            //allow RBS variant
            else if(g.Group.equals(A_gate.Group)) {
                //System.out.println("allowing RBS variant " + A_gate.Name + ": " + g.Name);
                allowed_B_gates.put(g.Name, g);
            }

            //allow non-duplicate groups
            else if (!currentlyAssignedGroup(lc, g.Group) || isNextGateCurrentlyUsed(lc, g)) {
                allowed_B_gates.put(g.Name, g);
            }
        }


        ArrayList<String> allowed_B_gate_names = new ArrayList<String>( allowed_B_gates.keySet());
        Collections.shuffle(allowed_B_gate_names);
        String B_gate_name = allowed_B_gate_names.get(0);
        
        /*
        System.out.println("allowed B: " + allowed_B_gate_names.toString());
        System.out.println("Current assignment " + lc.printAssignment());
        System.out.println("A_gate " + A_gate.Name);
        System.out.println("B_gate " + B_gate_name);
        */

        return get_gate_library().get_GATES_BY_NAME().get(B_gate_name);

    }
    private int FindGateIndexFromName(LogicCircuit lc, String gate_name){
    	int gate_name_index = 0; //need to know the second gate index
	    for(int j=0; j<lc.get_logic_gates().size(); ++j) {
	        if(lc.get_logic_gates().get(j).Name.equals(gate_name)) {
	            gate_name_index = j;
	        }
	    }
		return gate_name_index;
    }
    
    private int FindSubgraphGateIndexFromName(LogicCircuit lc, List<Integer> subgraph_indices, String gate_name){
    	int gate_name_index = 0; //need to know the second gate index
	    for(int j=0; j<lc.get_logic_gates().size(); ++j) {
	    	Gate g = lc.get_logic_gates().get(j);
	    	if(subgraph_indices.contains(g.Index)){
	    		if(g.Name.equals(gate_name)) {
	    			gate_name_index = j;
	    		}
	    	}
	    }
		return gate_name_index;
    }
    

    
    //debugging purposes.
    private void checkReuseError(LogicCircuit lc) {
        for(int i=0; i<lc.get_logic_gates().size()-1; ++i) {
            for(int j=i+1; j<lc.get_logic_gates().size(); ++j) {
                if(lc.get_logic_gates().get(i).Group.equals(lc.get_logic_gates().get(j).Group)) {
                    throw new IllegalStateException("Repressor reuse error in simulated annealing, \n" + lc.get_logic_gates().get(i).Name + " " + lc.get_logic_gates().get(j).Name);
                }
            }
        }
    }
    
    //debugging subgraph reuse
    private void checkSubgraphReuseError(LogicCircuit lc, List<List<Integer>> subgraphs_indices) {
    	HashMap<Integer, List<Integer>> subgraph_map = new HashMap<Integer, List<Integer>>();
    	HashMap<Integer, ArrayList<String>> subgraph_groups_map = new HashMap<Integer, ArrayList<String>>();
    	
    	for(int i=0; i<subgraphs_indices.size()-1; ++i){
    		List<Integer> subgraph_inds = subgraphs_indices.get(i);
    		subgraph_map.put(i, subgraph_inds);
    	}
    	
    	for(int k : subgraph_map.keySet()){
    		ArrayList<String> groups = new ArrayList<String>();
    		List<Integer> subgraph_inds = subgraph_map.get(k);
    		
    		for(int i=0; i<lc.get_logic_gates().size()-1; ++i) {
    			Gate g = lc.get_logic_gates().get(i);
    			if(subgraph_inds.contains(g.Index)){
    				groups.add(g.Group);
    			}
    		}
    		
    		subgraph_groups_map.put(k, groups);
    	}
    	
    	for(int k:subgraph_groups_map.keySet()){
    		ArrayList<String> subgraph_groups =subgraph_groups_map.get(k);
    		Set<String> group_set = new HashSet<String>(subgraph_groups);
    		if(group_set.size() < subgraph_groups.size()){
    			throw new IllegalStateException("Repressor reuse error in simulated annealing, \n");
    			}
    	}
    }
    

    //if rejected, reset the Name for all logic gates.
    private void revert(LogicCircuit B_lc, LogicCircuit A_lc) {

        for(int i=0; i<A_lc.get_logic_gates().size(); ++i) {
            B_lc.get_logic_gates().get(i).Name  = A_lc.get_logic_gates().get(i).Name;
            B_lc.get_logic_gates().get(i).Group = A_lc.get_logic_gates().get(i).Group;
        }

        Evaluate.evaluateCircuit(B_lc, get_gate_library(), get_options());
        Toxicity.evaluateCircuitToxicity(B_lc, get_gate_library());
    }



    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////

    private Logger logger  = Logger.getLogger(getClass());
    private int NumTrajectories = get_options().get_hill_trajectories();
    private int NumIterations = get_options().get_hill_iterations(); //# iterations per trajectory
    
    //Currently hardcoding fixed aka QS gates
    private ArrayList<String> fixed_gate_names = new ArrayList<String>(Arrays.asList("J1_DigiJ","J2_DigiJ",
    																					"QS1_DigiJQ","QS2_DigiJQ"));
    private ArrayList<String> fixed_groups  = new ArrayList<String>(Arrays.asList("DigiJ", "DigiJQ"));
    
    
    //private ArrayList<String> fixed_gate_names = new ArrayList<String>(Arrays.asList("Q1_QacR","Q2_QacR"));
    //private ArrayList<String> fixed_groups  = new ArrayList<String>(Arrays.asList("QacR"));

    
    //note this won't work b/c Arrays.asList() doesn't return an ArrayList so declaration is incorrect
    //private static ArrayList<ArrayList<Integer>> sub_groups = new ArrayList<ArrayList<Integer>>();
    //sub_groups.add(Arrays.asList(1,2));
    
    //subgraphs_indices must not overlap!
    private List<List<Integer>> subgraphs_indices = Arrays.asList(
    	    Arrays.asList( 1, 2, 3, 4),
    	    Arrays.asList( 5, 6, 7, 8 ));
    
    
    
    
    
    
    
    
}
