import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map.Entry;
class Bankers{

	/*Reading input from the file*/
	public static void readFromFile(ArrayList<String> Inputs, String inputFilename){
		try{
			File text = new File(inputFilename);
			Scanner scFile = new Scanner(text);
			while(scFile.hasNextLine()){
				String line = scFile.nextLine();
				Inputs.add(line);//Adding the input to an arraylist line by line
			}	
		}
		//If File not found!
		catch (FileNotFoundException fe){
			System.out.println("File not found!");
		}
		
	}

	public static void main(String args[]){
		//System.out.println("Hello, World!");
		int numberOfTasks, numberOfResources;
		//Initializing scanner object
		Scanner scanner = new Scanner(System.in);
		String inputFilename = args[0]; //args[0] will have the file name
		ArrayList<String> Inputs = new ArrayList<String>();

		readFromFile(Inputs, inputFilename);//Reading the file function
		
		ArrayList<ArrayList<String>> SortedInputs = new ArrayList<ArrayList<String>>();//ArrayList for sorted inputs
		ArrayList<Integer> SortedInputsIDs = new ArrayList<Integer>();
		//numberOfTasks, numberOfResources, Resources.
		String[] inputFirstLine = Inputs.get(0).split("\\s+");//Splitting the first line with respect to spaces, i.e numberOfTasks, numberOfResources, numberOfEachResource
		numberOfTasks = Integer.parseInt(inputFirstLine[0]);
		numberOfResources = Integer.parseInt(inputFirstLine[1]);
		
		//Store the numberOfEachResource
		int[] resources = new int[numberOfResources];
		
		//Unstarted, In Process, Terminated
		String[] resource_status = new String[numberOfResources];
		

		//Initial claims by the process
		int[][] initialClaims = new int[numberOfTasks][numberOfResources];
		
		//Claims made by the process stored in this array
		int[][] alreadyClaimed = new int[numberOfTasks][numberOfResources];
		
		//Resources released at the end of the time interval are added back to the banker(making it richer)
		int[] addResources = new int[numberOfResources];
		
		//Safe resources
		int[] safeResources = new int[numberOfResources];

		//Terminating condition, if(terminating==numberOfResources) => End!
		int terminating = 0;
		ArrayList<Integer> terminated = new ArrayList<Integer>();
		//Blocked processes (Process ID: Waiting Time)
		ArrayList<Integer> blocked = new ArrayList<Integer>();


		int[] WaitingTime = new int[numberOfTasks];

		//Initialization of numberOfEachResource present for the system to work on
		for(int x=0;x<numberOfResources;x++){
			resources[x] = Integer.parseInt(inputFirstLine[x+2]);
			resource_status[x] = "Unstarted";
		}


		//TaskID: [Total Time, Waiting Time]
		Map<Integer,ArrayList<Integer>> FinalOutput = new HashMap<Integer,ArrayList<Integer>>();
		for(int i=0;i<numberOfTasks;i++){
			FinalOutput.put(i,new ArrayList<Integer>());
		}


		
		ArrayList<Integer> tempBlockedIds = new ArrayList<Integer>();
		ArrayList<Integer> originalBlockedIds = new ArrayList<Integer>();


		HashMap<Integer,Integer> computeCount = new HashMap<Integer,Integer>();
		


		//Initial Claims stored and Input is sorted for each process
		for(int x=1; x<Inputs.size();x++){
			//Splitting each string with respect to spaces
			String[] inputLine = Inputs.get(x).split("\\s+");
			//If it is an initiate action, store the initial claims!
			if(inputLine[0].equals("initiate")){
				initialClaims[Integer.parseInt(inputLine[1])-1][Integer.parseInt(inputLine[2])-1]= Integer.parseInt(inputLine[3]);
			}
			else{
				if(Inputs.get(x).trim().isEmpty()){
					continue;
				}
				if(!SortedInputsIDs.contains(Integer.parseInt(inputLine[1]))){//Technical Debt, change it asap => Changed!
					SortedInputsIDs.add(Integer.parseInt(inputLine[1]));
					ArrayList<String> tempInput = new ArrayList<String>();
					tempInput.add(inputLine[0]+" "+inputLine[2]+" "+inputLine[3]);
					SortedInputs.add(tempInput);
				}
				else{
					ArrayList<String> tempInput = SortedInputs.get(Integer.parseInt(inputLine[1])-1);
					tempInput.add(inputLine[0]+" "+inputLine[2]+" "+inputLine[3]);
				}
			}
		}
		

		/* Checking sortedInputs
		*/
		for(int x=0;x<SortedInputs.size();x++){
			System.out.println(x+1);
			for(int y=0;y<SortedInputs.get(x).size();y++){
				System.out.println(SortedInputs.get(x).get(y));
			}
			System.out.println();
		}
		/**/


		for(int x=0;x<numberOfTasks;x++){
			for(int y=0;y<numberOfResources;y++){
				if(initialClaims[x][y]>resources[y]){
					terminating+=1;
					terminated.add(x+1);
					ArrayList<Integer> temp = FinalOutput.get(x);
					temp.add(-1);
					FinalOutput.put(x,temp);
					break;
				}
			}
		}



		int timeCounter = 0+numberOfResources;
		int[] minimumResources = new int[numberOfResources];
		while(terminating<numberOfTasks){
			System.out.println("Number of terminating processes: "+terminating);
			System.out.println("Time Interval: "+timeCounter+"-"+(timeCounter+1));
			for(int x=0;x<numberOfResources;x++){
				System.out.println("Resource ID: "+(x+1)+" Number of resources: "+resources[x]);
			}
			//Checking if the blocked resources 
			//Include the highest priority code according to waiting time at the end of the loop!
			//Tech Debt => ArrayList
			
			//System.out.println("STARTING HERE!");
			
			//System.out.println("ENDING HERE!");
			
			System.out.println("Checking blocked resources!");
			if(!originalBlockedIds.isEmpty()){
				System.out.println(originalBlockedIds);
				int[] tempResourcesSubtracted = new int[numberOfResources];

				for(Integer i:originalBlockedIds){
					WaitingTime[i-1]+=1;
					String req = SortedInputs.get(i-1).get(0);
					String[] zSplit = req.split("\\s+");
					int currentResourceID = Integer.parseInt(zSplit[1])-1;
					int currentResourceNeed = Integer.parseInt(zSplit[2]);
					//System.out.println(resources[currentResourceID]);
					//System.out.println(currentResourceNeed);
					boolean blockedToActiveFlag = false;
					System.out.println("Resource: "+i);
					System.out.println("Available resources: "+resources[currentResourceID]);
					System.out.println("Initial Claims: "+initialClaims[i-1][currentResourceID]);
					System.out.println("Already allocated: "+alreadyClaimed[i-1][currentResourceID]);
					if(resources[currentResourceID]>=(initialClaims[i-1][currentResourceID]-alreadyClaimed[i-1][currentResourceID])){
						blockedToActiveFlag = true;
						for(int x=0;x<numberOfResources;x++){
							if(x!=currentResourceID){
								if(resources[x]>=initialClaims[i-1][x]-alreadyClaimed[i-1][x]){
									blockedToActiveFlag=true;
								}	
								else{
									blockedToActiveFlag=false;
									break;
								}
							}
						}
						if(blockedToActiveFlag==true){
							resources[currentResourceID]-=currentResourceNeed;
							tempResourcesSubtracted[currentResourceID]+=currentResourceNeed;
							tempBlockedIds.add(i);	
						}
						
					}
				}

				for(int i=0;i<numberOfResources;i++){
					resources[i]+=tempResourcesSubtracted[i];
				}
				System.out.println("Temporary Blocked IDs:"+tempBlockedIds);
				for(Integer key:tempBlockedIds){
					originalBlockedIds.remove(key);
				}
			}
			
			//
			ArrayList<Integer> orderOfIds = new ArrayList<Integer>();
			for(Integer k: tempBlockedIds){
				orderOfIds.add(k);
			}
			
			System.out.println("Order of IDs: "+orderOfIds);
			for(int x=1;x<=numberOfTasks;x++){
				if(!orderOfIds.contains(x)){
					orderOfIds.add(x);
				}
			}
			for(Integer x:orderOfIds){
				//Checking if process is blocked or not!
				if(terminated.contains(x)){
					continue;
				}
				if(!originalBlockedIds.contains(x)){
					System.out.println(SortedInputs.get(x-1).get(0));
					String z = SortedInputs.get(x-1).get(0);
					String[] zSplit = z.split("\\s+");
					int currentResourceID = Integer.parseInt(zSplit[1])-1;
					int currentResourceNeed = Integer.parseInt(zSplit[2]);
					
					if(zSplit[0].equals("compute")){
						if(currentResourceID==0){
							SortedInputs.get(x-1).remove(0);
							continue;
						}
						if(computeCount.containsKey(x-1)){
							if(computeCount.get(x-1)<=	0){
								//System.out.println("Compute over!");
								SortedInputs.get(x-1).remove(0);
								computeCount.remove(x-1);
							}
							else{
								computeCount.put(x-1,computeCount.get(x-1)-1);
							}
						}
						else{
							computeCount.put(x-1,currentResourceID-1);
						}
						for(Integer key:computeCount.keySet()){
						System.out.println(Integer.toString(key)+": "+computeCount.get(key));
						}
					}
					if(zSplit[0].equals("request")){
						System.out.println("Requesting "+currentResourceNeed+" of Resource ID: "+(currentResourceID+1)+" for "+x);
						System.out.println(x+" has already claimed "+alreadyClaimed[x-1][currentResourceID]);
							if(alreadyClaimed[x-1][currentResourceID]+currentResourceNeed > initialClaims[x-1][currentResourceID]){
							//System.out.println("Process claiming more than initial limit.");
							ArrayList<Integer> temp = FinalOutput.get(x-1);
							temp.add(-1);
							FinalOutput.put(x-1,temp);
							SortedInputs.get(x-1).remove(0);
							for(int i=0;i<numberOfResources;i++){
								addResources[i]+=alreadyClaimed[x-1][i];
							}
							terminating+=1;
							terminated.add(x);
						}
						else{
							if(resources[currentResourceID]>=initialClaims[x-1][currentResourceID]-alreadyClaimed[x-1][currentResourceID]){
								System.out.println("Allocating resources!");
								alreadyClaimed[x-1][currentResourceID]+=currentResourceNeed;
								resources[currentResourceID]-=currentResourceNeed;
								SortedInputs.get(x-1).remove(0);
							}
							else{
								System.out.println("Not enough resources!");
								originalBlockedIds.add(x);
							}
						}
					}
					if(zSplit[0].equals("release")){
						System.out.println("Releasing "+currentResourceNeed+" of Resource ID: "+(currentResourceID+1)+"  for "+x);
						System.out.println(x+" has already claimed "+alreadyClaimed[x-1][currentResourceID]);
						alreadyClaimed[x-1][currentResourceID]-=currentResourceNeed;
						addResources[currentResourceID]+=currentResourceNeed;
						//Removing the line	
						SortedInputs.get(x-1).remove(0);
					}
					if(zSplit[0].equals("terminate")){
						System.out.println("Terminating "+x);
						terminating+=1;
						terminated.add(x);
						ArrayList<Integer> temp = FinalOutput.get(x-1);
						temp.add(WaitingTime[x-1]);
						temp.add(timeCounter);
						//System.out.println(temp);
						FinalOutput.put(x-1,temp);
						for(int i=0;i<numberOfResources;i++){
								addResources[i]+=alreadyClaimed[x-1][i];
						}
						//Removing the line	
						SortedInputs.get(x-1).remove(0);
					}
				}
			}
			//End of for loop OrderOfIds
			tempBlockedIds = new ArrayList<Integer>();
			//Releasing the resources back to the manager at the end
			for(int x=0;x<numberOfResources;x++){
				System.out.println("X: "+addResources[x]);
				resources[x] += addResources[x];
				addResources[x] = 0;
			}
			timeCounter+=1;
			System.out.println();	
			//System.out.println(blocked);
		}
		System.out.println("\tBankers Algorithm\t");
		int totalWaitingTime = 0, totalTime = 0, waitingTimePercent = 0;

		for(int i=0;i<numberOfTasks;i++){
			if(FinalOutput.get(i).get(0)!=-1){
				int percent = (FinalOutput.get(i).get(0)*100)/FinalOutput.get(i).get(1);
				totalWaitingTime+=FinalOutput.get(i).get(0);
				totalTime+=FinalOutput.get(i).get(1);
				System.out.print("Task "+(i+1)+"\t"+FinalOutput.get(i).get(1)+"\t"+FinalOutput.get(i).get(0)+"\t"+percent+"%\n");	
			}else{
				System.out.println("Task "+(i+1)+"\taborted\t");
			}
		}
		waitingTimePercent = (totalWaitingTime*100)/totalTime;
		System.out.println("total\t"+totalTime+"\t"+totalWaitingTime+"\t"+waitingTimePercent+"%");
	}
}