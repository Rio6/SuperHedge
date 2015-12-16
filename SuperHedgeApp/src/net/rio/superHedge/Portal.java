/**
 * Portal.java
 */
package net.rio.superHedge;

import android.content.Context;

/**
 * if the Hedgehog touch this entity, then the level is finished
 * @author rio
 *
 */
class Portal extends Entity {

	/**
	 * @param con Context from MainActivity
	 * @param pos The start position of the Portal
	 * @param id The ID of Entity
	 * @param data Data from map file
	 */
	public Portal(Context con, int[] pos, int id, int... data) {
		super(con, 1, pos, new int[]{40, 84}, id, data);
		touchable = false;
	}

	@Override
	void tick() {}

	@Override
	void touched(Entity ent) {
		if(ent.type == 0){
			Main.playSnd(3);
			Game.newGame(Game.HEG_NEXT_LEVEL);
		}
	}

}
