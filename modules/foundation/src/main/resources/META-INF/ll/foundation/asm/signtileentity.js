var MethodInsnNode = org.objectweb.asm.tree.MethodInsnNode;
var Opcodes = org.objectweb.asm.Opcodes;

function initializeCoreMod() {
    return {
        "ll.mirage.fallbackresourcemanager": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/tileentity/SignTileEntity"
            },
            'transformer': function (node) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var signFieldName = ASMAPI.mapField('field_200978_i')

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
                                insn.getOpcode() === Opcodes.GETSTATIC &&
                                insn.owner === "net/minecraft/tileentity/TileEntityType" &&
                                insn.name === signFieldName
                            ) {
                                method.instructions.insert(insn, interceptInsn);
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
