package blue.beaming.ftranslations.injected.mixin;

import blue.beaming.ftranslations.FormattedTranslations;
import blue.beaming.ftranslations.TextOps;
import blue.beaming.ftranslations.injected.interfaces.Renderable;
import com.google.common.collect.Lists;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static net.minecraft.text.Text.literal;

@Debug(export = true) @Mixin(value = TranslatableTextContent.class, priority = 1500) public abstract class TranslatableTextContentMixin
        implements Renderable, TextContent {
    @Final @Shadow private String key;
    @Final @Shadow private Object[] args;
    @Unique private Text rendered = null;

    @Shadow @Final private static Pattern ARG_FORMAT;

    @Unique private void ftranslations$bakeRendered(ServerLanguage language) {
        final var list = new LinkedList<Text>();
        if (!this.ftranslations$formattedForEachPart(language.serverTranslations().get(this.key.substring(TextOps.FORMATTED_KEY.length())), v -> list.add((Text) v))) return;
        this.rendered = MutableTextConstructorInvoker.newMutableText(PlainTextContent.EMPTY, list, Style.EMPTY);
    }

    @Unique private boolean ftranslations$formattedForEachPart(String translation, Consumer<StringVisitable> consumer) {
        if (translation == null) {
            return false;
        }

        final var text = FormattedTranslations.TEXT_PARSER.parseText(translation, ParserContext.of());
        this.ftranslations$forEachTree(text, consumer, 0);

        return true;
    }

    @Unique private int ftranslations$forEachTree(Text node, Consumer<StringVisitable> consumer, int index) {
        final var asString = node.getContent().visit(Optional::ofNullable).orElse("");
        final var matcher = ARG_FORMAT.matcher(asString);
        final Consumer<MutableText> withStyle = t -> consumer.accept(t.setStyle(node.getStyle()));

        var current = 0;

        while (matcher.find(current)) {
            final var start = matcher.start();
            final var end = matcher.end();
            if (start > current) {
                final var str = asString.substring(current, start);
                withStyle.accept(literal(str));
            }

            final var format = matcher.group(2);
            if ("%".equals(format)) withStyle.accept(literal("%"));
            else if (!"s".equals(format)) {
                withStyle.accept(literal("%" + format));
            } else {
                final var arg = matcher.group(1);
                final var i = arg == null? index++ : Integer.parseInt(arg) - 1;
                this.ftranslations$getArgSafely(i, node.getStyle())
                    .ifPresentOrElse(consumer, () -> withStyle.accept(arg == null? literal("%s") : literal("%" + (i + 1) + "$s")));
            }

            current = end;
        }

        if (current < asString.length()) {
            String str = asString.substring(current);
            consumer.accept(literal(str).setStyle(node.getStyle()));
        }

        for (final var child : node.getSiblings()) {
            index = this.ftranslations$forEachTree(child, consumer, index);
        }

        return index;
    }

    @Unique private Optional<Text> ftranslations$getArgSafely(int i, Style style) {
        if (i >= 0 && i < this.args.length) {
            Object object = this.args[i];
            if (object instanceof Text text) {
                return Optional.of(MutableTextConstructorInvoker.newMutableText(PlainTextContent.EMPTY, Lists.newArrayList(text), style));
            } else {
                return Optional.of(object == null? literal("null").setStyle(style) : literal(object.toString()).setStyle(style));
            }
        } else {
            return Optional.empty();
        }
    }

    @Override public Text ftranslations$render(ServerLanguage language) {
        if (!TextOps.isFormatted(this)) return null;
        else if (this.rendered == null) {
            this.ftranslations$bakeRendered(language);
            return this.rendered;
        } else return this.rendered;
    }
}
