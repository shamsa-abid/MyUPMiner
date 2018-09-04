package _4_MineFrequentSequences;

public class MineFrequentSequences {

	public static void main(String args[])
	{
		//each cluster is a pattern of sequences
		//sequences within a luster may be different from each other
		//a cluster is valuable if there are many different sequences in a cluster
		//only then it makes sense to mine frequent sequences from those sequences
		
		//what i have decided for now is that I will take only clusters having more than 3 sequences as a frequent pattern
		//then I will build a transaction table of clusterIDs against every project
		//these cluster IDs should represent a cluster containing at least 3 sequences
		
		//for now I will skip this part of UPMiner and build a transaction table
		//to detect co-occurring patterns of sequences across projects
		//the table that will contain the mined co-occurring patterns will be called related_features
		//related_features(id,clusterID,)
		
	}
}
