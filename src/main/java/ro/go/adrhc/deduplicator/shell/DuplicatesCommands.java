package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.deduplicator.services.FilesDedupService;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;

import java.io.IOException;
import java.nio.file.Path;

@ShellComponent("Duplicates management.")
@RequiredArgsConstructor
@Slf4j
public class DuplicatesCommands {
	private final IndexDataSource<Path, FileMetadata> indexDataSource;

	@ShellMethod(value = "Find duplicates.", key = {"find-dups"})
	public void findDuplicates() throws IOException {
		log.debug("\n{}", filesDedupService().findDups());
	}

	@ShellMethod(value = "Remove the duplicates, update the index and show duplicates.", key = {"remove-dups"})
	public void removeDuplicates() throws IOException {
		if (filesDedupService().removeDups()) {
			indexRepository().restore(indexDataSource);
		}
		findDuplicates();
	}

	@Lookup
	protected FilesDedupService filesDedupService() {
		return null;
	}

	@Lookup
	protected IndexRepository<Path, FileMetadata> indexRepository() {
		return null;
	}
}
