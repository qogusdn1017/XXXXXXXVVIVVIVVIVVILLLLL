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

package com.baehyeonwoo.xvl.plugin.events

import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.currentDate
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.motd
import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import net.kyori.adventure.text.Component.text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/***
 * @author BaeHyeonWoo
 */

class XVLMotdEvent : Listener {
    @EventHandler
    fun onPaperServerListPing(e: PaperServerListPingEvent) {
        // Project start date; it has been planned earlier, but I forgot to set up the Wakatime & in this date I actually started writing in-game managing codes.

        e.numPlayers = 20211122
        e.maxPlayers = currentDate()
        e.playerSample.clear()
        e.motd(text(motd()))
    }
}