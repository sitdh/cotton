package com.sitdh.thesis.core.cotton.analyzer.service.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
		
		Files.find(
				Paths.get(projectWorkspace), 
				Integer.MAX_VALUE, 
				(filePath, fileAttr) -> fileAttr.isRegularFile())
			.forEach(classFiles::add);
		
		return classFiles;
	}
	
}
