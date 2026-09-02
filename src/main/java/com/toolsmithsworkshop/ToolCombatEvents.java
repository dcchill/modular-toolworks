package com.toolsmithsworkshop;

import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolMaterials;
import com.toolsmithsworkshop.tool.ToolGems;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

@EventBusSubscriber(modid = ToolsmithsWorkshop.MOD_ID)
public final class ToolCombatEvents {
    private ToolCombatEvents() {}

    @SubscribeEvent
    public static void applyCriticalTraits(CriticalHitEvent event) {
        var stack = event.getEntity().getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!event.isVanillaCritical() || !(stack.getItem() instanceof ModularToolItem tool)
                || tool.archetype() != ToolArchetype.SWORD || build == null) return;
        if (build.grip().equals(ToolMaterials.BONE.id())) event.setDamageMultiplier(event.getDamageMultiplier() * 1.3f);
        if (build.binding().equals(ToolMaterials.SLIME.id()) && event.getTarget() instanceof LivingEntity target) {
            MobEffectInstance existing = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10,
                    Math.min(4, existing == null ? 0 : existing.getAmplifier() + 1)));
        }
    }

    @SubscribeEvent
    public static void applyEmeraldGemEnchantments(GetEnchantmentLevelEvent event) {
        var build = event.getStack().get(ModDataComponents.TOOL_BUILD);
        if (!(event.getStack().getItem() instanceof ModularToolItem) || build == null) return;
        int level = ToolGems.count(build, ToolGems.EMERALD);
        if (level == 0) return;
        event.getHolder(Enchantments.FORTUNE).ifPresent(fortune -> {
            if (event.isTargetting(fortune)) event.getEnchantments().upgrade(fortune, event.getEnchantments().getLevel(fortune) + level);
        });
        event.getHolder(Enchantments.LOOTING).ifPresent(looting -> {
            if (event.isTargetting(looting)) event.getEnchantments().upgrade(looting, event.getEnchantments().getLevel(looting) + level);
        });
    }
}
