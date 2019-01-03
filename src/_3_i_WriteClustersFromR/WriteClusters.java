package _3_i_WriteClustersFromR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import DataObjects.ClusterDTO;
import db_access_layer.DatabaseAccessLayer;

public class WriteClusters {
	
	public static void main(String args[]) throws Exception
	{		
		DatabaseAccessLayer dbLayer = DatabaseAccessLayer.getInstance();
		dbLayer.initializeConnectorToWriteClustersFromR();
		//first read the sequence IDs from database and put then in a linked hashmap alongwith the methodIDs
		LinkedHashMap<Integer, Integer> seqIDmethodIDMapping = new LinkedHashMap<Integer, Integer>() ;
		seqIDmethodIDMapping = dbLayer.getSeqIDMethodIDMapping();
		//then read a csv file and parse it to get the cluster IDs in an array
		//LinkedHashMap<Integer, ArrayList<Integer>> clusterIDList = getClusterIDs("clusterIDs.csv");
		//LinkedHashMap<Integer, ArrayList<Integer>> clusterIDList = getClusterIDSequenceIDsMapping("clusterIDs10projectwithignore.csv");
		LinkedHashMap<Integer, ArrayList<Integer>> clusterIDList = getClusterIDSequenceIDsMapping("F:/PhD/PhD Defense/Code/R/clusterIDs_musicplayer_all_seqid.csv");
		
		//and store the clsuterDTO with the obtained IDs in the clsuterDTO list
		Set<Entry<Integer, ArrayList<Integer>>> entries = clusterIDList.entrySet();
		ArrayList<ClusterDTO> clusterDTOsList = new ArrayList<ClusterDTO> ();
		//int count = 0;
		
		for ( Entry<Integer, ArrayList<Integer>> entry : entries ) {
			int clusterID = entry.getKey();
			ArrayList<Integer> seqIDs = entry.getValue();
			for(int seqID: seqIDs)
			{
			ClusterDTO clusterDTO = new ClusterDTO(clusterID,seqID, seqIDmethodIDMapping.get(seqID));			
			clusterDTOsList.add(clusterDTO);
			}
			//count++;
		}
		dbLayer.insertClusters(clusterDTOsList);
		dbLayer.closeConnector();		
	}
	
	private static LinkedHashMap<Integer, ArrayList<Integer>> getClusterIDSequenceIDsMapping(String fileName) throws IOException {
	
		LinkedHashMap<Integer, ArrayList<Integer>> seqIDclusterIDMapping = new LinkedHashMap<Integer, ArrayList<Integer>>() ;
		ArrayList<Integer> clusterIDsList = new ArrayList<Integer> ();
		File file = new File(fileName);		 
		BufferedReader br = new BufferedReader(new FileReader(file));
		 
		String st;
		while ((st = br.readLine()) != null)
		{
			try{
			int clusterID = Integer.parseInt(st.substring(st.indexOf(",")+1));
			int seqID = Integer.parseInt(st.substring(st.indexOf("\"")+1,st.indexOf("\"", 1)));
			
						
			System.out.println(st);
			clusterIDsList.add(clusterID);
			ArrayList<Integer> seqIDs = seqIDclusterIDMapping.get(clusterID);
			if(seqIDs == null)
			{
				seqIDs = new ArrayList<Integer>();
			}
			seqIDs.add(seqID);
			seqIDclusterIDMapping.put(clusterID, seqIDs);
			}
			catch(Exception ex){
				//do nothing
			}
		}
		 		
		return seqIDclusterIDMapping;
	}
}
