var MethodInsnNode = org.objectweb.asm.tree.MethodInsnNode;
var Opcodes = org.objectweb.asm.Opcodes;

function initializeCoreMod() {
    return {
        "ll.foundation.signtileentity": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/tileentity/SignTileEntity"
            },
            'transformer': function (node) {
                for (var m in node.methods) {
                    var method = node.methods[m];
                    if (method.name === "<init>") {
                        var interceptInsn = new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "com/teamwizardry/librarianlib/foundation/bridge/FoundationSignTileEntityCreator",
                            "interceptSignTileEntityType", "(Lnet/minecraft/tileentity/TileEntityType;)Lnet/minecraft/tileentity/TileEntityType;"
                        );

                        for (var i = 0; i < method.instructions.size(); i++) {
                            var insn = method.instructions.get(i);
                            if (
                                insn.getOpcode() === Opcodes.INVOKESPECIAL &&
                                insn.owner === "net/minecraft/tileentity/TileEntity" &&
                                insn.name === "<init>" &&
                                insn.desc === "(Lnet/minecraft/tileentity/TileEntityType;)V"
                            ) {
                                method.instructions.insertBefore(insn, interceptInsn);
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
