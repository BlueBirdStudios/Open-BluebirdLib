package it.bluebird.bluebirdlib.simplecora.animations.components;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimationSet {
    private Map<String, Animation> animations = new HashMap<>();

    public Animation get(String key) {
        if (this.animations.containsKey(key)) {
            return this.animations.get(key);
        }

        String suffix = "." + key;
        for (Map.Entry<String, Animation> entry : this.animations.entrySet()) {
            if (entry.getKey().endsWith(suffix)) {
                return entry.getValue();
            }
        }

        return Animation.EMPTY;
    }
}