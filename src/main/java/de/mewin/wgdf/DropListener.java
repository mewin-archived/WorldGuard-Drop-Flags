/*
 * Copyright (C) 2013 mewin<mewin001@hotmail.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.mewin.wgdf;

import com.mewin.util.Util;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class DropListener implements Listener
{
    private WGDropFlagsPlugin plugin;
    private WorldGuardPlugin wgPlugin;
    
    public DropListener(WGDropFlagsPlugin plugin, WorldGuardPlugin wgPlugin)
    {
        this.plugin = plugin;
        this.wgPlugin = wgPlugin;
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e)
    {
        RegionManager rm = wgPlugin.getRegionManager(e.getPlayer().getWorld());

        if (rm != null)
        {
            Integer time = Util.getFlagValue(wgPlugin, e.getPlayer().getLocation(), WGDropFlagsPlugin.DROP_DESPAWN_FLAG);
            if (time != null)
            {
                final Item itm = e.getItemDrop();
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {                            
                        if (itm != null && !itm.isDead())
                        {
                            itm.remove();
                        }
                    }
                }, time / 40);
            }
        }
    }
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e)
    {
        if (!wgPlugin.getGlobalRegionManager().allows(WGDropFlagsPlugin.ALLOW_PICKUP_FLAG, e.getItem().getLocation(), wgPlugin.wrapPlayer(e.getPlayer())))
        {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {
        Integer time = Util.getFlagValue(wgPlugin, e.getEntity().getLocation(), WGDropFlagsPlugin.DEATH_DROP_DESPAWN_FLAG);
        
        if (time != null)
        {
            World w = e.getEntity().getWorld();
            List<ItemStack> iss = new ArrayList<ItemStack>(e.getDrops());
            
            e.getDrops().clear();
            final Item[] items = new Item[iss.size()];
            
            for (int i = 0; i < iss.size(); i++)
            {
                items[i] = w.dropItemNaturally(e.getEntity().getLocation(), iss.get(i));
            }
            
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    for (Item itm : items)
                    {
                        if (itm != null && !itm.isDead())
                        {
                            itm.remove();
                        }
                    }
                }
            }, time / 40);
        }
        
        time = Util.getFlagValue(wgPlugin, e.getEntity().getLocation(), WGDropFlagsPlugin.DEATH_EXP_DESPAWN_FLAG);
        
        if (time != null)
        {
            World w = e.getEntity().getWorld();
            
            int exp = e.getDroppedExp();
            e.setDroppedExp(0);
            
            final List<ExperienceOrb> orbs = dropExp(e.getEntity().getLocation(), exp);
            
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    for (ExperienceOrb orb : orbs)
                    {
                        if (orb != null && !orb.isDead())
                        {
                            orb.remove();
                        }
                    }
                }
            }, time / 40);
        }
    }
    
    @EventHandler
    public void onExpBottle(ExpBottleEvent e)
    {
        Integer time = Util.getFlagValue(wgPlugin, e.getEntity().getLocation(), WGDropFlagsPlugin.EXP_DESPAWN_FLAG);
        
        if (time != null)
        {
            int exp = e.getExperience();
            e.setExperience(0);
            
            final List<ExperienceOrb> orbs = dropExp(e.getEntity().getLocation(), exp);
            
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    for (ExperienceOrb orb : orbs)
                    {
                        if (orb != null && !orb.isDead())
                        {
                            orb.remove();
                        }
                    }
                }
            }, time / 40);
        }
    }
    
    private List<ExperienceOrb> dropExp(Location loc, int exp)
    {
        World w = loc.getWorld();
        ArrayList<ExperienceOrb> orbs = new ArrayList<ExperienceOrb>();
        
        int orbNum = (int) Math.floor(exp / 3.);
        int restOrb = exp - orbNum * 3;
        
        for (int i = 0; i < orbNum; i++)
        {
            ExperienceOrb orb = w.spawn(loc, ExperienceOrb.class);
            orb.setExperience(3);
            orbs.add(orb);
        }
        
        ExperienceOrb rest = w.spawn(loc, ExperienceOrb.class);
        rest.setExperience(restOrb);
        orbs.add(rest);
        
        return orbs;
    }
}