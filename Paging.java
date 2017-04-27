import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map.Entry;
class Paging{
		
	public static int returnFirstWord(int processNumber, int processSize){
		return ((111*processNumber)%processSize);
	}	


	public static int nextCaseChoose(ArrayList<Integer> randoms, int randomFileCounter, double a, double b, double c){
		int r = randoms.get(randomFileCounter);
		double y = r/(Integer.MAX_VALUE + 1d);

		if(y>=(a+b+c)){
			return 4;
		}
		else if(y<a){
			return 1;
		}
		else if(y<a+b){
			return 2;
		}
		return 3;
	}


	public static int nextWordChoose(int currentWord, int nextCaseNumber, int processSize, int randomFileCounter, ArrayList<Integer> randoms){
		switch(nextCaseNumber){
			case 1:
				return (currentWord+1)%processSize;
			case 2:
				return (currentWord-5+(2*processSize))%processSize;
			case 3:
				return (currentWord+4)%processSize;		
			case 4:
				return (randoms.get(randomFileCounter))%processSize; //Technical Debt => Solved	
		}
		return -1;
	}


	public static int frameContainsPage(int processNumber, int currentPage, ArrayList<Integer> x, ArrayList<Integer> y){
		for(int i=0;i<x.size();i++){
			if(x.get(i)==currentPage){
				if(y.get(i)==processNumber){
					return i;
				}
			}
		}
		return -1;
	}

	public static int frameContains(int processNumber, int currentPage, ArrayList<Integer> frames, ArrayList<Integer> processes, int numberOfFrames){
		for(int i=0;i<frames.size();i++){
			if(frames.get(i)==currentPage){
				if(processes.get(i)==processNumber){
					//System.out.println("YESSSS!");
					return (numberOfFrames-(frames.size()-i));
				}
			}
		}
		return -1;
	}

	public static int insertPage(int time_counter, int processNumber, int currentPage, ArrayList<Integer> frames, ArrayList<Integer> processes, String replacementAlgorithm, int totalNumberOfFramesUsed, int numberOfFrames, ArrayList<Integer> randoms, int randomFileCounter, ArrayList<Integer> LRUQueueFrames, ArrayList<Integer> LRUQueueProcesses, ArrayList<Integer> LRUQueueTimes,int[] numberOfEvictions, ArrayList<Integer> loadingTimes, int[] runningSums){
		if(totalNumberOfFramesUsed<numberOfFrames){
				frames.add(0,currentPage);
				//System.out.print("Adding frame "+currentPage);
				processes.add(0,processNumber);
				loadingTimes.add(0,time_counter);
				if(replacementAlgorithm.equals("lru")){
					LRUQueueFrames.add(currentPage);
					LRUQueueProcesses.add(processNumber);
					LRUQueueTimes.add(time_counter);
				}
				//System.out.println("to process: "+processNumber);
				//System.out.println("Frames: "+frames);
				//System.out.println("Processes: "+processes);
				

				//System.out.println(", using free frame "+(numberOfFrames-totalNumberOfFramesUsed-1)+".");
				return totalNumberOfFramesUsed+=1;
		}	
		//System.out.print(", evicting page ");
		int pageToBeReplaced,processToBeReplaced,timeToBeAdded;
		switch(replacementAlgorithm){
			case "lru":
				pageToBeReplaced = LRUQueueFrames.get(0);
				processToBeReplaced = LRUQueueProcesses.get(0);
				timeToBeAdded = time_counter-LRUQueueTimes.get(0);
				
				//System.out.print(pageToBeReplaced+" of "+(processToBeReplaced+1));
				
				int tempIndex = frameContainsPage(processToBeReplaced,pageToBeReplaced,frames,processes);
				//System.out.println("AT INDEX: "+tempIndex);
				LRUQueueFrames.remove(0);
				LRUQueueProcesses.remove(0);
				LRUQueueTimes.remove(0);
				
				frames.remove(tempIndex);
				processes.remove(tempIndex);
				loadingTimes.remove(tempIndex);
				
				frames.add(tempIndex,currentPage);
				processes.add(tempIndex,processNumber);
				loadingTimes.add(tempIndex,time_counter);
				
				LRUQueueFrames.add(currentPage);
				LRUQueueProcesses.add(processNumber);
				LRUQueueTimes.add(time_counter);

				numberOfEvictions[processToBeReplaced]+=1;
				runningSums[processToBeReplaced]+=timeToBeAdded;
				break;
			case "random":
				int tempRandomNumber = randoms.get(randomFileCounter);
				//System.out.println("Random Number: "+tempRandomNumber);
				int tempFrameNumber = tempRandomNumber%numberOfFrames;
				
				pageToBeReplaced = frames.get(tempFrameNumber);
				processToBeReplaced = processes.get(tempFrameNumber);
				timeToBeAdded = time_counter-loadingTimes.get(tempFrameNumber);
				
				//System.out.print(pageToBeReplaced+" of "+(processToBeReplaced+1));
				
				frames.remove(tempFrameNumber);
				processes.remove(tempFrameNumber);
				loadingTimes.remove(tempFrameNumber);

				frames.add(tempFrameNumber,currentPage);
				processes.add(tempFrameNumber,processNumber);
				loadingTimes.add(tempFrameNumber,time_counter);

				numberOfEvictions[processToBeReplaced]+=1;
				runningSums[processToBeReplaced]+=timeToBeAdded;
				break;
			case "lifo":
				pageToBeReplaced = frames.get(0);
				processToBeReplaced = processes.get(0);
				timeToBeAdded = time_counter-loadingTimes.get(0);

				frames.remove(0);
				processes.remove(0);
				loadingTimes.remove(0);

				frames.add(0,currentPage);
				processes.add(0,processNumber);
				loadingTimes.add(0,time_counter);

				//System.out.print(pageToBeReplaced+" of "+(processToBeReplaced+1));
				numberOfEvictions[processToBeReplaced]+=1;
				runningSums[processToBeReplaced]+=timeToBeAdded;
				break;
		}
		return -1;
	}


