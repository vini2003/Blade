package com.github.vini2003.blade.common.handler

import com.github.vini2003.blade.common.utilities.Networks
import com.github.vini2003.blade.common.widget.OriginalWidgetCollection
import com.github.vini2003.blade.common.widget.base.AbstractWidget
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Identifier

open class BaseScreenHandler(type: ScreenHandlerType<out ScreenHandler>, syncId: Int, private val player: PlayerEntity) : ScreenHandler(type, syncId), OriginalWidgetCollection {
    override val widgets: ArrayList<AbstractWidget> = ArrayList()

    private val inventories = mutableMapOf<Int, Inventory>()

    val client = player.world.isClient

    open fun initialize(width: Int, height: Int) {

    }

    override fun addWidget(widget: AbstractWidget) {
        widgets.add(widget)
        widget.onAdded(this, this)

        widgets.forEach{ _ ->
            widget.onLayoutChanged()
        }
    }

    override fun removeWidget(widget: AbstractWidget) {
        widgets.remove(widget)
        widget.onRemoved(this, this)

        widgets.forEach{ _ ->
            widget.onLayoutChanged()
        }
    }

    open fun handlePacket(id: Identifier, buf: PacketByteBuf) {
        val hash = buf.readInt()

        widgets.forEach {
            if (it.hash == hash) {
                when (id) {
                    Networks.MOUSE_MOVE -> it.onMouseMoved(buf.readFloat(), buf.readFloat())
                    Networks.MOUSE_CLICK -> it.onMouseClicked(buf.readFloat(), buf.readFloat(), buf.readInt())
                    Networks.MOUSE_RELEASE -> it.onMouseReleased(buf.readFloat(), buf.readFloat(), buf.readInt())
                    Networks.MOUSE_DRAG -> it.onMouseDragged(buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readDouble(), buf.readDouble())
                    Networks.MOUSE_SCROLL -> it.onMouseScrolled(buf.readFloat(), buf.readFloat(), buf.readDouble())
                    Networks.KEY_PRESS -> it.onKeyPressed(buf.readInt(), buf.readInt(), buf.readInt())
                    Networks.KEY_RELEASE -> it.onKeyReleased(buf.readInt(), buf.readInt(), buf.readInt())
                    Networks.CHAR_TYPE -> it.onCharTyped(buf.readChar(), buf.readInt())
                    Networks.FOCUS_GAIN -> it.onFocusGained()
                    Networks.FOCUS_RELEASE -> it.onFocusReleased()
                }

                return@forEach
            }
        }
    }

    override fun getInventory(inventoryNumber: Int): Inventory? {
        return inventories[inventoryNumber]
    }

    override fun addInventory(inventoryNumber: Int, inventory: Inventory) {
        inventories[inventoryNumber] = inventory
    }

    override fun getPlayer(): PlayerEntity {
        return player
    }

    override fun getHandler(): BaseScreenHandler {
        return this
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return true
    }

    public override fun addSlot(slot: Slot?): Slot {
        return super.addSlot(slot)
    }

    fun removeSlot(slot: Slot?) {
        val id = slot!!.id
        slots.remove(slot)
        slots.forEach {
            if (it.id >= id) {
                --it.id
            }
        }
    }
}