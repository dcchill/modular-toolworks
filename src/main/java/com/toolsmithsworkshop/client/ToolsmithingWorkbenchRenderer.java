package com.toolsmithsworkshop.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toolsmithsworkshop.block.entity.ToolsmithingWorkbenchBlockEntity;
import com.toolsmithsworkshop.menu.ToolsmithingMenu;
import com.toolsmithsworkshop.item.ForgingHammerItem;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public final class ToolsmithingWorkbenchRenderer implements BlockEntityRenderer<ToolsmithingWorkbenchBlockEntity> {
    public ToolsmithingWorkbenchRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public AABB getRenderBoundingBox(ToolsmithingWorkbenchBlockEntity workbench) {
        return workbench.renderBounds();
    }

    @Override
    public void render(ToolsmithingWorkbenchBlockEntity workbench, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack hammer = workbench.getItem(ToolsmithingMenu.FORGING_HAMMER);
        // Do not leave a stale render behind when the slot is cleared or the
        // slot contains a non-forging item.
        if (hammer.isEmpty() || !(hammer.getItem() instanceof ForgingHammerItem)) return;
        poseStack.pushPose();
        // Match the hammer element removed from the JSON model. The item stack
        // supplies the complete forging-hammer model and therefore its texture.
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(blockRotation(workbench.getBlockState().getValue(
            com.toolsmithsworkshop.block.ToolsmithingWorkbenchBlock.FACING))));
        poseStack.translate(0.89D, 1.25D, 0.30D);
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(135.0F));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(0.375F, 0.375F, 0.375F);
        Minecraft.getInstance().getItemRenderer().renderStatic(hammer, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                poseStack, buffer, workbench.getLevel(), 0);
        poseStack.popPose();
    }

    private static float blockRotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 0.0F;
            case EAST -> 270.0F;
            case SOUTH -> 180.0F;
            case WEST -> 90.0F;
            default -> 0.0F;
        };
    }
}