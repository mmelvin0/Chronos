package chronos.asm;

import chronos.Info;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public class Transformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        boolean obfuscated = !name.equals(transformedName);
        if (transformedName.equals("net.minecraft.client.multiplayer.WorldClient")) {
            return transformWorldClient(bytes, obfuscated);
        } else if (transformedName.equals("net.minecraft.world.WorldServer")) {
            return transformWorldServer(bytes, obfuscated);
        } else {
            return bytes;
        }
    }

    private byte[] transformWorldClient(byte[] bytes, boolean obfuscated) {
        final String METHOD = obfuscated ? "b" : "tick";
        ClassNode node = readClass(bytes);
        for (MethodNode method : node.methods) {
            if (method.name.equals(METHOD) && method.desc.equals("()V")) {
                patchWorldClientTick(method, obfuscated);
            }
        }
        return saveClass(node);
    }

    private byte[] transformWorldServer(byte[] bytes, boolean obfuscated) {
        final String METHOD = obfuscated ? "b" : "tick";
        ClassNode node = readClass(bytes);
        for (MethodNode method : node.methods) {
            if (method.name.equals(METHOD) && method.desc.equals("()V")) {
                patchWorldServerTick(method, obfuscated);
            }
        }
        return saveClass(node);
    }

    private ClassNode readClass(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        return node;
    }

    private byte[] saveClass(ClassNode node) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    private void patchWorldClientTick(MethodNode method, boolean obfuscated) {
        final String WORLD_CLIENT = obfuscated ? "bjf" : "net/minecraft/client/multiplayer/WorldClient";
        final String GET_WORLD_TIME = obfuscated ? "J" : "getWorldTime";
        final String SET_WORLD_TIME = obfuscated ? "b" : "setWorldTime";
        final String TICK_DESC = obfuscated ? "(Lahb;)J" : "(Lnet/minecraft/world/World;)J";
        InsnList search = new InsnList();
        search.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, WORLD_CLIENT, GET_WORLD_TIME, "()J", false));
        search.add(new InsnNode(Opcodes.LCONST_1));
        search.add(new InsnNode(Opcodes.LADD));
        search.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, WORLD_CLIENT, SET_WORLD_TIME, "(J)V", false));
        InsnList replace = new InsnList();
        replace.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chronos/WorldHandler", "tick", TICK_DESC, false));
        replace.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, WORLD_CLIENT, SET_WORLD_TIME, "(J)V", false));
        if (patchInstructions(method.instructions, search, replace)) {
            FMLRelaunchLog.log(Info.MODID, Level.INFO, "Successfully patched WordClient.tick()");
        } else {
            FMLRelaunchLog.log(Info.MODID, Level.ERROR, "Failed to patch WordClient.tick()");
        }
    }

    private void patchWorldServerTick(MethodNode method, boolean obfuscated) {
        final String WORLD_SERVER = obfuscated ? "mt" : "net/minecraft/world/WorldServer";
        final String WORLD_INFO_FIELD = obfuscated ? "x" : "worldInfo";
        final String WORLD_INFO_DESC = obfuscated ? "Lays;" : "Lnet/minecraft/world/storage/WorldInfo;";
        final String WORLD_INFO = obfuscated ? "ays" : "net/minecraft/world/storage/WorldInfo";
        final String GET_WORLD_TIME = obfuscated ? "g" : "getWorldTime";
        final String SET_WORLD_TIME = obfuscated ? "c" : "setWorldTime";
        final String TICK_DESC = obfuscated ? "(Lahb;)J" : "(Lnet/minecraft/world/World;)J";
        InsnList search = new InsnList();
        search.add(new FieldInsnNode(Opcodes.GETFIELD, WORLD_SERVER, WORLD_INFO_FIELD, WORLD_INFO_DESC));
        search.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, WORLD_INFO, GET_WORLD_TIME, "()J", false));
        search.add(new InsnNode(Opcodes.LCONST_1));
        search.add(new InsnNode(Opcodes.LADD));
        search.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, WORLD_INFO, SET_WORLD_TIME, "(J)V", false));
        InsnList replace = new InsnList();
        replace.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "chronos/WorldHandler", "tick", TICK_DESC, false));
        replace.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, WORLD_INFO, SET_WORLD_TIME, "(J)V", false));
        if (patchInstructions(method.instructions, search, replace)) {
            FMLRelaunchLog.log(Info.MODID, Level.INFO, "Successfully patched WorldServer.tick()");
        } else {
            FMLRelaunchLog.log(Info.MODID, Level.ERROR, "Failed to patch WorldServer.tick()");
        }
    }

    private boolean patchInstructions(InsnList subject, InsnList search, InsnList replace) {
        List<AbstractInsnNode> found = new ArrayList<AbstractInsnNode>();
        int subjectSize = subject.size();
        int searchSize = search.size();
        for (int i = 0; i < subjectSize; i++) {
            found.clear();
            for (int j = 0; j < searchSize && i + j < subjectSize; j++) {
                AbstractInsnNode a1 = subject.get(i + j);
                AbstractInsnNode a2 = search.get(j);
                if (a1.getOpcode() != a2.getOpcode()) {
                    break;
                }
                switch (a2.getOpcode()) {
                    case Opcodes.DUP:
                    case Opcodes.LADD:
                    case Opcodes.LCONST_1:
                        found.add(a1);
                        continue;
                    case Opcodes.ALOAD:
                        if (((VarInsnNode)a1).var == ((VarInsnNode)a2).var) {
                            found.add(a1);
                            continue;
                        }
                        break;
                    case Opcodes.NEW:
                        if (((TypeInsnNode)a1).desc.equals(((TypeInsnNode)a2).desc)) {
                            found.add(a1);
                            continue;
                        }
                        break;
                    case Opcodes.GETFIELD:
                        FieldInsnNode f1 = (FieldInsnNode)a1;
                        FieldInsnNode f2 = (FieldInsnNode)a2;
                        if (f1.owner.equals(f2.owner) && f1.name.equals(f2.name) && f1.desc.equals(f2.desc)) {
                            found.add(a1);
                            continue;
                        }
                        break;
                    case Opcodes.INVOKESPECIAL:
                    case Opcodes.INVOKEVIRTUAL:
                        MethodInsnNode m1 = (MethodInsnNode)a1;
                        MethodInsnNode m2 = (MethodInsnNode)a2;
                        if (m1.owner.equals(m2.owner) && m1.name.equals(m2.name) && m1.desc.equals(m2.desc) && m1.itf == m2.itf) {
                            found.add(a1);
                            continue;
                        }
                        break;
                }
                break;
            }
            if (found.size() == searchSize) {
                break;
            }
        }
        if (found.size() == searchSize) {
            subject.insert(found.get(0), replace);
            for (AbstractInsnNode node : found) {
                subject.remove(node);
            }
            return true;
        } else {
            return false;
        }
    }

}
