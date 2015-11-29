/*
 * GameRule.java
 */
package net.rio.superHedge;

/**
 * this class controls the rule of the game
 * @author rio
 */
class GameRule {
	
	final static int ENTITY_EAT = 0;
	final static int ENTITY_DIE = 1;
	
	private static Entity[] ents;
	private static int apls = 0;
	
	private int id;
	
	GameRule(int id) {
		this.id = id;
	}

	/**
	 * set entities in the game
	 * @param ents the entities in this level
	 */
	void setEnts(Entity[] ents) {
		GameRule.ents = ents;
	}
	
	/**
	 * if the entity who calls this is touched by another entity, this method will move it back 
	 * @param dir moving direction
	 * @return true if this move hits another entity
	 */
	boolean moveTo(int dir) {
		boolean touched = false;
		
		for(int i = 0; i < ents.length; i++) {
			if( i == id || ents[i] == null)
				continue;
			if(entsTouched(ents[id], ents[i])) {
				
				if(ents[i].type == 5) {
					switch(dir) {
					case 0:
						ents[id].move(2, ents[i].pos[0] + ents[i].size[0] - ents[id].pos[0]);
						break;
					case 1:
						ents[id].move(3, ents[i].pos[1] + ents[i].size[1] - ents[id].pos[1]);					
						break;
					case 2:
						ents[id].move(0, ents[id].pos[0] + ents[id].size[0] - ents[i].pos[0]);
						break;
					case 3:
						ents[id].move(1, ents[id].pos[1] + ents[id].size[1] - ents[i].pos[1]);
						break;
					}
				} else {
					switch(dir) {
					case 0:
						ents[i].move(dir, ents[i].pos[0] + ents[i].size[0] - ents[id].pos[0]);
						break;
					case 1:
						ents[id].move(3, ents[i].pos[1] + ents[i].size[1] - ents[id].pos[1]);					
						break;
					case 2:
						ents[i].move(dir, ents[id].pos[0] + ents[id].size[0] - ents[i].pos[0]);
						break;
					case 3:
						ents[id].move(1, ents[id].pos[1] + ents[id].size[1] - ents[i].pos[1]);
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
	private boolean entsTouched(Entity ent1, Entity ent2) {
		return (ent1.pos[0] < ent2.pos[0] ? 
				ent2.pos[0] - ent1.pos[0] < ent1.size[0]: 
				ent1.pos[0] - ent2.pos[0] < ent2.size[0]) &&
				(ent1.pos[1] < ent2.pos[1] ? 
				ent2.pos[1] - ent1.pos[1] < ent1.size[1]: 
				ent1.pos[1] - ent2.pos[1] < ent2.size[1]);
	}
	
	void removeEnt(int stat, int id) {
		if(stat == ENTITY_EAT) {
			apls++;
		}
		ents[id] = null;
	}
	
	int getApls() {
		return apls;
	}
	
	void newGame() {
		Game.newGame(Game.HEG_FINISHED);
	}
	
}
