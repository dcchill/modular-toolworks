package com.toolsmithsworkshop.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** Component identities are authoritative. Derived stats are recalculated when needed. */
public record ToolBuildData(ResourceLocation head, ResourceLocation binding, ResourceLocation grip,
                            List<ResourceLocation> modules) {
    public static final Codec<ToolBuildData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("head").forGetter(ToolBuildData::head),
            ResourceLocation.CODEC.fieldOf("binding").forGetter(ToolBuildData::binding),
            ResourceLocation.CODEC.fieldOf("grip").forGetter(ToolBuildData::grip),
            ResourceLocation.CODEC.listOf().optionalFieldOf("modules", List.of()).forGetter(ToolBuildData::modules)
    ).apply(instance, ToolBuildData::new));

    public ToolBuildData(ResourceLocation head, ResourceLocation binding, ResourceLocation grip) {
        this(head, binding, grip, List.of());
    }

    public ToolBuildData {
        modules = List.copyOf(modules);
    }
}
