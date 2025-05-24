package blue.beaming.ftranslations.injected.mixin;

import blue.beaming.ftranslations.FormattedTranslations;
import blue.beaming.ftranslations.TextOps;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.placeholders.api.ParserContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.language.SystemDelegatedLanguage;

@Mixin(SystemDelegatedLanguage.class) public abstract class SystemDelegatedLanguageMixin {
    @Shadow(remap = false) protected abstract ServerLanguage getSystemLanguage();

    @ModifyVariable(method = "hasTranslation", at = @At("HEAD"), remap = false, index = 1, argsOnly = true)
    private String ftranslations$unformatForStringifiedHasKey(String original) {
        return original.startsWith(TextOps.FORMATTED_KEY)? original.substring(TextOps.FORMATTED_KEY.length()) : original;
    }

    @ModifyArg(method = "get(Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lxyz/nucleoid/server/translations/api/language/TranslationAccess;getOrNull(Ljava/lang/String;)Ljava/lang/String;"), remap = false, index = 0)
    private String ftranslations$unformatForStringifiedGetOrNull(String original) {
        return original.startsWith(TextOps.FORMATTED_KEY)? original.substring(TextOps.FORMATTED_KEY.length()) : original;
    }

    @ModifyArg(method = "get(Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"), remap = false, index = 1)
    private String ftranslations$unformatForStringifiedGet(String ignored, @Local(argsOnly = true) String original) {
        if (original.startsWith(TextOps.FORMATTED_KEY)) {
            final var key = original.substring(TextOps.FORMATTED_KEY.length());
            final var translation = this.getSystemLanguage().serverTranslations().getOrNull(key);
            return translation == null? key : FormattedTranslations.TEXT_PARSER.parseText(translation, ParserContext.of()).getString();
        } else return original;
    }
}
