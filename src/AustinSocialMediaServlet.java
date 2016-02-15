

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class AustinSocialMediaServlet extends HttpServlet {
	
	//URL and SESSION
	private static String baseURL;
	private String currentLink;
	private boolean sessionON;
	private String currentUser;
	private ArrayList visitedURLlist;
	//DATA LINK PARSING
	private static List<AustinEntity> entities;
	private static final long serialVersionUID = 1L;
	private static String socialMediaDataLink;
	private static String json;
	private static HttpServletResponse response;
    
	
    @Override
	public void init(ServletConfig config) {
        System.out.println("*** INIT ***");
        //TO PARSE URL DATA
    	socialMediaDataLink = "https://www.cs.utexas.edu/~devdatta/ej42-f7za.json";
    	try{
    		json = readUrl(socialMediaDataLink);
    	} catch (Exception e) {
    		System.out.println("Datalink unavailable");
			e.printStackTrace();
		} 
    	try{
    		entities = parseURLdata();
    	} catch (Exception e) {
    		System.out.println("Could not parse datalink.");
			e.printStackTrace();
    	}
    	//TO TRACK SESSIONS & URLs
    	baseURL = "http://localhost:8080/assignment1/austinsocialmedia?";
    	visitedURLlist = new ArrayList<String>();
        boolean sessionON = false;
    }
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException
    {
		System.out.println("** DO GET **");
		this.response = response;
		String queryString = request.getQueryString();
        currentLink = baseURL + queryString;
        boolean paramsPass = true;
      
        //GET PARAMS
		String username = request.getParameter("username");
        String session_param = request.getParameter("session");
		String account_name = request.getParameter("account_name");
        String type = request.getParameter("type");
        
        //CHECK TYPE PARAM
        if(type!=null){
        	Iterator it = entities.iterator();
        	boolean typeFound=false;
       	 	while(it.hasNext() && typeFound==false) {
       	 		AustinEntity curr = (AustinEntity)it.next();
       	 		if(type.equals(curr.getType())){
       	 			typeFound = true;
       	 		}
       	 	}
       	 	if(!typeFound){
				//throw new IOException("Disallowed value specified for parameter type");
       	 		response.getWriter().println("Disallowed value specified for parameter type");
       	 		paramsPass = false;
			}
        }
        //CHECK SESSION PARAM
        if(session_param != null){
       	    if(!session_param.equals("start") && !session_param.equals("end")){      	    	
       	    	response.getWriter().println("Disallowed value specified for parameter session");
       	    	paramsPass = false;
       	    }
        }
        
        if(paramsPass){
        //CHECK FOR SESSION
        Cookie cookies [] = request.getCookies();
        boolean sessionON = false;
        for(int i=0; cookies != null && i<cookies.length; i++) {
                Cookie ck = cookies[i];
                String cookieName = ck.getName();
                String cookieValue = ck.getValue();
                if ((cookieName != null && cookieName.equals(currentUser)) 
                                && cookieValue != null) {
                        sessionON = true;
                }
        }
        
        if(sessionON){
        	visitedURLlist.add(currentLink);
        	int numURLs = visitedURLlist.size();
        }
        
        //USERNAME IS PRESENT -- START OR END SESSION
        if(username != null && session_param != null){
            if(session_param.equals("start") || session_param.equals("end")){
	            if(!sessionON && session_param.equals("start")){
	            	sessionON = true;
	                Cookie cookie = new Cookie(username, "user-active");  //set cookie to start session
	                cookie.setDomain("localhost");
	                cookie.setPath("/assignment1" + request.getServletPath());
	                cookie.setMaxAge(1000);
	                response.addCookie(cookie);
	                currentUser = username;
	            }
	            else if(sessionON && username.equals(currentUser) && session_param.equals("end")){
	            	System.out.println("END SESSION");
	            	sessionON = false;
	            	currentUser = "";
	            	visitedURLlist.clear();
	            }
            }            
        }
        
        //PRINT VISITED URLS
        response.getWriter().println("Visited URLs");
        if(sessionON){ 
        	 Iterator it = visitedURLlist.iterator();
        	 while(it.hasNext()) {
        		 response.getWriter().println(it.next());
        	 }             
        }
        
        //PRINT URL DATA
        response.getWriter().println("URL Data");
        if(account_name != null){
        	getURLdata(account_name, type);
        }
        }//end paramsPass
		
    }

	private static List<AustinEntity> parseURLdata(){
		Gson gson = new Gson();
	    final Type AustinEntityListType = new TypeToken<List<AustinEntity>>(){}.getType();
	    final List<AustinEntity> entityListing = gson.fromJson(json, AustinEntityListType);
		return entityListing;
	}
	
	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(socialMediaDataLink);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 
	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	    
	}
	
	private static void getURLdata(String account_name, String type) throws IOException{
		//given account & type print list of account and type
		boolean typeFound = false;
		if(account_name != null){
			Iterator it = entities.iterator();
       	 	while(it.hasNext()) {
       	 		AustinEntity curr = (AustinEntity)it.next();
       	 		if(account_name.equals(curr.getAccount())){
       	 			if(type!= null && curr.getType().equals(type)){
       	 				response.getWriter().println(curr.toString());
       	 				typeFound = true;
       	 			}
       	 			else if(type == null){
       	 				response.getWriter().println(curr.toString());
       	 			}
       	 		} 
       	 	}
       	}
		
	}//end getURLdata

}
