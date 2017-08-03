from collections import Counter
import networkx as nx
import matplotlib.pyplot as plt
import graphviz as gv
from IPython.display import display
import pprint
import warnings
from graph_funcs import *
from get_cello_graph_data import *
import itertools
import pickle
import numpy as np
import sys

#super crappy hacky solution but will probably be thrown out when rewrite in java
#anyways


#my script assumes the graph is 'forward' aka the output has no edges
#and the inputs do. cello assumes the opposite so graph needs to be effectively reversed
#also note that the cello graph dict has all nodes in dict, even if they have no edges

def reverse_graph(graph):
    #reverses graph given as adjacency dict --> currently works only w/ cello graph
    reversed_graph = {node:[] for node in graph}
    nodes = graph.keys()
    for node in graph.keys():
        if graph[node]:
            for child in graph[node]:
                reversed_graph[child].append(node)
    return reversed_graph

def score_graph(partitioned_subgraphs, parent_graph):
    parent_num_verts = count_vertexes(parent_graph)
    subgraph_num_verts = []
    for subgraph in partitioned_subgraphs:
        subgraph_num_verts.append(count_vertexes(subgraph))
    addition_verts_penalty = 0
    num_addition_verts = np.sum(subgraph_num_verts) - parent_num_verts
    if num_addition_verts == 0:
        num_addition_verts = 1
    else:
        num_addition_verts = np.exp(num_addition_verts)
    score = ((np.var(subgraph_num_verts) * num_addition_verts)) * 100
    return score

def main():
	cello_dnacompiler_filepath = sys.argv[1]
	#get cello data file

	cello_graph_data = get_cello_graph(cello_dnacompiler_filepath)


	#get just the graph and keep data separate
	cello_gr = {}
	cello_gr_data = {}

	for v in cello_graph_data.keys():
		cello_gr_data[v] = {}
		for k in cello_graph_data[v].keys():
			if k == 'children':
				#print cello_graph_data[v][k]
				edges = [child for child in cello_graph_data[v][k]]
				cello_gr[v] = edges
			else:
				cello_gr_data[v][k] = cello_graph_data[v][k]

	rev_gr = reverse_graph(cello_gr)
	my_gr = rev_gr

	sink = '0'
	edge_list = get_edge_list(my_gr)
	sources = find_sources(my_gr)    


	#determine all possible combinations of edges to cut
	vertexes = [v for v in my_gr.keys() if v not in sources + [sink]] #ignore source and sink verts
	edge_combos = edge_combinations(vertexes, 2)



	terminal_vertexes = edge_combinations(vertexes, 2)
	subgraph_partitions_dict = {t:[] for t in terminal_vertexes}

	for cut_set in terminal_vertexes:
		subgraphs = find_subgraphs(my_gr, sources, sink, cut_set)
		subgraph_partitions_dict[cut_set] += subgraphs
	

	#find highest scoring graph
	best_score = 1000
	best_graphs = []
	for k in subgraph_partitions_dict.keys():
		subgraphs = subgraph_partitions_dict[k]
		score = score_graph(subgraphs, my_gr)
		print 'k, score: ', k, score
		if score < best_score:
			best_graphs = subgraphs
			best_score = score
	
	#find fixed and subgraph inds
	fixed_inds = [ind for subgraph in best_graphs for ind in find_sinks(subgraph) if ind != sink]
	all_subgraph_inds = []
	for subgraph in best_graphs:
		verts = set([ind for k in subgraph.keys() for ind in subgraph[k]] + [k for k in subgraph.keys()])
		subgraph_inds = []
		for v in verts:
			if v not in sources and v != sink and v not in fixed_inds:
				subgraph_inds.append(v)
		if subgraph_inds:
			all_subgraph_inds.append(subgraph_inds)
	
	outfile_dir = ('/').join(cello_dnacompiler_filepath.split('/')[0:-1]) + '/'
	'/Users/jaipadmakumar/Desktop/voigt_lab/cello/run_with_cmdline_test_001/'
	#write a file out
	with open(outfile_dir + "circuit_subgraph_data_for_cello_TEST.txt", 'w') as out:
		print "Fixed inds are: ", fixed_inds
		print "Subgraph found are: ", all_subgraph_inds
		fixed_str = ''
		subgr_str = ''
		for i in fixed_inds:
			fixed_str += str(i) + ","
		for sub in all_subgraph_inds:
			for i in sub:
				subgr_str += str(i) + ','
			subgr_str = subgr_str[0:-1] + "||" #get rid of extra final comma
		print fixed_str
		print subgr_str
		out.write(fixed_str[0:-1]) #get rid of final comma
		out.write("\n")
		out.write(subgr_str[0:-2]) #get rid of extra ||
	return 
		
main()





    
    
    