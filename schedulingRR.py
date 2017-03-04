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
	initial_processes.sort(key=lambda x: x[1]) 
	unstarted = []
	time_counter = 0
	ready_queue = []
	running_process = -1
	blocked = []
	terminating = []
	quantum = 0
	temp_ready = []
	for x in xrange(len(initial_processes)):
		process_arrival_index.append(initial_processes[x][0])
		unstarted.append(x)
	print unstarted
	print ready_queue
	cpu_burst = [0 for x in xrange(counter)]
	while len(terminating)!=counter:

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