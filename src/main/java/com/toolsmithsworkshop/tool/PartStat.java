package com.toolsmithsworkshop.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

/** One weighted random affix rolled after a modular tool is claimed. */
public record PartStat(Type type, int percent) {
    private static final Type[] WEAPON_POSITIVE = {Type.CRIT_RATE, Type.CRIT_DAMAGE, Type.ATTACK, Type.DEFENSE};
    private static final Type[] MINING_POSITIVE = {Type.MINING_SPEED, Type.PROSPECTING, Type.PRECISION,
            Type.RECOVERY, Type.FRACTURE, Type.DEFENSE};
    private static final Type[] NEGATIVE = {Type.EXHAUSTING, Type.FRAGILE, Type.FLIMSY};

    public static final Codec<PartStat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(PartStat::type),
            Codec.INT.fieldOf("percent").forGetter(PartStat::percent)
    ).apply(instance, PartStat::new));

    public PartStat {
        if (percent < type.minimum() || percent > type.maximum()) {
            throw new IllegalArgumentException(type.serializedName() + " must be between "
                    + type.minimum() + "% and " + type.maximum() + "%");
        }
    }

    public static PartStat roll(RandomSource random, boolean weapon) {
        Type[] pool = random.nextFloat() < 0.70f ? (weapon ? WEAPON_POSITIVE : MINING_POSITIVE) : NEGATIVE;
        Type type = pool[random.nextInt(pool.length)];
        float roll = random.nextFloat();
        int percent = type.minimum() + Math.round((type.maximum() - type.minimum()) * roll * roll);
        return new PartStat(type, percent);
    }

    public enum Type {
        CRIT_RATE("crit_rate", "Critical Rate", 5, 15, true),
        CRIT_DAMAGE("crit_damage", "Critical Damage", 10, 30, true),
        ATTACK("attack", "Attack", 5, 25, true),
        DEFENSE("defense", "Defense", 10, 25, true),
        MINING_SPEED("mining_speed", "Mining Speed", 5, 15, true),
        PROSPECTING("prospecting", "Prospecting", 10, 25, true),
        PRECISION("precision", "Precision", 15, 50, true),
        RECOVERY("recovery", "Recovery", 5, 15, true),
        FRACTURE("fracture", "Fracture", 5, 25, true),
        EXHAUSTING("exhausting", "Exhausting", 10, 40, false),
        FRAGILE("fragile", "Fragile", 15, 25, false),
        FLIMSY("flimsy", "Flimsy", 2, 5, false);

        public static final Codec<Type> CODEC = Codec.STRING.xmap(Type::byName, Type::serializedName);

        private final String serializedName;
        private final String displayName;
        private final int minimum;
        private final int maximum;
        private final boolean positive;

        Type(String serializedName, String displayName, int minimum, int maximum, boolean positive) {
            this.serializedName = serializedName;
            this.displayName = displayName;
            this.minimum = minimum;
            this.maximum = maximum;
            this.positive = positive;
        }

        public String serializedName() { return serializedName; }
        public String displayName() { return displayName; }
        public int minimum() { return minimum; }
        public int maximum() { return maximum; }
        public boolean positive() { return positive; }

        private static Type byName(String name) {
            for (Type type : values()) if (type.serializedName.equals(name)) return type;
            throw new IllegalArgumentException("Unknown part stat: " + name);
        }
    }
}
