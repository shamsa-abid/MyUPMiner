package _3_Clustering_1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import DataObjects.ClusterDTO;

import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.PDistClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import db_access_layer.DatabaseAccessLayer;

public class DistanceMatrix {
	static DatabaseAccessLayer dbLayer;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		dbLayer = DatabaseAccessLayer.getInstance();
		
		double[][] my_distancematrix = new double[][]{};
		//read distances into my_distances from table sim_score 
		my_distancematrix = getDistanceMatrix();  	
		//simultaneously populate my_names with distinct entries of method_ID_1 column 
    	String[] my_names = new String[]{};
    	my_names = getMatrixLabels();
    	System.out.println("Success!");
    	ClusteringAlgorithm clustering_algo = new DefaultClusteringAlgorithm();
    	Double threshold = 0.999;		
    	List<Cluster> clustersList = clustering_algo.performFlatClustering(my_distancematrix, my_names, new CompleteLinkageStrategy(), threshold);
    	//the lines below are for the case of generating one cluster of all the data
		//Cluster c = DendrogramPanel.createCluster(my_distancematrix, my_names);
		//DendrogramPanel.visualizeCluster(c);
    	
    	
    	//Assign cluster IDs to sequences
    	//1.populate a hashmap for method IDs and sequence IDs
    	LinkedHashMap<Integer, Integer> methodSequenceMapping = new LinkedHashMap<Integer, Integer>() ;
    	methodSequenceMapping = dbLayer.getMethodSequenceMapping();
    	//Iterating for each cluster and creating a list ClusterDTOs to persist in DB
    	ArrayList<ClusterDTO> clusterDTOsList = new ArrayList<ClusterDTO>();
    	int ClusterID = 0;
    	for(Cluster c: clustersList)
    	{
    		//DendrogramPanel.visualizeCluster(c);
    		List<String> methodIDs = c.getLeafNames();
    		System.out.print("Cluster ID: " + ClusterID + " " );
    		System.out.print("Method IDs: ");
    		for(String methodID: methodIDs)
    		{    			
    			System.out.print(methodID + " ");
    			int mID = Integer.parseInt(methodID);
    			int seqID = methodSequenceMapping.get(mID);
    			
    			ClusterDTO clusterDTO = new ClusterDTO(ClusterID, seqID, mID);
    			clusterDTOsList.add(clusterDTO);
    		}
    		System.out.println();
    		ClusterID += 1;
    	}
    	System.out.println("Cluster list size:"+clustersList.size());
    	
    	//put the clusterDTOsList in the database table cluster
    	dbLayer.insertClusters(clusterDTOsList);
    	
    	
    	
    	dbLayer.closeConnector();
    	

	}
	private static String[] getMatrixLabels() throws ClassNotFoundException, SQLException {
		String[] my_names = new String[]{};		
		dbLayer.initializeConnectorForGettingDistanceMatrixLabels();
		my_names = dbLayer.getDistanceMatrixLabels();		
		return my_names;
	}
	public static double[][] getDistanceMatrix() throws ClassNotFoundException, SQLException
	{
		double[][] my_distancematrix = new double[][]{};		
		dbLayer.initializeConnectorForGettingDistanceMatrix();
		my_distancematrix = dbLayer.getDistanceMatrix();
		
		return my_distancematrix;		
	}
}
