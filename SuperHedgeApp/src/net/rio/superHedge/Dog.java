/**
 * Dog.java
 */
package net.rio.superHedge;

import android.content.Context;

/**
 * Dog extends from Entity, it would move automatic 
 *  @author rio
 *  @see Entity
 */

class Dog extends Entity {
	
	private int cnt = data[0] == 0 ? 200 : 400;

	/**
	 * 
	 * @param con Context from MainActivity
	 * @param pos The start position of the Dog
	 * @param id The ID of Entity
	 * @param data Data from map file
	 * 
	 */
	public Dog(Context con, int[] pos, int id, int... data) {
		super(con, 3, pos, new int[]{100, 80}, id, data);
		if(data[0] == 0 || data[0] == 2) img = turnImg(oriImg, data[0]);
	}

	@Override
	void tick() {
		if(cnt > 300) advMove(2, 4);
		else if(cnt > 100 && cnt <= 200) advMove(0, 4);
		cnt--;
		if(cnt == 0) cnt = 400;
		
		advMove(3, Entity.GRAVITY);
	}

	@Override
	void touched(Entity ent) {
		if(ent.type == 0) Game.newGame(Game.HEG_DIED);
	}
	
	@Override
	boolean advMove(int dir, int speed) {
		
		if(data[0] != dir) {
			if(dir % 2 == 0) {
				data[0] = dir;
				img = turnImg(oriImg, data[0]);
			}
		}
			move(dir, speed);
			return GameRule.moveTo(id, dir);	//check if this entity is touched to others;
	}
}
