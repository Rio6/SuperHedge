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
	
	private int jmpFrc;			//i use jmpFrc to calculate the force of jumping and let the jumping more naturely
	private boolean tchGnd;
	private boolean jmpFirTick;
	
	/**
	 * @param con Context from MainActivity
	 * @param pos The start position of the Dog
	 * @param id The ID of Entity
	 * @param data Data from map file
	 */
	public Hedgehog(Context con, int[] pos, int id, int... data) {
		super(con, 0, pos, new int[]{100, 80}, id, data);
		if(data[0] == 0 || data[0] == 2) img = turnImg(oriImg, data[0]);
		
	}

	@Override
	void tick() {
		tchGnd = advMove(3, Entity.GRAVITY);
		checkJump();
		
		if(pos[1] > 800)
			Game.newGame(Game.HEG_DIED);
	}

	@Override
	void touched(Entity ent) {
	}
	
	/**
	 * if hedgehog is jumping then jump
	 */
	public void checkJump() {
		
		if(!jmpFirTick && tchGnd) {
			jmpFrc /= 2;	//use "/= 2" instead of = 0 to let the jumping more naturely
		}
		if(jmpFrc > 0) {
			if(super.advMove(1, jmpFrc))
				jmpFrc--;	//use "--" instead of = 0 to let the jumping more naturely
			jmpFrc--;
		}
		jmpFirTick = false;
		
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
			jmpFrc = (int) (Entity.GRAVITY * 1.99);
			jmpFirTick = true;
			return false;
		} else {
			move(dir, speed);
			return GameRule.moveTo(id, dir);	//check if this entity is touched to others;
		}		
	}

}
