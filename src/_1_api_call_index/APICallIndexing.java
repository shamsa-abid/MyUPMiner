package _1_api_call_index;

import db_access_layer.DatabaseAccessLayer;

public class APICallIndexing {
	public static void main (String args[]) throws Exception
	{
		DatabaseAccessLayer dbLayer = DatabaseAccessLayer.getInstance();
		dbLayer.initializeConnector();
		//populate api_call_index table
		dbLayer.populateAPICallIndex();
		dbLayer.closeConnector();
	}
}
