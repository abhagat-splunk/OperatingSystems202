inp = map(int,raw_input().split())

processes = []
counter = 1
for x in xrange(1,len(inp),4):
	processes.append([counter,inp[x],inp[x+1],inp[x+2],inp[x+3]]) #Process Number and parameters
	counter+=1
processes.sort(key=lambda x: x[1]) 
print processes


