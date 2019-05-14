package net.iubris.sscfse.battles_collector;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.proto.MediaItem;
import com.meyling.guava.TableCollector;

import net.iubris.sscfse.battles_collector._di.SSCFSEBattlesModule;
import net.iubris.sscfse.battles_collector._di.provider.ImageAnnotatorClientProvider;
import net.iubris.sscfse.battles_collector._di.provider.PhotosLibraryClientProvider;
import net.iubris.sscfse.battles_collector.model.GooglePhoto;

public class Main {

    // private final PhotosLibraryClient photosLibraryClient;
    // private final Album battlesAlbum;
    private final SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier;
    private final ImageAnnotatorClient imageAnnotatorClient;

    @Inject
    public Main(
            // PhotosLibraryClientProvider photosLibraryClientProvider
            // @Named(Config.SSCFSE_BATTLES_ALBUM_TITLE) Album battlesAlbum,
            @Named("BattlesAlbumSearchMediaItemSupplier") SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier,
            ImageAnnotatorClient imageAnnotatorClient) {
        // this.battlesAlbum = battlesAlbum;
        this.battlesAlbumSearchMediaItemSupplier = battlesAlbumSearchMediaItemSupplier;
        this.imageAnnotatorClient = imageAnnotatorClient;
        // photosLibraryClient = photosLibraryClientProvider.get();
    }

    public void retrievePhotosFromAlbum() throws IOException, GeneralSecurityException {

        Table<String, String, GooglePhoto> urlOrFilenameToGooglePhotoTable = retrieveMediaItems();

        // List<BatchAnnotateImagesRequest> batchAnnotateImagesRequests =
        // buildImagesTextRecognitionBatchRequest(googlePhotos);
        //
        // retrieveTextAnnotations(batchAnnotateImagesRequests);

        AtomicInteger i = new AtomicInteger();

        System.out.println("sending request for: " + urlOrFilenameToGooglePhotoTable.values().size());
        Map<String, String> collect = urlOrFilenameToGooglePhotoTable.cellSet().parallelStream().map(c -> {
            GooglePhoto photo = c.getValue();
            // System.out.println("rowKey:"+c.getRowKey()+", columnKey:"+c.getColumnKey()+", value:"+photo);
            AnnotateImageRequest request = annotateImageRequestForGooglePhoto.apply(photo);
            List<AnnotateImageRequest> asList = Arrays.asList(new AnnotateImageRequest[] { request });
            System.out.println(i.incrementAndGet() + ": sending request for: " + photo.getFilename());
            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(asList);
//                    imageAnnotatorClient.asyncBatchAnnotateFilesAsync(AsyncBatchAnnotateFilesRequest.newBuilder(). );
            /*String note = response.getResponsesList().stream().map(air -> {
                String n = air.getTextAnnotationsList().stream().map(ta -> {
                    String s = "position:" + ta.getBoundingPoly().toString() + ", description:" + ta.getDescription();
                    return s.replace("\n", " ");
                }).collect(Collectors.joining("; "));
                return n;
            }).findFirst().get();*/

            String note2 = response.getResponsesList().parallelStream().flatMap(air -> {
                Stream<String> stream = air.getTextAnnotationsList().parallelStream().map(ta -> {
                    String s = "position:"
                            + ta.getBoundingPoly().toString().replace("\n", " ").replace("y:", ",y:").replace("   ", "")
                            + ", description:" + ta.getDescription().replace("\n", " ");
                    return s;
                });
                return stream;
            }).collect(Collectors.joining("; "));
            System.out.println(i.get() + ": got response for: " + photo.getFilename());

            photo.setNote(note2);
            return photo;
        }).collect(Collectors.toMap(GooglePhoto::getFilename, GooglePhoto::getNote));

        System.out.println("results:");
        collect.forEach((k, v) -> {
            System.out.println(k + ": " + v + "\n");
        });

        // printRetrievedGooglePhotos(googlePhotos);

        // // Create a new Album with at title
        // Album createdAlbum = photosLibraryClient.createAlbum("My Album");
        //
        // // Get some properties from the album, such as its ID and product URL
        // String id = createdAlbum.getId();
        // String url = createdAlbum.getProductUrl();
    }

