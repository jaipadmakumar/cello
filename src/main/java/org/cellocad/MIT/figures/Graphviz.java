package org.cellocad.MIT.figures;


import java.util.*;

import org.cellocad.MIT.dnacompiler.*;

public class Graphviz {


    public Graphviz(String home, String output_directory, String dateID) {
        _home = home;
        _output_directory = output_directory;
        _dateID = dateID;
    }

    /***********************************************************************

     Synopsis    [  ]

     Wiring diagram with xfer functions (Gnuplot) as node images.
     This function was originally written to make animated GIFs to illustrate assignment algorithms.
     Now it's used to generate one graphviz file with transfer function images as nodes

     example file name: 1404317484362_A000_wiring_rpu.dot

     ***********************************************************************/
    public void printGraphvizXferPNG(LogicCircuit lc, String outfile) { //single output only

        String gvText = "#GRAPHVIZ_OUTPUT \n";
        gvText += "digraph{ \n";
        gvText += "rankdir=LR; \n";
        gvText += "splines=ortho; \n";

        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            gvText += lc.get_output_gates().get(i).Name + "[shape=none,label=\"" + lc.get_output_gates().get(i).Name + "\"]; \n";
        }

        for(int i=lc.get_Gates().size()-1; i>=0; --i) {

            Gate g = lc.get_Gates().get(i);

            if(g.Type == Gate.GateType.OUTPUT || g.Type == Gate.GateType.OUTPUT_OR ) { continue; }
            String shape = g.Name;
            if(g.Type == Gate.GateType.INPUT) {
                gvText += g.Name + "[shape=none,label=\"" + shape + "\"]; \n";
            }
            else if(g.Type == Gate.GateType.NOT || g.Type == Gate.GateType.NOR || g.Type == Gate.GateType.AND || g.Type == Gate.GateType.NAND || g.Type == Gate.GateType.OR || g.Type == Gate.GateType.XOR || g.Type == Gate.GateType.XNOR ) {
                String image_location = "\"" + lc.get_assignment_name() + "_xfer_model_" + g.Name +".png"+ "\"";
                gvText += g.Name + "[fixedsize=true,height=1.0,width=1.0,label=\"\",shape=none,image="+image_location+"]; \n";
            }
            else {
                String g_logics = "\\n" + BooleanLogic.logicString(g.get_logics());
                String labelscore = "";
                if(g.get_scores().get_score() != -1.0) {
                    labelscore = "\\n" + String.format("%5.4f", g.get_scores().get_score());
                }
                gvText += g.Name + "[shape=box,label=\"" + shape + labelscore + g_logics + "\"]; \n";
            }
        }

        for(int i=0; i<lc.get_Wires().size(); ++i) {
            Wire w = lc.get_Wires().get(i);

            String child = w.To.Name;
            String parent = w.From.Name;

            gvText += child + " ->" + parent + " ; \n";
        }

        gvText += "} \n";


