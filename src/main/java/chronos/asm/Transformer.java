package chronos.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return bytes;
    }

}
