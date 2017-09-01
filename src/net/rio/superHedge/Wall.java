/**
 * Wall.java
 */
package net.rio.superHedge;

import android.content.Context;
import net.rio.superHedge.Entity;

/**
 * the wall would stop ther entities go through
 * 
 * @author rio
 */
class Wall extends Entity {

	/**
	 * 
	 * @param con Context from MainActivity
	 * @param pos position of this wall
	 * @param id entity id of this wall
	 * @param data size of this wall
	 */
	public Wall(Context con, int[] pos, int id, int... data) {
		super(con, 4, pos, data, id, 0);
	}

	@Override
	void tick() {}

	@Override
	void touched(Entity ent) {}

}
