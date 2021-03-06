package com.github.vini2003.blade.testing.kotlin

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class DebugScreens {
	companion object {
		fun initialize() {
			ScreenRegistry.register(DebugContainers.DEBUG_HANDLER) { handler: DebugScreenHandler, inventory: PlayerInventory, title: Text -> DebugHandledScreen(handler, inventory, title) }
		}
	}
}