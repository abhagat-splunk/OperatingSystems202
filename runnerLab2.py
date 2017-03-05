import sys
import os


def checkForUnstarted(initial_processes,time_counter,ready_queue,unstarted,process_status):
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

def Unimain(inp,lines,randomNumberCounter):
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
	#print initial_processes
	#print processes
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
	us = unstarted[:]
	cpu_burst = 0
	if verbose_flag:
		print "This detailed printout gives the state and remaining burst for each process"
		print "Before Cycle\t0:\t"+"unstarted "*counter
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
					ready_queue = ready_queue[1:]
					running_process=-1
				else: 
					blocked.append(running_process)
					process_status[running_process] = "blocked"
					while initial_processes[running_process][5]!=0:
						ioTIME+=1
						checkForUnstarted(initial_processes,time_counter,ready_queue,unstarted,process_status)
						if verbose_flag:
							temp_str = ""
							for x in proceses_order:
								if process_status[x]=="running":
									temp_str+=process_status[x]+" "+str(cpu_burst+1)+" "
								elif process_status[x]=="blocked":
									temp_str+=process_status[x]+" "+str(initial_processes[x][5])+" "
								else:
									temp_str+=process_status[x]+" 0 "	
							print "Before Cycle\t"+str(time_counter)+":\t"+temp_str#+"\t"+process_status[2]+"\t"+process_status[0]+"\t"+process_status[1]
						for y in ready_queue:
							if y!=running_process:
								initial_processes[y][6]+=1
								initial_processes[y][10]+=1
						time_counter+=1
						initial_processes[running_process][5]-=1
						#print process_status
					running_process=-1	
						
			if ready_queue:
				running_process = ready_queue[0]
				#initial_processes[running_process][10]+=initial_processes[running_process][6]
				initial_processes[running_process][6]=0
				process_status[running_process] = "running"
				# if verbose_flag:
				# 	print "Find burst when choosing ready process to run "+str(lines[randomNumberCounter])
				t = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
				randomNumberCounter+=1
				
				if t>=initial_processes[running_process][3]:
					t = initial_processes[running_process][3]
					initial_processes[running_process][3]-=t
					initial_processes[running_process][5]=0				
				else:
					initial_processes[running_process][3] = initial_processes[running_process][3]-t
					initial_processes[running_process][5] = (initial_processes[running_process][4]*t)
					initial_processes[running_process][9]+= initial_processes[running_process][5]
				cpu_burst = t-1	
		else:
			cpu_burst-=1
		for y in ready_queue:
			if y!=running_process:
				initial_processes[y][6]+=1
				initial_processes[y][10]+=1
		if verbose_flag:
			temp_str = ""
			for x in proceses_order:
				if process_status[x]=="running":
					temp_str+=process_status[x]+" "+str(cpu_burst+1)+" "
				elif process_status[x]=="blocked":
					temp_str+=process_status[x]+" "+str(initial_processes[x][5]+1)+" "
				else:
					temp_str+=process_status[x]+" 0 "	
			print "Before Cycle\t"+str(time_counter)+":\t"+temp_str#+"\t"+process_status[2]+"\t"+process_status[0]+"\t"+process_status[1]
		"""Check if unstarted process has arrived"""
		checkForUnstarted(initial_processes,time_counter,ready_queue,unstarted,process_status)
		time_counter+=1
	print "The scheduling algorithm used was Uniprocessing"
	temp_count = 0
	for x in (proceses_order):
		print "Process "+str(temp_count)+":"
		print "\t(A,B,C,M) = ("+str(initial_processes[x][1])+", "+str(initial_processes[x][2])+", "+str(cpu_time_original[x])+", "+str(initial_processes[x][4])+")"
		print "\tFinishing Time: "+str(initial_processes[x][7])
		print "\tTurnaround Time: "+str(initial_processes[x][8])
		print "\tI/O Time: "+str(initial_processes[x][9])
		if initial_processes[x][0] in us and initial_processes[x][1]%2==0:
			print "\tWaiting Time: "+str(initial_processes[x][10]-1)
			initial_processes[x][10]-=1
		else:
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
	print "\tCPU Utilization:\t"+str("{0:.6f}".format(cpu_time_used/float(ft)))
	print "\tI/O Utilization:\t"+str("{0:.6f}".format(ioTIME/float(ft)))
	print "\tThroughput:\t"+str("{0:.6f}".format(counter*100/float(ft)))+" per hundred cycles"	
	print "\tAverage turnaround time:\t"+str("{0:.6f}".format(sum(tat_list)/float(counter)))
	print "\tAverage waiting time:\t"+str("{0:.6f}".format(sum(wt_list)/float(counter)))


