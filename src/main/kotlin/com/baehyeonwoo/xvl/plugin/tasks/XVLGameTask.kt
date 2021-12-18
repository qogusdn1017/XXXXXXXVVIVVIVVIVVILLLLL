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
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.freezing
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.highestFreezingTickValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.highestFreezingTicks
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.isNetherBiome
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.isWarmBiome
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.thirstValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.warmflag
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.warming
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.block.Biome
import org.bukkit.plugin.Plugin

/***
 * @author BaeHyeonWoo
 */

class XVLGameTask : Runnable {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val server = getInstance().server

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

        // 영혼 모래 계곡과 뒤틀린 숲은 분위기상 춥다고 가정하여 코드를 작성하였습니다. 착오가 없으시길 바랍니다.

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

    override fun run() {
        for (onlinePlayers in server.onlinePlayers) {
            val biome = onlinePlayers.location.block.biome

            when {
                coldBiome.contains(biome) -> {
                    if (biome.toString().lowercase().contains("frozen") || biome.toString().lowercase().contains("snowy") || biome == Biome.ICE_SPIKES) {
                        if (warmflag[onlinePlayers.uniqueId] == false) {
                            if (highestFreezingTicks[onlinePlayers.uniqueId] != 142) {
                                if (onlinePlayers.uniqueId.highestFreezingTickValue != 142) ++onlinePlayers.uniqueId.highestFreezingTickValue
                                highestFreezingTicks[onlinePlayers.uniqueId] = onlinePlayers.uniqueId.highestFreezingTickValue
                                freezing[onlinePlayers.uniqueId] = true
                            }
                            else if (onlinePlayers.uniqueId.highestFreezingTickValue != 82) {
                                if (onlinePlayers.uniqueId.highestFreezingTickValue < 82) ++onlinePlayers.uniqueId.highestFreezingTickValue
                                else if (onlinePlayers.uniqueId.highestFreezingTickValue > 82) --onlinePlayers.uniqueId.highestFreezingTickValue

                                highestFreezingTicks[onlinePlayers.uniqueId] = onlinePlayers.uniqueId.highestFreezingTickValue
                                freezing[onlinePlayers.uniqueId] = true
                            }
                        }
                    }
                }
                warmBiome.contains(biome) -> {
                    freezing[onlinePlayers.uniqueId] = false
                    warming[onlinePlayers.uniqueId] = true
                    isWarmBiome[onlinePlayers.uniqueId] = true
                }
                netherBiome.contains(biome) -> {
                    freezing[onlinePlayers.uniqueId] = false
                    warming[onlinePlayers.uniqueId] = true
                    isNetherBiome[onlinePlayers.uniqueId] = true
                }
                else -> {
                    freezing[onlinePlayers.uniqueId] = false
                    warming[onlinePlayers.uniqueId] = false
                    isWarmBiome[onlinePlayers.uniqueId] = false
                    isNetherBiome[onlinePlayers.uniqueId] = false
                }
            }

            onlinePlayers.sendActionBar(
                text("플레이어: ${onlinePlayers.name} | ${ChatColor.RED}체력: ${onlinePlayers.health.toInt()}${ChatColor.RESET} | ${ChatColor.DARK_GRAY}허기: ${onlinePlayers.foodLevel}${ChatColor.RESET} | ${ChatColor.DARK_BLUE}추위: ${onlinePlayers.freezeTicks}${ChatColor.RESET} | ${ChatColor.AQUA}갈증 : ${onlinePlayers.thirstValue}")
                    .decorate(TextDecoration.BOLD)
            )
        }
    }
}