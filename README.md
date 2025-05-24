# Formatted-Translations

Adds formatting support to Server-Translations with Placeholder API.

---

## Usage

### Translation

Currently, you cannot format translations by directly using the `Text.translatable` method.
You'll need to use the `formattedTranslatable` method from the `TextOps` class. This is because formatting is somewhat
costly, due to all the tags that need to be parsed and stuff. I'll try to optimise it better in the future, so that
maybe you can use the `Text.translatable` method directly.

_modid/data/lang/en_us.json_:
```json
"your.key": "<blue>Blue says Hello World!</blue>"
```

```java
import blue.beaming.ftranslations.TextOps;

final MutableText message = TextOps.formattedTranslatable("your.key");
getSomePlayer().sendMessage(message); // Sends the message 'Blue says Hello World!' in blue.
```

For a list of tags, see [Placeholder API](https://github.com/Patbox/TextPlaceholderAPI)'s documentation.

## Disclaimer

This is really early in development and also somewhat hacked together (because I didn't understand how codecs worked
\.\_\.), so things might break. If you find a bug, please open an issue.

## To do
1. [x] Basic replacement in `Text` (what you see in-game)
2. [x] Creative players don't break item translations
3. [x] Nested formatted translations work
4. [x] Basic replacement in `Language` (what you see in the console)
5. [ ] Optimise for performance & bandwidth
   1. [ ] Don't render text in the erased argument list to reduce packet size
   2. [ ] Don't fully parse the text where only a `String` is expected (like `SystemDelegatedLanguage`)
6. [ ] Add the ability to change the default tag parser