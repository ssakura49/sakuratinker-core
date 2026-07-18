package com.ssakura49.sakuratinker.core.network;

import com.ssakura49.sakuratinker.core.network.c2s.ModifierKeyPressPacket;
import com.ssakura49.sakuratinker.core.network.c2s.PlayerLeftClickEmpty;
import com.ssakura49.sakuratinker.core.network.s2c.RefreshAllModifierConfigSpecs;
import com.ssakura49.sakuratinker.core.network.s2c.SLightningPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
   private static final String PROTOCOL_VERSION = "1";
   public static final SimpleChannel INSTANCE = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath("sakuratinker", "main")).networkProtocolVersion(() -> "1").clientAcceptedVersions("1"::equals).serverAcceptedVersions("1"::equals).simpleChannel();
   private static int id = 0;

   public static void init() {
      INSTANCE.messageBuilder(PlayerLeftClickEmpty.class, id++, NetworkDirection.PLAY_TO_SERVER).decoder(PlayerLeftClickEmpty::new).encoder(PlayerLeftClickEmpty::toByte).consumerMainThread(PlayerLeftClickEmpty::handle).add();
      INSTANCE.messageBuilder(ModifierKeyPressPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).decoder(ModifierKeyPressPacket::new).encoder(ModifierKeyPressPacket::toBytes).consumerMainThread(ModifierKeyPressPacket::handle).add();
      INSTANCE.messageBuilder(SLightningPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).decoder(SLightningPacket::decode).encoder(SLightningPacket::encode).consumerMainThread(SLightningPacket::consume).add();
      INSTANCE.messageBuilder(RefreshAllModifierConfigSpecs.class, id++, NetworkDirection.PLAY_TO_CLIENT).decoder(RefreshAllModifierConfigSpecs::new).encoder(RefreshAllModifierConfigSpecs::toByte).consumerMainThread(RefreshAllModifierConfigSpecs::handle).add();
   }

   public static <MSG> void sendToServer(MSG msg) {
      INSTANCE.sendToServer(msg);
   }

   public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player) {
      INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
   }

   public static <MSG> void sendToClient(MSG msg) {
      INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
   }

   public static <MSG> void sendToAll(MSG msg) {
      INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
   }

   public static <MSG> void sentToTrackingChunk(LevelChunk chunk, MSG msg) {
      INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
   }

   public static <MSG> void sentToTrackingEntity(Entity entity, MSG msg) {
      INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
   }

   public static <MSG> void sentToTrackingEntityAndPlayer(Entity entity, MSG msg) {
      INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
   }

   public static <MSG> void sendToClient(ServerPlayer player, MSG msg) {
      INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }
}
