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
	
	private int cnt = Entity.GRAVITY / 2 * -1;
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
		tchGnd = advMove(3, Entity.GRAVITY / 2);
		checkJump();
		
		if(pos[1] > 800)
			Game.newGame(Game.HEG_DIED);
	}

	@Override
	void touched(Entity ent) {
	}
	
	public void checkJump() {
		if(cnt > 0) {
			if(jump(cnt)) {
				cnt /= 2;
			}
		} else if(cnt > Entity.GRAVITY / 2 * -1) {
			advMove(3, Math.abs(cnt));
		}
		cnt--;
	}
	
	private boolean jump(int speed) {
		pos[1] -= speed;
		return GameRule.moveTo(id, 1);
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
			cnt = Entity.GRAVITY;
			return false;
		} else {
			move(dir, speed);
			return GameRule.moveTo(id, dir);	//check if this entity is touched to others;
		}		
	}

}
