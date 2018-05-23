package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.util.ArrayList;
import java.util.List;

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

import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class MethodStructureAnalysis extends EmptyVisitor {
	
	private ConstantPoolGen constants;
	
	private MethodGen mg;
	
	private String format;
	
	private String interestedPackage;
	
	@Getter
	private List<String> structure;
	
	private MethodStructureAnalysis(MethodGen mg, String classname, String interestedPackage) {
	
		this.mg = mg;
		this.constants = mg.getConstantPool();
		this.interestedPackage = interestedPackage;
		this.structure = new ArrayList<String>();
		
		
		format = "'M:" + classname + "' -> '%s:%s' [label = '" + mg.getName() + ":%s'];";
	}
	
	public static List<String> forClass(MethodGen mg, String classname, String interestedPackage) {
		return new MethodStructureAnalysis(mg, classname, interestedPackage).analyze().getStructure();
	}
	
	public MethodStructureAnalysis analyze() {
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

	private void appendMessage(SourcecodeStructureConnectionType type, String referenceType, String method) {
		if (referenceType.startsWith(interestedPackage) && !"<init>".equals(method)) {
			
			String msg = String.format(format, type, referenceType, method);
			log.info(msg);
			
			this.structure.add(msg);
			
		} else {
			log.info("Not interested package: " + referenceType);
			
		}
	}

	private boolean instructionFilter(Instruction i) {
		short opcode = i.getOpcode();
		
        return ((InstructionConst.getInstruction(opcode) != null)
                && !(i instanceof ConstantPushInstruction) 
                && !(i instanceof ReturnInstruction));
	}

}
