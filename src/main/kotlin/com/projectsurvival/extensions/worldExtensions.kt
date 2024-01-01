package com.projectsurvival.extensions

import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun <B: BlockEntity> World.getBlockEntity(pos: BlockPos): B? = this.getBlockEntity(pos) as? B