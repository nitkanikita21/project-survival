package com.projectsurvival.mechanic.blockbreaking

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.extensions.convertTo
import com.projectsurvival.extensions.getData
import com.projectsurvival.extensions.setData
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import kotlin.reflect.full.primaryConstructor

class EnhancedBreakableBlockEntity(
    type: BlockEntityType<*>?,
    pos: BlockPos?,
    state: BlockState?
) : BlockEntity(
    type, pos,
    state
) {
    data class Data(
        var hp: Int
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("hp").forGetter(Data::hp)
                ).apply(instance, Data::class.primaryConstructor!!::call)
            }
        }
    }

    var data: Data? = null

    override fun readNbt(nbt: NbtCompound) {
        data = nbt.getData("data", Data.CODEC)
    }

    override fun writeNbt(nbt: NbtCompound) {
        data?.let { nbt.setData("data", Data.CODEC, it) }
    }
}