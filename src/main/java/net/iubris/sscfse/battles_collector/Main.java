package net.iubris.sscfse.battles_collector;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Table;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.proto.MediaItem;

import net.iubris.sscfse.battles_collector._di.SSCFSEBattlesModule;
import net.iubris.sscfse.battles_collector._di.providers.ImageAnnotatorClientProvider;
import net.iubris.sscfse.battles_collector._di.providers.PhotosLibraryClientProvider;
import net.iubris.sscfse.battles_collector.model.GooglePhoto;
import net.iubris.sscfse.battles_collector.model.MediaItemsTransformer;
import net.iubris.sscfse.battles_collector.services.text_annotations_retrievers.SequentiallyTextAnnotationsRetriever;

public class Main {

    // private final PhotosLibraryClient photosLibraryClient;
    // private final Album battlesAlbum;
    private final SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier;
//    private final ImageAnnotatorClient imageAnnotatorClient;
    private final SequentiallyTextAnnotationsRetriever sequentiallyTextAnnotationsRetriever;
//    private final PaginatedBatchTextAnnotationsRetriever paginatedBatchTextAnnotationsRetriever;

    @Inject
    public Main(
            // PhotosLibraryClientProvider photosLibraryClientProvider
            // @Named(Config.SSCFSE_BATTLES_ALBUM_TITLE) Album battlesAlbum,
            @Named(SSCFSEBattlesModule.BattlesAlbumSearchMediaItemSupplier) SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier,
//            ImageAnnotatorClient imageAnnotatorClient,
            SequentiallyTextAnnotationsRetriever sequentiallyTextAnnotationsRetriever
//            PaginatedBatchTextAnnotationsRetriever paginatedBatchTextAnnotationsRetriever
            ) {
        // this.battlesAlbum = battlesAlbum;
        this.battlesAlbumSearchMediaItemSupplier = battlesAlbumSearchMediaItemSupplier;
//        this.imageAnnotatorClient = imageAnnotatorClient;
        this.sequentiallyTextAnnotationsRetriever = sequentiallyTextAnnotationsRetriever;
//        this.paginatedBatchTextAnnotationsRetriever = paginatedBatchTextAnnotationsRetriever;
    }

    public void retrievePhotosFromAlbum() throws IOException, GeneralSecurityException {

        Iterable<MediaItem> iterable = battlesAlbumSearchMediaItemSupplier.get();

//        battlesAlbumSearchMediaItemSupplier.

        // 1
        Table<String, String, GooglePhoto> urlOrFilenameToGooglePhotoTable = MediaItemsTransformer.mediaItemsAsTable(iterable);
        Map<String, String> collect = sequentiallyTextAnnotationsRetriever.retrieveTextAnnotationsToFilenameToNoteMap(urlOrFilenameToGooglePhotoTable);
        System.out.println("results:");
        collect.forEach((k, v) -> {
            System.out.println(k + "\n" + v);
        });

        //        List<GooglePhoto> mediaItemsAsList = MediaItemsTransformer.mediaItemsAsList(iterable);
        //        List<BatchAnnotateImagesRequest> buildImagesTextRecognitionBatchRequestsByPagination = paginatedBatchTextAnnotationsRetriever
        //                .buildImagesTextRecognitionBatchRequestsByPagination(mediaItemsAsList);
        //        paginatedBatchTextAnnotationsRetriever
        //                .retrieveTextAnnotationsViaBatch(buildImagesTextRecognitionBatchRequestsByPagination);

        // printRetrievedGooglePhotos(googlePhotos);

        // // Create a new Album with at title
        // Album createdAlbum = photosLibraryClient.createAlbum("My Album");
        //
        // // Get some properties from the album, such as its ID and product URL
        // String id = createdAlbum.getId();
        // String url = createdAlbum.getProductUrl();
    }

    private void printRetrievedGooglePhotos(List<GooglePhoto> googlePhotos) {
        final AtomicInteger i = new AtomicInteger(1);
        System.out.println("found: " + googlePhotos.size() + " photos");
        googlePhotos.stream().forEach(gp -> {
            System.out.println(i.incrementAndGet() + " " + gp.getFilename() + ":: "
                    + GooglePhoto.DATE_FORMATTER.format(gp.getCreationDateTime()) + ": " + gp.getDescription());
        });
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // new AlbumDemo().doStuff(args);

        if (args.length > 1) {
            final String webCredentialsFile = args[0];
            final String serviceAccountCredentialsFile = args[1];

            final Injector injector = Guice.createInjector(new SSCFSEBattlesModule());
            injector.getInstance(PhotosLibraryClientProvider.class).setCredentialsPath(webCredentialsFile).init();
            // injector.getInstance(VisionServiceProvider.class).setCredentialsPath(serviceAccountCredentialsFile).init();
            injector.getInstance(ImageAnnotatorClientProvider.class).setCredentialsPath(serviceAccountCredentialsFile)
            .init();
            injector.getInstance(Main.class).retrievePhotosFromAlbum();
        }
    }

}
