package org.The4Horsemen.Senior.BaitBite.Resources;



import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.The4Horsemen.Senior.BaitBite.Database.DataBase;
import org.The4Horsemen.Senior.BaitBite.model.Chef;
import org.The4Horsemen.Senior.BaitBite.model.Dish;

@Path("/Chefs")
public class APItest {

	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Chef createChef(Chef chef){
		DataBase.addChefs(chef);
		
		return chef;		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Chef> getChef(){
		//DataBase.addChefs();
		//System.out.println(DataBase.getChefs().get(1L)+"");
		
		ArrayList<Chef> c1 = new ArrayList<Chef>(DataBase.getChefs().values()); 
		return  c1;
		
	}
	
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createDish(Dish d){
		System.out.println(d.getName());
		
		
		return "Done";
		
	}
	

	
	
}
