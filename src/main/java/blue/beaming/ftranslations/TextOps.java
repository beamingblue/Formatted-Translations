package blue.beaming.ftranslations;

import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

public final class TextOps {
    public static final String FORMATTED_KEY = "$ftf:";
    public static final String ERASED_KEY = "$fte:";

    private TextOps() {
    }

    public static Text formattedTranslatable(String key, Object... args) {
        return Text.translatable(FORMATTED_KEY + key, args);
    }

    public static boolean isFormatted(Text text) {
        return isFormatted(text.getContent());
    }

    public static boolean isFormatted(TextContent content) {
        return content instanceof TranslatableTextContent c && c.getKey().startsWith(FORMATTED_KEY);
    }
}
