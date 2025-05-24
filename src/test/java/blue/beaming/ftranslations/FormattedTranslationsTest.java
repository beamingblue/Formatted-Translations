package blue.beaming.ftranslations;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import static blue.beaming.ftranslations.TextOps.formattedTranslatable;

public class FormattedTranslationsTest implements ModInitializer {
    @Override public void onInitialize() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, base, real, blocked) -> {
            if (entity instanceof ServerPlayerEntity player) {
                final var m = player.getServer().getPlayerManager();
                final var message1 = formattedTranslatable("test.static");
                final var message2 = formattedTranslatable("test.vars", player.getName(), real);
                final var message3 = formattedTranslatable("test.more_vars", player.getName(), real, source.getName());
                final var message4 = formattedTranslatable("test.even_more_vars", player.getName(), real, source.getName());
                m.broadcast(message1, false);
                m.broadcast(message2, false);
                m.broadcast(message3, false);
                m.broadcast(message4, false);
            }
        });
    }
}
