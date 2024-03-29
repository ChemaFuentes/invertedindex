package es.cfuentes.code.test.invindex.monitor;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import es.cfuentes.code.test.invindex.ifaces.DocumentIndex;

/*
 * Component that monitors the document directory 
 */
@Profile("!test")
@Component
public class FolderMonitor implements ApplicationRunner {

	@Value("${monitor.folder:documents}")
	private String folder;

	@Autowired
	private DocumentIndex ie;

	private Logger log = Logger.getLogger(this.getClass().toString());

	@Override
	public void run(ApplicationArguments args) throws Exception {

		// We read the monitor file
		File file = new File(folder);

		// Check that it is a valid directory
		if (!file.isDirectory())
			throw new IllegalArgumentException("monitor.folder should be a valid directory: " + folder);

		// Index already existing files
		for (File indexFile : file.listFiles()) {
			log.info("Processing file: " + indexFile.getAbsolutePath());
			ie.indexDocument(indexFile);
		}

		// Monitor the folder for changes
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path dirPath = Paths.get(file.toURI());
		dirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				// Process the new file
				Path filePath = (Path) event.context();
				File inFile = dirPath.resolve(filePath).toFile();
				log.info("Processing file: " + inFile.getAbsolutePath());
				ie.indexDocument(inFile);
			}
			key.reset();
		}

	}
}
