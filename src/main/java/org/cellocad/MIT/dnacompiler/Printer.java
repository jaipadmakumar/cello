package org.cellocad.MIT.dnacompiler;

import org.cellocad.MIT.figures.Graphviz;
import org.cellocad.MIT.figures.ScriptCommands;

public class Printer {
	
	public static void writeWiringDiagram(IntegratedLogicCircuit ic, String outfile_name, Args options) {
		Graphviz graphviz = new Graphviz(options.get_home(), options.get_output_directory(), options.get_jobID());
		ScriptCommands script_commands = new ScriptCommands(options.get_home(), options.get_output_directory(), options.get_jobID());
		//String outfile_name = "IC_WIRING_DIAGRAM_" + count;
		//for(Gate combo:ic.qs_gates) {outfile_name += "_" + combo.Name;}
		outfile_name += ".dot";
		graphviz.printGraphvizDotText(ic, outfile_name);
		script_commands.makeDot2Png(outfile_name);
	}
	
	public static void writeWiringDiagram(LogicCircuit lc, String outfile_name, Args options) {
		//add code to write wiring diagram for logic circuit
	}
	
}
