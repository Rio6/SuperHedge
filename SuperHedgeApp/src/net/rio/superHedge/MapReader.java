/*
 * MapReader.java
 */
package net.rio.superHedge;

import java.util.Scanner;

import org.json.*;

import android.content.Context;

/**
 * Read JSON data from map file
 * @author rio
 *
 */
public class MapReader {
	
	JSONObject[] entDat;

	public MapReader(Context con, int level) {
		
		Scanner rdr = new Scanner(con.getResources().openRawResource(con.getResources().getIdentifier("map" + level, "raw", con.getPackageName())));
		
		String in = "";
		while(rdr.hasNextLine()) {
			in += rdr.nextLine();
		}
		rdr.close();
		try {
			JSONObject psr = new JSONObject(in);
			JSONArray datAry = psr.getJSONArray("entities");
			entDat = new JSONObject[datAry.length()];
			for(int i = 0; i < datAry.length(); i++) {
				entDat[i] = datAry.getJSONObject(i);				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * get entity data
	 * @return JSONObject with every entity data
	 */
	JSONObject[] getEnts() {
		return entDat;
	}

}
