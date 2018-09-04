package db_access_layer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import DataObjects.APICall;
import DataObjects.ClusterDTO;


public class DatabaseAccessLayer {
	
	public static final DatabaseAccessLayer SINGLETON = new DatabaseAccessLayer();
	private Connection connector;
	
	private PreparedStatement APICallSelection;
	private PreparedStatement APICallIndexSelection;
	private PreparedStatement APICallIndexInsertion;
	private PreparedStatement APICallUpdation;
	private PreparedStatement SequenceInsertion;
	private PreparedStatement SequenceSelection1;
	private PreparedStatement SequenceSelection2;
	private PreparedStatement ScoreInsertion;
	private PreparedStatement SimScoreSelection;
	private PreparedStatement DistinctMethodIDSelection;
	private PreparedStatement ClusterInsertion;
		
	 public static DatabaseAccessLayer getInstance() { 
	        return DatabaseAccessLayer.SINGLETON;
	} 
    public void initializeConnector() throws Exception
    {
    	Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        this.connector = DriverManager.getConnection(Utilities.Constants.DATABASE);       

        this.APICallSelection = this.connector.prepareStatement(
   				"SELECT id, api_name, api_usage from api_call");
        this.APICallIndexSelection = this.connector
				.prepareStatement("SELECT id FROM api_call_index WHERE INSTR(api_call, ?) > 0 ");
        this.APICallIndexInsertion = this.connector
				.prepareStatement("INSERT INTO api_call_index VALUES(0,?)", Statement.RETURN_GENERATED_KEYS);
        this.APICallUpdation = this.connector
				.prepareStatement("UPDATE api_call set api_call_index_id = ? where id = ?");
        
		this.connector.setAutoCommit(false);
    }
    
    public void initializeConnectorForSimSeq() throws Exception
    {
    	Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        this.connector = DriverManager.getConnection(Utilities.Constants.DATABASE);       

        this.APICallSelection = this.connector.prepareStatement(
   				"SELECT id, host_method_id, api_call_index_id from api_call");
        this.SequenceInsertion = this.connector
				.prepareStatement("INSERT INTO sequence VALUES(0,?,?)");//, Statement.RETURN_GENERATED_KEYS); 
        this.SequenceSelection1 = this.connector.prepareStatement(
   				"SELECT id, method_ID, sequence from sequence");
        this.SequenceSelection2 = this.connector.prepareStatement(
   				"SELECT id, method_ID, sequence from sequence");
        this.ScoreInsertion = this.connector
				.prepareStatement("INSERT INTO sim_score VALUES(0,?,?,?)");//, Statement.RETURN_GENERATED_KEYS); 
        
		this.connector.setAutoCommit(false);
    }
    
