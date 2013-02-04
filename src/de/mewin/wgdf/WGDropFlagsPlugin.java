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

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class WGDropFlagsPlugin extends JavaPlugin
{
    public static IntegerFlag DROP_DESPAWN_FLAG = new IntegerFlag("drop-despawn-time", RegionGroup.ALL);
    public static StateFlag ALLOW_PICKUP_FLAG = new StateFlag("allow-pickup", true, RegionGroup.NON_MEMBERS);
    public static IntegerFlag DEATH_DROP_DESPAWN_FLAG = new IntegerFlag("death-drop-despawn-time", RegionGroup.ALL);
    public static IntegerFlag DEATH_EXP_DESPAWN_FLAG = new IntegerFlag("death-exp-despawn-time", RegionGroup.ALL);
    public static IntegerFlag EXP_DESPAWN_FLAG = new IntegerFlag("exp-despawn-time", RegionGroup.ALL);

    private WorldGuardPlugin wgPlugin;
    private WGCustomFlagsPlugin custPlugin;
    private DropListener listener;
    
    @Override
    public void onEnable()
    {
        wgPlugin = getWorldGuard();
        custPlugin = getWGCustomFlags();
        listener = new DropListener(this, wgPlugin);
        
        if (wgPlugin == null) {
            getLogger().warning("This plugin requires WorldGuard, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        if (custPlugin == null) {
            getLogger().warning("This plugin requires WorldGuard Custom Flags, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        getServer().getPluginManager().registerEvents(listener, this);
        
        custPlugin.addCustomFlag(DROP_DESPAWN_FLAG);
        custPlugin.addCustomFlag(ALLOW_PICKUP_FLAG);
        custPlugin.addCustomFlag(DEATH_DROP_DESPAWN_FLAG);
        custPlugin.addCustomFlag(DEATH_EXP_DESPAWN_FLAG);
        custPlugin.addCustomFlag(EXP_DESPAWN_FLAG);
    }
    
    private WorldGuardPlugin getWorldGuard()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        
        return (WorldGuardPlugin) plugin;
    }
    
    private WGCustomFlagsPlugin getWGCustomFlags()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("WGCustomFlags");
        
        if (plugin == null || !(plugin instanceof WGCustomFlagsPlugin)) {
            return null;
        }
        
        return (WGCustomFlagsPlugin) plugin;
    }
}