	public static void hitThePage(int processNumber, ArrayList<Integer> LRUQueueFrames, ArrayList<Integer> LRUQueueProcesses, ArrayList<Integer> LRUQueueTimes, int currentPage){
		int tempIndex = frameContainsPage(processNumber,currentPage,LRUQueueFrames,LRUQueueProcesses);
		LRUQueueFrames.remove(tempIndex);
		LRUQueueProcesses.remove(tempIndex);
		int time = LRUQueueTimes.get(tempIndex);
		LRUQueueTimes.remove(tempIndex);

		LRUQueueFrames.add(currentPage);
		LRUQueueProcesses.add(processNumber);
		LRUQueueTimes.add(time);
	}


	public static void printFramesAndProcesses(ArrayList<Integer> frames, ArrayList<Integer> processes, ArrayList<Integer> loadingTimes, ArrayList<Integer> LRUQueueFrames, ArrayList<Integer> LRUQueueProcesses, ArrayList<Integer> LRUQueueTimes, int[] runningSums){
		System.out.println("\nFrames:    "+frames);
		System.out.println("\nProcesses: "+processes);
		System.out.println("\nTimes: "+loadingTimes);
		
		System.out.println("\nLRU Frames:    "+LRUQueueFrames);
		System.out.println("\nLRU Processes: "+LRUQueueProcesses);
		System.out.println("\nLRU Times: "+LRUQueueTimes);

		System.out.print("Running sums: ");
		for(int x=0;x<runningSums.length;x++){
			System.out.println("Process: "+(x+1)+" has "+runningSums[x]);
		}
		System.out.println();
	}


