package com.chaosbuffalo.mknpc.spawn;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class SpawnList implements INBTSerializable<CompoundNBT> {

    private final List<SpawnOption> options;

    public SpawnList(){
        this.options = new ArrayList<>();
    }

    public List<SpawnOption> getOptions() {
        return options;
    }

    public void addOption(SpawnOption option){
        options.add(option);
    }

    public void copyList(SpawnList other){
        this.options.clear();
        for (SpawnOption option : other.getOptions()){
            addOption(option);
        }
    }

    public void setWeightForOption(int index, double weight){
        options.get(index).setWeight(weight);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        ListNBT opts = new ListNBT();
        for (SpawnOption option : getOptions()){
            opts.add(option.serializeNBT());
        }
        tag.put("options", opts);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT opts = nbt.getList("options", Constants.NBT.TAG_COMPOUND);
        options.clear();
        for (int i = 0; i < opts.size(); i++){
            CompoundNBT option = opts.getCompound(i);
            SpawnOption spawnOption = new SpawnOption();
            spawnOption.deserializeNBT(option);
            addOption(spawnOption);
        }
    }
}
