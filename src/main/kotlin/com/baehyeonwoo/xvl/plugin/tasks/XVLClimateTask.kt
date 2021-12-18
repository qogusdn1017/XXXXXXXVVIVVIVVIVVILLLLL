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
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.thirstValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.warmflag
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/***
 * @author BaeHyeonWoo
 */

class XVLClimateTask : Runnable {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

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

    private val server = getInstance().server

    override fun run() {
        for (onlinePlayers in server.onlinePlayers) {
            val location = onlinePlayers.location

            if (freezing[onlinePlayers.uniqueId] == true) {
                for (x in location.blockX - 2..location.blockX + 2) {
                    for (y in location.blockY - 2..location.blockY + 2) {
                        for (z in location.blockZ - 2..location.blockZ + 2) {
                            if (warmBlocks.contains((location.world.getBlockAt(x, y, z)).type)) {
                                warmflag[onlinePlayers.uniqueId] = true
                            }
                        }
                    }
                }

                if (warmflag[onlinePlayers.uniqueId] == true) {
                    // warm
                    server.broadcast(text("WARM"))
                    server.broadcast(text("HIGHEST FREEZING TICKS - ${onlinePlayers.name}: ${highestFreezingTicks[onlinePlayers.uniqueId]}"))

                    if (highestFreezingTicks[onlinePlayers.uniqueId] != 0) {
                        if (onlinePlayers.uniqueId.highestFreezingTickValue != 0) --onlinePlayers.uniqueId.highestFreezingTickValue
                        highestFreezingTicks[onlinePlayers.uniqueId] = onlinePlayers.uniqueId.highestFreezingTickValue
                    }
                    onlinePlayers.freezeTicks = requireNotNull(highestFreezingTicks[onlinePlayers.uniqueId])

                    server.scheduler.scheduleSyncDelayedTask(getInstance(), { warmflag[onlinePlayers.uniqueId] = false }, 20L)
                    onlinePlayers.removePotionEffect(PotionEffectType.SLOW)
                } else {
                    //cold
                    server.broadcast(text("COLD"))

                    server.broadcast(text("HIGHEST FREEZING TICKS - ${onlinePlayers.name}: ${highestFreezingTicks[onlinePlayers.uniqueId]}"))
                    if (onlinePlayers.freezeTicks != highestFreezingTicks[onlinePlayers.uniqueId]) onlinePlayers.freezeTicks = requireNotNull(highestFreezingTicks[onlinePlayers.uniqueId])
                    onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 4, true, false))
                }
            }
            else {
                if (highestFreezingTicks[onlinePlayers.uniqueId] != 0) {
                    if (onlinePlayers.uniqueId.highestFreezingTickValue != 0) --onlinePlayers.uniqueId.highestFreezingTickValue
                    highestFreezingTicks[onlinePlayers.uniqueId] = onlinePlayers.uniqueId.highestFreezingTickValue
                }
                onlinePlayers.freezeTicks = requireNotNull(highestFreezingTicks[onlinePlayers.uniqueId])
            }

            server.broadcast(text("FREEZING TICKS - ${onlinePlayers.name}: ${onlinePlayers.freezeTicks}"))

            if (onlinePlayers.thirstValue >= 600) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 0, true, false))
            } else if (onlinePlayers.thirstValue >= 2400) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 1, true, false))
            } else if (onlinePlayers.thirstValue >= 3600) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false))
            } else if (onlinePlayers.thirstValue >= 7200) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 1000000, 0, true, false))
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 4, true, false))
            }
        }
    }
}
