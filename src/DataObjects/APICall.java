package DataObjects;

public class APICall {
	public int id;
	public String api_name ;
	public String api_usage ; 
	public String fullAPIcall;

	public APICall()
	{
	
	}
			
	public APICall(int Id, String APIName, String APIusage)
	{
		this.id = Id;		
		this.api_name = APIName;
		this.api_usage = APIusage;
		this.fullAPIcall = APIName + "." + APIusage;
 		
	
	}

}
