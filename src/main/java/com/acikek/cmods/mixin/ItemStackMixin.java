package com.acikek.cmods.mixin;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getAttributeModifiers",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/NbtList;"))
    private void combineModifiers(
        EquipmentSlot slot,
        CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir,
        @Local Multimap<EntityAttribute, EntityAttributeModifier> multimap
    ) {
        // Fabric Item API Compatibility
        ItemStack stack = (ItemStack) (Object) this;
        //we need to ensure it is modifiable for the callback, use linked map to preserve ordering
        Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = LinkedHashMultimap.create(stack.getItem().getAttributeModifiers(stack, slot));
        ModifyItemAttributeModifiersCallback.EVENT.invoker().modifyAttributeModifiers(stack, slot, attributeModifiers);
        multimap.putAll(attributeModifiers);
    }
}