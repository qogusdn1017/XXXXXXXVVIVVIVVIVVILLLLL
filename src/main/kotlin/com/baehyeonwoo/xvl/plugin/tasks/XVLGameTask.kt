/*
 * Copyright (c) 2021 BaeHyeonWoo
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baehyeonwoo.xvl.plugin.tasks

import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.blockArray
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.freezing
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.highestFreezingTickValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.highestFreezingTicks
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.manageFlags
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.plugin
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.server
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirstValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.warmflag
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/***
 * @author BaeHyeonWoo
 */

class XVLGameTask : Runnable {
    private val coldBiome = arrayListOf(
        Biome.SNOWY_PLAINS,
        Biome.ICE_SPIKES,
        Biome.SNOWY_TAIGA,
        Biome.SNOWY_BEACH,
        Biome.GROVE,
        Biome.SNOWY_SLOPES,
        Biome.JAGGED_PEAKS,
        Biome.FROZEN_PEAKS,
        Biome.FROZEN_RIVER,
        Biome.FROZEN_OCEAN,
        Biome.DEEP_OCEAN,
        Biome.DEEP_FROZEN_OCEAN,
        Biome.COLD_OCEAN,
        Biome.DEEP_COLD_OCEAN,
        Biome.DRIPSTONE_CAVES,
        Biome.SOUL_SAND_VALLEY,
        Biome.WARPED_FOREST
    )

    private val warmBiome = arrayListOf(
        Biome.DESERT,
        Biome.SAVANNA,
        Biome.SAVANNA_PLATEAU,
        Biome.WINDSWEPT_SAVANNA,
        Biome.BADLANDS,
        Biome.WOODED_BADLANDS,
        Biome.ERODED_BADLANDS,
        Biome.WARM_OCEAN,
        Biome.LUKEWARM_OCEAN,
        Biome.DEEP_LUKEWARM_OCEAN
    )

    private val netherBiome = arrayListOf(
        Biome.NETHER_WASTES,
        Biome.BASALT_DELTAS,
        Biome.CRIMSON_FOREST,
    )

    private val warmBlocks = arrayListOf(
        Material.FIRE,
        Material.SOUL_FIRE,
        Material.CAMPFIRE,
        Material.SOUL_CAMPFIRE,
        Material.LAVA,
        Material.LAVA_CAULDRON,
        Material.MAGMA_BLOCK,
        Material.TORCH,
        Material.SOUL_TORCH,
        Material.WALL_TORCH,
        Material.SOUL_WALL_TORCH
    )

