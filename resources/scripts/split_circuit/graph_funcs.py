#!/usr/bin/python

import graphviz as gv
import warnings
import pprint
import itertools

def find_all_paths(graph, start, end, path=[]):
	'''Finds all possible paths in graph between start and end by implementing 
	a breadth-first search. Graph should be passed as an adjacency dictionary.
	Returns a list of tuples representing all paths between 'start' and 'end' vertexes.'''

	path = path + [start]
	if start == end:
		return [path]
	if not graph.has_key(start):
		return []
	paths = []
	for node in graph[start]:
		if node not in path:
			newpaths = find_all_paths(graph, node, end, path)
			for newpath in newpaths:
				paths.append(newpath)
	return paths

def fep(paths):
	'''Find all the edges in a path list. Given a list of paths, returns a flat list 
	of paths as edges in tuple format. For ex. [['a','b','c'], ['e','k','l']]
	returns [('a','b'), ('b','c'), ('e','k'), ('k','l')]. '''

	path_edges = []
	for path in paths:
		if len(path) == 1: #path consists of one single vertex aka isn't actually path,
			pass #only way this should happen is when cut between vertex and sink
		else:
			#have something like this: [1,5,6,8,...] --> [1,5],[5,6],[6,8],...
			for i in range(0,len(path)-1):
				path_edges.append((path[i],path[i+1]))
	#print (path_edges)
	return list(set(path_edges))

def get_edge_list(graph):
	'''Takes a graph in an adjacency dictionary and converts in into an edge list'''
	edge_list = []
	for k in graph:
		for edge in graph[k]:
			edge_list.append([k,edge])
	return edge_list

def edge_list_to_adjacency_dict(edge_list):
	'''Takes a graph as an edge list and returns it as an adjacency dict.'''
	adj_dict = {e[0]:[] for e in edge_list}
	for edge in edge_list:
		k = edge[0]
		adj_dict[k].append(edge[1])
	return adj_dict    
			
def find_sources(graph):
	'''Finds all source nodes (nodes w/ indegree=0) in graph. 'Graph' is an adjacency 
	dict. Returns a list of source vertexes.'''

	#doesn't rely on numbering but untested
	sources = []
	vertexes = [v for k in graph.keys() for v in graph[k]]
	for k in graph.keys():
		if k not in vertexes:
			sources.append(k)
	return sources

def find_sinks(graph):
	'''Finds all sink nodes in graph. Aka all nodes with outdegree=0'''
	sinks = []
	vertexes = [v for k in graph.keys() for v in graph[k]]
	for v in set(vertexes):
		if v not in graph.keys():
			sinks.append(v)
	return sinks

def count_vertexes(graph):
	sources = find_sources(graph)
	num_vertexes = 1 #start at 1 to account for single output/terminal node
	for k in graph.keys():
		if k not in sources:
			num_vertexes+=1
	return num_vertexes

def check_graph_equality(g1, g2):
	g1 = [str(edge[0])+str(edge[1]) for edge in get_edge_list(g1)]
	g2 = [str(edge[0])+str(edge[1]) for edge in get_edge_list(g2)]
	#print sorted(set(x))
	#print sorted(set(y))
	if set(g1) == set(g2):
		return True
	else:
		return False
	
def draw_graph(graph, cut_edge=None):
	'''Draws a single graph, passed as an adjacency dictionary'''
	x = gv.Digraph(format='svg')
	source_nodes = find_sources(graph)
	sink_nodes = find_sinks(graph)
	for k in graph.keys():
		x.node(str(k))
	   # print 'inserting node:' , k
		for e in graph[k]:
			x.edge(str(k),str(e))
			#print 'adding edge', (str(k),str(e))
	for source in source_nodes:
		x.node(str(source), shape = 'rarrow',color='turquoise',style='filled', rank='source')
	for sink in sink_nodes:
		x.node(str(sink), color='red',style='filled', rank='sink')

	x.attr(rankdir='LR')
	if cut_edge is not None:
		x.node(str(cut_edge[0]), color='orange', style='filled')
	return x

def path_edges(paths):
	'''Similar to fep() but returns a list of lists where each sublist is a list of tuples
	representing edges. For ex. [['a','b','c'], ['e','k','l']]
	returns [[('a','b'), ('b','c')], [('e','k'), ('k','l')]]. '''

	all_path_edges = []
	for path in paths:
		path_edges = []
		if len(path) == 1: #path consists of one single vertex aka isn't actually path,
			pass #only way this should happen is when cut between vertex and sink
		else:
			#have something like this: [1,5,6,8,...] --> [1,5],[5,6],[6,8],...
			for i in range(0,len(path)-1):
				path_edges.append((path[i],path[i+1]))
		all_path_edges.append(path_edges)
	#print (path_edges)
	return all_path_edges

