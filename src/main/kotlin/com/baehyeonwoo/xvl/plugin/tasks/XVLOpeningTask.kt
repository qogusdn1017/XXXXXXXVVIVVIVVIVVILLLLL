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

import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.getInstance
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.titleFunction
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/***
 * @author BaeHyeonWoo
 */

class XVLOpeningTask : Runnable {
    private val server = getInstance().server

    private var count = 0

    private fun openingPlaySound() {
        server.onlinePlayers.forEach {
            it.playSound(it.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1F)
        }
    }

    override fun run() {
        when (count++) {
            0 -> {
                server.onlinePlayers.forEach {
                    it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 1000000, 255, true, false))
                    openingPlaySound()
                }
                titleFunction(text(" "), text("R"))
            }
            1 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Re"))
            }
            2 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rec"))
            }
            3 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rech"))
            }
            4 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Recha"))
            }
            5 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rechal"))
            }
            6 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rechall"))
            }
            7 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rechalle"))
            }
            8 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rechallen"))
            }
            9 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rechalleng"))
            }
            10 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rechallenge"))
            }
            11 -> {
                openingPlaySound()
                titleFunction(text(" "), text("Rechallenge."))
            }
            15 -> {
                titleFunction(text(" "), text("R e c h a l l e n g e .", NamedTextColor.BLACK).decorate(TextDecoration.ITALIC))
                server.onlinePlayers.forEach {
                    it.world.spawnParticle(Particle.ASH, it.location.add(0.0, 2.0, 0.0), 150, 0.5, 0.5, 1.5, 0.25, null, true)
                    it.removePotionEffect(PotionEffectType.BLINDNESS)
                    it.playSound(it.location, Sound.ENTITY_WITHER_SPAWN, 0.5F, 1F)
                }
            }
        }
    }
}