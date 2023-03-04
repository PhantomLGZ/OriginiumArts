package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.tags.ItemTags
import net.minecraftforge.common.Tags
import java.util.function.Consumer

class OARecipes(generator: DataGenerator) : RecipeProvider(generator) {

    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {
        smeltingResultFromBase(
            consumer,
            ItemRegister.ORIGINIUM_INGOT.get(),
            ItemRegister.ORIGINIUM.get()
        )
        nineBlockStorageRecipes(
            consumer,
            ItemRegister.ORIGINIUM_FRAGMENT.get(),
            ItemRegister.ORIGINIUM.get()
        )
        ShapedRecipeBuilder.shaped(ItemRegister.OA_STANDARD_PISTOL.get())
            .pattern("xxx")
            .pattern(" mx")
            .define('x', Tags.Items.INGOTS_IRON)
            .define('m', ItemRegister.ORIGINIUM_INGOT.get())
            .unlockedBy("has_chunk", has(ItemRegister.ORIGINIUM_INGOT.get()))
            .save(consumer)
        ShapedRecipeBuilder.shaped(ItemRegister.OA_STANDARD_STAFF.get())
            .pattern("  x")
            .pattern(" m ")
            .pattern("x  ")
            .define('x', Tags.Items.INGOTS_IRON)
            .define('m', ItemRegister.ORIGINIUM_INGOT.get())
            .unlockedBy("has_chunk", has(ItemRegister.ORIGINIUM_INGOT.get()))
            .save(consumer)
        ShapedRecipeBuilder.shaped(ItemRegister.OA_STANDARD_SWORD.get())
            .pattern("x")
            .pattern("x")
            .pattern("m")
            .define('x', Tags.Items.INGOTS_IRON)
            .define('m', ItemRegister.ORIGINIUM_INGOT.get())
            .unlockedBy("has_chunk", has(ItemRegister.ORIGINIUM_INGOT.get()))
            .save(consumer)
        ShapedRecipeBuilder.shaped(ItemRegister.OPERATING_BED_ITEM.get())
            .pattern("xxx")
            .pattern(" m ")
            .pattern("zzz")
            .define('x', ItemTags.WOOL)
            .define('m', ItemRegister.ORIGINIUM_INGOT.get())
            .define('z', Tags.Items.INGOTS_IRON)
            .unlockedBy("has_chunk", has(ItemRegister.ORIGINIUM_INGOT.get()))
            .save(consumer)
        ShapelessRecipeBuilder.shapeless(ItemRegister.ETCHED_AMMO.get(), 4)
            .requires(Tags.Items.INGOTS_IRON)
            .requires(ItemRegister.ORIGINIUM_INGOT.get())
            .unlockedBy("has_chunk", has(ItemRegister.ORIGINIUM_INGOT.get()))
            .save(consumer)
    }

}