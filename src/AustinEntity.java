public class AustinEntity {

	private String owner_department;
	private ExternalLink external_link;
    private String account;
    private String type;
	
	 	public static class ExternalLink {
	 		private String url;
		    
	 		public ExternalLink(final String url){
		    	this.url = url;
		   	}
		    	
		   	public void linkToString(){
		   		System.out.println(url);
		   	}
		}//end ExternalLink
	    
	 public AustinEntity(final String owner_department, final ExternalLink external_link, final String account, final String type) {
	        this.external_link = new ExternalLink("");
		 	this.owner_department = owner_department;
	        this.external_link = external_link;
	        this.account = account;
		    this.type = type;
	 }
	    
	 public String getOwnerDepartment() {
	        return owner_department;
	 }

	 public String getExternalLink() {
		    return external_link.url;
	 }

	 public String getAccount() {
		    return account;
	 }
		    
	 public String getType() {
		    return type;
	 }

	
     @Override
     public String toString() {
    	 String finalOutput = owner_department + " " + this.type  + " ";
    	 if(this.external_link != null){
    		 finalOutput += this.external_link.url;
    	 }
    	 else{
    		 finalOutput += "URL Unavailable";
    	 }
		 return finalOutput;
     }
	    
	
}
