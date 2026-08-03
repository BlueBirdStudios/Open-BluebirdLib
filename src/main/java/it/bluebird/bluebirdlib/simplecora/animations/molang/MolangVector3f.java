package it.bluebird.bluebirdlib.simplecora.animations.molang;

import lombok.Data;
import lombok.AllArgsConstructor;
import org.joml.Vector3f;

@Data
@AllArgsConstructor
public class MolangVector3f {
    private String x;
    private String y;
    private String z;

    public Vector3f calculate(float animTime) {
        float computedX = MolangParser.calculate(this.x, animTime);
        float computedY = MolangParser.calculate(this.y, animTime);
        float computedZ = MolangParser.calculate(this.z, animTime);

        return new Vector3f(computedX, computedY, computedZ);
    }
}