def find_subpaths(path, edges):
	'''Given a 'path' of n 2-item tuples where each tuple represents an edge between two nodes
	and 'edges', a list of 2-item tuples corresponding to edges to remove, returns 'sub_path', 
	a list of sliced paths. Checks if any edges are in path and slices path into corresponding
	subpaths s.t. each subpath spans the edges in between the edges given in 'edges' excluding
	the 'edges' themselves. In other words, removes edges in 'edges' from 'path' 
	and returns the corresponding subpaths.

	WRONG: NOW RETURNS INCLUSIVE OF SLICE EDGE, EDIT ABOVE AS WELL, ALSO NOT WELL TESTED
	For ex, if path=[('a','b'),('b','c'),('c','d'),('d','e'),('e','f'),('f','g'),('g','h')]
	and edges=[('b','c'), ('e','f'), ('d','e'), ('h','j')]
	returns [[('a', 'b')], [('c', 'd')], [('f', 'g'), ('g', 'h')]]
	'''

	path_set = set(path)
	edges_set = set(edges)
	inds = []
	edges_in_path = []
	if not path_set.intersection(edges_set):
		return [path]
	for e in edges:
		if e in path:
			inds.append(path.index(e))
			edges_in_path.append(e)
	inds = sorted(inds)
	#assuming inds in order of path
	#sub_paths = [path[0:inds[0]], ]
	sub_paths = [path[0:inds[0]+1], ]
	#print inds
	for i in range(0,len(inds[:-1])):
		#WARNING CASE WHERE EDGES ARE ADJACENT RESULTS IN SINGLY RETURNED EDGE AS PATH
		#MAY NOT BE DESIRABLE BEHAVIOR
		#sub_path = path[inds[i]+1:inds[i+1]]
		sub_path = path[inds[i]+1:inds[i+1]+1]
		sub_paths.append(sub_path)
		#if len(sub_path) == 1: 
		 #   sub_paths.append(sub_path) #in case edges are adjacent and get only edge
		#print sub_path
	#sub_paths.append(path[inds[-1]+1:]) #insert final subpath spanning to end of path list
	sub_paths.append(path[inds[-1]+1:])
	return sub_paths

def edge_combinations(array, k):
	'''Return list of tuples containing all 1...k combinations of edges.'''
	combinations = []
	for i in range(1,k+1):
		k_combos = itertools.combinations(array, i)
		combinations += k_combos
	return combinations
	
def draw_graph_clustered(subgraphs, parent_graph):
	''' Draw clustered graph. Takes as input:
		'subgraphs' = list of adjacency dicts representing subgraphs
		'parent_graph' = parent graph subgraphs originate from '''

	#BUG: crashes if one of the subgraphs is empty (not super important b/c shouldn't happen)
	#BUG: draws multiple edges for QS nodes

	g = gv.Digraph(format='svg')
	g.attr(rankdir='LR')

	sources = find_sources(parent_graph)
	qs_nodes = [find_sinks(subgraph) for subgraph in subgraphs]
	qs_nodes_flat = [n for l in qs_nodes for n in l]
	#print qs_nodes_flat

	#HARDCODED, SHOULD BE CHANGED
	source_int_map = {'a':100, 'b':200,'c':300}
	#for source in sources:
	 #   g.node(str(source), shape = 'rarrow',color='turquoise',style='filled', rank='source')

	#NEED TO COLOR FINAL SINK NODE
	final_sink = 'z'
	#g.node(str(final_sink), color='red',style='filled')


	subgraph_vertices = []
	for subgraph in subgraphs:
		#print 'subgraph:'
		#pprint.pprint(subgraph)
		for k in subgraph.keys():
			subgraph_vertices.append(k)
			subgraph_vertices += subgraph[k]
		
	vertex_subgraph_map = {v:[] for v in subgraph_vertices}
	i = 0
	for vertex in set(subgraph_vertices):
		if vertex in sources:
			vertex_subgraph_map[vertex] = [i] * len(subgraphs)
			i+=1
		elif vertex in qs_nodes_flat:
			vertex_subgraph_map[vertex] = [i] * len(subgraphs)
			i+=1
		else:
			for subgraph in subgraphs:
				#print 'subgraph', subgraph.keys()
				if vertex in subgraph.keys():
				   # print 'vertex',vertex
					vertex_subgraph_map[vertex].append(i)
					i+=1
				else:
					vertex_subgraph_map[vertex].append('')


	#print 'vertex subgraph map'
	#pprint.pprint(vertex_subgraph_map)
	subgraph_edges = [get_edge_list(subgraph) for subgraph in subgraphs]

	subgraph_mapped_edges = []
	for i in range(0, len(subgraph_edges)):
		mapped_edges = []
		for edge in subgraph_edges[i]:
			mapped_edge = (vertex_subgraph_map[edge[0]][i], vertex_subgraph_map[edge[1]][i])
			mapped_edges.append(mapped_edge)
		subgraph_mapped_edges.append(mapped_edges)
	#print 'subgraph map'
	#pprint.pprint(subgraph_mapped_edges)

	#determine QS edges

	parent_graph_edges = [tuple(t) for t in get_edge_list(parent_graph)]
	unique_subgraph_edges = set([tuple(t) for l in subgraph_edges for t in l])
	qs_edges = set.difference(set(parent_graph_edges), unique_subgraph_edges)

	#print 'qs edges', qs_edges

	for i in range(0, len(subgraphs)):
		name = 'cluster_' + str(i)
		with g.subgraph(name=name) as c:
			for edge in subgraph_edges[i]:
				node1 = vertex_subgraph_map[edge[0]][i]
				node2 = vertex_subgraph_map[edge[1]][i]
				#print edge[0]
				if edge[0] in sources:
					c.node(str(node1), label=edge[0], shape = 'rarrow', 
						   color='turquoise',style='filled', rank='source')
				else:
					c.node(str(node1), label=edge[0])
			
				c.node(str(node2), label=edge[1])
				c.edge(str(node1), str(node2))
	#insert QS edges back in
	print 'qs edges', qs_edges
	for qs_edge in qs_edges:
		edge1_inds = vertex_subgraph_map[qs_edge[0]][0]
		#print 'vsg map'
		#print vertex_subgraph_map.keys()
		edge2_inds = [ind for ind in vertex_subgraph_map[qs_edge[1]] if ind]
		for e2 in edge2_inds:
			e1 = edge1_inds
			g.edge(str(e1), str(e2)) #g.edge(e1, e2)
			g.node(str(e1), color='orange', style='filled')
	return g
	
	
