package com.toolsmithsworkshop.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ToolComponentData(ComponentRole role, ResourceLocation material) {
    public static final Codec<ToolComponentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentRole.CODEC.fieldOf("role").forGetter(ToolComponentData::role),
            ResourceLocation.CODEC.fieldOf("material").forGetter(ToolComponentData::material)
    ).apply(instance, ToolComponentData::new));
}
