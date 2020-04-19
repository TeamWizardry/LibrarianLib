function initializeCoreMod() {
    return {
        "asmenvcheck": {
            "target": {
                "type": "METHOD",
                "class": "com.teamwizardry.librarianlib.core.bridge.ASMEnvCheckTarget",
                "methodName": "isPatched",
                "methodDesc": "()Z"
            },
            'transformer': function(method) {
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var arrayLength = method.instructions.size();
                for (var i = 0; i < arrayLength; ++i) {
                    var instruction = method.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.ICONST_0) {
                        var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                        var newInstruction = new InsnNode(Opcodes.ICONST_1);
                        method.instructions.insertBefore(instruction, newInstruction);
                        method.instructions.remove(instruction);
                        break;
                    }
                }
                return method;
            }
        }
    };
}