def FCmain(inp,lines,randomNumberCounter):
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
		print "Before Cycle\t0:\t"+"unstarted 0 "*counter
	while len(terminating)!=counter:
	#for _ in xrange(5):
		"""Check for Ready Queue processes"""
		if cpu_burst==0:
			if running_process>=0:
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
				ready_queue.sort(key=lambda x: (-initial_processes[x][6],initial_processes[x][1]))
				running_process = ready_queue[0]
				initial_processes[running_process][6]=0
				process_status[running_process] = "running"
				ready_queue = ready_queue[1:]
				# if verbose_flag:
				# 	print "Find burst when choosing ready process to run "+str(lines[randomNumberCounter])
				t = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
				randomNumberCounter+=1
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
				if process_status[x]=="running":
					temp_str+=process_status[x]+" "+str(cpu_burst+1)+" "
				elif process_status[x]=="blocked":
					temp_str+=process_status[x]+" "+str(initial_processes[x][5]+1)+" "
				else:
					temp_str+=process_status[x]+" 0 "	
			print "Before Cycle\t"+str(time_counter)+":\t"+temp_str	

		"""Check if unstarted process has arrived"""
		temp_ready_unstarted_queue = []
		if unstarted:
			for x in unstarted:
				if initial_processes[x][1]==time_counter:
					process_status[x] = "ready"
					temp_ready_unstarted_queue.append(x)
		for x in temp_ready_unstarted_queue:
			unstarted.remove(x)
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
		temp_ready_blocked_queue.sort(key=lambda x: process_arrival_index.index(x))	
		ready_queue.extend(temp_ready_blocked_queue)
		time_counter+=1
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


def SJFmain(inp,lines,randomNumberCounter):
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
	#print initial_processes
	#print processes
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
		print "Before Cycle\t0:\t"+"unstarted 0 "*counter
	while len(terminating)!=counter:
	#for _ in xrange(5):
		"""Check for Ready Queue processes"""
		if cpu_burst==0:
			if running_process>=0:
				if initial_processes[running_process][3]==0:
					terminating.append(running_process)
					initial_processes[running_process][7] = time_counter-1
					initial_processes[running_process][8] = initial_processes[running_process][7]-initial_processes[running_process][1]-offset_timeCounter
					process_status[running_process] = "terminated"
					running_process=-1
				else: 
					blocked.append(running_process)
					process_status[running_process] = "blocked"
					running_process=-1	
						
			if ready_queue:
				ready_queue.sort(key=lambda x: initial_processes[x][3])
				running_process = ready_queue[0]
				ready_queue = ready_queue[1:]
				initial_processes[running_process][6]=0
				process_status[running_process] = "running"
				t = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
				# if verbose_flag:
				# 	print "Find burst when choosing ready process to run "+str(lines[randomNumberCounter])
				randomNumberCounter+=1
				if t>=initial_processes[running_process][3]:
					t = initial_processes[running_process][3]
					initial_processes[running_process][3]-=t
					initial_processes[running_process][5]=0				
				else:
					initial_processes[running_process][3] = initial_processes[running_process][3]-t
					initial_processes[running_process][5] = (initial_processes[running_process][4]*t)-1
					initial_processes[running_process][9] += initial_processes[running_process][5]+1
				cpu_burst = t-1	
		else:
			cpu_burst-=1
		for y in ready_queue:
			if y!=running_process:
				initial_processes[y][6]+=1
				initial_processes[y][10]+=1
		if verbose_flag:
			temp_str = ""
			for x in proceses_order:
				if process_status[x]=="running":
					temp_str+=process_status[x]+" "+str(cpu_burst+1)+" "
				elif process_status[x]=="blocked":
					temp_str+=process_status[x]+" "+str(initial_processes[x][5]+1)+" "
				else:
					temp_str+=process_status[x]+" 0 "
			print "Before Cycle\t"+str(time_counter)+":\t"+temp_str			

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
		temp_ready_blocked_queue.sort(key=lambda x: process_arrival_index.index(x))	
		ready_queue.extend(temp_ready_blocked_queue)
		time_counter+=1
	print "The scheduling algorithm used was Shortest Job First"
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
	print "\tCPU Utilization:\t"+str("{0:.6f}".format(cpu_time_used/float(ft)))
	print "\tI/O Utilization:\t"+str("{0:.6f}".format(ioTIME/float(ft)))
	print "\tThroughput:\t"+str("{0:.6f}".format(counter*100/float(ft)))+" per hundred cycles"	
	print "\tAverage turnaround time:\t"+str("{0:.6f}".format(sum(tat_list)/float(counter)))
	print "\tAverage waiting time:\t"+str("{0:.6f}".format(sum(wt_list)/float(counter)))

