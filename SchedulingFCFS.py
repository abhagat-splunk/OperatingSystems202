def main(lines,randomNumberCounter):
	inp = map(int,raw_input().split())

	processes = []
	counter = 0
	for x in xrange(1,len(inp),4):#Quadruples
		"""
		Process Number, Arrival Time, Interval Upper Limit, CPU Time, Multiplier
		"""
		processes.append([counter,inp[x],inp[x+1],inp[x+2],inp[x+3]]) #Process Number and parameters
		counter+=1

	"""Not needed here I guess, lets see."""
	process_status = ["unstarted" for x in xrange(counter)]	


	#First Come First Serve
	processes.sort(key=lambda x: x[1]) 
	print processes
	while processes[0][1]!=0:
		if(processes[0][1]!=0):
			for x in xrange(len(processes)):
				processes[x][1]-=1	
	print processes
	print "Before cycle    0:\t"+("unstarted  0\t"*(counter))
	unstarted = []
	time_counter = 0
	ready_queue = []
	running_process = -1
	blocked = []
	terminating = []
	for x in processes:
		if x[1]==0:
			ready_queue.append(x[0])
			process_status[x[0]] = "ready"
		else:
			unstarted.append(x[0])
			process_status[x[0]] = "unstarted" 

	print unstarted
	while len(terminating)!=(counter):
		"""Use the CPU for the first process in the ready_queue"""
		if ready_queue:
			running_process = ready_queue[0]
			ready_queue = ready_queue[1:]
			processes[running_process][3]-=1
		t = randomOS(lines,processes[running_process][2],randomNumberCounter)
		randomNumberCounter+=1
		if t>processes[running_process][3]:
			t = processes[running_process][3]
			prev_CPU_burst = t
		else:
			prev_CPU_burst = processes[running_process][3]
			processes[running_process][3] = t
		"""Priority wise extending queue"""
		temp_ready_queue = []
		
		"""Append the process at the end if not yet completed."""
		if processes[running_process][3]>0:
			temp_ready_queue.append(running_process)
		else:
			terminating.append(running_process)
		""" Check unstarted processes"""
		if unstarted:
			for x in unstarted:
				processes[x][1]-=1
				if processes[x][1]==0:
					temp_ready_queue.append(running_process)
		ready_queue.extend(temp_ready_queue)			
		time_counter+=1	
	print running_process
	print ready_queue
	print processes


"""randomOS"""
def randomOS(lines,B,randomNumberCounter):
	return 1+(int(lines[randomNumberCounter])%B)
f = open("random-numbers.txt","r")
lines = f.readlines()
f.close()
randomNumberCounter = 0
#print randomOS(lines,5,randomNumberCounter)
#print random.choice(lines).strip()
main(lines,randomNumberCounter)
