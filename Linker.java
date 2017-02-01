/* package codechef; // don't place package name! */

import java.util.*;
import java.lang.*;
import java.io.*;

/* Name of the class has to be "Main" only if the class is public. */
class Codechef
{
	public static void main (String[] args) throws java.lang.Exception
	{
		// your code goes here
	    Scanner scanner = new Scanner(System.in);
		int numberOfModules = scanner.nextInt();
		int currentBaseAddress = 0;
		HashMap<String, Integer> definitions = new HashMap<String, Integer>();
		for(int x=0;x<numberOfModules;x++){
			int numberOfDefinitions = scanner.nextInt();
			for(int y=0;y<numberOfDefinitions;y++){
			    String temp1;
			    int temp2;
			    temp1 = scanner.next();
			    temp2 = scanner.nextInt();
			    definitions.put(temp1,temp2);
			}
            System.out.println(definitions);
            int numberOfUses = scanner.nextInt();
            Map<String, List<Integer>> uses = new HashMap<String, List<Integer>>();
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
            int numberOfProgramLines = scanner.nextInt();
            HashMap<String, Integer> program = new HashMap<String, Integer>();
            for(int y=0;y<numberOfProgramLines;y++){
                
                String temp1;
			    int temp2;
			    temp1 = scanner.next();
			    temp2 = scanner.nextInt();
			    program.put(temp1,temp2);
                
                
            }
		    System.out.println(program);
		    
		}    
	    
	}
	
}
