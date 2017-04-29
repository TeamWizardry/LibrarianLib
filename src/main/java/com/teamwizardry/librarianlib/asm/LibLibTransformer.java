package com.teamwizardry.librarianlib.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

// Boilerplate code taken with love from Vazkii's Quark mod
// Quark is distrubted at https://github.com/Vazkii/Quark

public class LibLibTransformer implements IClassTransformer, Opcodes {

    private static final String ASM_HOOKS = "com/teamwizardry/librarianlib/asm/LibLibAsmHooks";

    private static final Map<String, Transformer> transformers = new HashMap<>();

    public static final ClassnameMap CLASS_MAPPINGS = new ClassnameMap(
            "net/minecraft/item/ItemStack", "afj",
            "net/minecraft/client/renderer/block/model/IBakedModel", "cbh",
            "net/minecraft/client/renderer/RenderItem", "bve"
    );


    static {
        transformers.put("net.minecraft.client.renderer.RenderItem", LibLibTransformer::transformRenderItem);
    }

    private static byte[] transformRenderItem(byte[] basicClass) {
        log("Transforming RenderItem");
        MethodSignature sig = new MethodSignature("renderItem", "func_180454_a", "a",
                "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");

        MethodSignature target = new MethodSignature("renderModel", "func_175045_a", "a",
                "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");

        return transform(basicClass, sig, combine(
                (AbstractInsnNode node) -> { // Filter
                    return node.getOpcode() == INVOKEVIRTUAL && target.matches((MethodInsnNode) node);
                }, (MethodNode method, AbstractInsnNode node) -> { // Action
                    InsnList newInstructions = new InsnList();

                    newInstructions.add(new VarInsnNode(ALOAD, 1));
                    newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "renderGlow", "(Lnet/minecraft/item/ItemStack;)V", false));

                    method.instructions.insert(node, newInstructions);
                    return true;
                }));
    }


    // BOILERPLATE =====================================================================================================

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformers.containsKey(transformedName))
            return transformers.get(transformedName).apply(basicClass);

        return basicClass;
    }

    private static byte[] transform(byte[] basicClass, MethodSignature sig, MethodAction action) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        log("Applying Transformation to method (" + sig + ")");
        boolean didAnything = findMethodAndTransform(node, sig, action);

        if(didAnything) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction pred) {
        for(MethodNode method : node.methods) {
            if(sig.matches(method)) {
                log("Located Method, patching...");

                boolean finish = pred.test(method);
                log("Patch result: " + finish);

                return finish;
            }
        }

        return false;
    }

    public static MethodAction combine(NodeFilter filter, NodeAction action) {
        return (MethodNode mnode) -> applyOnNode(mnode, filter, action);
    }

    public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
        Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

        boolean didAny = false;
        while(iterator.hasNext()) {
            AbstractInsnNode anode = iterator.next();
            if(filter.test(anode)) {
                didAny = true;
                if(action.test(method, anode))
                    break;
            }
        }

        return didAny;
    }

    private static void log(String str) {
        FMLLog.info("[Quark ASM] %s", str);
    }

    private static void prettyPrint(AbstractInsnNode node) {
        Printer printer = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        log(sw.toString().replaceAll("\n", ""));
    }

    private static class MethodSignature {
        private final String funcName, srgName, obfName, funcDesc, obfDesc;

        public MethodSignature(String funcName, String srgName, String obfName, String funcDesc) {
            this.funcName = funcName;
            this.srgName = srgName;
            this.obfName = obfName;
            this.funcDesc = funcDesc;
            this.obfDesc = obfuscate(funcDesc);
        }

        @Override
        public String toString() {
            return "Names [" + funcName + ", " + srgName + ", " + obfName + "] Descriptor " + funcDesc + " / " + obfDesc;
        }

        private static String obfuscate(String desc) {
            for(String s : CLASS_MAPPINGS.keySet())
                if(desc.contains(s))
                    desc = desc.replaceAll(s, CLASS_MAPPINGS.get(s));

            return desc;
        }

        private boolean matches(String methodName, String methodDesc) {
            return (methodName.equals(funcName) || methodName.equals(obfName) || methodName.equals(srgName))
                    && (methodDesc.equals(funcDesc) || methodDesc.equals(obfDesc));
        }

        private boolean matches(MethodNode method) {
            return matches(method.name, method.desc);
        }

        private boolean matches(MethodInsnNode method) {
            return matches(method.name, method.desc);
        }

    }

    // Basic interface aliases to not have to clutter up the code with generics over and over again
    private interface Transformer extends Function<byte[], byte[]> { }
    private interface MethodAction extends Predicate<MethodNode> { }
    private interface NodeFilter extends Predicate<AbstractInsnNode> { }
    private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> { }
}
