/*
 * Copyright 2012 Thomas Loy.
 *
 * This file is part of EggMob.
 *
 * EggMob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EggMob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EggMob.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.self.eggMob;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum SpawnEggType {
        CREEPER(50, EntityType.CREEPER),
        SKELETON(51, EntityType.SKELETON),
        SPIDER(52, EntityType.SPIDER),
        GIANT(53, EntityType.GIANT),
        ZOMBIE(54, EntityType.ZOMBIE),
        SLIME(55, EntityType.SLIME),
        GHAST(56, EntityType.GHAST),
        PIG_ZOMBIE(57, EntityType.PIG_ZOMBIE),
        ENDERMAN(58, EntityType.ENDERMAN),
        CAVE_SPIDER(59, EntityType.CAVE_SPIDER),
        SILVERFISH(60, EntityType.SILVERFISH),
        BLAZE(61, EntityType.BLAZE),
        MAGMA_CUBE(62, EntityType.MAGMA_CUBE),
        ENDER_DRAGON(63, EntityType.ENDER_DRAGON),
        WITHER(64, EntityType.WITHER),
		BAT(65, EntityType.BAT),
        WITCH(66, EntityType.WITCH),
        PIG(90, EntityType.PIG),
        SHEEP(91, EntityType.SHEEP),
        COW(92, EntityType.COW),
        CHICKEN(93, EntityType.CHICKEN),
        SQUID(94, EntityType.SQUID),
        WOLF(95, EntityType.WOLF),
        MUSHROOM_COW(96, EntityType.MUSHROOM_COW),
        SNOWMAN(97, EntityType.SNOWMAN),
        OCELOT(98, EntityType.OCELOT),
        VILLAGER(120, EntityType.VILLAGER);

        private int id; // the spawnegg id
        private EntityType entityType;
        private boolean enabled = false;
        private int percentcutoff = 80;
        private int highhealthcatch = 20;
        private int lowhealthcatch = 80;

        private SpawnEggType(int id, EntityType entityType) {
                this.id = id;
                this.entityType = entityType;
        }

        // initialize dynamic settings
        public static void init() {
                for (SpawnEggType spawnEggType : values()) {
                        spawnEggType.enabled = false;
                }
        }

        public static SpawnEggType getByName(String name) {
                if (name == null) return null;
                for (SpawnEggType spawnEggType : values()) {
                        if (spawnEggType.getName().equalsIgnoreCase(name)) {
                                return spawnEggType;
                        }
                }
                return null;
        }

        public static SpawnEggType getByEntityType(EntityType entityType) {
                if (entityType == null) return null;
                for (SpawnEggType spawnEggType : values()) {
                        if (spawnEggType.getEntityType().equals(entityType)) {
                                return spawnEggType;
                        }
                }
                return null;
        }

        public int getId() {
                return id;
        }

        public boolean isInstance(Entity e) {
                return e.getType().equals(getEntityType());
        }

        public String getName() {
                return entityType.getName();
        }

        public EntityType getEntityType() {
                return entityType;
        }

        void setEnabled(boolean v) {
                enabled = v;
        }

        boolean isEnabled() {
                return enabled;
        }
        
        void setPercentCutoff(int v) {
        	percentcutoff = v;
        }
        
        int getPercentCutoff() {
        	return percentcutoff;
        }
        
        void setHighHealthSuccess(int v) {
        	highhealthcatch = v;
        }
        
        int getHighHealthSuccess() {
        	return highhealthcatch;
        }
        
        void setLowHealthSuccess(int v) {
        	lowhealthcatch = v;
        }
        
        int getLowHealthSuccess() {
        	return lowhealthcatch;
        }
}
