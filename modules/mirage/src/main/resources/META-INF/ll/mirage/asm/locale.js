var MethodInsnNode = org.objectweb.asm.tree.MethodInsnNode;
var VarInsnNode = org.objectweb.asm.tree.VarInsnNode;
var InsnList = org.objectweb.asm.tree.InsnList;
var Opcodes = org.objectweb.asm.Opcodes;
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

function initializeCoreMod() {
    return {
        "ll.mirage.locale": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/resources/Locale"
            },
            'transformer': function(node) {
                var translateKeyPrivate = ASMAPI.mapMethod("func_135026_c");
                var hasKey = ASMAPI.mapMethod("func_188568_a");

                for(var m in node.methods) {
                    var method = node.methods[m];
                    if(method.name === translateKeyPrivate) {
                        transform_translateKeyPrivate(method)
                    }
                    if(method.name === hasKey) {
                        transform_hasKey(method)
                    }
                }
                return node;
            }
        }
    };
}

//
//   LINENUMBER 84 L0
//   ALOAD 0
//   GETFIELD net/minecraft/client/resources/Locale.properties : Ljava/util/Map;
//   ALOAD 1
//   INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; (itf)
//   CHECKCAST java/lang/String
// + ALOAD 1
// + INVOKESTATIC com/teamwizardry/librarianlib/mirage/Mirage.locale-translatekeyprivate-asm (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
//   ASTORE 2
//
function transform_translateKeyPrivate(method) {
    for(var i = 0; i < method.instructions.size(); i++) {
        var insn = method.instructions.get(i);
        if(insn instanceof VarInsnNode && insn.opcode === Opcodes.ASTORE && insn.var === 2) {
            method.instructions.insertBefore(insn,
                insnList(
                    new VarInsnNode(Opcodes.ALOAD, 1),
                    new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/teamwizardry/librarianlib/mirage/Mirage",
                        "locale-translatekeyprivate-asm", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
                    )
                )
            );
            break;
        }
    }
}

//
//   ALOAD 1
//   INVOKEINTERFACE java/util/Map.containsKey (Ljava/lang/Object;)Z (itf)
// + ALOAD 1
// + INVOKESTATIC com/teamwizardry/librarianlib/mirage/Mirage.locale-haskey-asm (ZLjava/lang/String;)Z
//   IRETURN
//
function transform_hasKey(method) {
    for(var i = 0; i < method.instructions.size(); i++) {
        var insn = method.instructions.get(i);
        if(insn.opcode === Opcodes.IRETURN) {
            method.instructions.insertBefore(insn,
                insnList(
                    new VarInsnNode(Opcodes.ALOAD, 1),
                    new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/teamwizardry/librarianlib/mirage/Mirage",
                        "locale-haskey-asm", "(ZLjava/lang/String;)Z"
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
