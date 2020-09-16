package com.github.vini2003.blade.testing.kotlin

import com.github.vini2003.blade.client.handler.BaseContainerScreen
import com.github.vini2003.blade.common.handler.BaseContainer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent

class DebugContainerScreen(handler: BaseContainer, inventory: PlayerInventory, title: ITextComponent) : BaseContainerScreen<DebugContainer>(handler, inventory, title) {
	init {
	}
}