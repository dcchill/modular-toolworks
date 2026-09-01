package com.toolsmithsworkshop.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolBuildData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ModularToolRenderer extends BlockEntityWithoutLevelRenderer {
    public ModularToolRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models) {
        super(dispatcher, models);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        if (build == null || !(stack.getItem() instanceof com.toolsmithsworkshop.item.ModularToolItem tool)) return;
        packedLight = LightTexture.FULL_BRIGHT;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        if (context != ItemDisplayContext.GUI) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(Items.STICK), Minecraft.getInstance().level, null, 0);
            boolean leftHanded = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                    || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            model.getTransforms().getTransform(context).apply(leftHanded, poseStack);
        }
        renderPart(ModItems.component(ComponentRole.BINDING, build.binding()), poseStack, buffer,
                packedLight, packedOverlay, 0.02f);
        renderPart(ModItems.component(tool.archetype().headRole(), build.head()), poseStack, buffer,
                packedLight, packedOverlay, 0.01f);
        ItemStack grip = build.grip().equals(com.toolsmithsworkshop.tool.ToolMaterials.WOOD.id())
                ? new ItemStack(ModItems.WOODEN_GRIP.get())
                : new ItemStack(ModItems.component(ComponentRole.GRIP, build.grip()).get());
        renderStack(grip, poseStack, buffer, packedLight, packedOverlay, 0.0f);
        poseStack.popPose();
    }

    private static void renderPart(net.neoforged.neoforge.registries.DeferredItem<?> item,
                                   PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
                                   float zOffset) {
        if (item != null) renderStack(new ItemStack(item.get()), poseStack, buffer, packedLight, packedOverlay, zOffset);
    }

    private static void renderStack(ItemStack stack, PoseStack poseStack,
                                    MultiBufferSource buffer, int packedLight, int packedOverlay, float zOffset) {
        poseStack.pushPose();
        poseStack.translate(0.0f, 0.0f, zOffset);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, packedLight, packedOverlay,
                poseStack, buffer, Minecraft.getInstance().level, 0);
        poseStack.popPose();
    }
}
