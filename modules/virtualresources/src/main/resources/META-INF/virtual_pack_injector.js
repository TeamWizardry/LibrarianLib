var MethodInsnNode = org.objectweb.asm.tree.MethodInsnNode;
var VarInsnNode = org.objectweb.asm.tree.VarInsnNode;
var InsnList = org.objectweb.asm.tree.InsnList;
var Opcodes = org.objectweb.asm.Opcodes;

function initializeCoreMod() {
    return {
        "virtual_pack_injector": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/resources/FallbackResourceManager"
            },
            'transformer': function(node) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                for(var m in node.methods) {
                    var method = node.methods[m];
                    if(method.name === "<init>") {
                        var insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0))
                        insnList.add(
                            new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "com/teamwizardry/librarianlib/virtualresources/VirtualResources",
                                "inject", "(Lnet/minecraft/resources/FallbackResourceManager;)V"
                            )
                        );
                        for(var i = 0; i < method.instructions.size(); i++) {
                            var insn = method.instructions.get(i);
                            if(insn.getOpcode() === Opcodes.RETURN) {
                                method.instructions.insertBefore(insn, insnList);
                                break;
                            }
                        }
                        break;
                    }
                }
                return node;
            }
        }
    };
}