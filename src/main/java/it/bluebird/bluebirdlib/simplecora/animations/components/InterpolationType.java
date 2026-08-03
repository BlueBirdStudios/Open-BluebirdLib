package it.bluebird.bluebirdlib.simplecora.animations.components;

public enum InterpolationType {
    LINEAR,
    CATMULLROM;

    public static InterpolationType fromString(String type) {
        return switch (type) {
            case "catmullrom" -> CATMULLROM;
            default -> LINEAR;
        };
    }
}
