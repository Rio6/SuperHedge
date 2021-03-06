/**
 * MapReader.java
 */
package net.rio.superHedge;

import java.io.IOException;
import java.util.Scanner;

import org.json.*;

import android.content.Context;

/**
 * Read JSON data from map file
 * @author rio
 *
 */
class MapReader {

    JSONObject[] entDat;

    public MapReader(Context con, int level) {

        Scanner rdr = null;
        try {
            rdr = new Scanner(con.getAssets().open("levels/level" + level + ".json"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

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
