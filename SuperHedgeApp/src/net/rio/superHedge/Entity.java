/**
 * Entity.java
 */
package net.rio.superHedge;

import android.content.Context;
import android.graphics.*;

/**
 * The entity, every thing in SuperHedge should be an entity
 * @author rio
 */
abstract class Entity {
	
	static final int GRAVITY = 13;
	
	protected int type, id;
	protected int[] pos, size, data;
	protected boolean touchable = true;
	
	protected Bitmap oriImg;
	protected Bitmap img = null;

	/**
	 * 
	 * @param con Context from MainActivity
	 * @param type Entity type
	 * @param pos The start position
	 * @param size Entity size
	 * @param dmg The default health
	 * @param id Entity ID
	 * @param data data Data from map file
	 */
	Entity(Context con, int type, int[] pos, int[] size, int id, int... data) {
		
		this.type = type;
		this.pos = pos;
		this.size = size;
		this.id = id;
		this.data = data;
		
		oriImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(con.getResources(),
				con.getResources().getIdentifier(getTypeName(type), "drawable", con.getPackageName())),
				(int) (size[0] * (Main.scrW / 750f)), (int) (size[1] * (Main.scrH / 500f)), false);
	}

	/**
	 * call this every tick
	 */
	abstract void tick();
	
	/**
	 * this would be called if this entity is touched by another entity 
	 */
	abstract void touched(Entity ent);
	
	/**
	 * Move this entity
	 * @param dir Direction to move, 0 => left, 1 => up, 2 => right, 3 => down
	 * @param speed Move speed
	 */
	void move(int dir, int speed) {
		switch(dir) {
		case 0:
			pos[0] -= speed;
			break;
		case 1:
			pos[1] -= speed;
			break;
		case 2:
			pos[0] += speed;
			break;
		case 3:
			pos[1] += speed;
			break;
		}
	}
	
	/**
	 * advance move for entity, can override this for turning or other stuff
	 * @param dir
	 * @param speed
	 * @return true if this move hits another entity
	 */
	boolean advMove(int dir, int speed) {
		move(dir, speed);
		return GameRule.moveTo(id, dir);
	}
	
	/**
	 * teleport this entity to x and y
	 * @param x
	 * @param y
	 */
	void teleport(int x, int y) {
		pos[0] = x;
		pos[1] = y;
	}
	
	/**
	 * get the image of this entity
	 * @return The image of this entity
	 */
	Bitmap getImg() {
		return img != null ? img : oriImg;
	}
	
	/**
	 * turning the direction of image
	 * @param img Image to turn
	 * @param dir Direction to turn
	 * @return An image after turning to specific direction
	 */
	protected static Bitmap turnImg(Bitmap img, int dir) {
		Matrix mat = new Matrix();
		if(dir % 2 == 1) {
			mat.postRotate(dir * 90f);
		} else {
			if(dir == 2)
				mat.setScale(-1f, 1f);
			mat.postTranslate(img.getWidth(), 0);
		}
		return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), mat, true);
	}
	
	/**
	 * Get the type name from type
	 * @param type the type ID
	 * @return the name of the type ID
	 */
	private String getTypeName(int type) {
		switch(type) {
		case 0:
			return "hedgehog";
		case 1:
			return "portal";
		case 2:
			return "apple";
		case 3:
			return "dog";
		case 4:
			return "wall";
		}
		return null;
	}
	
}
