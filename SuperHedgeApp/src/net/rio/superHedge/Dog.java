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
	
	private int cnt = 400;

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
	}

	@Override
	void tick() {
		if(cnt >= 300) advMove(2, 4);
		else if(cnt >= 100 && cnt < 200) advMove(0, 4);
		cnt--;
		if(cnt == 0) cnt = 400;
		
		advMove(3, GRAVITY / 2);
	}

	@Override
	void touched(Entity ent) {}

	
	@Override
	protected boolean move(int dir, int speed) {
		return super.move(dir, speed);
	}
	
	@Override
	boolean advMove(int dir, int speed) {
		
		if(data[0] != dir) {
			if(dir % 2 == 0) {
				data[0] = dir;
				img = turnImg(oriImg, data[0]);
			}
		}
		
		return move(dir, speed);
	}
}
