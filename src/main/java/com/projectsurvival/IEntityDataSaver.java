package com.projectsurvival;

import net.minecraft.nbt.NbtElement;

public interface IEntityDataSaver {
    boolean project_survival$hasData(String key);
    NbtElement project_survival$getData(String key);
    void project_survival$setData(String key, NbtElement data);
}
