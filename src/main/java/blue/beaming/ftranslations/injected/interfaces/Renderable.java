package blue.beaming.ftranslations.injected.interfaces;

import net.minecraft.text.Text;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;

public interface Renderable {
    Text ftranslations$render(ServerLanguage language);
}
