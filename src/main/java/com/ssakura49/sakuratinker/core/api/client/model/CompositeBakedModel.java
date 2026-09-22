package com.ssakura49.sakuratinker.core.api.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompositeBakedModel implements BakedModel {
    private final List<BakedQuad> quads;
    public CompositeBakedModel(List<BakedQuad> quads) {
        this.quads = ImmutableList.copyOf(quads);
    }
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return quads;
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
    public TextureAtlasSprite getParticleIcon() {
        return quads.isEmpty() ? null : quads.get(0).getSprite();
    }
    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }
    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