def find_subgraphs(graph, source_nodes, sink_node, terminal_vertexes):
	'''Main function to find subgraphs in graph'''
	#should return list of dicts corresponding to subgraphs based on terminal vertexes

	#BUG: Returns an empty subgraph if all edges cut lead to sink --> non-fatal but 
	#causes crash in plotting function (currently fixed by sticking in try except block)

	all_paths = []
	for source in source_nodes:
		all_paths = all_paths + find_all_paths(graph, source, sink_node)


	path_partition_graphs = {v:[] for v in terminal_vertexes}
	for edge in terminal_vertexes:
		for source in source_nodes:
			path_partition_graphs[edge[0]] += find_all_paths(graph, source, edge[0])

	sliced_path_subgraphs = {v:[] for v in terminal_vertexes}
	for v in terminal_vertexes:
		verts = [s for s in set(terminal_vertexes) if s!=v] #terminal vertexes excluding current one
		for subpath in path_partition_graphs[v]:
			if set.intersection(set(subpath), verts):
				#lookback in path from terminal to first other terminal vertex
				inds = [subpath.index(node) for node in subpath if node in verts]
				sliced_subpath = subpath[sorted(inds)[-1]+1:]
				sliced_path_subgraphs[v].append(sliced_subpath)
				#print 'subpath', sliced_subpath
			else:
				sliced_path_subgraphs[v].append(subpath)

	#collapse subgraphs into unique paths aka dedup paths
	unique_path_subgraphs = {}
	for k in sliced_path_subgraphs.keys():
		unique_path_subgraphs[k] = list(set([tuple(path) for path in sliced_path_subgraphs[k]]))
	#print 'unique path subgraphs'
	#pprint.pprint(unique_path_subgraphs) 

	#you should probably be able to do this entire thing in one step right here using 
	#something similar to original find_subpaths approach --> when you get the 'inds' list
	#containing the intersecting QS nodes for that path, just split path based on that
	#using a function similar to find_subpaths and sink those paths in their respective list. 
	#That should give you the path disjoint list you're looking for.

	final_graph_paths = []
	for path in all_paths:
		intersecting_nodes = set(path).intersection(set(terminal_vertexes))
		if intersecting_nodes:
			 #need to do some kind of path slicing --> *should be able to do ALL required slicing
			#right here! just pass 'inds' to a function that splits a path based on those indices
			#and returns the number of paths desired

			inds = [path.index(n) for n in intersecting_nodes]
			new_path = path[sorted(inds)[-1]+1:]
			final_graph_paths.append(new_path)
		else:
			#path doesn't cross any QS nodes so just keep as is, runs from source to sink
			final_graph_paths.append(path)
	unique_final_graph_paths = list(set([tuple(path) for path in final_graph_paths]))
	#print 'final graph'
	#pprint.pprint(unique_final_graph_paths)


	cut_subgraphs = []
	for k in unique_path_subgraphs:
		subgraph_edges = fep(unique_path_subgraphs[k])
		cut_subgraphs.append(edge_list_to_adjacency_dict(subgraph_edges))

	final_graph_edges = fep(unique_final_graph_paths)
	final_graph = edge_list_to_adjacency_dict(final_graph_edges)

	#print final_graph_edges
	#print cut_subgraphs
	return cut_subgraphs + [final_graph]


if __name__ == "__main__":
	main()
