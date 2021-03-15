#!/usr/bin/env python3

import networkx as nx
from random import randrange
import matplotlib.pyplot as plt
import math 

size = 25000

def genWithPreferentialAttachment(size):
    G = nx.Graph()
    prefList = [0]
    G.add_node(0)
    for i in range(1, size):
        vfrom = prefList[randrange(len(prefList))]
        G.add_edge(vfrom, i)
        prefList.append(vfrom)
        prefList.append(i)
    return G

G = genWithPreferentialAttachment(size)
freq = {}
for key in G.adj:
    kfreq = len(G.adj[key])
    if kfreq in freq:
        freq[kfreq] = freq[kfreq] + 1
    else:
        freq[kfreq] = 1

freqx = []
freqy = []
freqylog = []
for key in freq:
    freqx.append(key)
    freqy.append(freq[key])
    freqylog.append(math.log(freq[key]))

#print(G.edges)
#nx.draw(G)
plt.plot(freqx, freqy, 'ro')
#plt.plot(freqx, freqylog, 'ro')
plt.show()