        Util.fileWriter(_output_directory + outfile, gvText, false);
    }


    /***********************************************************************

     Synopsis    [  ]

     ***********************************************************************/
    public void printGraphvizDistrPNG(LogicCircuit lc, String outfile) { //single output only

        String gvText = "#GRAPHVIZ_OUTPUT \n";
        gvText += "digraph{ \n";
        gvText += "rankdir=LR; \n";
        gvText += "splines=ortho; \n";

        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            Gate g = lc.get_output_gates().get(i);
            gvText += g.Name + "[shape=none,label=\"" + lc.get_output_gates().get(i).Name + "\"]; \n";
        }

        for(int i=lc.get_Gates().size()-1; i>=0; --i) {

            Gate g = lc.get_Gates().get(i);

            if(g.Type == Gate.GateType.OUTPUT || g.Type == Gate.GateType.OUTPUT_OR) { continue; }
            String shape = g.Name;
            if(g.Type == Gate.GateType.INPUT) {
                gvText += g.Name + "[shape=none,label=\"" + shape + "\"]; \n";
            }
            else {
                //String image_location = "\"" + _output_directory + prefixA + "_" + g.Name +"_gate.png"+ "\"";
                String image_location = "\"" + lc.get_assignment_name() + "_" + g.Name +"_gate.png"+ "\"";
                gvText += g.Name + "[fixedsize=true,height=1.0,width=1.0,label=\"\",shape=none,image="+image_location+"]; \n";
            }
        }

        for(int i=0; i<lc.get_Wires().size(); ++i) {
            Wire w = lc.get_Wires().get(i);

            String child = w.To.Name;
            String parent = w.From.Name;

            gvText += child + " ->" + parent + " ; \n";
        }

        gvText += "} \n";


        Util.fileWriter(_output_directory + outfile, gvText, false);
    }


    /***********************************************************************

     Synopsis    [  ]


     write .dot file for Graphviz.  Works for AGRN and GRN.

     /////////////////// AGRN ////////////////////
     #GRAPHVIZ_OUTPUT
     digraph{
     rankdir=LR;
     splines=ortho;
     out1[shape=none,label="out1\n0001"];
     in2[shape=none,label="in2\n0101"];
     in1[shape=none,label="in1\n0011"];
     3[shape=box,style=filled,fillcolor=gray100,label="3\n1100"];
     2[shape=box,style=filled,fillcolor=gray100,label="2\n1010"];
     1[shape=box,style=filled,fillcolor=gray100,label="1\n0001"];
     1->out1;
     2->1;
     3->1;
     in2->2;
     in1->3;
     }


     /////////////////// GRN ////////////////////
     #GRAPHVIZ_OUTPUT
     digraph{
     rankdir=LR;
     splines=ortho;
     output_YFP[shape=none,label="output_YFP\n2.6691\n0001"];
     pTet[shape=none,label="pTet\n2.1083\n0101"];
     pTac[shape=none,label="pTac\n2.6344\n0011"];
     PhlF[shape=box,style=filled,fillcolor=gray99,label="PhlF\nrbs0\n2.4254\n1100"];
     QacR[shape=box,style=filled,fillcolor=gray98,label="QacR\nrbs2\n2.2019\n1010"];
     SrpR[shape=box,style=filled,fillcolor=gray99,label="SrpR\nrbs3\n2.6691\n0001"];
     SrpR->output_YFP;
     QacR->SrpR;
     PhlF->SrpR;
     pTet->QacR;
     pTac->PhlF;
     }
     ***********************************************************************/
    public void printGraphvizDotText(LogicCircuit lc, String outfile) {

        String gvText = "#GRAPHVIZ_OUTPUT \n";
        gvText += "digraph{ \n";
        gvText += "rankdir=LR; \n";
        gvText += "splines=ortho; \n";

        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            String output_score = "";
            if(lc.get_output_gates().get(i).get_scores().get_score() != -1.0000)
                output_score = "\\n" + String.format("%8.2f", lc.get_output_gates().get(i).get_scores().get_score());
            String output_logics = "\\n" + BooleanLogic.logicString(lc.get_output_gates().get(i).get_logics());
            gvText += lc.get_output_gates().get(i).Name + "[shape=none,label=\"" + lc.get_output_gates().get(i).Name + output_score + output_logics + "\"]; \n";
        }

        for(int i=lc.get_Gates().size()-1; i>=0; --i) {

            Gate g = lc.get_Gates().get(i);
            String g_logics = "\\n" + BooleanLogic.logicString(g.get_logics());

            String graycolor = "100";

            String labelscore = "";
            if(g.get_scores().get_score() != -1.0) {
                labelscore = "\\n" + String.format("%8.2f", g.get_scores().get_score());
            }

            if(g.Type == Gate.GateType.OUTPUT || g.Type == Gate.GateType.OUTPUT_OR) { continue; }

            if(!g.Name.matches("[A-Za-z0-9_]+")) {
                g.Name = g.Type + "" + Integer.toString(g.RIndex); //abstract gate
            }

            String shape = g.Type + " " + g.Name  + " " + g.get_distance_to_input();

            if(g.Type == Gate.GateType.INPUT) {
                shape = g.Type + " " + g.Name + " " + g.get_distance_to_input();
                gvText += g.Name + "[shape=none,label=\"" + shape +labelscore + g_logics + "\"]; \n";
            }
            else {
                String shape_type = "box";
                if(g.Type == Gate.GateType.OR)
                    shape_type = "none";
                if(g.Type == Gate.GateType.AND)
                    shape_type = "oval";
                gvText += g.Name + "[shape="+shape_type+",style=filled,fillcolor=gray"+graycolor+",label=\"" + shape + labelscore + g_logics + "\"]; \n";
            }
        }

        for(int i=0; i<lc.get_Wires().size(); ++i) {
            Wire w = lc.get_Wires().get(i);

            String child = w.To.Name;
            String parent = w.From.Name;

            gvText += child + "->" + parent + "; \n";
        }

        gvText += "} \n";

        Util.fileWriter(_output_directory + outfile, gvText, false);
    }
    
    
    //currently known to work appropriately for structural_iphone_blue.v and 
    //cutting @ gates g6 and g8
    //currently fails when two qs nodes are on same path
