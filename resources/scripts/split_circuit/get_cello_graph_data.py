#!/usr/bin/python
import re
import pickle

#file = '/Users/jaipadmakumar/Desktop/voigt_lab/cello/demo/demo_abstract0xFE001/demo_abstract0xFE001_dnacompiler_output.txt'
#file = '/Users/jaipadmakumar/Desktop/voigt_lab/cello/cello/demo/demo_abstract0xFE001/demo_abstract0xFE001_dnacompiler_output.txt'
def get_cello_graph(dnacompiler_output_file):
	file = dnacompiler_output_file
	#use finite state machine to find start of wiring diagram indicated by presence of 
	#'----- Logic Circuit' and read until end of diagram indicted by a newline
	wiring_diagram_lines = []
	found_start = False
	with open(file, 'r') as f:
		for line in f.readlines():
			if found_start:
				if line == '\n': 
					break
				else:
					wiring_diagram_lines.append(line.strip())
			else:
				if line.strip().startswith('----- Logic Circuit'):
					found_start = True
					wiring_diagram_lines.append(line.strip())

	#convert wiring diagram to adjacency dict representation of DAG
	#with extra value to store gate type
	vertexes = []

	#print wiring_diagram_lines
	''' ['OUTPUT', '0001', 'out1', '0', '(1)']
	  becomes adj dict
	  '''
  
	adj_dict = {}
	for line in wiring_diagram_lines[1:]:
		#print line
		split = line.split()
		type = split[0]
		bool_string = split[1]
		name = split[2]
		gate_index = split[3]
		if type != 'INPUT':
			children_regex = re.findall( ('[0-9]+'), split[4])
			children = [s for s in children_regex if children_regex]
		else:
			children = []
	
		adj_dict[gate_index] = {'children':children, 'type':type, 
								'bool_string':bool_string, 'name':name}
		#print 'split line:',split
	return adj_dict

	#with open('pickled_cello_graph.txt', 'w') as out:
	#	pickle.dump(adj_dict, out)

if __name__ == "__main__":
	main()






	