def RRmain(inp,lines,randomNumberCounter):
	print "The original input was:\t"+inp
	inp = inp.replace('(','')
	inp = inp.replace(')','')
	#inp[:] = (value for value in inp if value!='(' or value!=')')

	inp = map(int,inp.split())
	processes = []
	counter = 0
	cpu_time_original = []
	original_proc = []
	for x in xrange(1,len(inp),4):#Quadruples
		"""
		0:Process Number, 1:Arrival Time, 2:Interval Upper Limit, 3:CPU Time, 4:Multiplier, 5:IO Burst, 6:ReadyQueueTime, 7:FinishingTime, 8:TurnAroundTime, 9:IOTime, 10:ReadyQueueOverallTime, 11: Arrival time again
		"""
		processes.append([counter,inp[x],inp[x+1],inp[x+2],inp[x+3],0,0,0,0,0,0,inp[x]]) #Process Number and parameters
		cpu_time_original.append(inp[x+2])
		original_proc.append(counter)
		counter+=1
	"""Not needed here I guess, lets see. Status for debugging."""
	process_status = ["unstarted" for x in xrange(counter)]	
	

	"""Sorted Processes Index"""
	process_arrival_index = []

	initial_processes = processes[:]
	#First Come First Serve


	"""Sorting input"""
	initial_processes.sort(key=lambda x: (x[1])) 
	



	sortedprocessesstring = str(counter)+" "
	proceses_order = []
	for x in initial_processes:
		proceses_order.append(x[0])
		sortedprocessesstring+="("+str(x[1])+" "+str(x[2])+" "+str(x[3])+" "+str(x[4])+") "
	
			 
	print "The sorted input is:\t"+sortedprocessesstring
	offset_timeCounter = 0
	unstarted = []
	time_counter = 0
	ready_queue = []
	running_process = -1
	blocked = []
	terminating = []
	quantum = 2
	temp_ready = []
	ioTIME = 0
	
	for x in xrange(counter):
		process_arrival_index.append(initial_processes[x][0])
		unstarted.append(x)

	if verbose_flag:
		print "This detailed printout gives the state and remaining burst for each process"
	cpu_burst = [0 for x in xrange(counter)]
	while len(terminating)!=counter:
		if verbose_flag:
			temp_str = ""
			for x in xrange(len(proceses_order)):
				if process_status[x]=="running":
					temp_str+=process_status[x]+" "+str(cpu_burst[x])+" "
				elif process_status[x]=="blocked":
					temp_str+=process_status[x]+" "+str(initial_processes[x][5]+1)+" "
				else:
					temp_str+=process_status[x]+" 0 "
			print "Before Cycle\t"+str(time_counter)+":\t"+temp_str	
		"""Check for quantum and CPU Burst only if running process != -1"""
		if running_process>=0:	
			"""Check if CPU Burst time for a process is 0 or if quantum is 0"""
			cpu_burst[running_process]-=1
			quantum-=1
			if quantum==0 or cpu_burst[running_process]==0:
				quantum = 2
				"""Re-initialize quantum to 2. Now check what happened actually.
				First condition: Check if CPU Burst is over => (Yes)
				Check for terminating/Blocked condition."""
				if cpu_burst[running_process]==0:
					if initial_processes[running_process][3]==0:
						terminating.append(running_process)
						initial_processes[running_process][7] = time_counter-1
						initial_processes[running_process][8] = initial_processes[running_process][7]-initial_processes[running_process][1]-offset_timeCounter
						process_status[running_process] = "terminated"
						running_process=-1
					else: 
						blocked.append(running_process)
						process_status[running_process] = "blocked"
						running_process=-1
				else:
					"""Now Else for only quantum is 0. Pre-emption"""
					temp_ready.append(running_process)
					process_status[running_process]="ready"
					running_process=-1
		

		"""Check if blocked processes have finished their IO Burst time"""
		temp_ready_blocked_queue = []
		if blocked:
			for x in blocked:
				if initial_processes[x][5]==0:
					temp_ready_blocked_queue.append(x)
					process_status[x] = "ready"
				else:
					initial_processes[x][5]-=1				
		for x in temp_ready_blocked_queue:
			blocked.remove(x)
		if blocked:
			ioTIME+=1
		"""Check if unstarted process has arrived"""
		temp_ready_unstarted_queue = []
		if unstarted:
			for x in unstarted:
				if initial_processes[x][1]==time_counter:
					process_status[x] = "ready"
					temp_ready_unstarted_queue.append(x)
		for x in temp_ready_unstarted_queue:
			unstarted.remove(x)
		

		temp_ready.extend(temp_ready_blocked_queue)
		temp_ready.extend(temp_ready_unstarted_queue)
		"""Appending everything to temp_ready. Sort this using tie-breakers and append it to the original ready queue"""
		temp_ready.sort(key=lambda x:(-initial_processes[x][6],initial_processes[x][11],initial_processes[x][0]))
		ready_queue.extend(temp_ready)
		temp_ready = []
		"""Time for a new running process!"""
		if running_process==-1:
			if ready_queue:
				running_process = ready_queue[0]
				process_status[running_process] = "running"
				ready_queue = ready_queue[1:]
				initial_processes[running_process][10]+=initial_processes[running_process][6]
				initial_processes[running_process][6]=0
				"""No remaining CPU Burst time"""
				if cpu_burst[running_process]<=0:
					# if verbose_flag:
					# 	print "Find burst when choosing ready process to run "+str(lines[randomNumberCounter])
					t = randomOS(lines,initial_processes[running_process][2],randomNumberCounter)
					randomNumberCounter+=1
					if t>=initial_processes[running_process][3]:
						t = initial_processes[running_process][3]
						initial_processes[running_process][3]-=t
						initial_processes[running_process][5]=0				
					else:
						initial_processes[running_process][3] = initial_processes[running_process][3]-t
						initial_processes[running_process][5] = (initial_processes[running_process][4]*t)
						initial_processes[running_process][9]+= initial_processes[running_process][5]
					cpu_burst[running_process] = t
				else:
					initial_processes[running_process][10]+=initial_processes[running_process][6]
					initial_processes[running_process][6] = 0
		"""Adding ReadyQueueTime"""	
		for y in ready_queue:
			initial_processes[y][6]+=1			
		time_counter+=1
	print "The scheduling algorithm used was Round Robin"
	for x in xrange(len(initial_processes)):
		initial_processes[x][7]+=1
		initial_processes[x][8]+=1
		print "Process "+str(initial_processes[x][0])+":"
		print "\t(A,B,C,M) = ("+str(initial_processes[x][1])+", "+str(initial_processes[x][2])+", "+str(cpu_time_original[x])+", "+str(initial_processes[x][4])+")"
		print "\tFinishing Time: "+str(initial_processes[x][7])
		print "\tTurnaround Time: "+str(initial_processes[x][8])
		print "\tI/O Time: "+str(initial_processes[x][9])
		print "\tWaiting Time: "+str(initial_processes[x][10])
		print ""
		



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
if len(sys.argv) > 1:
	if sys.argv[1] == '--verbose':
		verbose_flag = True
		file_name = sys.argv[2]
	else:
		file_name = sys.argv[1]
g = open(file_name,"r")
for inp in g:
	inp = inp.strip()
	print "------------------First Come First Serve--------------------"
	FCmain(inp,lines,randomNumberCounter)
	print "------------------------------------------------------------"
	print "----------------------Round Robin---------------------------"
	RRmain(inp,lines,randomNumberCounter)
	print "------------------Shortest Job First------------------------"
	SJFmain(inp,lines,randomNumberCounter)
	print "------------------------------------------------------------"
	print "--------------------Uniprocessing---------------------------"
	Unimain(inp,lines,randomNumberCounter)
	print "------------------------------------------------------------" 
g.close()						