    override fun run() {
        server.onlinePlayers.forEach {
            val biome = it.location.block.biome
            val inventory = it.inventory

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // In-Gmae Managing Functions

            fun increaseFreezing(WinterRelated: Boolean) {
                if (WinterRelated) {
                    if (highestFreezingTicks[it.uniqueId] != 142) {
                        if (it.uniqueId.highestFreezingTickValue != 142) ++it.uniqueId.highestFreezingTickValue
                    }
                }
                else {
                    if (highestFreezingTicks[it.uniqueId] != 82) {
                        if (it.uniqueId.highestFreezingTickValue != 82) {
                            if (it.uniqueId.highestFreezingTickValue < 82) ++it.uniqueId.highestFreezingTickValue
                            else --it.uniqueId.highestFreezingTickValue
                        }
                    }
                }

                highestFreezingTicks[it.uniqueId] = it.uniqueId.highestFreezingTickValue
            }

            fun decreaseFreezing() {
                if (highestFreezingTicks[it.uniqueId] != 0) {
                    if (it.uniqueId.highestFreezingTickValue != 0) --it.uniqueId.highestFreezingTickValue
                }

                highestFreezingTicks[it.uniqueId] = it.uniqueId.highestFreezingTickValue
                if (it.freezeTicks != 0) it.freezeTicks = requireNotNull(highestFreezingTicks[it.uniqueId])
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Biome Check

            if (plugin.config.getBoolean("debug")) {
                server.broadcast(text("BIOME - ${it.name}: $biome"))
            }

            when {
                coldBiome.contains(biome) -> {
                    if (plugin.config.getBoolean("debug")) {
                        server.broadcast(text("COLD BIOME"))
                    }
                    if (warmflag[it.uniqueId] == false) {
                        if (biome.toString().lowercase().contains("frozen") || biome.toString().lowercase().contains("snowy") || biome == Biome.ICE_SPIKES || biome == Biome.GROVE) {
                            if (plugin.config.getBoolean("debug")) {
                                server.broadcast(text("WINTER RELATED"))
                            }
                            increaseFreezing(WinterRelated = true)
                        } else {
                            if (plugin.config.getBoolean("debug")) {
                                server.broadcast(text("WINTER NOT RELATED"))
                            }
                            increaseFreezing(WinterRelated = false)
                        }
                    }
                    manageFlags(FreezingFlag = true, ThirstyFlag = false, WarmBiomeFlag = false, NetherBiomeFlag = false)
                }
                warmBiome.contains(biome) -> {
                    if (plugin.config.getBoolean("debug")) {
                        server.sendMessage(text("WARM BIOME"))
                    }
                    manageFlags(FreezingFlag = false, ThirstyFlag = true, WarmBiomeFlag = true, NetherBiomeFlag = false)
                }
                netherBiome.contains(biome) -> {
                    if (plugin.config.getBoolean("debug")) {
                        server.sendMessage(text("NETHER BIOME"))
                    }
                    manageFlags(FreezingFlag = false, ThirstyFlag = true, WarmBiomeFlag = false, NetherBiomeFlag = true)
                }
                else -> {
                    if (plugin.config.getBoolean("debug")) {
                        server.sendMessage(text("OTHER BIOME"))
                    }
                    manageFlags(FreezingFlag = false, ThirstyFlag = false, WarmBiomeFlag = false, NetherBiomeFlag = false)
                }
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Freezing

            if (freezing[it.uniqueId] == true) {
                blockArray[it.uniqueId] = ArrayList()
                for (x in -3..3) {
                    for (y in -3..3) {
                        for (z in -3..3) {
                            blockArray[it.uniqueId]?.add(it.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block.type)
                        }
                    }
                }
                if ((blockArray[it.uniqueId]?.any { block -> warmBlocks.contains(block) } == true) || (inventory.helmet?.type == Material.LEATHER_HELMET && inventory.chestplate?.type == Material.LEATHER_CHESTPLATE && inventory.leggings?.type == Material.LEATHER_LEGGINGS && inventory.boots?.type == Material.LEATHER_BOOTS)) {
                    warmflag[it.uniqueId] = true
                    decreaseFreezing()
                    it.removePotionEffect(PotionEffectType.SLOW)
                } else {
                    warmflag[it.uniqueId] = false
                    if (it.freezeTicks != highestFreezingTicks[it.uniqueId]) it.freezeTicks = requireNotNull(highestFreezingTicks[it.uniqueId])
                    it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 4, true, false))
                }
            }
            else decreaseFreezing()

            if (plugin.config.getBoolean("debug")) {
                server.broadcast(text("HIGHEST FREEZING TICKS - ${it.name}: ${highestFreezingTicks[it.uniqueId]}"))
                server.broadcast(text("FREEZING TICKS - ${it.name}: ${it.freezeTicks}"))
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Actionbar Stat

            it.sendActionBar(
                text("플레이어: ${it.name} | ${ChatColor.RED}체력: ${it.health.toInt()}${ChatColor.RESET} | ${ChatColor.GOLD}허기: ${it.foodLevel}${ChatColor.RESET} | ${ChatColor.DARK_BLUE}추위: ${it.freezeTicks}${ChatColor.RESET} | ${ChatColor.AQUA}갈증 : ${it.thirstValue}")
                    .decorate(TextDecoration.BOLD)
            )

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
        }
    }
}
