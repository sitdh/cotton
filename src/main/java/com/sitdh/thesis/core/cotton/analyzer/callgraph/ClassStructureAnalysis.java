package com.sitdh.thesis.core.cotton.analyzer.callgraph;

import java.util.List;
import java.util.Optional;

import com.sitdh.thesis.core.cotton.analyzer.data.GraphVector;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

import com.google.common.collect.Lists;
import com.sitdh.thesis.core.cotton.analyzer.data.InterestedConstant;
import com.sitdh.thesis.core.cotton.database.entity.ConstantCollection;
import com.sitdh.thesis.core.cotton.database.entity.ControlFlowGraph;
import com.sitdh.thesis.core.cotton.database.entity.Project;
import com.sitdh.thesis.core.cotton.database.entity.Vector;
import com.sitdh.thesis.core.cotton.database.repository.VectorRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassStructureAnalysis extends EmptyVisitor implements Visitor {
	
	private VectorRepository vectorRepo;
	
	@Getter
	private List<ConstantCollection> constantsCollection;
	
	@Getter
	private List<String> structure;
	
	private JavaClass jc;
	
	private ConstantPoolGen constants;
	
	private String graphFormat;

	private final List<Byte> interestedFiledInstructions;
	
	@Getter
	private List<ControlFlowGraph> controlFlowGraphs;

	@Getter
	private List<GraphVector> connections;
	
	private Project project;
	
	public ClassStructureAnalysis(JavaClass jc, Project project, VectorRepository vectorRepo) {
		this.jc = jc;
		constants = new ConstantPoolGen(jc.getConstantPool());
		
		graphFormat = "\"C:" + jc.getClassName() + "\" -> \"C:%s\";";
		
		this.project = project;
		
		log.debug("Log already constructed");
		// Add more
		interestedFiledInstructions = Lists.newArrayList(
				Const.CONSTANT_Class, Const.CONSTANT_Double, 
				Const.CONSTANT_Float, Const.CONSTANT_Integer,
				Const.CONSTANT_String);
		
		this.vectorRepo = vectorRepo;
		
		constantsCollection = Lists.newArrayList();
		controlFlowGraphs = Lists.newArrayList();
		connections = Lists.newArrayList();
		structure = Lists.newArrayList();
		
		jc.getConstantPool().accept(this);
		log.debug("Accept class: " + jc.getClassName());
		log.debug("For Project: " + project.getProjectId());
		
		Method[] methods = jc.getMethods();
		for(int i = 0; i < methods.length; i++) {
			if (!"<init>".equals(methods[i].getName())) 
				methods[i].accept(this);
			
			for(LocalVariable localVar : methods[i].getLocalVariableTable().getLocalVariableTable()) {
				log.debug(Utility.signatureToString(localVar.getSignature()) + " => " + localVar.getName());
				localVar.accept(this);
			}
		}
	}
	
	public ClassStructureAnalysis analyze() {
		return this;
	}

	public static ClassStructureAnalysis forClass(JavaClass jc, Project project, VectorRepository vectorRepo) {
		return new ClassStructureAnalysis(jc, project, vectorRepo);
	}
	
	public void visitMethod(Method method) {
		MethodGen mg = new MethodGen(method, jc.getClassName(), constants);
		
		if (mg.isAbstract() || mg.isNative()) return;
		
		MethodStructureAnalysis msa = MethodStructureAnalysis.forClass(mg, jc, project);
		msa.analyze();
		
		structure.addAll(msa.getStructure());
		controlFlowGraphs.addAll(msa.getClassControlFlowGraph());
		if (msa.getConnections().size() > 0) {
			msa.getConnections().stream().forEach(gv -> {
				Vector v = new Vector(gv);
				v.setProject(project);
				
				vectorRepo.save(v);
			});
//			connections.addAll(msa.getConnections());
		}
	}
	
	public void visitConstantPool(ConstantPool constantPool) {
		for (int i = 0; i < constantPool.getLength(); i++) {
			Constant constant = constantPool.getConstant(i);
			
			if (!this.isConformedToCondition(Optional.ofNullable(constant))) continue;
			
			if (Const.CONSTANT_Class == constant.getTag()) {
				String referencedClass = constantPool.constantToString(constant);
				
				if(!referencedClass.startsWith(project.getInterestedPackage())) continue;
				
				// Add to format
				log.debug("Get " + constantPool.constantToString(constant));
				
				this.structure.add(
						String.format(
								graphFormat,
								constantPool.constantToString(constant)
								)
						);
			} else {
				InterestedConstant c = InterestedConstant.getInterestedConstant(constant.getTag());
				String value = constantPool.constantToString(constant).replaceAll("\"", "");
				log.debug(String.format("Type: %s, value: %s", c.getType(), value));
				
				constantsCollection.add(new ConstantCollection("", c.getType(), value));
			}
			
		}
		
	}
	
	
	
	private boolean isConformedToCondition(Optional<Constant> constant) {
		
		return constant.isPresent() 
				&& this.interestedFiledInstructions.contains(
						constant.get().getTag()
					);
	}

}
