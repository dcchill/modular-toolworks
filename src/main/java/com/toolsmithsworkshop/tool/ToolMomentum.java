package com.toolsmithsworkshop.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ToolMomentum(long lastMineTick, int stacks) {
    public static final Codec<ToolMomentum> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("last_mine_tick").forGetter(ToolMomentum::lastMineTick),
            Codec.INT.fieldOf("stacks").forGetter(ToolMomentum::stacks)
    ).apply(instance, ToolMomentum::new));
}
