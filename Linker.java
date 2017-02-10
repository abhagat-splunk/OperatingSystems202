import java.util.*;
import java.lang.*;
import java.io.*;

/* Name of the class has to be "Main" only if the class is public. */
class Linker
{
	public static void main (String[] args) throws java.lang.Exception
	{
		// your code goes here
	    Scanner scanner = new Scanner(System.in);
		int numberOfModules = scanner.nextInt();
		int currentBaseAddress = 0;
		ArrayList<Integer> baseAddresses = new ArrayList<Integer>();
		//baseAddresses.add(currentBaseAddress);
		//System.out.println("LOL");
		ArrayList<HashMap<String,List<Integer>>> newUses = new ArrayList<HashMap<String,List<Integer>>>();
		ArrayList<String> programLines = new ArrayList<String>();
		HashMap<String, Integer> SymbolTable = new HashMap<String, Integer>();
		ArrayList<String> MultipleDefinitions = new ArrayList<String>();
		HashMap<String,Integer> UsedOrNot = new HashMap<String,Integer>();
		for(int x=0;x<numberOfModules;x++){
			int numberOfDefinitions = scanner.nextInt();
			for(int y=0;y<numberOfDefinitions;y++){
			    String temp1;
			    int temp2;
			    temp1 = scanner.next();
			    temp2 = scanner.nextInt();
			    if(SymbolTable.containsKey(temp1)){
			    	MultipleDefinitions.add(temp1);
			    }
			    else{
			    SymbolTable.put(temp1,temp2+currentBaseAddress);
			    UsedOrNot.put(temp1,x+1);
				}
			}
            int numberOfUses = scanner.nextInt();
            HashMap<String, List<Integer>> uses = new HashMap<String, List<Integer>>();
            for(int y=0;y<numberOfUses;y++){
                String tempKey = scanner.next();
                if (uses.get(tempKey) == null) {
                        uses.put(tempKey,new ArrayList<Integer>());
                    }
                int tempVal = scanner.nextInt();
                while(tempVal!=-1){
                    uses.get(tempKey).add(tempVal);
                    tempVal = scanner.nextInt();
                }
                }
            System.out.println(uses);    
            newUses.add(uses);
            int numberOfProgramLines = scanner.nextInt();
            System.out.println(numberOfProgramLines);
            baseAddresses.add(currentBaseAddress);
            currentBaseAddress+=numberOfProgramLines;
            
            if(numberOfProgramLines!=0){
            	String s = "";
            	numberOfProgramLines=numberOfProgramLines*2;
            	while(numberOfProgramLines>0){
            		String t = scanner.next()+" ";
            		s+=t;
            		numberOfProgramLines-=1;
            	}
            	programLines.add(s);
            }
            //System.out.println(programLines);
		}
	 System.out.println("Symbol Table");
	 Iterator<HashMap.Entry<String, Integer>> entries =  SymbolTable.entrySet().iterator();
	 HashMap<String, String> stringSymbolTable = new HashMap<String, String>();
	 while(entries.hasNext()){
	 	HashMap.Entry<String, Integer> entry = entries.next();
	 	String tempKey = entry.getKey();
	 	Integer tempVal = entry.getValue();
		System.out.print(tempKey+'='+Integer.toString(tempVal));
	 	if (MultipleDefinitions.contains(tempKey)){
	 		System.out.print(" Error: This variable is multiply defined; first value used.");
	 	}
	 	System.out.print("\n");
	 	stringSymbolTable.put(tempKey,String.format("%03d",SymbolTable.get(tempKey)));
	 }
	 
	 int counterSecond = 0;
	 int overAllCounter = 0;
	 System.out.println("Memory Map");
	 for(String s: programLines){
	     //System.out.println(s);
	     s = s.replaceAll("\\s+", " ");
	     //System.out.println(s);
	     String[] temparr = s.split(" ");
	     HashMap<String, List<Integer>> thisUses = newUses.get(counterSecond);
	     //System.out.println(thisUses);   
	     for(int i=0;i<temparr.length;i+=2){
	     	if(temparr[i].charAt(0)=='R'){
	     		temparr[i+1]=Integer.toString(Integer.parseInt(temparr[i+1])+baseAddresses.get(counterSecond));
	     	}
	     	if(temparr[i].charAt(0)=='E'){
	     		int findIndex = i/2;
	     		//System.out.println("Find Index"+Integer.toString(i));
	     		String findKey = "";
	     		//System.out.println(findIndex);
	     		
	     		for (String key : thisUses.keySet())
        		{
            		List values = thisUses.get(key);
            		for(int q=0;q<values.size();q++){
            			//System.out.print("Inside");
            			//System.out.println(values.get(q));
            			if((Integer)values.get(q)==findIndex){
            				//System.out.println("WTF!");
            				findKey = key;
            				UsedOrNot.put(key,-1);
            				break;
            			}
            		}
            		//use key and value
        		}
        		if(stringSymbolTable.containsKey(findKey))
	     			temparr[i+1] = Character.toString(temparr[i+1].charAt(0))+stringSymbolTable.get(findKey);
	     		else{
	     			temparr[i+1] = Character.toString(temparr[i+1].charAt(0))+"000";
	     			System.out.print(overAllCounter+": "+temparr[i+1]);
	     			System.out.println(" Error: "+findKey+" is not defined; zero used.");
	     			continue;
	     		}
	     		//System.out.println(temparr[i+1]);
	     		//temparr[i+1]=Character.toString(temparr[i+1].charAt(0))+;
	     	}
	     	if(temparr[i].charAt(0)=='A'){
	     		if(Integer.parseInt(temparr[i+1].substring(1,4))>200){
	     			temparr[i+1] = temparr[i+1].substring(0,1)+"000";
	     			System.out.print(overAllCounter+": "+temparr[i+1]);
	     			System.out.println(" Error: Absolute address exceeds machine size; zero used.");
	     			continue;
	     		}
	     	}	

	     	System.out.println(overAllCounter+": "+temparr[i+1]);
	     	overAllCounter+=1;
	     }

	     counterSecond+=1;
	 }
	Iterator<HashMap.Entry<String, Integer>> entriesThree =  UsedOrNot.entrySet().iterator();
	while(entriesThree.hasNext()){
		HashMap.Entry<String, Integer> entry = entriesThree.next();
		String tempKey = entry.getKey();
	 	Integer tempVal = entry.getValue();
	 	if(tempVal!=-1)
	 		System.out.println("Warning: "+tempKey+" was defined in module "+Integer.toString(tempVal)+" but never used.");
	}  
	}
	
}