    public void initializeConnectorForGettingDistanceMatrix() throws ClassNotFoundException, SQLException {
		
    	Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        this.connector = DriverManager.getConnection(Utilities.Constants.DATABASE);       

        this.SimScoreSelection = this.connector.prepareStatement(
   				"SELECT id, method_ID_1, method_ID_2, score from sim_score");
       
        
		this.connector.setAutoCommit(false);
		
	}
    public void closeConnector() throws SQLException{
    	this.connector.close();
    }
    public void populateAPICallIndex() throws SQLException {
    	
    	APICall apiCall = new APICall();
    	ResultSet apiCallsResultSet = APICallSelection.executeQuery();

    	//iterate on every api call
    	while(apiCallsResultSet.next())
    	{            	
    		apiCall.id = apiCallsResultSet.getInt(1);
    		apiCall.api_name = apiCallsResultSet.getString(2);
    		apiCall.api_usage = apiCallsResultSet.getString(3);
    		apiCall.fullAPIcall = apiCall.api_name + "." + apiCall.api_usage;
    		
    		//check if not exists in api_call_index table
    		APICallIndexSelection.setString(1,apiCall.fullAPIcall);
    		ResultSet apiCallIndexResultSet = APICallIndexSelection.executeQuery();
    		if(apiCallIndexResultSet.next())//exists
        	{ 
    			//update the api_call_index column in api_call table with the id
    			int index_id = apiCallIndexResultSet.getInt(1);
    			APICallUpdation.setInt(1, index_id);
    			APICallUpdation.setInt(2, apiCall.id);
    			APICallUpdation.addBatch();
        	}
    		else
    		{
    			//add the api call to the api_call_index table    			
    			APICallIndexInsertion.setString(1, apiCall.fullAPIcall);        		
        		APICallIndexInsertion.executeUpdate();    
        		ResultSet rs = APICallIndexInsertion.getGeneratedKeys();                 
                if(rs.next()){
                	int index_id = rs.getInt(1);
                	//update the api_call_index column in api_call table with the id
        			APICallUpdation.setInt(1, index_id);
        			APICallUpdation.setInt(2, apiCall.id);
        			APICallUpdation.addBatch();
                }
                rs.close();
        		
    		}
    		apiCallIndexResultSet.close();

    	}
    	APICallUpdation.executeBatch();
    	apiCallsResultSet.close();
    	APICallSelection.close();
    	APICallIndexSelection.close();
    	APICallIndexInsertion.close();
    	APICallUpdation.close();
    	connector.commit();
    	

    }
	public void populateSequenceTable() throws SQLException {
		
		ResultSet apiCallsResultSet = APICallSelection.executeQuery();
		String sequence = "";
		int prev_methodID = -1;

    	//iterate on every api call
    	while(apiCallsResultSet.next())
    	{       
    		int method_id = apiCallsResultSet.getInt(2);
    		int index_id = apiCallsResultSet.getInt(3);
    		if(method_id == prev_methodID || prev_methodID == -1)
    			sequence = sequence.concat(Integer.toString(index_id)+ " ");
    		else
    		{
    			//insert sequence string in sequence table
    			//remove the last space
    			sequence = sequence.substring(0, sequence.length()-1);
    			SequenceInsertion.setInt(1, prev_methodID);   
    			SequenceInsertion.setString(2, sequence);       			
    			SequenceInsertion.executeUpdate();  
    			sequence = "";
    			sequence = sequence.concat(Integer.toString(index_id)+ " ");
    			
    		}
    		prev_methodID = method_id;
    		
    	}
    	apiCallsResultSet.close();
    	APICallSelection.close();
    	SequenceInsertion.close();
    	connector.commit();
    	
	}
	public void populateSimScoreTable() throws SQLException {
		//iterate over the sequence table in two for loops for pairwise comparison
		//for every comparison call SeqSimCalculation.seqSim function to get scores and save in sim_score
		String seq1 = "a b c";
		String seq2 = "c a b";
		ResultSet seqResultSet1 = SequenceSelection1.executeQuery();
		while(seqResultSet1.next())
		{
			int method_ID_1 = seqResultSet1.getInt(2);
			String sequence1 = seqResultSet1.getString(3);
			
			ResultSet seqResultSet2 = SequenceSelection2.executeQuery();
			while(seqResultSet2.next())
			{
				int method_ID_2 = seqResultSet2.getInt(2);
				String sequence2 = seqResultSet2.getString(3);
				double score = Utilities.SeqSimCalculation.seqSim(sequence1, sequence2);
				//insert in sim_score
				ScoreInsertion.setInt(1, method_ID_1);   
				ScoreInsertion.setInt(2, method_ID_2); 
				DecimalFormat df = new DecimalFormat("#.###");
				
    			ScoreInsertion.setDouble(3, Double.parseDouble(df.format(score)));       			
    			ScoreInsertion.executeUpdate(); 
			}
			seqResultSet2.close();
		}
		
		seqResultSet1.close();
		
		SequenceSelection1.close();
		SequenceSelection2.close();
		SequenceInsertion.close();
		connector.commit();
		
	}
	public double[][] getDistanceMatrix() throws SQLException {
		ArrayList<ArrayList<Double>> my_distances = new ArrayList<ArrayList<Double>>();
    	ArrayList<Double> distance = new ArrayList<Double>();   
    	
		ResultSet resultSet = SimScoreSelection.executeQuery();	
		
		int prev_method_ID = -1;
		
		while(resultSet.next())
		{
			int method_ID_1 = resultSet.getInt(2);
			int method_ID_2 = resultSet.getInt(3);
			double score = resultSet.getDouble(4);
			
			
			//put the first entry in the first position
			//keep track of index1 and index2, the change in method id should
			//result in increment of index1 and reset index2 to 0
			
			
			if((prev_method_ID == -1) || prev_method_ID == method_ID_1)
			{
				
					//index2 += 1;
					distance.add(1-score);
			}
			else
			{
					my_distances.add(distance);
					distance = new ArrayList<Double>();
					distance.add(1-score);
					//index1 += 1;
					//index2 = 0;
			}						
			
			prev_method_ID = method_ID_1;	
				    	
			//my_distancematrix[index1][index2] = score;
		}
		my_distances.add(distance);//for the last row
		
		SimScoreSelection.close();
		double[][] my_distancematrix = new double[my_distances.size()][];    	
    	for (int i = 0; i < my_distances.size(); i++) {    		
    	    ArrayList<Double> row = my_distances.get(i);
    	    Double[] array1 = row.toArray(new Double[row.size()]);    	    
    	    my_distancematrix[i] = Stream.of(array1).mapToDouble(Double::doubleValue).toArray();    	    
    	}		
		return my_distancematrix;
		
		
	}
	public void initializeConnectorForGettingDistanceMatrixLabels() throws ClassNotFoundException, SQLException {
		
		Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        this.connector = DriverManager.getConnection(Utilities.Constants.DATABASE);       

        // this.DistinctMethodIDSelection = this.connector.prepareStatement(
   		//		"SELECT DISTINCT method_ID_1 from sim_score");
        this.DistinctMethodIDSelection = this.connector.prepareStatement(
   				"SELECT DISTINCT method_ID_1, sequence.id FROM sim_score INNER JOIN sequence ON sim_score.method_ID_1 = sequence.method_ID");
        this.ClusterInsertion = this.connector.prepareStatement("INSERT INTO cluster VALUES(0,?,?,?)");     
		this.connector.setAutoCommit(false);
		
	}
	public String[] getDistanceMatrixLabels() throws SQLException {
		
		ArrayList<Integer> labels = new ArrayList<Integer>();
		ResultSet resultSet = DistinctMethodIDSelection.executeQuery();
				
		while(resultSet.next())
		{
			int method_ID = resultSet.getInt(1);
			int seq_ID = resultSet.getInt(2);
			labels.add(method_ID);
			
		}
		String[] my_labels = new String[labels.size()];
		for(int i=0; i<labels.size(); i++)
		{
			my_labels[i] = labels.get(i).toString();
		}
		//DistinctMethodIDSelection.close();
		return my_labels;
	}
	
	public LinkedHashMap<Integer, Integer> getMethodSequenceMapping() throws SQLException {
		
		LinkedHashMap<Integer, Integer> methodSeqMap = new LinkedHashMap<Integer, Integer>();		
		ResultSet resultSet = DistinctMethodIDSelection.executeQuery();
		
		while(resultSet.next())
		{
			int method_ID = resultSet.getInt(1);
			int seq_ID = resultSet.getInt(2);
			methodSeqMap.put(method_ID, seq_ID);
			
		}
		DistinctMethodIDSelection.close();
		return methodSeqMap;
	}
	public void insertClusters(ArrayList<ClusterDTO> clusterDTOsList) throws SQLException {
		for(ClusterDTO cluster: clusterDTOsList)
		{
			ClusterInsertion.setInt(1, cluster.clusterID);   
			ClusterInsertion.setInt(2, cluster.seqID);    
			ClusterInsertion.setInt(3, cluster.methodID);  
			ClusterInsertion.addBatch();  
		}
		
		int[] inserted = ClusterInsertion.executeBatch();
		ClusterInsertion.close();
		connector.commit();
	}
	

	
}