	public static void printOutput(int numberOfProcesses, int[] runningSums, int[] numberOfEvictions,int[] numberOfFaults){
		System.out.println();
		int totalNumberOfFaults = 0;
		int totalRunningSum = 0;
		int totalNumberOfEvictions = 0;
		for(int x=0;x<numberOfProcesses;x++){
			totalNumberOfFaults+=numberOfFaults[x];
			if(numberOfEvictions[x]>0){
				totalNumberOfEvictions+=numberOfEvictions[x];
				totalRunningSum+=runningSums[x];
				double temp = (double) (runningSums[x])/numberOfEvictions[x];
				System.out.println("Process "+(x+1)+" has "+numberOfFaults[x]+" faults and "+temp+" average residency.");	
			}
			else{
				System.out.println("Process "+(x+1)+" had "+numberOfFaults[x]+" faults.");
				System.out.println("\tWith no evictions, the average residence is undefined.");
			}
		}
		System.out.println();
		if(totalNumberOfEvictions==0){	
			System.out.println("The total number of faults is "+totalNumberOfFaults);
			System.out.println("\tWith no evictions, the overall average residence is undefined.");
		}
		else{
			double avReTi = (double) totalRunningSum/totalNumberOfEvictions;
		System.out.println("The total number of faults is "+totalNumberOfFaults+" and the overall average residency is "+avReTi+".");	
		}
		
	}

