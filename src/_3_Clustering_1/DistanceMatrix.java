package _3_Clustering_1;

import java.sql.SQLException;

import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import db_access_layer.DatabaseAccessLayer;

public class DistanceMatrix {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		double[][] my_distancematrix = new double[][]{};
		//read distances into my_distances from table sim_score 
		my_distancematrix = getDistanceMatrix();  	
		//simultaneously populate my_names with distinct entries of method_ID_1 column 
    	String[] my_names = new String[]{};
    	my_names = getMatrixLabels();
    	System.out.println("Success!");
		Cluster c = DendrogramPanel.createCluster(my_distancematrix, my_names);
		DendrogramPanel.visualizeCluster(c);

	}
	private static String[] getMatrixLabels() throws ClassNotFoundException, SQLException {
		String[] my_names = new String[]{};
		DatabaseAccessLayer dbLayer = DatabaseAccessLayer.getInstance();
		dbLayer.initializeConnectorForGettingDistanceMatrixLabels();
		my_names = dbLayer.getDistanceMatrixLabels();
		dbLayer.closeConnector();
		return my_names;
	}
	public static double[][] getDistanceMatrix() throws ClassNotFoundException, SQLException
	{
		double[][] my_distancematrix = new double[][]{};
		DatabaseAccessLayer dbLayer = DatabaseAccessLayer.getInstance();
		dbLayer.initializeConnectorForGettingDistanceMatrix();
		my_distancematrix = dbLayer.getDistanceMatrix();
		dbLayer.closeConnector();
		return my_distancematrix;		
	}
}
