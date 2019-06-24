package es.cfuentes.code.text.invindex.monitor;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import es.cfuentes.code.text.invindex.index.InvertedIndex;

@Component
public class FolderMonitor implements ApplicationRunner {
	
	@Value("${monitor.folder:documents}")
	private String folder = "";
	
	@Autowired
	private InvertedIndex ie;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("Abradacurcix: " + args);
		// We read the monitor file
		File file = new File(folder);
		
		// Check that it is a valid directory
		if(!file.isDirectory())
			throw new IllegalArgumentException("monitor.folder should be a valid directory: " + folder);
		
		// Index already existing files
		for(File indexFile: file.listFiles()) {
			System.out.println("Processing file: " + indexFile.getAbsolutePath());
			ie.indexDocument(indexFile);
		}
		
		// Monitor the folder for changes
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path dirPath = Paths.get(file.toURI());
		dirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		WatchKey key;
		while ((key = watchService.take()) != null) {
		    for (WatchEvent<?> event : key.pollEvents()) {
		        //process
		    	System.out.println("Procesando evento + " + event.context());
		    	Path filePath = (Path)event.context();		    	
		    	ie.indexDocument(dirPath.resolve(filePath).toFile());
		    }
		    key.reset();
		}
		
	}



}
