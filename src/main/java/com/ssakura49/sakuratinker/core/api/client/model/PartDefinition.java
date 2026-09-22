package com.ssakura49.sakuratinker.core.api.client.model;

import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record PartDefinition(
        String name,
        int index,
        ResourceLocation modelLocation,
        String filter,
        Transformation transform
) {
    @SuppressWarnings("removal")
    public static PartDefinition fromJson(JsonObject json) {
        String name = GsonHelper.getAsString(json, "name");
        int index = GsonHelper.getAsInt(json, "index", 0);

        ResourceLocation location = null;
        if (json.has("model")) {
            location = new ResourceLocation(GsonHelper.getAsString(json, "model"));
        }
        String filter = null;
        if (json.has("filter")) {
            filter = GsonHelper.getAsString(json, "filter");
        }
        if (location == null && filter == null) {
            filter = name;
        }
        Transformation transform = Transformation.identity();
        return new PartDefinition(name, index, location, filter, transform);
    }
}