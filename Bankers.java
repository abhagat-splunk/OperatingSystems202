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

	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
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
		HashMap<Integer,Integer> blocked = new HashMap<Integer,Integer>();

		//Initialization of numberOfEachResource present for the system to work on
		for(int x=0;x<numberOfResources;x++){
			resources[x] = Integer.parseInt(inputFirstLine[x+2]);
			resource_status[x] = "Unstarted";
		}

		//Process P chosen, initialized to -1.
		int processP = -1;
		//Resource ID of the resource released by Process P
		int processPReleaseResourceID = -1;
		//Number of resources released by P
		int processPReleaseAmount = 0;

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
		int timeCounter = 1;
		int[] minimumResources = new int[numberOfResources];
		while(terminating<numberOfTasks){
			System.out.println("Number of terminating processes: "+terminating);
			System.out.println("Time Interval: "+timeCounter+"-"+(timeCounter+1));
			//Checking if the blocked resources 
			//Include the highest priority code according to waiting time at the end of the loop!
			for(Integer key: blocked.keySet()){
				boolean status = false;
				for(int y=0;y<numberOfResources;y++){
					if(initialClaims[key-1][y]<=resources[y]){
						status = true;
					}
					else{
						status = false;
						break;
					}
				}
				if(status==true){
					blocked.remove(key);
				}
			}
			//
			for(int x=1;x<=numberOfTasks;x++){
				//Checking if process is blocked or not!
				if(terminated.contains(x)){
					continue;
				}
				System.out.println(SortedInputs.get(x-1).get(0));
				if(!blocked.containsKey(x)){
					String z = SortedInputs.get(x-1).get(0);
					String[] zSplit = z.split("\\s+");
					int currentResourceID = Integer.parseInt(zSplit[1])-1;
					int currentResourceNeed = Integer.parseInt(zSplit[2]);
					if(processP==-1){
						processP = x;
						//Copying the number of resources of each type needed to the safeResources array to calculate how much can be afforded
						for(int q=0;q<numberOfResources;q++){
							safeResources[q] = initialClaims[x-1][q];
						}
					}
					if(zSplit[0].equals("request")){
						System.out.println("Requesting for "+x);
						if(x==processP){
							//Adding resources to claimed just now
							alreadyClaimed[x-1][currentResourceID]+=currentResourceNeed;
							//Check if the recent claim is greater than initial claim
							if(alreadyClaimed[x-1][currentResourceID]>initialClaims[x-1][currentResourceID]){
								System.out.println("Aborting process "+Integer.toString(x)+". Claiming more resources than initial claim.");

								terminated.add(x);
								terminating+=1;
								/*
								Remaining - Release resources back to the banker.  Release all the resources used by the process!
								*/
							}
							else{
								safeResources[currentResourceID]-=currentResourceNeed;
								resources[currentResourceID]-=currentResourceNeed;
								//Removing the line	
								SortedInputs.get(x-1).remove(0);
							}
						}
						else{
							//If the process is not the selected one, check whether we have sufficient safe resources for the P if we provide resources to this process
							if((resources[currentResourceID]-currentResourceNeed)>=safeResources[currentResourceID]){
								//Check if resources claimed are more than initial claims
								resources[currentResourceID]-=currentResourceNeed;
								alreadyClaimed[x][currentResourceID]+=currentResourceNeed;
								//Removing the line	
								SortedInputs.get(x-1).remove(0);
							}
							else{
								if(!blocked.containsKey(x)){
									System.out.println("Blocking process "+x);
									blocked.put(x,1);
								}
							}
						}
					}
					if(zSplit[0].equals("release")){
						System.out.println("Releasing resources for "+x);
						addResources[currentResourceID]+=currentResourceNeed;
						if(x==processP){
							//Extra if part for safeResources adding up
							processPReleaseAmount+=currentResourceNeed;
							processPReleaseResourceID = currentResourceID;
						}
						//Removing the line	
						SortedInputs.get(x-1).remove(0);
					}
					if(zSplit[0].equals("terminate")){
						System.out.println("Terminating "+x);
						terminating+=1;
						terminated.add(x);
						if(x==processP){
							processP=-1;
							for(int q=0;q<numberOfResources;q++){
								safeResources[q] = 0;
							}
						}
						for(int q=0;q<numberOfResources;q++){
							resources[q] +=alreadyClaimed[x-1][q];
						}
						//Removing the line	
						SortedInputs.get(x-1).remove(0);
					}
				}
			}
			//Releasing the resources back to the manager at the end
			for(int x=0;x<numberOfResources;x++){
				resources[x] += addResources[x];
			}
			if(processPReleaseResourceID!=-1){
				safeResources[processPReleaseResourceID] += processPReleaseAmount;	
			}
			timeCounter+=1;
			System.out.println();	
			//System.out.println(blocked);
		}

	}
}