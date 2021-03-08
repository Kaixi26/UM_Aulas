#!/usr/bin/env python3

import networkx as nx
from random import randrange
import matplotlib.pyplot as plt

def genConnected(size):
    G = nx.Graph()
    for node in range(0, size):
        G.add_node(node)
    while not nx.is_connected(G):
        vi = randrange(size)
        vf = randrange(size)
        if vf not in G.adj[vi]:
            G.add_edge(vi, vf)
    return G

max_size = 100
step = 10
repetitions = 25

size_list = [x for x in range(10,(max_size+1),step)]
added_vertices_list = [0 for x in size_list]
#print(size_list)
#print(added_vertices_list)

for i in range(0, len(size_list)):
    total = 0
    for j in range(0,repetitions):
        total = total + len(genConnected(size_list[i]).edges)
    added_vertices_list[i] = total/repetitions
    print("\x1b[1F" + "Last size calculated: " + str(size_list[i]))

#print("Size List:\t" + str(size_list))
#print("Added vertices:\t" + str(added_vertices_list))

plt.plot(size_list, added_vertices_list, 'ro')
plt.plot(size_list, added_vertices_list)
plt.show()
