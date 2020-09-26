package vini2003.xyz.blade.common.widget.base

import vini2003.xyz.blade.client.utilities.Drawings
import vini2003.xyz.blade.client.utilities.Instances
import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent

open class ItemWidget(var stack: ItemStack = ItemStack.EMPTY) : AbstractWidget() {
	override fun getTooltip(): List<ITextComponent> {
		if (stack.isEmpty) return emptyList()

		return stack.getTooltipLines(null) {
			Instances.client().options.advancedItemTooltips
		}
	}

	override fun drawWidget(matrices: MatrixStack, provider: IRenderTypeBuffer) {
		if (hidden) return

		Drawings.getItemRenderer()!!.renderGuiItem(stack, position.x.toInt(), position.y.toInt())
	}
}