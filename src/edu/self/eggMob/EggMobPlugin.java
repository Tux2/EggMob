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

import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EggMobPlugin extends JavaPlugin implements Listener {
        private Logger logger;
        private double threshold = 0.0d;
        private PotionEffectType effectType;
        private int minimumLevel = 0;
        private Random rand = new Random();
        
        public void onEnable() {
                logger = getServer().getLogger();
                if (!loadConfiguration()) {
                        return;
                }
                getServer().getPluginManager().registerEvents(this, this);
        }

        public void onDisable() {
                logger = null;
                effectType = null;
        }
        
        private boolean loadConfiguration() {
                getConfig().options().copyDefaults(true);
                // load potion config
                threshold = getConfig().getDouble("threshold");
                String effectName = getConfig().getString("potioneffect");
                effectType = PotionEffectType.getByName(effectName);
                if (effectType == null) {
                        logger.log(Level.SEVERE, "Unknown potion effect type: `" + effectName + "'; EggMob will not work!");
                        return false;
                }
                minimumLevel = getConfig().getInt("minimum_level", 0);
                // load mobtype config
                SpawnEggType.init();
                ConfigurationSection mobsSection = getConfig().getConfigurationSection("mobs");
        keyLoop:
                for (String key : mobsSection.getKeys(false)) {
                        boolean enable = mobsSection.getBoolean(key + ".enabled");
                        SpawnEggType spawnEggType = SpawnEggType.getByName(key);
                        if (spawnEggType == null) {
                                logger.log(Level.WARNING, "Unknown or unsupported entity type: `" + key + "'; ignoring!");
                                continue keyLoop;
                        }
                        spawnEggType.setEnabled(enable);
                        spawnEggType.setPercentCutoff(mobsSection.getInt(key + ".catchpercentageswitch"));
                        spawnEggType.setHighHealthSuccess(mobsSection.getInt(key + ".highhealthcatchpercentage"));
                        spawnEggType.setLowHealthSuccess(mobsSection.getInt(key + ".lowhealthcatchpercentage"));
                }
                saveConfig();
                return true;
        }

        @EventHandler
        public void onPotionSplash(PotionSplashEvent event) {
                boolean correctEffect = false;
        potionEffectLoop:
                for (PotionEffect effect : event.getPotion().getEffects()) {
                        PotionEffectType type = effect.getType();
                        if (type.equals(effectType) && effect.getAmplifier() >= minimumLevel) {
                                correctEffect = true;
                                break potionEffectLoop;
                        }
                }
                if (!correctEffect) return;

                Collection<LivingEntity> affect;
                affect = event.getAffectedEntities();
        affectedEntityLoop:
                for (LivingEntity entity : affect) {
                        // check if the threshold is met
                        double intensity = event.getIntensity(entity);
                        if (intensity < threshold) {
                                continue affectedEntityLoop;
                        }
                        // never turn tamed animals into eggs
                        if (entity instanceof Tameable) {
                                if (((Tameable)entity).isTamed()) {
                                        continue affectedEntityLoop;
                                }
                        }
                        // don't pack baby animals either
                        if (entity instanceof Animals) {
                                if (!((Animals)entity).isAdult()) {
                                        continue affectedEntityLoop;
                                }
                        }
                        // check if the entity is supported and enabled
                        SpawnEggType spawnEggType = SpawnEggType.getByEntityType(entity.getType());
                        if (spawnEggType == null || !spawnEggType.isEnabled()) {
                                continue affectedEntityLoop;
                        }
                        
                        //Let's see if we caught the mob!
                        double healthcutoff = ((double)entity.getMaxHealth())*(((double)spawnEggType.getPercentCutoff())/100d);
                        //System.out.println("Health cutoff for mob is: " + healthcutoff + ". Current health is: " + entity.getHealth());
                        if(entity.getHealth() < healthcutoff) {
                        	if(rand.nextInt(101) > spawnEggType.getLowHealthSuccess()) {
                        		//System.out.println("Didn't catch mob...");
                        		continue affectedEntityLoop;
                        	}
                        }else {
                        	if(rand.nextInt(101) > spawnEggType.getHighHealthSuccess()) {
                        		//System.out.println("Didn't catch mob...");
                        		continue affectedEntityLoop;
                        	}
                        }
                        // replace the Entity with an egg
                        entity.remove();
                        Location location = entity.getLocation();
                        World world = location.getWorld();
                        ItemStack item = new ItemStack(383, 1, (short)spawnEggType.getId());
                        world.dropItem(location, item);
                        // make a nice smoke effect
                        world.playEffect(location, Effect.SMOKE, 0); // data seems to be ignored
                }
        }
}