    private Table<String, String, GooglePhoto> retrieveMediaItems() {
        Iterable<MediaItem> iterable = battlesAlbumSearchMediaItemSupplier.get();

        List<GooglePhoto> googlePhotos = StreamSupport.stream(iterable.spliterator(), false)
                // MediaItem
                .map(mi -> {
                    return new GooglePhoto(mi.getId(), mi.getFilename(), mi.getDescription(), mi.getBaseUrl(),
                            new Date(mi.getMediaMetadata().getCreationTime().getSeconds() * 1000));
                }).collect(Collectors.toList());

        Table<String, String, GooglePhoto> urlOrFilenameToGooglePhotoTable = StreamSupport
                .stream(iterable.spliterator(), false)
                // MediaItem
                .map(mi -> {
                    return new GooglePhoto(mi.getId(), mi.getFilename(), mi.getDescription(), mi.getBaseUrl(),
                            new Date(mi.getMediaMetadata().getCreationTime().getSeconds() * 1000));
                }).collect(TableCollector.toHashBasedTable(GooglePhoto::getBaseUrl, GooglePhoto::getFilename,
                        Function.identity()));

        // urlOrFilenameToGooglePhotoTable.cellSet().forEach(c->{
        // System.out.println("rowKey:"+c.getRowKey()+", columnKey:"+c.getColumnKey()+", value:"+c.getValue());
        // });

        return urlOrFilenameToGooglePhotoTable;
    }

    private final static Function<GooglePhoto, AnnotateImageRequest> annotateImageRequestForGooglePhoto = gp -> {
        String gpUrl = gp.getBaseUrl();
        ImageSource imgSource = ImageSource.newBuilder().setImageUri(gpUrl).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        return request;
    };

    private static final int QUOTA_MAX = 16;

    private List<BatchAnnotateImagesRequest> buildImagesTextRecognitionBatchRequest(List<GooglePhoto> googlePhotos) {
        // AtomicInteger i = new AtomicInteger(0);
        List<BatchAnnotateImagesRequest> collect = Lists.partition(googlePhotos, QUOTA_MAX).stream().flatMap(l -> {
            List<AnnotateImageRequest> annotateImageRequests = l.parallelStream()
                    .map(annotateImageRequestForGooglePhoto).collect(Collectors.toList());
            // System.out.println(i.incrementAndGet()+ ": adding "+annotateImageRequests.size()+" requests [flatMap]");
            BatchAnnotateImagesRequest batchAnnotateImagesRequest = BatchAnnotateImagesRequest.newBuilder()
                    .addAllRequests(annotateImageRequests).build();
            BatchAnnotateImagesRequest[] bairs = new BatchAnnotateImagesRequest[] { batchAnnotateImagesRequest };
            return Arrays.stream(bairs);
        }).collect(Collectors.toList());
        // System.out.println("batch requests [flatMap]: "+collect.size());

        return collect;
    }

    private void retrieveTextAnnotations(List<BatchAnnotateImagesRequest> batchAnnotateImagesRequests) {

        batchAnnotateImagesRequests.forEach(bair -> {

            List<AnnotateImageRequest> requestsList = bair.getRequestsList();
            requestsList.forEach(rl -> rl.getImage().getSource().getImageUri());

            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(bair);

            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse air : responses) {
                if (air.hasError()) {
                    System.err.printf("Error: %s\n", air.getError().getMessage());
                    return;
                }

                String uri = air.getContextOrBuilder().getUri();
                System.out.println("image url: " + uri);

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                // for (EntityAnnotation annotation : air.getTextAnnotationsList()) {
                // System.out.printf("Text: %s\n", annotation.getDescription());
                // System.out.printf("Position : %s\n", annotation.getBoundingPoly());
                // }
                System.out.println("annotations: " + air.getTextAnnotationsCount());
                System.out.println("");
            }

        });
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
