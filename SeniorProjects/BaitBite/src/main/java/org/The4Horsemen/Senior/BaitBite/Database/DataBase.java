package org.The4Horsemen.Senior.BaitBite.Database;

import java.util.HashMap;
import java.util.Map;

import org.The4Horsemen.Senior.BaitBite.model.Chef;

public class DataBase {

	
	
	private static Map<Long, Chef> Chefs = new HashMap<>();
	
	
	
	
	public static Map<Long,Chef> getChefs(){
		
	return Chefs;
	}
	public static void addChefs(Chef x){
		
		Chefs.put((long) (Chefs.size()+1),x);
		//Chefs.put(2L,new Chef("justin","approve",10));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
