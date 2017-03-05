import sys
def main(lines,randomNumberCounter):
	inp = raw_input()
	print "The original input was:\t"+inp
	inp = inp.replace('(','')
	inp = inp.replace(')','')
	#inp[:] = (value for value in inp if value!='(' or value!=')')

	inp = map(int,inp.split())
	processes = []
	counter = 0
	cpu_time_original = []
	for x in xrange(1,len(inp),4):#Quadruples
		"""
		0:Process Number, 1:Arrival Time, 2:Interval Upper Limit, 3:CPU Time, 4:Multiplier, 5:IO Burst, 6:ReadyQueueTime, 7:FinishingTime, 8:TurnAroundTime, 9:IOTime, 10:ReadyQueueOverallTime, 11: BurstTime, 12:BlockedTime
		"""
		processes.append([counter,inp[x],inp[x+1],inp[x+2],inp[x+3],-1,0,0,0,0,0,0,0]) #Process Number and parameters
		cpu_time_original.append(inp[x+2])
		counter+=1
	process_status = ["unstarted" for x in xrange(counter)]	
	process_arrival_index = []

	initial_processes = processes[:]
	#First Come First Serve
	initial_processes.sort(key=lambda x: x[1]) 
	sortedprocessesstring = str(counter)+" "
	proceses_order = []
	for x in processes:
		proceses_order.append(x[0])
		sortedprocessesstring+="("+str(x[1])+" "+str(x[2])+" "+str(x[3])+" "+str(x[4])+") " 
	print "The sorted input is:\t"+sortedprocessesstring	
	time_counter = 0
	ready_queue = []
	temp_ready = []
	running = False
	quantum = 0
	terminated = 0
	quantum_over = False
	while terminated!=counter:
		for p in initial_processes:
			if p[1]==time_counter:
				process_status[p[0]]='ready'
				p[6]=0
				temp_ready.append(p[0])
			elif process_status[p[0]]=='running':
				p[3]-=1
				p[11]-=1
				quantum+=1
				if quantum==2 or p[11]==0:
					quantum_over = True
					running = False
					quantum = 0
					if p[11]==0:
						process_status[p[0]]=='blocked'
					else:
						process_status[p[0]]=='ready'
						temp_ready.append(p[0])
				if p[3]==0:
					process_status[p[0]]=='terminated'
					p[7]==time_counter
					terminated+=1
					running=False
					quantum=0
			elif process_status[p[0]]=='blocked':
				p[5]-=1
				if p[5]==0:
					process_status[p[0]]=='ready'
					p[6]=0
					temp_ready.append(p[0])
			elif process_status[p[0]]=='ready':
				p[6]+=1

		if temp_ready:
			max_waiting_time = 0
			index = -1
			count = 0
			while len(temp_ready)>0:
				current = temp_ready[0]
				if max_waiting_time<initial_processes[current][6]:
					max_waiting_time = initial_processes[current][6]
					index = count
				if max_waiting_time == initial_processes[current][6]:
					if index==-1 or initial_processes[temp_ready[index]][1] > initial_processes[current][1]:
						max_waiting_time = initial_processes[current][6]
						index = count
				count+=1
				if index!=-1:
					ready_queue.append(temp_ready[index])
					temp_ready.remove(index)

		if not running:
			if ready_queue and process_status[ready_queue[0]]=='ready':
				current = ready_queue[0]
				process_status[current] = 'running'
				running = True
				if initial_processes[current][11]==0:
					initial_processes[current][10]+=initial_processes[current][6]
					initial_processes[current][6]=0
					t = randomOS(lines,initial_processes[current][2],randomNumberCounter)
					if t<initial_processes[current][3]:
						initial_processes[current][5] = t*initial_processes[current][4] 
						initial_processes[current][12] += initial_processes[current][5]
					else:
						t = initial_processes[current][3]
						initial_processes[current][5] = 0
					initial_processes[current][11] = t
				else:
					initial_processes[current][10]+= initial_processes[current][6]
					initial_processes[current][6]=0		
def randomOS(lines,B,randomNumberCounter):
	return 1+(int(lines[randomNumberCounter])%B)

f = open("random-numbers.txt","r")
lines = f.readlines()
f.close()
randomNumberCounter = 0
verbose_flag = False

if sys.argv[1] == '--verbose':
	verbose_flag = True
	main(lines,randomNumberCounter)

																							

