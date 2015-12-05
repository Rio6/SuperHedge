/**
 * Hedgehog.java
 */
package net.rio.superHedge;

import android.content.Context;

/**
 * Hedgehog extends from Entity, it is controlled by user 
 * @author rio
 * @see Entity
 */
class Hedgehog extends Entity {
	
	private int cnt = GRAVITY / 2 * -1;
	private boolean tchGnd = true;
	
	/**
	 * @param con Context from MainActivity
	 * @param pos The start position of the Dog
	 * @param id The ID of Entity
	 * @param data Data from map file
	 */
	public Hedgehog(Context con, int[] pos, int id, int... data) {
		super(con, 0, pos, new int[]{100, 80}, id, data);
		img = turnImg(oriImg, data[0]);
		
	}

	@Override
	void tick() {
		tchGnd = move(3, GRAVITY / 2);
		jump();
	}

	@Override
	void touched(Entity ent) {
	}
	
	public void jump() {
		if(cnt > 0)
			super.move(1, cnt);
		else if(cnt > GRAVITY / 2 * -1)
			super.move(3, Math.abs(cnt));
		cnt--;
	}
	
	@Override
	boolean move(int dir, int speed) {
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
		
		if(dir == 1 && tchGnd) {
			cnt = GRAVITY;
			return false;
		} else {
			return move(dir, speed);
		}		
	}

}
