package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourcecodeMethodGraphBuilder extends EmptyVisitor {
	
	private MethodGen mg;
	private ConstantPoolGen constants;
	
	private String format;
	
	private String interestedPackage;
	
	@Getter
	private List<String> traversalStack;
	
	public SourcecodeMethodGraphBuilder(MethodGen mg, 
			JavaClass classname, 
			GraphTraversalSource g,
			String interestedPackage) {
		
		this.mg = mg;
		this.constants = mg.getConstantPool();
		this.interestedPackage = interestedPackage;
		
		traversalStack = new ArrayList<String>();
		
		format = "'M:" + classname.getClassName() + "' -> '%s:%s' [label = '" +mg.getName() + ":%s'];";
		
	}
	
	@SuppressWarnings("unused")
	private String argumentList(Type[] argumentTypes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < argumentTypes.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(argumentTypes[i].toString());
		}
		
		return sb.toString();
	}

	public static SourcecodeMethodGraphBuilder analyzeForMethod(MethodGen mg, 
			JavaClass classname, 
			GraphTraversalSource g,
			String interestedPackage) {
		
		return new SourcecodeMethodGraphBuilder(mg, classname, g, interestedPackage);
	}
	
	public SourcecodeMethodGraphBuilder start() {
		for (InstructionHandle ih = mg.getInstructionList().getStart(); 
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            
            if (!this.instructionFilter(i))
                i.accept(this);
        }
		
		return this;
	}
	
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		log.info("INVOKEVIRTUAL");
		
		this.appendMessage(
				SourcecodeStructureConnectionType.INVOKEVIRTUAL, 
				i.getReferenceType(constants).toString(), 
				i.getMethodName(constants));
		
	}
	
	@Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		log.info("INVOKEINTERFACE");
		this.appendMessage(
				SourcecodeStructureConnectionType.INVOKEINTERFACE, 
				i.getReferenceType(constants).toString(), 
				i.getMethodName(constants));
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        log.info("INVOKESPECIAL");
        
        this.appendMessage(
        		SourcecodeStructureConnectionType.INVOKESPECIAL, 
        		i.getReferenceType(constants).toString(), 
        		i.getMethodName(constants));
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        log.info("INVOKESTATIC");
        
        this.appendMessage(
        		SourcecodeStructureConnectionType.INVOKESTATIC, 
        		i.getReferenceType(constants).toString(), 
        		i.getMethodName(constants));
        
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
        log.info("INVOKEDYNAMIC");
        
        this.appendMessage(
        		SourcecodeStructureConnectionType.INVOKEDYNAMIC, 
        		i.getReferenceType(constants).toString(), 
        		i.getMethodName(constants));
    }
    
    /**
     * Private
     * @param instructionHandle
     * @return
     */

	private boolean instructionFilter(Instruction i) {
		short opcode = i.getOpcode();
		
        return ((InstructionConst.getInstruction(opcode) != null)
                && !(i instanceof ConstantPushInstruction) 
                && !(i instanceof ReturnInstruction));
	}
	
	private void appendMessage(SourcecodeStructureConnectionType type, String referenceType, String method) {
		if (referenceType.startsWith(interestedPackage) && !"<init>".equals(method)) {
			String msg = String.format(format, type, referenceType, method);
			this.traversalStack.add(msg);
		} else {
			log.info("Not interested package: " + referenceType);
			
		}
	}

}
