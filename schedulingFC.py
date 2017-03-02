def main(lines,randomNumberCounter):
	inp = map(int,raw_input().split())

	processes = []
	counter = 0
	for x in xrange(1,len(inp),4):#Quadruples
		"""
		Process Number, Arrival Time, Interval Upper Limit, CPU Time, Multiplier, IO Burst
		"""
		processes.append([counter,inp[x],inp[x+1],inp[x+2],inp[x+3],-1]) #Process Number and parameters
		counter+=1
	initial_processes = processes[:]
	"""Not needed here I guess, lets see."""
	process_status = ["unstarted" for x in xrange(counter)]	
	process_arrival_index = []

	#First Come First Serve
	processes.sort(key=lambda x: x[1]) 
	print initial_processes
	#print processes
	while processes[0][1]!=0:
		if(processes[0][1]!=0):
			for x in xrange(len(processes)):
				processes[x][1]-=1	
	print processes
	unstarted = []
	time_counter = 1
	ready_queue = []
	running_process = -1
	blocked = []
	terminating = []
	for x in processes:
		process_arrival_index.append(x[0])
		if x[1]==0:
			ready_queue.append(x[0])
			process_status[x[0]] = "ready"
		else:
			unstarted.append(x[0])
			process_status[x[0]] = "unstarted" 
	#print unstarted
	#print ready_queue
	cpu_burst = 0
	while len(terminating)!=counter:
	#for _ in xrange(5):
		"""Check for Ready Queue processes"""
		if cpu_burst==0:
			#print "INSIDE IF"
			if running_process>=0:	
				if initial_processes[running_process][3]==0:
					terminating.append(running_process)
					running_process=-1
				else:
					m = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
					initial_processes[running_process][5] = initial_processes[running_process][4]*m
					print "Find I/O burst when blocking a process "+str(lines[randomNumberCounter])
					randomNumberCounter+=1
					blocked.append(running_process)
					running_process=-1		
			if ready_queue:
				running_process = ready_queue[0]
				ready_queue = ready_queue[1:]
				print "Find burst when choosing ready process to run "+str(lines[randomNumberCounter])
				t = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
				randomNumberCounter+=1
				# print t
				# print initial_processes[running_process][3]
				if t>initial_processes[running_process][3]:
					t = initial_processes[running_process][3]				
				else:
					initial_processes[running_process][3] = initial_processes[running_process][3]-t
				cpu_burst = t-1	
		else:
			cpu_burst-=1
		print "BEFORE CYCLE"
		print time_counter
		print "CPU BURST"
		print cpu_burst
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
		#print "LOL HERE"	
		"""Check if unstarted process has arrived"""
		temp_ready_unstarted_queue = []
		if unstarted:
			for x in unstarted:
				if initial_processes[x][1]==time_counter:
					process_status[x] = "ready"
					temp_ready_unstarted_queue.append(x)
		for x in temp_ready_unstarted_queue:
			unstarted.remove(x)
		print "Unstarted to Ready"	
		print temp_ready_unstarted_queue	
		ready_queue.extend(temp_ready_unstarted_queue)
		

		"""Check if blocked processes have finished their IO Burst time"""
		temp_ready_blocked_queue = []
		if blocked:
			for x in blocked:
				if initial_processes[x][5]==0:
					temp_ready_blocked_queue.append(x)
				else:
					initial_processes[x][5]-=1
		for x in temp_ready_blocked_queue:
			blocked.remove(x)
		print "Blocked to Ready queue"
		print temp_ready_blocked_queue
		temp_ready_blocked_queue.sort(key=lambda x: process_arrival_index.index(x))	
		ready_queue.extend(temp_ready_blocked_queue)
		time_counter+=1
	#print ready_queue
	#print unstarted	
	#print process_status	
	#print initial_processes
	print process_arrival_index
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