//    public void printGraphvizDotText(IntegratedLogicCircuit ic, String outfile) {
//
//    		System.out.println("---------Generating Integrated Logic Circuit dot file.-----------");
//        String gvText = "#GRAPHVIZ_OUTPUT \n";
//        gvText += "digraph{ \n";
//        gvText += "rankdir=LR; \n";
//        gvText += "splines=ortho; \n";
//        
//        List<LogicCircuit> sub_lcs = ic.sub_lcs;
//        HashSet<String> input_output_edge_strings = new HashSet<String>();
//
//        int sublc_count = 0;
//        for(LogicCircuit sublc : sub_lcs) {
//        		//start of single cluster definition
//        	
//        		System.out.println("working on sublc: " + sublc_count);
//        		System.out.println(sublc.printGraph());
//        		gvText += "\nsubgraph cluster" + sublc_count + " {\n" ;
//        		//gvText += "node [style=filled, color=white];\n";
//        		gvText += "rank=same;\n";
//        		
//        		//integrated logic circuit contains duplicate wires
//    	        //this workaround solves that by using a hashset to hold the edge strings
//    	        HashSet<String> cluster_edges_set = new HashSet<String>();
//    	        
//	        for(Gate g: sublc.get_Gates()) { //put gates in cluster
//	        		
//	            String g_logics = "\\n" + BooleanLogic.logicString(g.get_logics());
//	
//	            String graycolor = "100";
//	
////	            String labelscore = "";
////	            if(g.get_scores().get_score() != -1.0) {
////	                labelscore = "\\n" + String.format("%8.2f", g.get_scores().get_score());
////	            }
//	
//	            if(g.Type == Gate.GateType.OUTPUT || g.Type == Gate.GateType.OUTPUT_OR) { continue; }
//	
//	            if(!g.Name.matches("[A-Za-z0-9_]+")) {
//	            		System.out.println("name matches nothing"); //not sure when we would ever get here, as Name should always be assigned?
//	                g.Name = g.Type + "" + Integer.toString(g.RIndex); //abstract gate
//	            }
//	
//	            String shape = g.Type + " " + g.Name  + " " + g.get_distance_to_input();
//	            
//	            //if a gate has a qs gate as its child, qs edge should be this in this cluster
//	            if(ic.terminal_parents.contains(g)) {
//	            		for(Gate qs_child:ic.terminal_gate_map.keySet()) { //find which qs gate connects to current parent gate
//	            			if(ic.terminal_gate_map.get(qs_child).contains(g)) {
//	            				//System.out.println("qs wire: " + qs_child.Name + "->" + g.Name );
//	    	            			cluster_edges_set.add(qs_child.Name + "->" + g.Name + "; \n");
//	            			}
//	            		}
//	            		
//	            		
//	            }
//	            
//	            if(g.Type == Gate.GateType.INPUT) {
//	            		continue;
//	            }
//	            
//	            else {
//	                String shape_type = "box";
//	                if(g.Type == Gate.GateType.OR)
//	                    shape_type = "none";
//	                if(g.Type == Gate.GateType.AND)
//	                    shape_type = "oval";
//	                if(ic.qs_gates.contains(g)) {
//	                		gvText += g.Name + "[shape=oval" + ",style=filled,fillcolor=gray"+graycolor+",label=\"" + shape + g_logics + "\"]; \n";
//	                }
//	                else {
//	                		gvText += g.Name + "[shape="+shape_type+",style=filled,fillcolor=gray"+graycolor+",label=\"" + shape + g_logics + "\"]; \n";
//	                }
//	            }    
//	        	}
//	        //end of cluster gates 
//	        
//	      //add edges present in cluster
//	        for(Wire w: sublc.get_Wires()) { //get non duplicate edges contained in cluster
//		        	String child = w.To.Name;
//		        	String parent = w.From.Name;
//	            if(w.To.Type==Gate.GateType.INPUT) {
//	            		input_output_edge_strings.add(child + "->" + parent + "; \n");
//	            }
//	            else if(w.From.Type == Gate.GateType.OUTPUT) {
//	            		input_output_edge_strings.add(child + "->" + parent + "; \n");
//	            }
//	            
//	            else if(ic.qs_gates.contains(w.To)) { //qs_gate edges should be in other cluster
//	            		//System.out.println("QS Wire From, To: " + w.From.Name + ", " + w.To.Name);
//	            		continue;
//	            }
//	            else {
//	            		//System.out.println("adding: " + child + "->" + parent);
//	            		cluster_edges_set.add(child + "->" + parent + "; \n");
//	            	}
//	        }
//	        
//	        //add edges back in
//	        for(String edge_str:cluster_edges_set) {gvText += edge_str;}
//	        
//	        sublc_count +=1 ;
//	        gvText += "\n }\n";
//	    
//        }
//        
//        //non cluster specific graph information
//        String out_rank = "{rank=sink;";
//        String in_rank = "{rank=source;";
//        for(Gate out :ic.output_gates) {
////          if(ic.get_output_gates().get(i).get_scores().get_score() != -1.0000)
////              output_score = "\\n" + String.format("%8.2f", ic.get_output_gates().get(i).get_scores().get_score());
//          String output_logics = "\\n" + BooleanLogic.logicString(out.get_logics());
//          gvText += out.Name + "[shape=none,label=\"" + out.Name + output_logics + "\"]; \n";
//          out_rank += out.Name + ";";
//          }
//        
//        for(Gate in:ic.input_gates) {
//        		String input_logics = "\\n" + BooleanLogic.logicString(in.get_logics());
//        		gvText += in.Name + "[shape=none,label=\"" + in.Name + input_logics + "\"]; \n";
//        		in_rank += in.Name + ";";
//        }
//        gvText += in_rank + "}\n";
//        gvText += out_rank + "}\n";
//        		
//      	for(String edge_str:input_output_edge_strings) {gvText += edge_str;}
//
//        gvText += "} \n";
//
//        Util.fileWriter(_output_directory + outfile, gvText, false);
//        System.out.println("---------------End of graphviz ----------------");
//    }
    
    
    
    //remember that: in graphviz, if an edge belongs to a cluster, so do its nodes
    // and node can only belong to single cluster --> thus qs (all is probably easier)
    // edges should all be defined outside cluster
    public void printGraphvizDotText(IntegratedLogicCircuit ic, String outfile) {
    	
    		System.out.println("---------Generating Integrated Logic Circuit dot file.-----------");
	    String gvText = "#GRAPHVIZ_OUTPUT \n";
	    gvText += "digraph{ \n";
	    gvText += "rankdir=LR; \n";
	    gvText += "splines=ortho; \n";
	    
	    List<LogicCircuit> sub_lcs = ic.sub_lcs;
	   
	    HashSet<String> edge_strings_set = new HashSet<String>();
	    HashSet<String> logic_gate_strings_set = new HashSet<String>();
	    
	    int sublc_count = 0;
	    for(LogicCircuit sublc : sub_lcs) {
	    		//start of single cluster definition, should contain names of nodes in this graph
	    	
	    		System.out.println("working on sublc: " + sublc_count);
	    		System.out.println(sublc.printGraph());
	    		gvText += "\nsubgraph cluster" + sublc_count + " {\n" ;
	    		//gvText += "node [style=filled, color=white];\n";
	    		gvText += "rank=same;\n";
	    		
	        for(Gate g: sublc.get_Gates()) { //put gates in cluster
	        		
	            String g_logics = "\\n" + BooleanLogic.logicString(g.get_logics());
	
	            String graycolor = "100";
	
	//            String labelscore = "";
	//            if(g.get_scores().get_score() != -1.0) {
	//                labelscore = "\\n" + String.format("%8.2f", g.get_scores().get_score());
	//            }
	            
	            //will be dealt with later
	            if(g.Type == Gate.GateType.OUTPUT || g.Type == Gate.GateType.OUTPUT_OR || g.Type == Gate.GateType.INPUT) { continue; }
	
	            if(!g.Name.matches("[A-Za-z0-9_]+")) {
	            		System.out.println("name matches nothing"); //not sure when we would ever get here, as Name should always be assigned?
	                g.Name = g.Type + "" + Integer.toString(g.RIndex); //abstract gate
	            }
	
	            String shape = g.Type + " " + g.Name  + " " + g.get_distance_to_input();
	  
	            String shape_type = "box";
	            if(g.Type == Gate.GateType.OR)
	                shape_type = "none";
	            if(g.Type == Gate.GateType.AND)
	                shape_type = "oval";
	            	if(ic.qs_gates.contains(g)) {
	           		gvText += g.Name + ";\n";
	           		shape_type = "oval";
	                		
	                }
	         	gvText += g.Name + ";\n";
	     		logic_gate_strings_set.add(g.Name + "[shape="+shape_type+",style=filled,fillcolor=gray"+graycolor+",label=\"" + shape + g_logics + "\"]; \n");
	                
	               
	        	}
	        //end of cluster gates 
	        
	        //collect edges present in subcircuit to be added at end --> integrated logic circuit has duplicate wires
	        //so workaround used hashset to store strings
	        for(Wire w: sublc.get_Wires()) { //get non duplicate edges contained in cluster
		        	String child = w.To.Name;
		        	String parent = w.From.Name;
	            	edge_strings_set.add(child + "->" + parent + "; \n");
	        }
	        
	        sublc_count +=1 ;
	        gvText += "\n }\n";
	    
	    }
	    //non cluster specific graph information
	    
	    String out_rank = "{rank=sink;";
	    String in_rank = "{rank=source;";
	    // add input/output gate attributes + specify node ranks
	    for(Gate out :ic.output_gates) {
	//      if(ic.get_output_gates().get(i).get_scores().get_score() != -1.0000)
	//          output_score = "\\n" + String.format("%8.2f", ic.get_output_gates().get(i).get_scores().get_score());
	      String output_logics = "\\n" + BooleanLogic.logicString(out.get_logics());
	      gvText += out.Name + "[shape=none,label=\"" + out.Name + output_logics + "\"]; \n";
	      out_rank += out.Name + ";";
	      }
	    
	    for(Gate in:ic.input_gates) {
	    		String input_logics = "\\n" + BooleanLogic.logicString(in.get_logics());
	    		gvText += in.Name + "[shape=none,label=\"" + in.Name + input_logics + "\"]; \n";
	    		in_rank += in.Name + ";";
	    }
	    gvText += in_rank + "}\n";
	    gvText += out_rank + "}\n";
	    	
	    //add all gates attributes in
	    for(String gate_str:logic_gate_strings_set) {gvText += gate_str;}
	    
	    //add all edges
	    for(String edge_str:edge_strings_set) {gvText += edge_str;}
	    
	
	    gvText += "} \n";
	
	    Util.fileWriter(_output_directory + outfile, gvText, false);
	    System.out.println("---------------End of graphviz ----------------");
}


    private String _home;
    private String _output_directory;
    private String _dateID;
}
