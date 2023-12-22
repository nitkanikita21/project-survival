package com.projectsurvival.mixin;

import com.projectsurvival.IEntityDataSaver;
import com.projectsurvival.Projectsurvival;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityDataSaverMixin implements IEntityDataSaver {
    private NbtCompound persistNbtData;


    @Override
    public NbtElement project_survival$getData(String key) {
        if (persistNbtData == null) persistNbtData = new NbtCompound();
        return persistNbtData.getCompound(key);
    }

    @Override
    public void project_survival$setData(String key, NbtElement data) {
        persistNbtData.put(key, data);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if(persistNbtData != null) {
            nbt.put(Projectsurvival.ID, persistNbtData);
        }

    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains(Projectsurvival.ID)){
            persistNbtData = nbt.getCompound(Projectsurvival.ID);
        }
    }
}
