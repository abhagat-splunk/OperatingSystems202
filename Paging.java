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
		System.out.println("The random number chosen was: "+r);
		System.out.println(y);

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


	public static int nextWordChoose(int currentWord, int nextCaseNumber, int processSize){
		switch(nextCaseNumber){
			case 1:
				return (currentWord+1)%processSize;
			case 2:
				return (currentWord-5+processSize)%processSize;
			case 3:
				return (currentWord+4)%processSize;		
			case 4:
				return -1;//Technical Debt	
		}
		return -1;
	}

	public static int frameContains(int processNumber, int currentPage, int[][] frames){
		// System.out.println("Checking for: "+currentPage);
		// System.out.println("Process number: "+processNumber);
		for(int i=0;i<frames[processNumber].length;i++){
			if(frames[processNumber][i]==currentPage){
				//System.out.println(processNumber+"Iska hain apne pass!");
				return i;
			}
		}
		return -1;
	}

	public static int insertPage(int processNumber, int currentPage, int[][] frames, String replacementAlgorithm, ArrayList<ArrayList<Integer>> pages, int totalNumberOfFramesUsed, int numberOfFrames){
		System.out.println("Inserting!");
		if(totalNumberOfFramesUsed<numberOfFrames){
			for(int i=0;i<frames[processNumber].length;i++){
				if(frames[processNumber][i]==-1){
					//System.out.println("Storing for process: "+processNumber);
					frames[processNumber][i]=currentPage;
					pages.get(processNumber).add(currentPage);
					System.out.println(", using free frame "+i+".");
					return totalNumberOfFramesUsed+=1;
				}
			}	
		}
		System.out.print(", evicting page ");
		switch(replacementAlgorithm){
			case "lru":
				int pageToBeReplaced = pages.get(processNumber).get(0);
				for(int i=0;i<frames[processNumber].length;i++){
					if(frames[processNumber][i]==pageToBeReplaced){
						frames[processNumber][i]=currentPage;
						System.out.println(pageToBeReplaced+" from frame "+i);
						break;
					}
				}
				pages.get(processNumber).remove(0);
				pages.get(processNumber).add(currentPage);
				break;
			case "random":
				System.out.println(" random");
				break;
			case "lifo":
				System.out.println(" lifo");
				break;
		}
		return -1;
	}


	public static void hitThePage(int processNumber, ArrayList<ArrayList<Integer>> pages, int currentPage){
		pages.get(processNumber).remove(Integer.valueOf(currentPage));
		pages.get(processNumber).add(currentPage);
	}


	public static void secondCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter, int numberOfReferencesPerProcess, int originalQuantum, int pageSize, int numberOfFrames, String replacementAlgorithm){
		int time_counter=1;
		float a=1,b=0,c=0;
		int numberOfProcesses = 4, terminating=4;	
		int[] startingWord = new int[numberOfProcesses];
		int[] currentWord = new int[numberOfProcesses];
		ArrayList<ArrayList<Integer>> pages = new ArrayList<ArrayList<Integer>>();
		// int[] currentPage = new int[currentPage];
		int[][] frames = new int[numberOfProcesses][numberOfFrames];
		for(int i=0;i<numberOfProcesses;i++){
			for(int j=0;j<numberOfFrames;j++){
				frames[i][j]=-1;
			}	
		}
		int totalNumberOfFramesUsed = 0;
		for(int i=1;i<=numberOfProcesses;i++){
			startingWord[i-1] = returnFirstWord(i,processSize);
			//System.out.println("Starting reference "+startingWord[i-1]);
			currentWord[i-1] = startingWord[i-1];
			pages.add(new ArrayList<Integer>());
		}
		int[] numberOfReferencesLeft = new int[numberOfProcesses];
		for(int i=0;i<numberOfProcesses;i++){
			numberOfReferencesLeft[i]=numberOfReferencesPerProcess;
		}

		while(terminating>0){
			for(int i=1;i<=numberOfProcesses;i++){
				int quantum = originalQuantum;
				while(quantum>0){
					if(numberOfReferencesLeft[i-1]<=0){
						terminating--;
						break;
					}
					//System.out.println("Process calling:"+i);
					int currentPage = currentWord[i-1]/pageSize;
					if(frameContains(i-1,currentPage,frames)>=0){
						System.out.println(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Hit in frame "+frameContains(i-1,currentPage,frames)+".");	
						hitThePage(i-1,pages,currentPage);
					}
					else{
						System.out.print(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Fault");
						totalNumberOfFramesUsed = insertPage(i-1,currentPage,frames,replacementAlgorithm,pages,totalNumberOfFramesUsed,numberOfFrames);
						hitThePage(i-1,pages,currentPage);
					}
					//System.out.println("Next Case: "+nextCaseChoose(randoms,randomFileCounter,a,b,c));
					//System.out.println("Current word: "+currentWord[i-1]);
					/*System.out.println("\n\nPrinting frames matrix");
					for(int z=0;z<numberOfProcesses;z++){
						for(int j=0;j<numberOfFrames;j++){
							System.out.print(frames[z][j]+"\t");
						}	
						System.out.println();
					}*/



					currentWord[i-1] = nextWordChoose(currentWord[i-1],nextCaseChoose(randoms,randomFileCounter,a,b,c),processSize);
					quantum--;
					numberOfReferencesLeft[i-1]--;
					randomFileCounter++;
					time_counter++;
				}
			}
		}
	}
	

	public static void firstCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter, int numberOfReferencesPerProcess, int originalQuantum, int pageSize, int numberOfFrames, String replacementAlgorithm){
		int time_counter = 1;
		double a=1.0,b=0,c=0;
		int terminating = 1, numberOfProcesses = 1;
		int[] startingWord = new int[numberOfProcesses];
		int[] currentWord = new int[numberOfProcesses];
		ArrayList<ArrayList<Integer>> pages = new ArrayList<ArrayList<Integer>>();
		// int[] currentPage = new int[currentPage];
		int totalNumberOfFramesUsed = 0;
		int[][] frames = new int[numberOfProcesses][numberOfFrames];
		for(int i=0;i<numberOfProcesses;i++){
			for(int j=0;j<numberOfFrames;j++){
				frames[i][j]=-1;
			}	
		}
		for(int i=1;i<=numberOfProcesses;i++){
			startingWord[i-1] = returnFirstWord(i,processSize);
			//System.out.println("Starting reference "+startingWord[i-1]);
			currentWord[i-1] = startingWord[i-1];
			pages.add(new ArrayList<Integer>());
		}
		int[] numberOfReferencesLeft = new int[numberOfProcesses];
		for(int i=0;i<numberOfProcesses;i++){
			numberOfReferencesLeft[i]=numberOfReferencesPerProcess;
		}

		while(terminating>0){
			for(int i=1;i<=numberOfProcesses;i++){
				int quantum = originalQuantum;
				while(quantum>0){
					if(numberOfReferencesLeft[i-1]<=0){
						terminating--;
						break;
					}
					int currentPage = currentWord[i-1]/pageSize;
					if(frameContains(i-1,currentPage,frames)>=0){
						System.out.println(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Hit in frame "+frameContains(i-1,currentPage,frames)+".");	
						hitThePage(i-1,pages,currentPage);
					}
					else{
						System.out.print(i+" references word "+currentWord[i-1]+" (page "+currentPage+") at time "+time_counter+": Fault");
						totalNumberOfFramesUsed = insertPage(i-1,currentPage,frames,replacementAlgorithm,pages,totalNumberOfFramesUsed,numberOfFrames);
						hitThePage(i-1,pages,currentPage);
					}


					// System.out.println("\n\nPrinting the frames matrix");
					// for(int i=0;i<numberOfProcesses;i++){
					// 	for(int j=0;j<numberOfFrames;j++){

					// 	}
					// }

					//System.out.println("Next Case: "+nextCaseChoose(randoms,randomFileCounter,a,b,c));
					//System.out.println("Current word: "+currentWord[i-1]);
					currentWord[i-1] = nextWordChoose(currentWord[i-1],nextCaseChoose(randoms,randomFileCounter,a,b,c),processSize);
					quantum--;
					numberOfReferencesLeft[i-1]--;
					randomFileCounter++;
					time_counter++;
				}
			}
		}
			
	}



	public static void thirdCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter){
		float a=0,b=0,c=0;
		int numberOfProcesses = 4;
	}
	
	public static void fourthCase(int processSize, ArrayList<Integer> randoms, int randomFileCounter){
		int numberOfProcesses = 4;	
	}

	public static void readFromRandomFile(){

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
		System.out.println("Number of frames: "+numberOfFrames);
		switch(jobMixNumber){
			case 1:
				firstCase(processSize, randoms, randomFileCounter, numberOfReferencesPerProcess, quantum, pageSize, numberOfFrames, replacementAlgorithm);
				break;
			case 2:
				secondCase(processSize, randoms, randomFileCounter, numberOfReferencesPerProcess, quantum, pageSize, numberOfFrames, replacementAlgorithm);
				break;
			case 3:
				thirdCase(processSize, randoms, randomFileCounter);
				break;
			case 4:
				fourthCase(processSize, randoms, randomFileCounter);
				break;					
		}


	}
}