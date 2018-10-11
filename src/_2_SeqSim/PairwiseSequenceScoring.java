package _2_SeqSim;

import db_access_layer.DatabaseAccessLayer;

public class PairwiseSequenceScoring {
	
	//create a table sim_score(id, method_ID_1, method_ID_2, score)
	//create a table sequence(id, method_id, sequence) this will contain the sequence strings
 	
	public static void main (String args[]) throws Exception
	{
		DatabaseAccessLayer dbLayer = DatabaseAccessLayer.getInstance();
		dbLayer.initializeConnectorForSimSeq();
		//populate sequence table
		//iterate over the api_call table and host_method_id column and
		//for every new methodID, persist the api_call_index_id accumulated so far in a string into the sequence table
		//=====Step1=======
		dbLayer.populateSequenceTable();
		
		//======Step2======
		//Calculating the scores
		
		//dbLayer.populateSimScoreTable();
		dbLayer.closeConnector();
	}
	
	
}
