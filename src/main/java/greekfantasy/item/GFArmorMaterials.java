package greekfantasy.item;

import greekfantasy.GFRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.function.Supplier;

public class GFArmorMaterials {

    private static final ResourceLocation COPPER_INGOTS = new ResourceLocation("forge", "ingots/copper");

    public static final GFArmorMaterial HELLENIC = new GFArmorMaterial("hellenic", 26, new int[]{2, 5, 6, 2}, 18,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(ForgeRegistries.ITEMS.tags().createOptionalTagKey(COPPER_INGOTS, Set.of(() -> Items.COPPER_INGOT))));

    public static final GFArmorMaterial SNAKESKIN = new GFArmorMaterial("snakeskin", 16, new int[]{1, 4, 5, 2}, 15,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(GFRegistry.ItemReg.TOUGH_SNAKESKIN.get()));

    public static final GFArmorMaterial AVERNAL = new GFArmorMaterial("avernal", 22, new int[]{2, 5, 6, 2}, 11,
            SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, () -> Ingredient.of(GFRegistry.ItemReg.AVERNAL_SHARD.get()));

    public static final GFArmorMaterial NEMEAN = new GFArmorMaterial("nemean", 37, new int[]{2, 5, 6, 3}, 9,
            SoundEvents.ARMOR_EQUIP_LEATHER, 2.0F, 0.0F, () -> Ingredient.EMPTY);

    public static final GFArmorMaterial WINGED = new GFArmorMaterial("winged", 12, new int[]{2, 5, 6, 2}, 22,
            SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, -0.05F, () -> Ingredient.of(GFRegistry.ItemReg.AVERNAL_FEATHER.get()));

    private static final class GFArmorMaterial implements ArmorMaterial {
        private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
        private final String name;
        private final int durabilityMultiplier;
        private final int[] slotProtections;
        private final int enchantmentValue;
        private final SoundEvent sound;
        private final float toughness;
        private final float knockbackResistance;
        private Ingredient repairIngredient;
        private final Supplier<Ingredient> repairIngredientSupplier;

        private GFArmorMaterial(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.slotProtections = slotProtections;
            this.enchantmentValue = enchantmentValue;
            this.sound = sound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairIngredientSupplier = repairIngredient;
        }

        public int getDurabilityForSlot(EquipmentSlot equipmentSlot) {
            return HEALTH_PER_SLOT[equipmentSlot.getIndex()] * this.durabilityMultiplier;
        }

        public int getDefenseForSlot(EquipmentSlot equipmentSlot) {
            return this.slotProtections[equipmentSlot.getIndex()];
        }

        public int getEnchantmentValue() {
            return this.enchantmentValue;
        }

        public SoundEvent getEquipSound() {
            return this.sound;
        }

        public Ingredient getRepairIngredient() {
            if(null == this.repairIngredient) {
                this.repairIngredient = this.repairIngredientSupplier.get();
            }
            return this.repairIngredient;
        }

        public String getName() {
            return this.name;
        }

        public float getToughness() {
            return this.toughness;
        }

        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }


    }
}
