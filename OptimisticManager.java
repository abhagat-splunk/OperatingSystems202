import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author xynazog
 *
 */
public class OptimisticManager {

	
	public static void printArray(int[] array){
		if(array.length>0){
			for(int x: array){
			System.out.print(x+" ");
		}
		System.out.println();	
		}
		else{
			System.out.println("NOTHING IN THE ARRAY!");
		}
	}

	public static void readFromFile(ArrayList<String> Inputs, String inputFilename){
		try{
			File text = new File(inputFilename);
			Scanner scFile = new Scanner(text);
			while(scFile.hasNextLine()){
				String line = scFile.nextLine();
				Inputs.add(line);//Adding the input to an arraylist line by line
			}	
			scFile.close();
		}
		//If File not found!
		catch (FileNotFoundException fe){
			System.out.println("File not found!");
		}
	}


	public static int getIndexOfMinimum(ArrayList<Integer> Blocked){
		int indexMin = -1;
		int numberMin = Integer.MAX_VALUE;
		for(int i=0;i<Blocked.size();i++){
			if(Blocked.get(i)<numberMin){
				indexMin = i;
				numberMin = Blocked.get(i);
			}
		}
		return indexMin;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int numberOfTasks, numberOfResources;
		//Initializing scanner object
		String inputFilename = args[0]; //args[0] will have the file name
		ArrayList<String> Inputs = new ArrayList<String>();
		//System.out.println(inputFilename);
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
		
		for(int x=0;x<SortedInputs.size();x++){
			System.out.println(x+1);
			for(int y=0;y<SortedInputs.get(x).size();y++){
				System.out.println(SortedInputs.get(x).get(y));
			}
			System.out.println();
		}
		*/
		int timeCounter = 0+numberOfResources;
		int[] minimumResources = new int[numberOfResources];
		
		while(terminating<numberOfTasks){
			
			if(originalBlockedIds.size()!=0){
					

				//System.out.println("Original blocked IDs: "+originalBlockedIds);


				if(originalBlockedIds.size()==(numberOfTasks-terminating)){
					//System.out.println("Deadlock!");
					int minIndex = getIndexOfMinimum(originalBlockedIds);
					int deadlockRemoveProcess = originalBlockedIds.get(minIndex);
					//System.out.println(originalBlockedIds);
					ArrayList<Integer> temp = FinalOutput.get(deadlockRemoveProcess-1);
					temp.add(-1);
					FinalOutput.put(deadlockRemoveProcess-1,temp);
					//System.out.println("Removing process "+deadlockRemoveProcess);
					terminating+=1;
					terminated.add(originalBlockedIds.get(minIndex));
					originalBlockedIds.remove(minIndex);
					for(int i=0;i<numberOfResources;i++){
						resources[i]+=alreadyClaimed[deadlockRemoveProcess-1][i];
					}
				}	
				int[] tempResourcesSubtracted = new int[numberOfResources];
				for(Integer i:originalBlockedIds){
					WaitingTime[i-1]+=1;
					String req = SortedInputs.get(i-1).get(0);
					String[] zSplit = req.split("\\s+");
					int currentResourceID = Integer.parseInt(zSplit[1])-1;
					int currentResourceNeed = Integer.parseInt(zSplit[2]);
					//System.out.println(resources[currentResourceID]);
					//System.out.println(currentResourceNeed);
					if(resources[currentResourceID]>=currentResourceNeed){
						resources[currentResourceID]-=currentResourceNeed;
						tempResourcesSubtracted[currentResourceID]+=currentResourceNeed;
						tempBlockedIds.add(i);
					}
				}
				for(int i=0;i<numberOfResources;i++){
					resources[i]+=tempResourcesSubtracted[i];
				}
				//System.out.println("Temporary Blocked IDs:"+tempBlockedIds);
				for(Integer key:tempBlockedIds){
					originalBlockedIds.remove(key);
				}
			}
			if(originalBlockedIds.size()>0 && tempBlockedIds.size()==0 && (originalBlockedIds.size()+tempBlockedIds.size()==(numberOfTasks-terminating))){
				for(Integer i:originalBlockedIds){
					WaitingTime[i-1]-=1;
				}
				continue;
			}
			ArrayList<Integer> orderOfIds = new ArrayList<Integer>();
			for(Integer k: tempBlockedIds){
				orderOfIds.add(k);
			}
			
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
					//System.out.println("Process "+x+": "+SortedInputs.get(x-1).get(0));
					String z = SortedInputs.get(x-1).get(0);
					String[] zSplit = z.split("\\s+");
					int currentResourceID = Integer.parseInt(zSplit[1])-1;
					int currentResourceNeed = Integer.parseInt(zSplit[2]);
					
					//printArray(computeCount);
					
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
					// System.out.println("Requesting number "+currentResourceNeed+" of Resource ID: "+(currentResourceID+1)+" for "+x);
					// System.out.println(x+" has already claimed "+alreadyClaimed[x-1][currentResourceID]);
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
							if(resources[currentResourceID]>=currentResourceNeed){
								//System.out.println("Allocating resources!");
								alreadyClaimed[x-1][currentResourceID]+=currentResourceNeed;
								resources[currentResourceID]-=currentResourceNeed;
								SortedInputs.get(x-1).remove(0);
							}
							else{
								//System.out.println("Not enough resources!");
								originalBlockedIds.add(x);
							}
						}
					}
					
					if(zSplit[0].equals("release")){
						//System.out.println("Releasing number "+currentResourceNeed+" of Resource ID: "+(currentResourceID+1)+"  for "+x);
						//System.out.println(x+" has already claimed "+alreadyClaimed[x-1][currentResourceID]);
						alreadyClaimed[x-1][currentResourceID]-=currentResourceNeed;
						addResources[currentResourceID]+=currentResourceNeed;
						//Removing the line	
						SortedInputs.get(x-1).remove(0);
					}
					if(zSplit[0].equals("terminate")){
						//System.out.println("Terminating "+x);
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
			tempBlockedIds = new ArrayList<Integer>();
			//Releasing the resources back to the manager at the end
			for(int x=0;x<numberOfResources;x++){
				//System.out.println("X: "+addResources[x]);
				resources[x] += addResources[x];
				addResources[x] = 0;
			}

			timeCounter+=1;
			//System.out.println();			
			//terminating = numberOfTasks;//Technical debt

		}
		System.out.println("\tFIFO\t");
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