package net.armoredmobs;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ArmoredMobsHandler {

    private static final Random RANDOM = new Random();

    // Chance de spawner avec une armure (65%)
    private static final double ARMOR_CHANCE = 0.65;

    // Répartition matériaux : 60% netherite, 25% diamond, 15% iron
    private static final double NETHERITE_CHANCE = 0.60;
    private static final double DIAMOND_CHANCE   = 0.625; // sur les 40% restants

    // Trims
    private static final String[] TRIM_PATTERNS = {
        "sentry","vex","wild","coast","dune","wayfinder","raiser","shaper",
        "host","ward","silence","tide","snout","rib","eye","spire","flow","bolt"
    };
    private static final String[] TRIM_MATERIALS = {
        "quartz","iron","netherite","redstone","copper","gold",
        "emerald","diamond","lapis","amethyst"
    };

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Mob mob)) return;
            if (!(world instanceof ServerLevel level)) return;
            if (!shouldEquip(mob)) return;
            if (RANDOM.nextDouble() > ARMOR_CHANCE) return;

            String material = pickMaterial();
            equipArmor(mob, level, material);
            equipWeapon(mob, level);
            forceDropChances(mob);
        });
    }

    private static boolean shouldEquip(Mob mob) {
        return mob instanceof Zombie
            || mob instanceof Husk
            || mob instanceof ZombieVillager
            || mob instanceof Drowned
            || mob instanceof Skeleton
            || mob instanceof Stray
            || mob instanceof Bogged
            || mob instanceof WitherSkeleton
            || mob instanceof Pillager
            || mob instanceof Vindicator;
    }

    private static String pickMaterial() {
        double r = RANDOM.nextDouble();
        if (r < NETHERITE_CHANCE) return "netherite";
        if (r < NETHERITE_CHANCE + (1.0 - NETHERITE_CHANCE) * DIAMOND_CHANCE) return "diamond";
        return "iron";
    }

    private static void equipArmor(Mob mob, ServerLevel level, String material) {
        ItemStack helmet     = makeHelmet(material, level);
        ItemStack chestplate = makeChestplate(material, level);
        ItemStack leggings   = makeLeggings(material, level);
        ItemStack boots      = makeBoots(material, level);

        mob.setItemSlot(EquipmentSlot.HEAD, helmet);
        mob.setItemSlot(EquipmentSlot.CHEST, chestplate);
        mob.setItemSlot(EquipmentSlot.LEGS, leggings);
        mob.setItemSlot(EquipmentSlot.FEET, boots);
    }

    private static void equipWeapon(Mob mob, ServerLevel level) {
        ItemStack weapon;
        if (mob instanceof Skeleton || mob instanceof Stray || mob instanceof Bogged) {
            weapon = makeBow(level);
        } else if (mob instanceof Vindicator) {
            weapon = makeAxe(level);
        } else {
            weapon = makeSword(level);
        }
        mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
    }

    private static void forceDropChances(Mob mob) {
        // Drop 100% pour tous les slots équipés
        mob.setDropChance(EquipmentSlot.HEAD, 2.0f);
        mob.setDropChance(EquipmentSlot.CHEST, 2.0f);
        mob.setDropChance(EquipmentSlot.LEGS, 2.0f);
        mob.setDropChance(EquipmentSlot.FEET, 2.0f);
        mob.setDropChance(EquipmentSlot.MAINHAND, 2.0f);
    }

    // ═══════════════════════════════════════════════
    // CRÉATION D'ITEMS AVEC ENCHANTEMENTS ET TRIMS
    // ═══════════════════════════════════════════════

    private static ItemStack makeHelmet(String mat, ServerLevel level) {
        ItemStack item = new ItemStack(switch (mat) {
            case "netherite" -> Items.NETHERITE_HELMET;
            case "diamond"   -> Items.DIAMOND_HELMET;
            default          -> Items.IRON_HELMET;
        });
        // Protections (1, 2 ou 3 combinées — pas blast)
        addProtections(item, level);
        // Bonus tête
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "respiration", 3);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "aqua_affinity", 1);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "thorns", 3);
        addEnchantment(item, level, "unbreaking", 3);
        if (mat.equals("netherite")) addEnchantment(item, level, "mending", 1);
        addTrim(item, level);
        return item;
    }

    private static ItemStack makeChestplate(String mat, ServerLevel level) {
        ItemStack item = new ItemStack(switch (mat) {
            case "netherite" -> Items.NETHERITE_CHESTPLATE;
            case "diamond"   -> Items.DIAMOND_CHESTPLATE;
            default          -> Items.IRON_CHESTPLATE;
        });
        addProtections(item, level);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "thorns", 3);
        addEnchantment(item, level, "unbreaking", 3);
        if (mat.equals("netherite")) addEnchantment(item, level, "mending", 1);
        addTrim(item, level);
        return item;
    }

    private static ItemStack makeLeggings(String mat, ServerLevel level) {
        ItemStack item = new ItemStack(switch (mat) {
            case "netherite" -> Items.NETHERITE_LEGGINGS;
            case "diamond"   -> Items.DIAMOND_LEGGINGS;
            default          -> Items.IRON_LEGGINGS;
        });
        addProtections(item, level);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "swift_sneak", 3);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "thorns", 3);
        addEnchantment(item, level, "unbreaking", 3);
        if (mat.equals("netherite")) addEnchantment(item, level, "mending", 1);
        addTrim(item, level);
        return item;
    }

    private static ItemStack makeBoots(String mat, ServerLevel level) {
        ItemStack item = new ItemStack(switch (mat) {
            case "netherite" -> Items.NETHERITE_BOOTS;
            case "diamond"   -> Items.DIAMOND_BOOTS;
            default          -> Items.IRON_BOOTS;
        });
        addProtections(item, level);
        addEnchantment(item, level, "feather_falling", 4);
        // Boots exclusives (une seule au hasard)
        int bootExtra = RANDOM.nextInt(4);
        switch (bootExtra) {
            case 0 -> addEnchantment(item, level, "depth_strider", 3);
            case 1 -> addEnchantment(item, level, "frost_walker", 2);
            case 2 -> addEnchantment(item, level, "soul_speed", 3);
            // case 3 : rien
        }
        addEnchantment(item, level, "unbreaking", 3);
        if (mat.equals("netherite")) addEnchantment(item, level, "mending", 1);
        addTrim(item, level);
        return item;
    }

    private static ItemStack makeSword(ServerLevel level) {
        ItemStack item = new ItemStack(Items.NETHERITE_SWORD);
        // Dégâts : sharpness, smite, bane — 1, 2 ou 3 combinés au hasard
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "sharpness", 5);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "smite", 5);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "bane_of_arthropods", 5);
        // Bonus
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "fire_aspect", 2);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "knockback", 2);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "looting", 3);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "sweeping_edge", 3);
        addEnchantment(item, level, "unbreaking", 3);
        addEnchantment(item, level, "mending", 1);
        return item;
    }

    private static ItemStack makeBow(ServerLevel level) {
        ItemStack item = new ItemStack(Items.BOW);
        addEnchantment(item, level, "power", 5);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "punch", 2);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "flame", 1);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "infinity", 1);
        addEnchantment(item, level, "unbreaking", 3);
        addEnchantment(item, level, "mending", 1);
        return item;
    }

    private static ItemStack makeAxe(ServerLevel level) {
        ItemStack item = new ItemStack(Items.NETHERITE_AXE);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "sharpness", 5);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "smite", 5);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "bane_of_arthropods", 5);
        addEnchantment(item, level, "efficiency", 5);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "fire_aspect", 2);
        if (RANDOM.nextBoolean()) addEnchantment(item, level, "looting", 3);
        addEnchantment(item, level, "unbreaking", 3);
        addEnchantment(item, level, "mending", 1);
        return item;
    }

    // ═══════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════

    /** Ajoute toujours les 3 protections : normale + feu + projectile (pas blast) */
    private static void addProtections(ItemStack item, ServerLevel level) {
        addEnchantment(item, level, "protection", 4);
        addEnchantment(item, level, "fire_protection", 4);
        addEnchantment(item, level, "projectile_protection", 4);
    }

    private static void addEnchantment(ItemStack item, ServerLevel level, String enchantId, int lvl) {
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var key = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace(enchantId));
        registry.get(key).ifPresent(enchHolder -> {
            ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(
                item.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
            );
            enchantments.set(enchHolder, lvl);
            item.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        });
    }

    private static void addTrim(ItemStack item, ServerLevel level) {
        String patternId = TRIM_PATTERNS[RANDOM.nextInt(TRIM_PATTERNS.length)];
        String materialId = TRIM_MATERIALS[RANDOM.nextInt(TRIM_MATERIALS.length)];

        var patternReg = level.registryAccess().lookupOrThrow(Registries.TRIM_PATTERN);
        var materialReg = level.registryAccess().lookupOrThrow(Registries.TRIM_MATERIAL);

        var patternKey = ResourceKey.create(Registries.TRIM_PATTERN,
            ResourceLocation.withDefaultNamespace(patternId));
        var materialKey = ResourceKey.create(Registries.TRIM_MATERIAL,
            ResourceLocation.withDefaultNamespace(materialId));

        patternReg.get(patternKey).ifPresent(pattern ->
            materialReg.get(materialKey).ifPresent(material -> {
                ArmorTrim trim = new ArmorTrim(material, pattern);
                item.set(DataComponents.TRIM, trim);
            })
        );
    }
}
