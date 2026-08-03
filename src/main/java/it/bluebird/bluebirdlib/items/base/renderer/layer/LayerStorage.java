package it.bluebird.bluebirdlib.items.base.renderer.layer;

import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimated;
import it.bluebird.bluebirdlib.items.base.renderer.ICustomRenderLayer;
import java.util.HashMap;
import java.util.Map;

public class LayerStorage<T extends IAnimated> {
    private final Map<String,ICustomRenderLayer<T>> layers = new HashMap<>();

    public void addLayer(String id,ICustomRenderLayer<T> layer) {
        layers.put(id,layer);
    }
    public void removeLayer(String id) {
        layers.remove(id);
    }
    public void clearLayers() {
        layers.clear();
    }

    public Map<String,ICustomRenderLayer<T>> getLayers() {
        return layers;
    }
}