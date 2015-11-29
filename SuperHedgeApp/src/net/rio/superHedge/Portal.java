/*
 * Portal.java
 */
package net.rio.superHedge;

import android.content.Context;

class Portal extends Entity {

	/**
	 * @param con Context from MainActivity
	 * @param pos The start position of the Portal
	 * @param id The ID of Entity
	 * @param data Data from map file
	 */
	public Portal(Context con, int[] pos, int id, int... data) {
		super(con, 1, pos, new int[]{40, 100}, id, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	void touched(Entity ent) {
		if(ent.type == 0) rule.newGame();
	}

}
