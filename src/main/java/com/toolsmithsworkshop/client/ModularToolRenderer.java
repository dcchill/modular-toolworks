package com.toolsmithsworkshop.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolComponentData;
import com.toolsmithsworkshop.tool.ToolVisualTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        if (context != ItemDisplayContext.GUI) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(Items.STICK), Minecraft.getInstance().level, null, 0);
            boolean leftHanded = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                    || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            model.getTransforms().getTransform(context).apply(leftHanded, poseStack);
        }
        renderPart(ModItems.visual(tool.archetype(), ComponentRole.BINDING, build.binding()), ComponentRole.BINDING, build.binding(), tool.archetype().visualTransform(ComponentRole.BINDING), poseStack, buffer,
                packedLight, packedOverlay);
        renderPart(ModItems.visual(tool.archetype(), tool.archetype().headRole()), tool.archetype().headRole(), build.head(), tool.archetype().visualTransform(tool.archetype().headRole()), poseStack, buffer,
                packedLight, packedOverlay);
        renderPart(ModItems.visual(tool.archetype(), ComponentRole.GRIP, build.grip()), ComponentRole.GRIP, build.grip(), tool.archetype().visualTransform(ComponentRole.GRIP), poseStack, buffer,
                packedLight, packedOverlay);
        poseStack.popPose();
    }

    private static void renderPart(net.neoforged.neoforge.registries.DeferredItem<?> item, ComponentRole role,
                                   net.minecraft.resources.ResourceLocation material,
                                   ToolVisualTransform transform,
                                   PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (item == null) return;
        ItemStack stack = new ItemStack(item.get());
        stack.set(ModDataComponents.TOOL_COMPONENT.value(), new ToolComponentData(role, material));
        poseStack.pushPose();
        poseStack.translate(transform.x(), transform.y(), 0.0f);
        poseStack.scale(transform.scale(), transform.scale(), transform.depthScale());
        renderStack(stack, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private static void renderStack(ItemStack stack, PoseStack poseStack,
                                    MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, packedLight, packedOverlay,
                poseStack, buffer, Minecraft.getInstance().level, 0);
    }
}
