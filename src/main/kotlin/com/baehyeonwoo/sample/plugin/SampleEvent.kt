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

package com.baehyeonwoo.sample.plugin

import io.github.monun.tap.effect.playFirework
import net.kyori.adventure.text.Component.text
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

/***
 * @author BaeHyeonWoo
 */

class SampleEvent : Listener {
    private fun getInstance(): Plugin {
        return SamplePluginMain.instance
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val loc = e.player.location.add(0.0, 0.5, 0.0)
        val firework = FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(Color.AQUA).build()

        if (getInstance().config.getString("administrator").toString().contains(e.player.uniqueId.toString())) {
            e.player.sendMessage(text("Tap Config Sample: Admin Join"))
        }

        getInstance().logger.info("Hello World!")
        e.player.sendMessage(text("Hello World!"))
        loc.world.playFirework(loc, firework)
    }
}