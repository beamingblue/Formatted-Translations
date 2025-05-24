package blue.beaming.ftranslations.injected.mixin;

import blue.beaming.ftranslations.TextOps;
import blue.beaming.ftranslations.injected.interfaces.Renderable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

import java.util.ArrayList;

import static blue.beaming.ftranslations.injected.mixin.MutableTextConstructorInvoker.newMutableText;

@Debug(export = true) @Mixin(TextCodecs.class) public class TextCodecsMixin {
    @Unique private static final int INDEX = TextOps.ERASED_KEY.length();

    @ModifyVariable(method = "createCodec", at = @At("HEAD"), argsOnly = true)
    private static Codec<Text> ftranslations$eraseRecursive(Codec<Text> original) {
        return ftranslations$commonErasedCodec(original);
    }

    @ModifyExpressionValue(remap = false, method = "<clinit>", at = @At(value = "INVOKE",
                                                                        target = "Lcom/mojang/serialization/Codec;recursive(Ljava/lang/String;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<Text> ftranslations$eraseElsewhere(Codec<Text> original) {
        return ftranslations$commonErasedCodec(original);
    }

    @Unique private static Codec<Text> ftranslations$commonErasedCodec(Codec<Text> original) {
        return original.xmap(text -> {
            if (text.getContent() instanceof TranslatableTextContent translatable
                    && translatable.getKey().startsWith(TextOps.ERASED_KEY)) {
                return newMutableText(
                        new TranslatableTextContent(TextOps.FORMATTED_KEY + translatable.getKey().substring(INDEX), null, translatable.getArgs()),
                        text.getSiblings().size() > 1? text.getSiblings().subList(1, text.getSiblings().size()) : new ArrayList<>(),
                        text.getStyle());
            } else return text;
        }, text -> {
            if (text.getContent() instanceof Renderable r
                    && text.getContent() instanceof TranslatableTextContent translatable
                    && TextOps.isFormatted(text)) {
                final var language = ftranslations$getLanguage();
                if (language == null) return text;

                final var rendered = r.ftranslations$render(language);
                if (rendered == null) return newMutableText(
                        new TranslatableTextContent(translatable.getKey().substring(TextOps.FORMATTED_KEY.length()), "", translatable.getArgs()),
                        text.getSiblings().size() > 1? text.getSiblings().subList(1, text.getSiblings().size()) : new ArrayList<>(),
                        text.getStyle()
                );

                final var mutable = newMutableText(
                        new TranslatableTextContent(TextOps.ERASED_KEY + translatable.getKey().substring(TextOps.FORMATTED_KEY.length()), "",
                                                    translatable.getArgs()),
                        new ArrayList<>(text.getSiblings().size() + 1),
                        text.getStyle());
                mutable.getSiblings().addFirst(rendered);
                mutable.getSiblings().addAll(text.getSiblings());
                return mutable;
            } else return text;
        });
    }

    @Unique private static ServerLanguage ftranslations$getLanguage() {
        final var l = ServerTranslations.TRANSLATION_CONTEXT.get();
        if (l != null) return l;
        final var t = LocalizationTarget.forPacket();
        return t == null? null : t.getLanguage();
    }

}
