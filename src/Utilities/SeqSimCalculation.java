package Utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//This uses LinkedHashMaps to store key value pairs of <sequence,length>
public class SeqSimCalculation {
	public static boolean isMaxGram;
	public static void main(String args[])
	{
		//two sequences s1 and s2
		String seq1 = "a b c";
		String seq2 = "c a b";
		
		double score = seqSim(seq1, seq2);
	}

	public static double seqSim(String seq1, String seq2) {
		LinkedHashMap<String, Integer> ngramsSeq1 = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, Integer> ngramsSeq2 = new LinkedHashMap<String, Integer>();
		
		LinkedHashMap<String, Integer> GIntersection = new LinkedHashMap<String, Integer>() ;		
		LinkedHashMap<String, Integer> GUnion = new LinkedHashMap<String, Integer>() ;
		
		//print all n-grams one by one
		ngramsSeq1.putAll(getNgrams(seq1));	
		ngramsSeq2.putAll(getNgrams(seq2));
		//get intersection		
		GIntersection.putAll(ngramsSeq1);
		GIntersection.keySet().retainAll(ngramsSeq2.keySet());		
		
		//GIntersection = intersection(ngramsSeq1, ngramsSeq2);	
		//print intersection 
		System.out.println("Printing intersection");
		Set<String> keys = GIntersection.keySet();
        for(String k:keys){
            System.out.println(k);
        }
		//get union
		GUnion.putAll(union(ngramsSeq1, ngramsSeq2));		
		//print union
		System.out.println("Printing union");
		Set<String> unionkeys = GUnion.keySet();
        for(String k:unionkeys){
            System.out.println(k);
        }
		
		//calculate seq simm by formula
		double score = getSumofWeights(GIntersection)/getSumofWeights(GUnion);
		System.out.println(score);
		return score;
	}
	
	 private static double getSumofWeights(LinkedHashMap<String, Integer> seq) {
		double sum = 0;
		
		Set<Entry<String, Integer>> entries = seq.entrySet();
		
		for ( Entry<String, Integer> entry : entries ) {
		   sum += entry.getValue();
		} 
		return sum;
	}
	
	public static LinkedHashMap<String, Integer> union(LinkedHashMap<String, Integer> ngramsSeq1, LinkedHashMap<String, Integer> ngramsSeq2) {
	        
		LinkedHashMap<String, Integer> set = new LinkedHashMap<String, Integer>();
		set.putAll(ngramsSeq1);
		
		//iterate on ngramsSeq1 and then compare its keys with 
		Set<Entry<String, Integer>> entries = ngramsSeq2.entrySet();
		for ( Entry<String, Integer> entry : entries ) {
		  String secondMapKey = entry.getKey();
		  if ( !set.containsKey(secondMapKey) ) {
			  set.put( entry.getKey(), entry.getValue() );
		  } 
		 
		} 

        return set;
	}
	
	 public static <NGram>List<NGram> intersection(List<NGram> list1, List<NGram> list2) {
		// unnecessary; just an optimization to iterate over the smaller set
		
	        
		    if (list1.size() > list2.size()) {
		        return intersection(list2, list1);
		    }
		    Set<NGram> set1 = new HashSet<NGram>();
			Set<NGram> set2 = new HashSet<NGram>();

		     set1.addAll(list1);
		     set2.addAll(list2);

		    List<NGram> results = new ArrayList<NGram>();

		    for (NGram element : set1) {
		        if (!results.contains(element)) {
		            results.add(element);
		        }
		    }
		    for (NGram element : set2) {
		        if (!results.contains(element)) {
		            results.add(element);
		        }
		    }

		    return results;
	    } 

	private static LinkedHashMap<String, Integer> getNgrams(String seq1) {
		
		LinkedHashMap<String, Integer> ngrams = new LinkedHashMap<String, Integer>() ;
		for(int i=1; i<=seq1.length(); i++)
		{
			ngrams.putAll(getNgramsSpaceSeparated(i, seq1));
			//if( ngrams.get(ngrams.size()-1).ngramString.length() == seq1.length() )
			if(isMaxGram)	
				break;
		}
		isMaxGram = false;
		return ngrams;
	}
	
	private static LinkedHashMap<String, Integer> getNgramsSpaceSeparated(int n,
			String str) {
		
		//List<String> ngrams = new ArrayList<String>();
		LinkedHashMap<String, Integer> ngramObjects = new LinkedHashMap<String, Integer>();
		int i=0;
	    while(i < str.length())
	    {
	    	//get the substr till location of space at nth occurrence
	    	int iterationSpaceIndex = i;
	    	int spaceIndex = i;
	    	int spaceCount = 0;
	    	int it = 1;
	    	
	    	iterationSpaceIndex = str.indexOf(" ", i); 
	    	
	    	while(it <= n && spaceIndex != -1)
	    	{	    		
	    		spaceIndex = str.indexOf(" ", spaceIndex+1);
	    		spaceCount++;
	    		it++;
	    	}
	    	
	    	String igram = "";
	    	if(spaceIndex == -1)
	    	{
	    		igram = str.substring(i, str.length());
	    		//update i
		        i = str.length();
	    	}
	    	else
	    	{
	    		igram = str.substring(i, spaceIndex);
	    		//update i
		        i = iterationSpaceIndex +1;
	    	}
	        //ngrams.put(igram);
	        //NGram ng = new NGram(igram, igram.length()-spaceCount+1);
	        ngramObjects.put(igram, igram.length()-spaceCount+1);
	        System.out.println(igram);
	        if(igram.length()== str.length())
	        {
	        	isMaxGram = true;
	        }
	        //System.out.println(ng.ngramLength);
	        
	    }
	    
	    return ngramObjects;
	}

	/*//ngrams(3, "abcde") = ["abc", "bcd", "cde"].
	public static List<String> getNgrams(int n, String str) {
	    List<String> ngrams = new ArrayList<String>();
	    for (int i = 0; i < str.length() - n + 1; i++)
	    {
	        ngrams.add(str.substring(i, i + n));
	    }
	    return ngrams;
	}*/
}
