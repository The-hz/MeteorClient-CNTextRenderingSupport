package com.cjkfix.mixin;

import net.minecraft.util.DeprecatedLanguageData;
import org.spongepowered.asm.mixin.*;

import java.util.Map;

@Mixin(DeprecatedLanguageData.class)
public class MixinDeprecatedLanguageData {

    /**
     * @author The-hz
     * @reason 屏蔽产生的 "Missing translation key for rename" 刷屏日志
     */
    @Overwrite
    public void apply(Map<String, String> map) {
        DeprecatedLanguageData self = (DeprecatedLanguageData) (Object) this;

        for (String removedKey : self.removed()) {
            map.remove(removedKey);
        }

        self.renamed().forEach((oldKey, newKey) -> {
            String translation = map.remove(oldKey);
            if (translation == null) {
                map.remove(newKey);
            } else {
                map.put(newKey, translation);
            }
        });
    }
}
