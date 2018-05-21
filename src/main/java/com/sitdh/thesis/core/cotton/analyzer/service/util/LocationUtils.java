package com.sitdh.thesis.core.cotton.analyzer.service.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

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
	
	public String getProjectFromWorkspace(String slug, String branch) throws FileNotFoundException {
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

}