	public static void secondCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter, int numberOfReferencesPerProcess, int originalQuantum, int pageSize, int numberOfFrames, String replacementAlgorithm){
		int time_counter=1;
		float a=1,b=0,c=0;
		int numberOfProcesses = 4, terminating=4;	
		int currentFrame = -1;
		int[] startingWord = new int[numberOfProcesses];
		int[] currentWord = new int[numberOfProcesses];
		int[] numberOfEvictions = new int[numberOfProcesses];
		int[] runningSums = new int[numberOfProcesses];

		for(int x=0;x<numberOfProcesses;x++){
			runningSums[x]=0;
		}
		int[] numberOfFaults = new int[numberOfProcesses];

		ArrayList<Integer> LRUQueueFrames = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueProcesses = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueTimes = new ArrayList<Integer>();
		// int[] currentPage = new int[currentPage];
		ArrayList<Integer> frames = new ArrayList<Integer>();
		ArrayList<Integer> processes = new ArrayList<Integer>();
		ArrayList<Integer> loadingTimes = new ArrayList<Integer>();
		for(int i=1;i<=numberOfProcesses;i++){
			startingWord[i-1] = returnFirstWord(i,processSize);
			//System.out.println("Starting reference "+startingWord[i-1]);
			currentWord[i-1] = startingWord[i-1];
		}
		int[] numberOfReferencesLeft = new int[numberOfProcesses];
		for(int i=0;i<numberOfProcesses;i++){
			numberOfReferencesLeft[i]=numberOfReferencesPerProcess;
		}
		int totalNumberOfFramesUsed = 0;
		while(terminating>0){
			for(int i=1;i<=numberOfProcesses;i++){
				int quantum = originalQuantum;
				//printFramesAndProcesses(frames,processes,LRUQueueFrames,LRUQueueProcesses);
				while(quantum>0){
					//printFramesAndProcesses(frames,processes,loadingTimes,LRUQueueFrames,LRUQueueProcesses, LRUQueueTimes,runningSums);
					
					//System.out.println("\n\nPrinting randomFileCounter: "+randomFileCounter+"\n\n");
					if(numberOfReferencesLeft[i-1]<=0){
						terminating--;
						break;
					}
					//System.out.println("Process calling:"+i);
					int currentPage = currentWord[i-1]/pageSize;
					if(frameContains(i-1,currentPage,frames,processes,numberOfFrames)>=0){
						//System.out.println(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Hit in frame "+frameContains(i-1,currentPage,frames,processes,numberOfFrames)+".");	
						if(replacementAlgorithm.equals("lru")){
							hitThePage(i-1, LRUQueueFrames, LRUQueueProcesses, LRUQueueTimes, currentPage);	
						}
						
					}
					else{
						//runningSums[i-1]-=1;		
						
						//System.out.print(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Fault");
						

						//System.out.println("\n BEFORE Total Number of frames used: "+totalNumberOfFramesUsed);
						numberOfFaults[i-1]+=1;
						if(insertPage(time_counter,i-1,currentPage,frames,processes,replacementAlgorithm,totalNumberOfFramesUsed,numberOfFrames,randoms,randomFileCounter,LRUQueueFrames,LRUQueueProcesses,LRUQueueTimes,numberOfEvictions,loadingTimes,runningSums)!=-1){
							totalNumberOfFramesUsed+=1;
							currentFrame+=1;
							// /runningSums[i-1]-=1;
						}
						else{
							if(currentFrame<0){
								currentFrame=totalNumberOfFramesUsed;
							}
							//System.out.println(" from frame "+(numberOfFrames-currentFrame));
							currentFrame-=1;
							if(replacementAlgorithm.equals("random")){
								randomFileCounter+=1;
							}

						}
						//if(replacementAlgorithm.equals("lru")){
						//	LRUQueueFrames.add(currentPage);
						//	LRUQueueProcesses.add(i-1);	
						//}
						
					}
					int tempNextCase = nextCaseChoose(randoms,randomFileCounter,a,b,c);
					if(tempNextCase==4){
						randomFileCounter++;
					}
					currentWord[i-1] = nextWordChoose(currentWord[i-1],tempNextCase,processSize,randomFileCounter,randoms);
					quantum--;
					numberOfReferencesLeft[i-1]--;
					randomFileCounter++;
					time_counter++;
				}
			}
		}
		printOutput(numberOfProcesses,runningSums,numberOfEvictions,numberOfFaults);
	}
	

	public static void firstCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter, int numberOfReferencesPerProcess, int originalQuantum, int pageSize, int numberOfFrames, String replacementAlgorithm){
		int time_counter = 1;
		double a=1.0,b=0,c=0;
		int numberOfProcesses = 1, terminating=1;	
		int[] startingWord = new int[numberOfProcesses];
		int[] currentWord = new int[numberOfProcesses];
		// int[] currentPage = new int[currentPage];
		int currentFrame = -1;
		int[] numberOfEvictions = new int[numberOfProcesses];
		int[] runningSums = new int[numberOfProcesses];
		int [] numberOfFaults = new int[numberOfProcesses];
		ArrayList<Integer> LRUQueueFrames = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueProcesses = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueTimes = new ArrayList<Integer>();
		ArrayList<Integer> frames = new ArrayList<Integer>();
		ArrayList<Integer> processes = new ArrayList<Integer>();
		ArrayList<Integer> loadingTimes = new ArrayList<Integer>();
		for(int i=1;i<=numberOfProcesses;i++){
			startingWord[i-1] = returnFirstWord(i,processSize);
			//System.out.println("Starting reference "+startingWord[i-1]);
			currentWord[i-1] = startingWord[i-1];
		}
		int[] numberOfReferencesLeft = new int[numberOfProcesses];
		for(int i=0;i<numberOfProcesses;i++){
			numberOfReferencesLeft[i]=numberOfReferencesPerProcess;
		}
		int totalNumberOfFramesUsed = 0;
		while(terminating>0){
			for(int i=1;i<=numberOfProcesses;i++){
				int quantum = originalQuantum;
				while(quantum>0){
					//printFramesAndProcesses(frames,processes,loadingTimes,LRUQueueFrames,LRUQueueProcesses,LRUQueueTimes,runningSums);
					if(numberOfReferencesLeft[i-1]<=0){
						terminating--;
						break;
					}
					//System.out.println("Process calling:"+i);
					int currentPage = currentWord[i-1]/pageSize;
					if(frameContains(i-1,currentPage,frames,processes,numberOfFrames)>=0){
						//System.out.println(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Hit in frame "+frameContains(i-1,currentPage,frames,processes,numberOfFrames)+".");	
						if(replacementAlgorithm.equals("lru")){
							//System.out.println("Hitting the page!");
							hitThePage(i-1, LRUQueueFrames, LRUQueueProcesses, LRUQueueTimes,currentPage);	
						}
					}
					else{
						//System.out.print(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Fault");
						//System.out.println("\n BEFORE Total Number of frames used: "+totalNumberOfFramesUsed);
						numberOfFaults[i-1]+=1;
						if(insertPage(time_counter, i-1,currentPage,frames,processes,replacementAlgorithm,totalNumberOfFramesUsed,numberOfFrames,randoms,randomFileCounter,LRUQueueFrames,LRUQueueProcesses, LRUQueueTimes,numberOfEvictions, loadingTimes, runningSums)!=-1){
							totalNumberOfFramesUsed+=1;
						}
						else{
							if(currentFrame<0){
								currentFrame=totalNumberOfFramesUsed;
							}
							//System.out.println(" from frame "+(numberOfFrames-currentFrame));
							currentFrame-=1;
							if(replacementAlgorithm.equals("random")){
								randomFileCounter+=1;
							}	
						}
						//if(replacementAlgorithm.equals("lru")){
							//hitThePage(i-1, frames, processes,currentPage);	
						//}
						
					}
					int tempNextCase = nextCaseChoose(randoms,randomFileCounter,a,b,c);
					if(tempNextCase==4){
						randomFileCounter++;
					}
					currentWord[i-1] = nextWordChoose(currentWord[i-1],tempNextCase,processSize,randomFileCounter,randoms);
					quantum--;
					numberOfReferencesLeft[i-1]--;
					randomFileCounter++;
					time_counter++;
					
					//System.out.println("\n\nEND");
					//printFramesAndProcesses(frames,processes,LRUQueueFrames,LRUQueueProcesses);
				}
			}
		}
		printOutput(numberOfProcesses,runningSums,numberOfEvictions,numberOfFaults);
	}


	public static void thirdCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter, int numberOfReferencesPerProcess, int originalQuantum, int pageSize, int numberOfFrames, String replacementAlgorithm){
		float a=0,b=0,c=0;
		int time_counter = 1;
		int numberOfProcesses = 4, terminating=4;	
		int currentFrame = -1;
		int[] startingWord = new int[numberOfProcesses];
		int[] currentWord = new int[numberOfProcesses];
		// int[] currentPage = new int[currentPage];
		int[] numberOfFaults = new int[numberOfProcesses];
		int[] numberOfEvictions = new int[numberOfProcesses];
		int[] runningSums = new int[numberOfProcesses];

		ArrayList<Integer> LRUQueueFrames = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueProcesses = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueTimes = new ArrayList<Integer>();

		ArrayList<Integer> frames = new ArrayList<Integer>();
		ArrayList<Integer> processes = new ArrayList<Integer>();
		ArrayList<Integer> loadingTimes = new ArrayList<Integer>();

		for(int i=1;i<=numberOfProcesses;i++){
			startingWord[i-1] = returnFirstWord(i,processSize);
			//System.out.println("Starting reference "+startingWord[i-1]);
			currentWord[i-1] = startingWord[i-1];
		}
		int[] numberOfReferencesLeft = new int[numberOfProcesses];
		for(int i=0;i<numberOfProcesses;i++){
			numberOfReferencesLeft[i]=numberOfReferencesPerProcess;
		}
		int totalNumberOfFramesUsed = 0;
		while(terminating>0){
			for(int i=1;i<=numberOfProcesses;i++){
				int quantum = originalQuantum;
				//printFramesAndProcesses(frames,processes,LRUQueueFrames,LRUQueueProcesses);
				while(quantum>0){
					if(numberOfReferencesLeft[i-1]<=0){
						terminating--;
						break;
					}
					//printFramesAndProcesses(frames,processes,loadingTimes,LRUQueueFrames,LRUQueueProcesses,LRUQueueTimes,runningSums);
				
					//System.out.println("Process calling:"+i);
					int currentPage = currentWord[i-1]/pageSize;
					if(frameContains(i-1,currentPage,frames,processes,numberOfFrames)>=0){
						//System.out.println(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Hit in frame "+frameContains(i-1,currentPage,frames,processes,numberOfFrames)+".");	
						if(replacementAlgorithm.equals("lru")){
							hitThePage(i-1, LRUQueueFrames, LRUQueueProcesses,LRUQueueTimes,currentPage);	
						}
					}
					else{
						//System.out.print(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Fault");
						//System.out.println("\n BEFORE Total Number of frames used: "+totalNumberOfFramesUsed);
						numberOfFaults[i-1]+=1;
						if(insertPage(time_counter,i-1,currentPage,frames,processes,replacementAlgorithm,totalNumberOfFramesUsed,numberOfFrames,randoms,randomFileCounter,LRUQueueFrames,LRUQueueProcesses, LRUQueueTimes,numberOfEvictions, loadingTimes, runningSums)!=-1){
							totalNumberOfFramesUsed+=1;
							currentFrame+=1;
						}
						else{
							if(currentFrame<0){
								currentFrame=totalNumberOfFramesUsed;
							}
							//System.out.println(" from frame "+(currentFrame));
							currentFrame-=1;
							if(replacementAlgorithm.equals("random")){
								randomFileCounter+=1;
							}
						}
						
						//if(replacementAlgorithm.equals("lru")){
						//	hitThePage(i-1, frames, processes,currentPage);	
						//}
						
					}
					int tempNextCase = nextCaseChoose(randoms,randomFileCounter,a,b,c);
					if(tempNextCase==4){
						randomFileCounter++;
					}
					currentWord[i-1] = nextWordChoose(currentWord[i-1],tempNextCase,processSize,randomFileCounter,randoms);
					quantum--;
					numberOfReferencesLeft[i-1]--;
					randomFileCounter++;
					time_counter++;
				}
			}
		}
		printOutput(numberOfProcesses,runningSums,numberOfEvictions,numberOfFaults);
	}
	
	public static void fourthCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter, int numberOfReferencesPerProcess, int originalQuantum, int pageSize, int numberOfFrames, String replacementAlgorithm){
		double[] a = {0.75,0.75,0.75,0.5};
		double[] b = {0.25,0,0.125,0.125};
		double[] c = {0,0.25,0.125,0.125};
		int time_counter = 1;
		int numberOfProcesses = 4, terminating=4;	
		int currentFrame = -1;
		int[] startingWord = new int[numberOfProcesses];
		int[] currentWord = new int[numberOfProcesses];
		int[] numberOfFaults = new int[numberOfProcesses];
		// int[] currentPage = new int[currentPage];
		int[] numberOfEvictions = new int[numberOfProcesses];
		int[] runningSums = new int[numberOfProcesses];
		ArrayList<Integer> LRUQueueFrames = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueProcesses = new ArrayList<Integer>();
		ArrayList<Integer> LRUQueueTimes = new ArrayList<Integer>();

		ArrayList<Integer> frames = new ArrayList<Integer>();
		ArrayList<Integer> processes = new ArrayList<Integer>();
		ArrayList<Integer> loadingTimes = new ArrayList<Integer>();

		for(int i=1;i<=numberOfProcesses;i++){
			startingWord[i-1] = returnFirstWord(i,processSize);
			//System.out.println("Starting reference "+startingWord[i-1]);
			currentWord[i-1] = startingWord[i-1];
		}
		int[] numberOfReferencesLeft = new int[numberOfProcesses];
		for(int i=0;i<numberOfProcesses;i++){
			numberOfReferencesLeft[i]=numberOfReferencesPerProcess;
		}
		int totalNumberOfFramesUsed = 0;
		while(terminating>0){
			for(int i=1;i<=numberOfProcesses;i++){
				int quantum = originalQuantum;
				//printFramesAndProcesses(frames,processes,LRUQueueFrames,LRUQueueProcesses);
				while(quantum>0){
					if(numberOfReferencesLeft[i-1]<=0){
						terminating--;
						break;
					}
					//printFramesAndProcesses(frames,processes,loadingTimes,LRUQueueFrames,LRUQueueProcesses,LRUQueueTimes,runningSums);
				
					//System.out.println("Process calling:"+i);
					int currentPage = currentWord[i-1]/pageSize;
					if(frameContains(i-1,currentPage,frames,processes,numberOfFrames)>=0){
						//System.out.println(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Hit in frame "+frameContains(i-1,currentPage,frames,processes,numberOfFrames)+".");	
						if(replacementAlgorithm.equals("lru")){
							hitThePage(i-1, LRUQueueFrames, LRUQueueProcesses, LRUQueueTimes,currentPage);	
						}
					}
					else{
						//System.out.print(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Fault");
						//System.out.println("\n BEFORE Total Number of frames used: "+totalNumberOfFramesUsed);
						numberOfFaults[i-1]+=1;
						if(insertPage(time_counter, i-1,currentPage,frames,processes,replacementAlgorithm,totalNumberOfFramesUsed,numberOfFrames,randoms,randomFileCounter,LRUQueueFrames,LRUQueueProcesses,LRUQueueTimes,numberOfEvictions, loadingTimes, runningSums)!=-1){
							totalNumberOfFramesUsed+=1;
							currentFrame+=1;
						}
						else{
							if(currentFrame<0){
								currentFrame=totalNumberOfFramesUsed;
							}
							//System.out.println(" from frame "+(currentFrame));
							currentFrame-=1;
							if(replacementAlgorithm.equals("random")){
								randomFileCounter+=1;
							}
						}
						//if(replacementAlgorithm.equals("lru")){
						//	hitThePage(i-1, frames, processes,currentPage);	
						//}
						
					}

					int tempNextCase = nextCaseChoose(randoms,randomFileCounter,a[i-1],b[i-1],c[i-1]);
					if(tempNextCase==4){
						randomFileCounter++;
					}
					currentWord[i-1] = nextWordChoose(currentWord[i-1],tempNextCase,processSize,randomFileCounter,randoms);
					quantum--;
					numberOfReferencesLeft[i-1]--;
					randomFileCounter++;
					time_counter++;
					
				}
			}
		}
		printOutput(numberOfProcesses,runningSums,numberOfEvictions,numberOfFaults);
	}

	public static ArrayList<Integer> readRandomFile(String inputFilename){
		ArrayList<Integer> Inputs = new ArrayList<Integer>();
		try{
			File text = new File(inputFilename);
			Scanner scFile = new Scanner(text);
			while(scFile.hasNextLine()){
				String line = scFile.nextLine();
				Inputs.add(Integer.parseInt(line));//Adding the input to an arraylist line by line
			}	
		}
		//If File not found!
		catch (FileNotFoundException fe){
			System.out.println("File not found!");
		}
		return Inputs;
	}


	public static void main(String args[]){
		int machineSize=Integer.parseInt(args[0]),pageSize=Integer.parseInt(args[1]),processSize=Integer.parseInt(args[2]),jobMixNumber=Integer.parseInt(args[3]),numberOfReferencesPerProcess=Integer.parseInt(args[4]);
		String replacementAlgorithm=args[5];
		int quantum=3;
		String fileName = "random-numbers.txt";
		System.out.println("The machine size is "+machineSize+".");
		System.out.println("The page size is "+pageSize+".");
		System.out.println("The process size is "+processSize+".");
		System.out.println("The job mix number is "+jobMixNumber+".");
		System.out.println("The number of references per process are "+numberOfReferencesPerProcess+".");
		System.out.println("The replacement algorithm is "+replacementAlgorithm+".");
		int randomFileCounter = 0;
		ArrayList<Integer> randoms = readRandomFile(fileName);
		/*Checking next first word working
		for(int i=0;i<4;i++){
			System.out.println("Next word referenced:"+returnFirstWord(i+1,processSize));
		}*/
		int numberOfFrames = machineSize/pageSize;
		//System.out.println("Number of frames: "+numberOfFrames);
		switch(jobMixNumber){
			case 1:
				firstCase(processSize, randoms, randomFileCounter, numberOfReferencesPerProcess, quantum, pageSize, numberOfFrames, replacementAlgorithm);
				break;
			case 2:
				secondCase(processSize, randoms, randomFileCounter, numberOfReferencesPerProcess, quantum, pageSize, numberOfFrames, replacementAlgorithm);
				break;
			case 3:
				thirdCase(processSize, randoms, randomFileCounter, numberOfReferencesPerProcess, quantum, pageSize, numberOfFrames, replacementAlgorithm);
				break;
			case 4:
				fourthCase(processSize, randoms, randomFileCounter, numberOfReferencesPerProcess, quantum, pageSize, numberOfFrames, replacementAlgorithm);
				break;					
		}


	}
}