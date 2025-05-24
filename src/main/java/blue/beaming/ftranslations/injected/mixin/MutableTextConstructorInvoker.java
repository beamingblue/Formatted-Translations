package blue.beaming.ftranslations.injected.mixin;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(MutableText.class) public interface MutableTextConstructorInvoker {
    @Invoker("<init>") static MutableText newMutableText(TextContent content, List<Text> siblings, Style style) {
        throw new AssertionError("Invoker body");
    }
}
