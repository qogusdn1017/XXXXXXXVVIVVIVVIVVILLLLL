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

import com.baehyeonwoo.xvl.plugin.XVLPluginMain
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.freezing
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.highestFreezingTickValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.highestFreezingTicks
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.manageFlags
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirstValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.warmflag
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.time.Duration

/***
 * @author BaeHyeonWoo
 */

class XVLGameTask : Runnable {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

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

    private val configFile = File(getInstance().dataFolder, "config.yml")

    private var configFileLastModified = configFile.lastModified()

    private val server = getInstance().server

    override fun run() {
        for (onlinePlayers in server.onlinePlayers) {

            val biome = onlinePlayers.location.block.biome
            val location = onlinePlayers.location

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Live Config Reloading

            if (configFileLastModified != configFile.lastModified()) {
                getInstance().logger.info("Config Reloaded.")
                getInstance().reloadConfig()
                getInstance().saveConfig()

                configFileLastModified = configFile.lastModified()
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // In-Gmae Managing Functions

            fun increaseFreezing(WinterRelated: Boolean) {
                if (WinterRelated) {
                    if (highestFreezingTicks[onlinePlayers.uniqueId] != 142) {
                        if (onlinePlayers.uniqueId.highestFreezingTickValue != 142) ++onlinePlayers.uniqueId.highestFreezingTickValue
                    }
                } else if (!WinterRelated) {
                    if (highestFreezingTicks[onlinePlayers.uniqueId] != 82) {
                        if (onlinePlayers.uniqueId.highestFreezingTickValue != 82) {
                            if (onlinePlayers.uniqueId.highestFreezingTickValue < 82) ++onlinePlayers.uniqueId.highestFreezingTickValue
                            else --onlinePlayers.uniqueId.highestFreezingTickValue
                        }
                    }
                }

                highestFreezingTicks[onlinePlayers.uniqueId] = onlinePlayers.uniqueId.highestFreezingTickValue
            }

            fun decreaseFreezing() {
                if (highestFreezingTicks[onlinePlayers.uniqueId] != 0) {
                    if (onlinePlayers.uniqueId.highestFreezingTickValue != 0) --onlinePlayers.uniqueId.highestFreezingTickValue
                }

                highestFreezingTicks[onlinePlayers.uniqueId] = onlinePlayers.uniqueId.highestFreezingTickValue
                if (onlinePlayers.freezeTicks != 0) onlinePlayers.freezeTicks = requireNotNull(highestFreezingTicks[onlinePlayers.uniqueId])
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Biome Check

            if (getInstance().config.getBoolean("debug")) {
                server.broadcast(text("BIOME - ${onlinePlayers.name}: $biome"))
            }

            when {
                coldBiome.contains(biome) -> {
                    if (getInstance().config.getBoolean("debug")) {
                        server.broadcast(text("COLD BIOME"))
                    }
                    if (warmflag[onlinePlayers.uniqueId] == false) {
                        if (biome.toString().lowercase().contains("frozen") || biome.toString().lowercase().contains("snowy") || biome == Biome.ICE_SPIKES) {
                            if (getInstance().config.getBoolean("debug")) {
                                server.broadcast(text("WINTER RELATED"))
                            }
                            increaseFreezing(WinterRelated = true)
                        } else {
                            if (getInstance().config.getBoolean("debug")) {
                                server.broadcast(text("WINTER NOT RELATED"))
                            }
                            increaseFreezing(WinterRelated = false)
                        }
                    }
                    manageFlags(FreezingFlag = true, ThirstyFlag = false, WarmBiomeFlag = false, NetherBiomeFlag = false)
                }
                warmBiome.contains(biome) -> {
                    if (getInstance().config.getBoolean("debug")) {
                        server.sendMessage(text("WARM BIOME"))
                    }
                    manageFlags(FreezingFlag = false, ThirstyFlag = true, WarmBiomeFlag = true, NetherBiomeFlag = false)
                }
                netherBiome.contains(biome) -> {
                    if (getInstance().config.getBoolean("debug")) {
                        server.sendMessage(text("NETHER BIOME"))
                    }
                    manageFlags(FreezingFlag = false, ThirstyFlag = true, WarmBiomeFlag = false, NetherBiomeFlag = true)
                }
                else -> {
                    if (getInstance().config.getBoolean("debug")) {
                        server.sendMessage(text("OTHER BIOME"))
                    }
                    manageFlags(FreezingFlag = false, ThirstyFlag = false, WarmBiomeFlag = false, NetherBiomeFlag = false)
                }
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Freezing

            if (freezing[onlinePlayers.uniqueId] == true) {
                for (x in location.blockX - 2..location.blockX + 2) {
                    for (z in location.blockZ - 2..location.blockZ + 2) {
                        val loc1 = location.subtract(0.0, 0.01, 0.0)
                        val loc2 = loc1.clone().subtract(0.0, 0.1, 0.0)
                        val y = loc1.y.toInt().takeIf { !location.world.getBlockAt(x, loc1.y.toInt(), z).type.isAir } ?: loc2.y.toInt()
                        val boolArray = ArrayList<Boolean>()
                        val blockType = location.world.getBlockAt(x, y, z).type

                        boolArray.add(warmBlocks.contains(blockType))

                        server.showTitle(Title.title(text(" "), text("$boolArray"), Title.Times.of(Duration.ofSeconds(0L), Duration.ofSeconds(5L), Duration.ofSeconds(0L))))

                        if (boolArray.toString().contains("true")) {
                            warmflag[onlinePlayers.uniqueId] = true
                            decreaseFreezing()
                            onlinePlayers.removePotionEffect(PotionEffectType.SLOW)
                            boolArray.clear()
                        }
                        else {
                            warmflag[onlinePlayers.uniqueId] = false
                            if (onlinePlayers.freezeTicks != highestFreezingTicks[onlinePlayers.uniqueId]) onlinePlayers.freezeTicks = requireNotNull(highestFreezingTicks[onlinePlayers.uniqueId])
                            onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 4, true, false))
                            boolArray.clear()
                        }
                    }
                }

                if (getInstance().config.getBoolean("debug")) {
                    server.broadcast(text("HIGHEST FREEZING TICKS - ${onlinePlayers.name}: ${highestFreezingTicks[onlinePlayers.uniqueId]}"))
                    server.broadcast(text("FREEZING TICKS - ${onlinePlayers.name}: ${onlinePlayers.freezeTicks}"))
                }
            }
            else decreaseFreezing()

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Actionbar Stat

            onlinePlayers.sendActionBar(
                text("플레이어: ${onlinePlayers.name} | ${ChatColor.RED}체력: ${onlinePlayers.health.toInt()}${ChatColor.RESET} | ${ChatColor.DARK_GRAY}허기: ${onlinePlayers.foodLevel}${ChatColor.RESET} | ${ChatColor.DARK_BLUE}추위: ${onlinePlayers.freezeTicks}${ChatColor.RESET} | ${ChatColor.AQUA}갈증 : ${onlinePlayers.thirstValue}")
                    .decorate(TextDecoration.BOLD)
            )

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
        }
    }
}
