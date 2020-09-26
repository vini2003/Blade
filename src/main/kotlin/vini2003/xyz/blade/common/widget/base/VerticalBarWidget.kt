package vini2003.xyz.blade.common.widget.base

import vini2003.xyz.blade.client.data.PartitionedTexture
import vini2003.xyz.blade.client.utilities.Instances
import vini2003.xyz.blade.client.utilities.Scissors
import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.util.ResourceLocation
import vini2003.xyz.blade.Blade

open class VerticalBarWidget(var maximum: () -> Float = {100F}, var current: () -> Float = {0F}, foregroundId: ResourceLocation = Blade.resourceLocation("textures/widget/bar_foreground.png"), backgroundId: ResourceLocation = Blade.resourceLocation("textures/widget/bar_background.png")) : AbstractWidget() {
	var foregroundTexture = PartitionedTexture(foregroundId, 18F, 18F, 0.05555555555555555556F, 0.05555555555555555556F, 0.05555555555555555556F, 0.05555555555555555556F)
	var backgroundTexture = PartitionedTexture(backgroundId, 18F, 18F, 0.05555555555555555556F, 0.05555555555555555556F, 0.05555555555555555556F, 0.05555555555555555556F)

	override fun drawWidget(matrices: MatrixStack, provider: IRenderTypeBuffer) {
		if (hidden) return

		val rawHeight = Instances.client().window.height.toFloat()
		val scale = Instances.client().window.guiScale.toFloat()

		val sBGY: Float = height / maximum() * current()

		var area: Scissors?

		area = Scissors(provider, (x * scale).toInt(), (rawHeight - (y + height - sBGY) * scale).toInt(), (width * scale).toInt(), ((height - sBGY) * scale).toInt())

		backgroundTexture.draw(matrices, provider, x, y, width, height)

		area.destroy(provider)

		area = Scissors(provider, (x * scale).toInt(), (rawHeight - (y + height) * scale).toInt(), (width * scale).toInt(), (sBGY * scale).toInt())

		foregroundTexture.draw(matrices, provider, x, y, width, height)

		foregroundTexture.draw(matrices, provider, x, y, width, height)

		area.destroy(provider)
	}
}
