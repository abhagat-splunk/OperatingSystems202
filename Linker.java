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
		ArrayList<HashMap<String,List<Integer>>> newUses = new ArrayList<HashMap<String,List<Integer>>>();
		ArrayList<String> programLines = new ArrayList<String>();
		HashMap<String, Integer> SymbolTable = new HashMap<String, Integer>();
		for(int x=0;x<numberOfModules;x++){
			int numberOfDefinitions = scanner.nextInt();
			for(int y=0;y<numberOfDefinitions;y++){
			    String temp1;
			    int temp2;
			    temp1 = scanner.next();
			    temp2 = scanner.nextInt();
			    SymbolTable.put(temp1,temp2+currentBaseAddress);
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
            if(numberOfUses!=0) 
                newUses.add(uses);
            int numberOfProgramLines = scanner.nextInt();
            baseAddresses.add(currentBaseAddress);
            currentBaseAddress+=numberOfProgramLines;
            
            if(numberOfProgramLines!=0)
                programLines.add(scanner.nextLine());
		}
	 System.out.println("Symbol Table");
	 Iterator<HashMap.Entry<String, Integer>> entries =  SymbolTable.entrySet().iterator();
	 HashMap<String, String> stringSymbolTable = new HashMap<String, String>();
	 while(entries.hasNext()){
	 	HashMap.Entry<String, Integer> entry = entries.next();
	 	String tempKey = entry.getKey();
	 	Integer tempVal = entry.getValue();
	 	System.out.println(tempKey+'='+Integer.toString(tempVal));
	 	stringSymbolTable.put(tempKey,String.format("%03d",SymbolTable.get(tempKey)));
	 }
	 //  Iterator<HashMap.Entry<String, String>> entriesTwo = stringSymbolTable.entrySet().iterator();
	 // while(entriesTwo.hasNext()){
	 // 	HashMap.Entry<String, String> entry = entriesTwo.next();
	 // 	String tempKey = entry.getKey();
	 // 	String tempVal = entry.getValue();
	 // 	System.out.println(tempKey+'='+tempVal);		
	 // }

	 // System.out.println(baseAddresses);
	 // System.out.println(newUses);
	 int counterSecond = 0;
	 int overAllCounter = 0;
	 for(String s: programLines){
	     //System.out.println(s);
	     s = s.replaceAll("\\s+", " ");
	     System.out.println(s);
	     String[] temparr = s.split(" ");
	     HashMap<String, List<Integer>> thisUses = newUses.get(counterSecond);
	     //System.out.println(thisUses);   
	     for(int i=1;i<temparr.length;i+=2){
	     	if(temparr[i].charAt(0)=='R'){
	     		temparr[i+1]=Integer.toString(Integer.parseInt(temparr[i+1])+baseAddresses.get(counterSecond));
	     	}
	     	if(temparr[i].charAt(0)=='E'){
	     		int findIndex = (i-1)/2;
	     		String findKey = "";
	     		for (String key : thisUses.keySet())
        		{
            		List values = thisUses.get(key);
            		for(int q=0;q<values.size();q++){
            			//System.out.println(values.get(q));
            			if((Integer)values.get(q)==findIndex){
            				findKey = key;
            				break;
            			}
            		}
            		//use key and value
        		}
	     		//System.out.println(findIndex);
	     		temparr[i+1] = Character.toString(temparr[i+1].charAt(0))+stringSymbolTable.get(findKey);
	     		//temparr[i+1]=Character.toString(temparr[i+1].charAt(0))+;
	     	}
	     	System.out.println(overAllCounter+": "+temparr[i+1]);
	     	overAllCounter+=1;
	     }

	     counterSecond+=1;
	 }
	}
	
}
