#from __future__ import division
import sys
def main(lines,randomNumberCounter):
	inp = raw_input()
	print "The original input was:\t"+inp
	inp = inp.replace('(','')
	inp = inp.replace(')','')
	#inp[:] = (value for value in inp if value!='(' or value!=')')
	ioTIME = 0
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
	#First Come First Serve
	processes.sort(key=lambda x: x[1]) 
	sortedprocessesstring = str(counter)+" "

	proceses_order = []
	for x in processes:
		proceses_order.append(x[0])
		sortedprocessesstring+="("+str(x[1])+" "+str(x[2])+" "+str(x[3])+" "+str(x[4])+") " 
	print "The sorted input is:\t"+sortedprocessesstring
	offset_timeCounter = 0
	while processes[0][1]!=0:
		if(processes[0][1]!=0):
			offset_timeCounter+=1
			for x in xrange(len(processes)):
				processes[x][1]-=1	
	#print processes
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
	if verbose_flag:
		print "This detailed printout gives the state and remaining burst for each process"
		print "Before Cycle\t0:\tunstarted\tunstarted\tunstarted"
	while len(terminating)!=counter:
	#for _ in xrange(5):
		"""Check for Ready Queue processes"""
		if cpu_burst==0:
			#print "INSIDE IF"
			if running_process>=0:
				#print "LOL"	
				if initial_processes[running_process][3]==0:
					terminating.append(running_process)
					initial_processes[running_process][7] = time_counter-1
					initial_processes[running_process][8] = initial_processes[running_process][7]-initial_processes[running_process][1]-offset_timeCounter
					process_status[running_process] = "terminated"
					running_process=-1
				else: 
					#print "BLOCKED!"
					blocked.append(running_process)
					process_status[running_process] = "blocked"
					running_process=-1		
			if ready_queue:
				#s = sorted(s, key = lambda x: (x[1], x[2]))
				ready_queue.sort(key=lambda x: (-initial_processes[x][6],initial_processes[x][1]))
				running_process = ready_queue[0]
				initial_processes[running_process][6]=0
				process_status[running_process] = "running"
				ready_queue = ready_queue[1:]
				
				#print "Find burst when choosing ready process to run "+str(lines[randomNumberCounter])
				t = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
				randomNumberCounter+=1
				# print t
				# print initial_processes[running_process][3]
				if t>=initial_processes[running_process][3]:
					t = initial_processes[running_process][3]
					initial_processes[running_process][3]-=t
					initial_processes[running_process][5]=0				
				else:
					initial_processes[running_process][3] = initial_processes[running_process][3]-t
					initial_processes[running_process][5] = (initial_processes[running_process][4]*t)-1
					initial_processes[running_process][9]+= initial_processes[running_process][5]+1
				cpu_burst = t-1
		else:
			cpu_burst-=1
		for y in ready_queue:
			initial_processes[y][6]+=1
			initial_processes[y][10]+=1
		if verbose_flag:
			temp_str = ""
			for x in proceses_order:
				temp_str+=process_status[x]+"\t"
			print "Before Cycle\t"+str(time_counter)+":\t"+temp_str	
		# print "BEFORE CYCLE "+str(time_counter)#+"\t"+process_status[2]+"\t"+process_status[0]+"\t"+process_status[1]
		# print "CPU BURST"
		# print cpu_burst
		# print "RUNNING PROCESS"
		# print running_process
		# print "READY"
		# print ready_queue
		# print "BLOCKED"
		# print blocked
		# print "UNSTARTED"
		# print unstarted
		# print "TERMINATING"
		# print terminating
		# print "PROCESSES"
		# print initial_processes
		# print process_status
		"""Check if unstarted process has arrived"""
		temp_ready_unstarted_queue = []
		if unstarted:
			for x in unstarted:
				if initial_processes[x][1]==time_counter:
					process_status[x] = "ready"
					temp_ready_unstarted_queue.append(x)
		for x in temp_ready_unstarted_queue:
			unstarted.remove(x)
		#print "Unstarted to Ready"	
		#print temp_ready_unstarted_queue	
		ready_queue.extend(temp_ready_unstarted_queue)
		

		"""Check if blocked processes have finished their IO Burst time"""
		temp_ready_blocked_queue = []
		if blocked:
			ioTIME+=1
			for x in blocked:
				if initial_processes[x][5]==0:
					temp_ready_blocked_queue.append(x)
					process_status[x] = "ready"
				else:
					initial_processes[x][5]-=1
		for x in temp_ready_blocked_queue:
			blocked.remove(x)
		#print "Blocked to Ready queue"
		#print temp_ready_blocked_queue
		temp_ready_blocked_queue.sort(key=lambda x: process_arrival_index.index(x))	
		ready_queue.extend(temp_ready_blocked_queue)
		time_counter+=1
	#print ready_queue
	#print unstarted	
	#print process_status	
	#print initial_processes
	#print process_arrival_index
	print "The scheduling algorithm used was First Come First Served"
	temp_count = 0
	for x in proceses_order:
		print "Process "+str(temp_count)+":"
		print "\t(A,B,C,M) = ("+str(initial_processes[x][1])+", "+str(initial_processes[x][2])+", "+str(cpu_time_original[x])+", "+str(initial_processes[x][4])+")"
		print "\tFinishing Time: "+str(initial_processes[x][7])
		print "\tTurnaround Time: "+str(initial_processes[x][8])
		print "\tI/O Time: "+str(initial_processes[x][9])
		print "\tWaiting Time: "+str(initial_processes[x][10])
		print ""
		temp_count+=1
	



	print "Summary Data"
	ft_list = []
	tat_list = []
	wt_list = []
	for x in initial_processes:
		ft_list.append(x[7])
		tat_list.append(x[8])
		wt_list.append(x[10])
	ft = max(ft_list)
	print "\tFinishing Time:\t"+str(ft)
	cpu_time_used = 0
	io_time_used = 0
	for x in initial_processes:
		cpu_time_used+=x[8]-x[9]-x[10]
		io_time_used+=x[9]
	print "\tCPU Utilization: "+str("{0:.6f}".format(cpu_time_used/float(ft)))
	print "\tI/O Utilization: "+str("{0:.6f}".format(ioTIME/float(ft)))
	print "\tThroughput: "+str("{0:.6f}".format(counter*100/float(ft)))+" per hundred cycles"	
	print "\tAverage turnaround time: "+str("{0:.6f}".format(sum(tat_list)/float(counter)))
	print "\tAverage waiting time: "+str("{0:.6f}".format(sum(wt_list)/float(counter)))
"""randomOS"""
def randomOS(lines,B,randomNumberCounter):
	return 1+(int(lines[randomNumberCounter])%B)
f = open("random-numbers.txt","r")
lines = f.readlines()
f.close()
randomNumberCounter = 0
verbose_flag = False
try:
	if sys.argv[1] == '--verbose':
		verbose_flag = True
		main(lines,randomNumberCounter)
except:
	main(lines,randomNumberCounter)
