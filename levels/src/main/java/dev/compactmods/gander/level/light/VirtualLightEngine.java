package dev.compactmods.gander.level.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class VirtualLightEngine extends LevelLightEngine {
	private final LayerLightEventListener blockListener;
	private final LayerLightEventListener skyListener;

	public VirtualLightEngine(ToIntFunction<BlockPos> blockLightFunc, ToIntFunction<BlockPos> skyLightFunc, Supplier<BlockGetter> level) {
		// TODO - VirtualLightChunkGetter?
		super(new LightChunkGetter() {
			@Override
			@Nullable
			public LightChunk getChunkForLighting(int x, int z) {
				return null;
			}

			@Override
			public @NotNull BlockGetter getLevel() {
				return level.get();
			}
		}, false, false);

		blockListener = new VirtualLayerLightEventListener(blockLightFunc);
		skyListener = new VirtualLayerLightEventListener(skyLightFunc);
	}

	@Override
	public LayerLightEventListener getLayerListener(LightLayer layer) {
		return layer == LightLayer.BLOCK ? blockListener : skyListener;
	}

	@Override
	public int getRawBrightness(BlockPos pos, int amount) {
		int i = skyListener.getLightValue(pos) - amount;
		int j = blockListener.getLightValue(pos);
		return Math.max(j, i);
	}

	private record VirtualLayerLightEventListener(ToIntFunction<BlockPos> lightFunc) implements LayerLightEventListener
	{

		@Override
		public void checkBlock(BlockPos pos)
		{
		}

		@Override
		public boolean hasLightWork()
		{
			return false;
		}

		@Override
		public int runLightUpdates()
		{
			return 0;
		}

		@Override
		public void updateSectionStatus(SectionPos pos, boolean isSectionEmpty)
		{
		}

		@Override
		public void setLightEnabled(ChunkPos pos, boolean lightEnabled)
		{
		}

		@Override
		public void propagateLightSources(ChunkPos pos)
		{
		}

		@Override
		public DataLayer getDataLayerData(SectionPos pos)
		{
			return null;
		}

		@Override
		public int getLightValue(BlockPos pos)
		{
			return lightFunc.applyAsInt(pos);
		}
	}
}
