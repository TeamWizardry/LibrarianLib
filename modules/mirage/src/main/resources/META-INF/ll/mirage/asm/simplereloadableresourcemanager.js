var MethodInsnNode = org.objectweb.asm.tree.MethodInsnNode;
var FieldInsnNode = org.objectweb.asm.tree.FieldInsnNode;
var TypeInsnNode = org.objectweb.asm.tree.TypeInsnNode;
var VarInsnNode = org.objectweb.asm.tree.VarInsnNode;
var InsnNode = org.objectweb.asm.tree.InsnNode;
var InsnList = org.objectweb.asm.tree.InsnList;
var Opcodes = org.objectweb.asm.Opcodes;
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var methodNames = [
    ASMAPI.mapMethod("func_199002_a"), // SimpleReloadableResourceManager.getResource
    ASMAPI.mapMethod("func_219533_b"), // SimpleReloadableResourceManager.hasResource
    ASMAPI.mapMethod("func_199004_b"), // SimpleReloadableResourceManager.getAllResources
    ASMAPI.mapMethod("func_195758_a")  // SimpleReloadableResourceManager.getAllResourceLocations
];

function initializeCoreMod() {
    return {
        "ll.mirage.simplereloadableresourcemanager": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/resources/SimpleReloadableResourceManager"
            },
            'transformer': function(node) {
                for(var m in node.methods) {
                    var method = node.methods[m];
                    if(methodNames.indexOf(method.name) >= 0) {
                        transform(method)
                    }
                }
                return node;
            }
        }
    };
}

//
//   ALOAD 0
//   GETFIELD net/minecraft/resources/SimpleReloadableResourceManager.namespaceResourceManagers : Ljava/util/Map;
//   ALOAD 1
//   INVOKEVIRTUAL net/minecraft/util/ResourceLocation.getNamespace ()Ljava/lang/String;
//   INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; (itf)
//   CHECKCAST net/minecraft/resources/IResourceManager
// + ALOAD 0
// + GETFIELD net/minecraft/resources/SimpleReloadableResourceManager.type : Lnet/minecraft/resources/ResourcePackType;
// + INVOKESTATIC com/teamwizardry/librarianlib/mirage/Mirage.simplereloadableresourcemanager-namespace_fallback-asm (Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/resources/ResourcePackType;)Lnet/minecraft/resources/IResourceManager;
//   ASTORE 2
//
function transform(method) {
    for(var i = 0; i < method.instructions.size(); i++) {
        var insn = method.instructions.get(i);
        if(insn instanceof TypeInsnNode && insn.desc === "net/minecraft/resources/IResourceManager") {
            method.instructions.insert(insn,
                insnList(
                    new VarInsnNode(Opcodes.ALOAD, 0),
                    new FieldInsnNode(Opcodes.GETFIELD,
                        "net/minecraft/resources/SimpleReloadableResourceManager",
                        ASMAPI.mapField("field_199017_f"), // SimpleReloadableResourceManager.type
                        "Lnet/minecraft/resources/ResourcePackType;"
                    ),
                    new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/teamwizardry/librarianlib/mirage/Mirage",
                        "simplereloadableresourcemanager-namespace_fallback-asm", "(Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/resources/ResourcePackType;)Lnet/minecraft/resources/IResourceManager;"
                    )
                )
            );
            break;
        }
    }
}

function insnList() {
    var list = new InsnList();
    for(var i = 0; i < arguments.length; i++) {
        list.add(arguments[i]);
    }
    return list
}
