package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
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
import org.apache.bcel.verifier.structurals.ControlFlowGraph;
import org.apache.bcel.verifier.structurals.InstructionContext;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.sitdh.thesis.core.cotton.database.entity.Project;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodStructureAnalysis extends EmptyVisitor {
	
	private ConstantPoolGen constants;
	
	private MethodGen mg;
	
	private String format;
	
	private Project project;
	
	private JavaClass jc;
	
	private String interestedPackage;
	
	@Getter
	private List<String> structure;
	
	@Getter
	private List<com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph> classControlFlowGraph;
	
	private MethodStructureAnalysis(MethodGen mg, JavaClass jc, Project project) {
	
		this.mg = mg;
		this.constants = mg.getConstantPool();
		this.structure = new ArrayList<String>();
		this.jc = jc;
		this.interestedPackage = project.getInterestedPackage();
		
		this.classControlFlowGraph = Lists.newArrayList();
		
		format = "'M:" + jc.getClassName() + "' -> '%s:%s' [label = '" + mg.getName() + ":%s'];";
	}
	
	public static MethodStructureAnalysis forClass(MethodGen mg, JavaClass jc, Project project) {
		return new MethodStructureAnalysis(mg, jc, project);
	}
	
	public MethodStructureAnalysis analyze() {
		for (InstructionHandle ih = mg.getInstructionList().getStart(); 
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            log.debug("Instruction", i);
            if (!this.instructionFilter(i))
                i.accept(this);
        }
		
		/** Hello **/
		
		Method[] methods = jc.getMethods();
		for (int i = 0; i < methods.length; i++) {
			MethodGen mg = new MethodGen(methods[i], jc.getClassName(), constants);
			LineNumberTable lnt = mg.getLineNumberTable(constants);
			ControlFlowGraph cfg = new ControlFlowGraph(mg);
			
			InstructionContext[] ics = cfg.getInstructionContexts();
			
			StringBuilder sb = new StringBuilder();
			sb.append(
					StringUtils.join(
							Arrays.asList(
									"digraph", 
									methods[i].getName(),
									"{\n"
									), " "));
			sb.append(
					StringUtils.join(
							Arrays.asList(
									"label=\"", 
									methods[i].getName(),
									"\";"
									), " "));
			int pos;
			String instr;
			for (int j = 0; j < ics.length; j++) {
				pos = ics[j].getInstruction().getPosition();
				instr = ics[j].getInstruction().toString().trim();
				
				sb.append(
						StringUtils.join(
								Arrays.asList("\n\t", pos, "[label=\"[LN:" + String.valueOf(lnt.getSourceLine(pos)) + "]", instr + "\"];"), " "));
				
				InstructionContext[] scc = ics[j].getSuccessors();
				int succpos; 
				for (int k = 0; k < scc.length; k++) {
					succpos = scc[k].getInstruction().getPosition();
					sb.append(StringUtils.join(
							Arrays.asList("\n\t", pos, "->", succpos, ";\n"), " "));
				}
			}
			
			sb.append("\n}");
			
			com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph c 
				= new com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph(this.project, jc.getClassName(), methods[i].getName());
			c.setGraph(sb.toString());
			
			classControlFlowGraph.add(c);
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
		log.debug("Append package: ", interestedPackage);
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
