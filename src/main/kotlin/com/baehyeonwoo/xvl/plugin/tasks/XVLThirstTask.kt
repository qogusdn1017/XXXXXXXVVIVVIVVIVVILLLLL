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
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.thirstValue
import org.bukkit.plugin.Plugin

class XVLThirstTask : Runnable {

    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val server = getInstance().server

    override fun run() {
        for (onlinePlayers in server.onlinePlayers) {
            if (XVLGameStatus.warming[onlinePlayers.uniqueId] == true) {
                if (XVLGameStatus.isWarmBiome[onlinePlayers.uniqueId] == true) {
                    ++onlinePlayers.thirstValue
                    ++onlinePlayers.thirstValue
                } else if (XVLGameStatus.isNetherBiome[onlinePlayers.uniqueId] == true) {
                    ++onlinePlayers.thirstValue
                    ++onlinePlayers.thirstValue
                    ++onlinePlayers.thirstValue
                }
            } else {
                ++onlinePlayers.thirstValue
            }
        }
    }
}