#!/usr/bin/env python3

import networkx as nx
from random import randrange
import matplotlib.pyplot as plt

size_list = [x for x in range(10,1001,10)]
added_vertices_list = [0 for x in size_list]
#print(size_list)
#print(added_vertices_list)

print("")
for i in range(0, len(size_list)):
    G = nx.Graph()
    size = size_list[i]

    for node in range(0, size):
        G.add_node(node)
        
    added_vertices = 0
    while not nx.is_connected(G):
        vi = randrange(size)
        vf = randrange(size)
        if vf not in G.adj[vi]:
            G.add_edge(vi, vf)
            added_vertices = added_vertices + 1

    added_vertices_list[i] = added_vertices
    print("\x1b[1F" + "Last size calculated: " + str(size))

#print("Size List:\t" + str(size_list))
#print("Added vertices:\t" + str(added_vertices_list))

plt.plot(size_list, added_vertices_list, 'ro')
#plt.axis([0, 6, 0, 20])
plt.show()
