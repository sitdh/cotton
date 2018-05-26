package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassStructureAnalysis extends EmptyVisitor {
	
	private JavaClass jc;
	
	private ConstantPoolGen constants;
	
	private String graphFormat;
	
	@Getter
	private List<String> structure;
	
	private String interestedPackage;
	
	public ClassStructureAnalysis(JavaClass jc, String interestedPackage) {
		this.jc = jc;
		constants = new ConstantPoolGen(jc.getConstantPool());
		
		graphFormat = "\"C:" + jc.getClassName() + "\" -> \"C:%s\";";
		
		structure = new ArrayList<String>();
		
		this.interestedPackage = interestedPackage;
		
		log.debug("Log already constructed");
	}
	
	public ClassStructureAnalysis analyze() {
		
		jc.getConstantPool().accept(this);
		log.info("Accept class: " + jc.getClassName());
		
		Method[] methods = jc.getMethods();
		for(int i = 0; i < methods.length; i++) {
			if (!"<init>".equals(methods[i].getName())) 
				methods[i].accept(this);
		}
		
		return this;
	}

	public static List<String> forClass(JavaClass jc, String interestedPackage) {
		return new ClassStructureAnalysis(jc, interestedPackage).analyze().getStructure();
	}
	
	public void visitMethod(Method method) {
		MethodGen mg = new MethodGen(method, jc.getClassName(), constants);
		
		if (mg.isAbstract() || mg.isNative()) return;
		
		List<String> g = MethodStructureAnalysis.forClass(mg, jc.getClassName(), interestedPackage);
		
		structure.addAll(g);
	}
	
	public void visitConstantPool(ConstantPool constantPool) {
		for (int i = 0; i < constantPool.getLength(); i++) {
			Constant constant = constantPool.getConstant(i);
			
			if (!this.isConformedToCondition(Optional.ofNullable(constant))) continue;
			
			String referencedClass = constantPool.constantToString(constant);
			
			if(!referencedClass.startsWith(interestedPackage)) continue;
			
			// Add to format
			log.info("Get " + constantPool.constantToString(constant));
			
			this.structure.add(
					String.format(
							graphFormat, 
							constantPool.constantToString(constant)
							)
					);
		}
		
	}
	
	private boolean isConformedToCondition(Optional<Constant> constant) {
		return constant.isPresent() && 7 == constant.get().getTag();
	}

}
