package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolComponentData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ToolsmithsWorkshop.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolComponentData>> TOOL_COMPONENT =
            REGISTER.registerComponentType("tool_component", builder -> builder.persistent(ToolComponentData.CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolBuildData>> TOOL_BUILD =
            REGISTER.registerComponentType("tool_build", builder -> builder.persistent(ToolBuildData.CODEC).cacheEncoding());

    private ModDataComponents() {}
}
