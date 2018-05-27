package com.sitdh.thesis.core.cotton.analyzer.service.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Method;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Log
@Service
@PropertySource("classpath:/graph.properties")
public class LocationUtils {
	
	@Value("${graph.app.jenkins.workspace-location}")
	private String projectWorkspace;
	
	@Value("${graph.app.project.classes}")
	private String classLocation;
	
	public String getProjectWorkspace(String slug, String branch) throws FileNotFoundException {
		String projectname = String.format("%s_%s-", slug, branch);
		
		log.info("Project name: " + projectname);
		
		File[] f = new File(projectWorkspace).listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				log.info("File name: " + pathname.getName());
				return pathname.isDirectory() 
						&& pathname.getName().startsWith(projectname) 
						&& !pathname.getName().contains("@");
			}
			
		});
		if (f.length != 1) throw new FileNotFoundException("Project location not found");
		
		log.info(f[0].getAbsolutePath());
		
		return f[0].getAbsolutePath();
	}

	public List<Path> listClassFiles(String slug, String branch) throws IOException {
		String projectWorkspace = this.getProjectWorkspace(slug, branch);

		List<Path> classFiles = new ArrayList<Path>();
		
		FileUtils
			.listFiles(new File(projectWorkspace), new String[] {"class"}, true)
			.stream()
			.forEach(p -> classFiles.add(Paths.get(p.getPath())));
		
		classFiles.stream().forEach(c -> log.info(c.toString()));
		
		return classFiles;
	}
	
	public Optional<Path> getMainClass(String slug, String branch) throws IOException {
		
		return this.listClassFiles(slug, branch)
				.stream()
				.filter(LocationUtils::filterForMainClass)
				.findFirst();
	}
	
	public static boolean filterForMainClass(Path path) {
		
		boolean hasMain = false;
		
		try {
			Method[] methods = new ClassParser(path.toString()).parse().getMethods();
			for(Method method : methods) {
				log.info("Method: " + method.getName());
				if("main".equals(method.getName())) {
					hasMain = true;
					break;
				}
			}
		} catch (ClassFormatException | IOException e) {
			log.throwing("LocationUtils", "fiterForMainClass", e);
		}
		
		return hasMain;
	}
	
}
