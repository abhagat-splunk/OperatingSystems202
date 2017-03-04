def main(lines,randomNumberCounter):
	inp = raw_input()
	inp = inp.replace('(','')
	inp = inp.replace(')','')
	#inp[:] = (value for value in inp if value!='(' or value!=')')

	inp = map(int,inp.split())
	processes = []
	counter = 0
	cpu_time_original = []
	for x in xrange(1,len(inp),4):#Quadruples
		"""
		0:Process Number, 1:Arrival Time, 2:Interval Upper Limit, 3:CPU Time, 4:Multiplier, 5:IO Burst, 6:ReadyQueueTime, 7:FinishingTime, 8:TurnAroundTime, 9:IOTime, 10:ReadyQueueOverallTime
		"""
		processes.append([counter,inp[x],inp[x+1],inp[x+2],inp[x+3],-1,0,0,0,0,0]) #Process Number and parameters
		cpu_time_original.append(inp[x+2])
		counter+=1
	"""Not needed here I guess, lets see. Status for debugging."""
	process_status = ["unstarted" for x in xrange(counter)]	
	process_arrival_index = []

	initial_processes = processes[:]
	processes.sort(key=lambda x: x[1]) 

	offset_timeCounter = 0
	unstarted = []
	time_counter = 0
	ready_queue = []
	running_process = -1
	blocked = []
	terminating = []
	quantum = 0
	temp_ready = []
	for x in processes:
		process_arrival_index.append(x[0])
		if x[1]==0:
			ready_queue.append(x[0])
			process_status[x[0]] = "ready"
		else:
			unstarted.append(x[0])
			process_status[x[0]] = "unstarted" 
	print unstarted
	print ready_queue
	cpu_burst = [0 for x in xrange(counter)]
	while len(terminating)!=counter:
		"""Check if unstarted process has arrived"""
		temp_ready_unstarted_queue = []
		if unstarted:
			for x in unstarted:
				print "LOL"
				if initial_processes[x][1]==time_counter:
					process_status[x] = "ready"
					temp_ready_unstarted_queue.append(x)
		for x in temp_ready_unstarted_queue:
			unstarted.remove(x)
		print temp_ready_unstarted_queue
		time_counter+=1


		"""If no running processes, retrieve from ready queue"""
		if running_process==-1:
			if ready_queue:
				running_process = ready_queue[0]
				if cpu_burst[running_process]<=0:
					print "Find burst when choosing ready process to run "+str(lines[randomNumberCounter])
					t = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
					randomNumberCounter+=1
					print t
					#print initial_processes[running_process][3]
					if t>=initial_processes[running_process][3]:
						t = initial_processes[running_process][3]
						initial_processes[running_process][3]-=t
						initial_processes[running_process][5]=0				
					else:
						initial_processes[running_process][3] = initial_processes[running_process][3]-t
						initial_processes[running_process][5] = (initial_processes[running_process][4]*t)
						initial_processes[running_process][9]+= initial_processes[running_process][5]
					cpu_burst[running_process] = t-1
		quantum-=1

		print "QUANTUM: "+str(quantum)
		print "BEFORE CYCLE "+str(time_counter)+"\t"+process_status[2]+"\t"+process_status[0]+"\t"+process_status[1]
		print "CPU BURST"
		print str(cpu_burst[2])+"\t"+str(cpu_burst[0])+"\t"+str(cpu_burst[1])
		print "RUNNING PROCESS"
		print running_process
		print "READY"
		print ready_queue
		print "BLOCKED"
		print blocked
		print "UNSTARTED"
		print unstarted
		print "TERMINATING"
		print terminating
		print "PROCESSES"
		print initial_processes
		print process_status

		"""Adding ReadyQueueTime"""	
		for y in ready_queue:
			initial_processes[y][6]+=1			
		time_counter+=1

	for x in xrange(len(initial_processes)):
		print "Process "+str(initial_processes[x][0])+":"
		print "\t(A,B,C,M) = ("+str(initial_processes[x][1])+", "+str(initial_processes[x][2])+", "+str(cpu_time_original[x])+", "+str(initial_processes[x][4])+")"
		print "\tFinishing Time: "+str(initial_processes[x][7])
		print "\tTurnaround Time: "+str(initial_processes[x][8])
		print "\tI/O Time: "+str(initial_processes[x][9])
		print "\tWaiting Time: "+str(initial_processes[x][10])
		print ""

"""randomOS"""
def randomOS(lines,B,randomNumberCounter):
	return 1+(int(lines[randomNumberCounter])%B)


f = open("random-numbers.txt","r")
lines = f.readlines()
f.close()
randomNumberCounter = 0
main(lines,randomNumberCounter)