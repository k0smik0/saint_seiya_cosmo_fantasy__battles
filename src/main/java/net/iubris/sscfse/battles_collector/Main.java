package net.iubris.sscfse.battles_collector;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.api.services.vision.v1.Vision;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageSource;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.MediaItem;
import com.google.photos.library.v1.proto.MediaMetadata;
import com.google.protobuf.Timestamp;

import net.iubris.sscfse.battles_collector._di.SSCFSEBattlesModule;
import net.iubris.sscfse.battles_collector._di.provider.PhotosLibraryClientProvider;
import net.iubris.sscfse.battles_collector._di.provider.VisionServiceProvider;
import net.iubris.sscfse.battles_collector.model.GooglePhoto;

public class Main {

    //    private final PhotosLibraryClient photosLibraryClient;
    private final Album battlesAlbum;
    private final SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier;
    private final Vision vision;

    @Inject
    public Main(
            //            PhotosLibraryClientProvider photosLibraryClientProvider
            @Named(Config.SSCFSE_BATTLES_ALBUM_TITLE) Album battlesAlbum,
            @Named("BattlesAlbumSearchMediaItemSupplier") SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier
            ,Vision vision
            ) {
        this.battlesAlbum = battlesAlbum;
        this.battlesAlbumSearchMediaItemSupplier = battlesAlbumSearchMediaItemSupplier;
        this.vision = vision;
        //        photosLibraryClient = photosLibraryClientProvider.get();
    }

    public void retrievePhotosFromAlbum() throws IOException, GeneralSecurityException {

        System.out.println("Found "+Config.SSCFSE_BATTLES_ALBUM_TITLE+": "+battlesAlbum.getId());

        final List<GooglePhoto> googlePhotos = retrieveMediaItems();

        System.out.println("vision: "+vision);

        BatchAnnotateImagesRequest batchAnnotateImagesRequest = buildImagesTextRecognitionBatchRequest(googlePhotos);

        asd(batchAnnotateImagesRequest);



        //        printRetrievedGooglePhotos(googlePhotos);

        //          // Create a new Album with at title
        //          Album createdAlbum = photosLibraryClient.createAlbum("My Album");
        //
        //          // Get some properties from the album, such as its ID and product URL
        //          String id = createdAlbum.getId();
        //          String url = createdAlbum.getProductUrl();
    }

    private List<GooglePhoto> retrieveMediaItems() {
        final Iterable<MediaItem> iterable = battlesAlbumSearchMediaItemSupplier.get();

        final List<GooglePhoto> googlePhotos = StreamSupport.stream(iterable.spliterator(), false)
                // MediaItem
                .map(mi -> {
                    final String description = mi.getDescription();
                    final String filename = mi.getFilename();
                    final String id = mi.getId();
                    final String baseUrl = mi.getBaseUrl();

                    final MediaMetadata mediaMetadata = mi.getMediaMetadata();
                    final Timestamp creationTime = mediaMetadata.getCreationTime();

                    final GooglePhoto googlePhoto = new GooglePhoto(id, filename, description, baseUrl,
                            new Date(creationTime.getSeconds()*1000));

                    return googlePhoto;
                })
                .collect(Collectors.toList());

        return googlePhotos;
    }

    private BatchAnnotateImagesRequest buildImagesTextRecognitionBatchRequest(List<GooglePhoto> googlePhotos) {
        List<AnnotateImageRequest> requests = googlePhotos.parallelStream()
                .map(gp -> {
                    String gcsPath = gp.getBaseUrl();
                    ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
                    Image img = Image.newBuilder().setSource(imgSource).build();
                    Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
                    AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
                    return request;
                })
                .collect(Collectors.toList());
        BatchAnnotateImagesRequest batchAnnotateImagesRequest = BatchAnnotateImagesRequest.newBuilder().addAllRequests(requests).build();
        return batchAnnotateImagesRequest;
    }

    private void asd(BatchAnnotateImagesRequest batchAnnotateImagesRequest) {

        //        vision.

        //        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
        //        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        //        List<AnnotateImageResponse> responses = response.getResponsesList();
        //
        //        for (AnnotateImageResponse res : responses) {
        //          if (res.hasError()) {
        //            out.printf("Error: %s\n", res.getError().getMessage());
        //            return;
        //          }
        //
        //          // For full list of available annotations, see http://g.co/cloud/vision/docs
        //          for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
        //            out.printf("Text: %s\n", annotation.getDescription());
        //            out.printf("Position : %s\n", annotation.getBoundingPoly());
        //          }
        //        }


    }

    private void printRetrievedGooglePhotos(List<GooglePhoto> googlePhotos) {
        final AtomicInteger i = new AtomicInteger(1);
        System.out.println("found: "+ googlePhotos.size()+ " photos");
        googlePhotos.stream().forEach(gp->{
            System.out.println(i.incrementAndGet()+" "+gp.getFilename()+":: "
                    +GooglePhoto.DATE_FORMATTER.format(gp.getCreationDateTime())
                    +": "+gp.getDescription());
        });
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        //        new AlbumDemo().doStuff(args);

        if (args.length > 1) {
            final String webCredentialsFile = args[0];
            final String serviceAccountCredentialsFile = args[1];

            final Injector injector = Guice.createInjector(new SSCFSEBattlesModule());
            injector.getInstance(PhotosLibraryClientProvider.class).setCredentialsPath(webCredentialsFile);
            injector.getInstance(VisionServiceProvider.class).setCredentialsPath(serviceAccountCredentialsFile);
            injector.getInstance(Main.class).retrievePhotosFromAlbum();
        }
    }

}
