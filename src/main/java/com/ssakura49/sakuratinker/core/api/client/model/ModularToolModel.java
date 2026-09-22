package com.ssakura49.sakuratinker.core.api.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * 模块化工具模型加载器
 * 对应 JSON 中的 "loader": "modid:modular_tool"
 */
public class ModularToolModel implements IUnbakedGeometry<ModularToolModel> {
    public static final IGeometryLoader<ModularToolModel> LOADER = (json, context) -> deserialize(json, context);
    private final ResourceLocation baseModelLocation;
    private static final Map<String, Integer> INDEX_CACHE = new HashMap<>();
    private final List<PartDefinition> parts;
    public ModularToolModel(IGeometryBakingContext context, ResourceLocation baseModelLocation) {
        this.baseModelLocation = baseModelLocation;
        this.parts = new ArrayList<>();
    }

    public ModularToolModel(List<PartDefinition> parts, ResourceLocation baseModelLocation) {
        this.parts = parts;
        this.baseModelLocation = baseModelLocation;
    }
    public ModularToolModel(ResourceLocation baseModelLocation) {
        this.baseModelLocation = baseModelLocation;
        this.parts = new ArrayList<>();
    }
    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        BlockModel baseBlockModel = null;
        if (this.baseModelLocation != null) {
            UnbakedModel unbaked = baker.getModel(this.baseModelLocation);
            if (unbaked instanceof BlockModel) {
                baseBlockModel = (BlockModel) unbaked;
            }
        }
        List<PartBakedModel> bakedParts = new ArrayList<>();
        for (PartDefinition part : parts) {
            BakedModel partModel = null;
            if (part.modelLocation() != null) {
                partModel = baker.bake(part.modelLocation(), modelTransform, spriteGetter);
            }
            else if (part.filter() != null && baseBlockModel != null) {
                int index = getElementIndexByName(this.baseModelLocation, part.filter());
                if (index >= 0 && index < baseBlockModel.getElements().size()) {
                    BlockElement element = baseBlockModel.getElements().get(index);
                    BlockModel subModel = new BlockModel(
                            baseBlockModel.getParentLocation(),
                            Collections.singletonList(element),
                            baseBlockModel.textureMap,
                            baseBlockModel.hasAmbientOcclusion(),
                            baseBlockModel.getGuiLight(),
                            ItemTransforms.NO_TRANSFORMS,
                            Collections.emptyList()
                    );
                    ResourceLocation subModelId = ResourceLocation.fromNamespaceAndPath(
                            this.baseModelLocation.getNamespace(),
                            this.baseModelLocation.getPath() + "/" + index
                    );
                    partModel = subModel.bake(baker, subModel, spriteGetter, modelTransform, subModelId, true);
                }
            }
            if (partModel != null) {
                bakedParts.add(new PartBakedModel(part, partModel));
            }
        }
        return new ModularBakedModel(bakedParts, owner.getTransforms());
    }

    private int getElementIndexByName(ResourceLocation modelLoc, String targetName) {
        String cacheKey = modelLoc.toString() + "_" + targetName;
        if (INDEX_CACHE.containsKey(cacheKey)) {
            return INDEX_CACHE.get(cacheKey);
        }
        ResourceLocation jsonLoc = modelLoc.withSuffix(".json");
        Optional<Resource> resourceOpt = Minecraft.getInstance().getResourceManager().getResource(jsonLoc);

        if (resourceOpt.isEmpty()) {
            return -1;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceOpt.get().open(), StandardCharsets.UTF_8))) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);

            if (json.has("elements")) {
                JsonArray elements = json.getAsJsonArray("elements");
                for (int i = 0; i < elements.size(); i++) {
                    JsonObject elObj = elements.get(i).getAsJsonObject();
                    if (elObj.has("name") && targetName.equals(elObj.get("name").getAsString())) {
                        INDEX_CACHE.put(cacheKey, i); // 存入缓存
                        return i;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
    public static ModularToolModel deserialize(JsonObject json, JsonDeserializationContext context) {
        List<PartDefinition> list = new ArrayList<>();
        ResourceLocation baseModel = null;
        if (json.has("model")) {
            baseModel = ResourceLocation.parse(GsonHelper.getAsString(json, "model"));
        }
        if (json.has("parts")) {
            JsonArray partsArray = json.getAsJsonArray("parts");
            for (int i = 0; i < partsArray.size(); i++) {
                list.add(PartDefinition.fromJson(partsArray.get(i).getAsJsonObject()));
            }
        }
        return new ModularToolModel(list, baseModel);
    }

    public record PartBakedModel(PartDefinition definition, BakedModel baseModel) {}


    public static class ModularBakedModel implements BakedModel {
        private final List<PartBakedModel> parts;
        private final ItemTransforms transforms;

        private final Cache<AssemblyKey, BakedModel> modelCache = CacheBuilder.newBuilder().maximumSize(50).build();
        public ModularBakedModel(List<PartBakedModel> parts, ItemTransforms transforms) {
            this.parts = parts;
            this.transforms = transforms;
        }
        @Override
        public ItemOverrides getOverrides() {
            return new ModularOverrideHandler(this.parts, this.modelCache);
        }

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(@Nullable net.minecraft.world.level.block.state.BlockState state, @Nullable net.minecraft.core.Direction direction, net.minecraft.util.RandomSource rand) {
            return List.of();
        }
        @Override
        public boolean useAmbientOcclusion() { return true; }
        @Override
        public boolean isGui3d() { return false; }
        @Override
        public boolean usesBlockLight() { return false; }
        @Override
        public boolean isCustomRenderer() { return false; }
        @Override
        public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon() {
            return parts.isEmpty() ? null : parts.get(0).baseModel().getParticleIcon();
        }
        @Override
        public ItemTransforms getTransforms() {
            return transforms;
        }
    }
    public static class ModularOverrideHandler extends ItemOverrides {
        private final List<PartBakedModel> parts;
        private final Cache<AssemblyKey, BakedModel> cache;
        public ModularOverrideHandler(List<PartBakedModel> parts, Cache<AssemblyKey, BakedModel> cache) {
            this.parts = parts;
            this.cache = cache;
        }
        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            AssemblyKey key = new AssemblyKey();
            for (PartBakedModel part : parts) {
                MaterialVariantId matId = getMaterialForPart(stack, part.definition());
                key.put(part.definition().name(), matId);
            }
            try {
                return cache.get(key, () -> assembleModel(key));
            } catch (ExecutionException e) {
                e.printStackTrace();
                return originalModel;
            }
        }
        private BakedModel assembleModel(AssemblyKey key) {
            List<net.minecraft.client.renderer.block.model.BakedQuad> allQuads = new ArrayList<>();
            for (PartBakedModel part : parts) {
                String partName = part.definition().name();
                MaterialVariantId matId = key.get(partName);

                int color = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(matId)
                        .map(info -> info.vertexColor())
                        .orElse(-1);
                List<net.minecraft.client.renderer.block.model.BakedQuad> partQuads = new ArrayList<>();
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    partQuads.addAll(part.baseModel().getQuads(null, dir, net.minecraft.util.RandomSource.create()));
                }
                partQuads.addAll(part.baseModel().getQuads(null, null, net.minecraft.util.RandomSource.create()));
                List<net.minecraft.client.renderer.block.model.BakedQuad> processedQuads = partQuads;
                if (color != -1) {
                    IQuadTransformer colorTransformer = QuadTransformers.applyingColor(color);
                    processedQuads = colorTransformer.process(processedQuads);
                }

                if (!part.definition().transform().isIdentity()) {
                    IQuadTransformer transformTransformer = QuadTransformers.applying(part.definition().transform());
                    processedQuads = transformTransformer.process(processedQuads);
                }
                allQuads.addAll(processedQuads);
            }
            return new CompositeBakedModel(allQuads);
        }

        private MaterialVariantId getMaterialForPart(ItemStack stack, PartDefinition part) {
            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("tic_materials", 9)) {
                    ListTag materialsList = tag.getList("tic_materials", Tag.TAG_STRING); // 8 是 String 类型

                    if (part.index() >= 0 && part.index() < materialsList.size()) {
                        String matIdStr = materialsList.getString(part.index());
                        return MaterialVariantId.tryParse(matIdStr);
                    }
                }
            }
            return MaterialVariantId.tryParse("tconstruct:wood");
        }
    }
    public static class AssemblyKey {
        private final Map<String, MaterialVariantId> materials = new HashMap<>();
        public void put(String partName, MaterialVariantId matId) {
            materials.put(partName, matId);
        }
        public MaterialVariantId get(String partName) {
            return materials.get(partName);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssemblyKey that = (AssemblyKey) o;
            return materials.equals(that.materials);
        }
        @Override
        public int hashCode() {
            return materials.hashCode();
        }
    }
}
