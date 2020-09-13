package com.github.vini2003.blade.common.widget.base

import com.github.vini2003.blade.Blade
import com.github.vini2003.blade.client.data.PartitionedTexture
import com.github.vini2003.blade.client.utilities.Drawings
import com.github.vini2003.blade.client.utilities.Layers
import com.github.vini2003.blade.common.data.Size
import com.github.vini2003.blade.common.data.geometry.Rectangle
import com.github.vini2003.blade.common.data.widget.TabCollection
import com.github.vini2003.blade.common.utilities.Networks
import com.github.vini2003.blade.common.utilities.Positions
import com.github.vini2003.blade.common.widget.OriginalWidgetCollection
import com.github.vini2003.blade.common.widget.WidgetCollection
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text


class TabWidget : AbstractWidget(), WidgetCollection {
	private val tabRectangles: MutableList<Rectangle> = mutableListOf()
	private val tabCollections: MutableList<TabCollection> = mutableListOf()
	private val tabSymbols: MutableList<ItemStack> = mutableListOf()
	private val tabTooltips: MutableList<() -> List<Text>> = mutableListOf()
	override val widgets : MutableList<AbstractWidget> = mutableListOf()

	var selected: Int = 0

	var leftActiveTexture = Blade.identifier("textures/widget/tab_left_active.png")
	var middleActiveTexture = Blade.identifier("textures/widget/tab_middle_active.png")
	var rightActiveTexture = Blade.identifier("textures/widget/tab_right_active.png")

	var leftInactiveTexture = Blade.identifier("textures/widget/tab_left_inactive.png")
	var middleInactiveTexture = Blade.identifier("textures/widget/tab_middle_inactive.png")
	var rightInactiveTexture = Blade.identifier("textures/widget/tab_right_inactive.png")

	var panelTexture = PartitionedTexture(Blade.identifier("textures/widget/panel.png"), 18F, 18F, 0.25F, 0.25F, 0.25F, 0.25F)

	override fun onLayoutChanged() {
		super.onLayoutChanged()

		widgets.clear()

		tabCollections.forEach {
			widgets.addAll(it.widgets)
		}
	}

	override fun onAdded(original: OriginalWidgetCollection, immediate: WidgetCollection) {
		super.onAdded(original, immediate)

		tabCollections.forEachIndexed { index, collection ->
			collection.original = original
			collection.parent = this

			if (index != 0) {
				collection.widgets.forEach {
					it.hidden = true
				}
			}
		}

		synchronize.add(Networks.MOUSE_CLICK)
	}

	fun addTab(symbol: ItemStack): WidgetCollection {
		return addTab(symbol) {
			emptyList()
		}
	}

	fun addTab(symbol: Item): WidgetCollection {
		return addTab(ItemStack(symbol)) {
			emptyList()
		}
	}

	fun addTab(symbol: Item, tooltip: () -> List<Text>): WidgetCollection {
		return addTab(ItemStack(symbol), tooltip)
	}

	fun addTab(symbol: ItemStack, tooltip: () -> List<Text>): WidgetCollection {
		val collection = TabCollection(tabCollections.size)
		collection.original = original
		collection.parent = this

		tabCollections.add(collection)
		tabSymbols.add(symbol)
		tabTooltips.add(tooltip)

		tabRectangles.also {
			it.clear()

			var x = 0F

			tabCollections.forEach { _ ->
				tabRectangles.add(Rectangle(position.offset(x, 0), Size.of(26F, 25F)))
				x += 26F
			}
		}

		return collection
	}

	override fun getTooltip(): List<Text> {
		tabRectangles.forEachIndexed { index, rectangle ->
			if (rectangle.isWithin(Positions.mouseX, Positions.mouseY)) {
				if (tabTooltips.size >= index) {
					return tabTooltips[index].invoke()
				}
			}
		}

		return super.getTooltip()
	}

	override fun onMouseClicked(x: Float, y: Float, button: Int) {
		super.onMouseClicked(x, y, button)

		if (tabRectangles.any { it.isWithin(x, y) }) {
			tabRectangles.forEachIndexed { index, rectangle ->
				val hidden = !rectangle.isWithin(x, y)

				tabCollections[index].widgets.forEach {
					it.hidden = hidden
				}

				if (!hidden) {
					selected = index
				}
			}
		}
	}

	override fun drawWidget(matrices: MatrixStack, provider: VertexConsumerProvider) {
		panelTexture.draw(matrices, provider, position.x, position.y + 25F, size.width, size.height - 25F)

		if (provider is VertexConsumerProvider.Immediate) provider.draw()

		tabRectangles.forEachIndexed { index, rectangle ->
			if (selected == index) {
				when (rectangle.position.x) {
					position.x -> Drawings.drawTexturedQuad(matrices, provider, Layers.get(leftActiveTexture), position.x + (26F * index), position.y, 25F, 29F, leftActiveTexture)
					position.x + size.width - 25F -> Drawings.drawTexturedQuad(matrices, provider, Layers.get(rightActiveTexture), position.x + (26F * index), position.y, 25F, 28F, rightActiveTexture)
					else -> Drawings.drawTexturedQuad(matrices, provider, Layers.get(middleActiveTexture), position.x + (26F * index), position.y, 25F, 28F, middleActiveTexture)
				}
			} else {
				when (rectangle.position.x) {
					position.x -> Drawings.drawTexturedQuad(matrices, provider, Layers.get(leftInactiveTexture), position.x + (26F * index), position.y + 3F, 25F, 24F, leftInactiveTexture)
					position.x + size.width - 25F -> Drawings.drawTexturedQuad(matrices, provider, Layers.get(rightInactiveTexture), position.x + (26F * index), position.y + 2F, 25F, 26F, rightInactiveTexture)
					else -> Drawings.drawTexturedQuad(matrices, provider, Layers.get(middleInactiveTexture), position.x + (26F * index), position.y + 2F, 25F, 25F, middleInactiveTexture)
				}
			}

			Drawings.getItemRenderer()?.renderGuiItemIcon(tabSymbols[index], (position.x + (26F * index) + 4.5F).toInt(), position.y.toInt() + 7)
		}

		widgets.forEach {
			it.drawWidget(matrices, provider)
		}
	}
}