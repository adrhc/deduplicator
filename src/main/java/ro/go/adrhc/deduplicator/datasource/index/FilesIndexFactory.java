package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChangesProvider;
import ro.go.adrhc.deduplicator.lib.LuceneFactories;
import ro.go.adrhc.persistence.lucene.IndexAdmin;
import ro.go.adrhc.persistence.lucene.IndexUpdater;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

import static ro.go.adrhc.deduplicator.datasource.index.changes.DefaultActualData.actualPaths;

@RequiredArgsConstructor
public class FilesIndexFactory {
	private final FilesIndexProperties indexProperties;
	private final LuceneTokenizer luceneTokenizer;
	private final SimpleDirectory filesDirectory;
	private final FileMetadataProvider metadataProvider;

	public FilesIndex create(Path indexPath) {
		return new FilesIndex(
				metadataProvider,
				createIndexAdmin(indexPath),
				createIndexUpdater(indexPath),
				indexChangesProvider(indexPath));
	}

	private IndexChangesProvider<Path> indexChangesProvider(Path indexPath) {
		return new IndexChangesProvider<>(IndexFieldType.filePath.name(),
				() -> actualPaths(filesDirectory::getAllPaths),
				LuceneFactories.create(indexProperties, indexPath));
	}

	private FileMetadataToDocumentConverter createAudioMetadataToDocumentConverter() {
		return new FileMetadataToDocumentConverter(luceneTokenizer);
	}

	private IndexAdmin<FileMetadata> createIndexAdmin(Path indexPath) {
		return IndexAdmin.create(indexPath, luceneTokenizer, createAudioMetadataToDocumentConverter()::convert);
	}

	private IndexUpdater<FileMetadata> createIndexUpdater(Path indexPath) {
		return IndexUpdater.create(IndexFieldType.filePath,
				indexPath, luceneTokenizer, createAudioMetadataToDocumentConverter()::convert);
	}
}
