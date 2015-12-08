/**
 * GameRule.java
 */
package net.rio.superHedge;

/**
 * this class controls the rule of the game
 * @author rio
 */
class GameRule {
	
	private static Entity[] ents;
	private static int apls = 0;
	private static int svdApl;

	/**
	 * set entities in the game
	 * @param ents the entities in this level
	 */
	static void setEnts(Entity[] ents) {
		GameRule.ents = ents;
	}
	
	/**
	 * reset apple count to 0
	 */
	static void resetApl() {
		apls = 0;
	}
	
	/**
	 * set apple count to the count when start level
	 */
	static void loseApl() {
		apls = svdApl;
	}
	
	/**
	 * finished a level, save apple count
	 */
	static void saveApl() {
		svdApl = apls;
	}
	
	/**
	 * if the entity who calls this is touched by another entity, this method will stop the entity
	 * @param id entity id to check
	 * @param dir moving direction
	 * @return true if this move hits another entity
	 */
	static boolean moveTo(int id, int dir) {
		boolean touched = false;
		
		for(int i = ents.length - 1; i >= 0; i--) {	//reverse the order because i need to check the walls first
			if( i == id || ents[i] == null)
				continue;
			if(entsTouched(ents[id], ents[i])) {
				
				if(ents[i].touchable) {
					switch(dir) {
					case 0:
						ents[id].teleport(ents[i].pos[0] + ents[i].size[0], ents[id].pos[1]);
						break;
					case 1:
						ents[id].teleport(ents[id].pos[0], ents[i].pos[1] + ents[i].size[1]);
						break;
					case 2:
						ents[id].teleport(ents[i].pos[0] - ents[id].size[0], ents[id].pos[1]);
						break;
					case 3:
						ents[id].teleport(ents[id].pos[0], ents[i].pos[1] - ents[id].size[1]);
						break;
					}
				}
				
				//call touched event to both entities
				ents[id].touched(ents[i]);
				ents[i].touched(ents[id]);
					
				touched = true;
			}
		}
		return touched;
	}

	/**
	 * check if two entities are touched
	 * @param ent1
	 * @param ent2
	 * @return true if two given entities are touched
	 */
	private static boolean entsTouched(Entity ent1, Entity ent2) {
		return (ent1.pos[0] < ent2.pos[0] ? 
				ent2.pos[0] - ent1.pos[0] < ent1.size[0]: 
				ent1.pos[0] - ent2.pos[0] < ent2.size[0]) &&
				(ent1.pos[1] < ent2.pos[1] ? 
				ent2.pos[1] - ent1.pos[1] < ent1.size[1]: 
				ent1.pos[1] - ent2.pos[1] < ent2.size[1]);
	}
	
	static void eatApl(int id) {
		apls++;
		ents[id] = null;
	}
	
	static int getApls() {
		return apls;
	}
	
}
