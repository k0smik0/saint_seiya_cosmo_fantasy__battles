package net.iubris.sscfse.battles_collector;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.photos.library.v1.proto.MediaItem;
import com.google.photos.library.v1.proto.MediaMetadata;
import com.google.protobuf.Timestamp;

import net.iubris.sscfse.battles_collector._di.PhotosLibraryClientProvider;
import net.iubris.sscfse.battles_collector._di.SSCFSEBattlesModule;
import net.iubris.sscfse.battles_collector.model.GooglePhoto;

public class Main {


    private final BattlesItemSearchMediaItemSupplierProvider battlesItemSearchMediaItemSupplierProvider;

    @Inject
    public Main(BattlesItemSearchMediaItemSupplierProvider battlesItemSearchMediaItemSupplierProvider) {
        this.battlesItemSearchMediaItemSupplierProvider = battlesItemSearchMediaItemSupplierProvider;
    }

    public void demoSimple() throws IOException, GeneralSecurityException {
        //      Credentials.
        //        PhotosLibraryClient photosLibraryClient = PhotosLibraryClientFactory.createClient(credentialsPath, REQUIRED_SCOPES)

        /*
         * final ListAlbumsRequest listAlbumsRequest =
         * ListAlbumsRequest.getDefaultInstance(); final ListAlbumsSupplier
         * listAlbumsSupplier = new ListAlbumsSupplier(photosLibraryClient,
         * listAlbumsRequest); final Album album = listAlbumsSupplier.get()
         * .parallelStream() .filter(a ->
         * a.getTitle().equalsIgnoreCase(Config.SSCFSE_BATTLES_ALBUM_TITLE))
         * .findFirst().get();
         * System.out.println("Found "+Config.SSCFSE_BATTLES_ALBUM_TITLE+": "+album.
         * getId());
         */

        //        final Album album = battlesAlbumProvider.get();


        //        final SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(album.getId()).build();
        //        final SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClient, request);

        final Iterable<MediaItem> iterable = battlesItemSearchMediaItemSupplierProvider.get().get();

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

        final AtomicInteger i = new AtomicInteger(1);
        System.out.println("found: "+ googlePhotos.size()+ " photos");
        googlePhotos.stream().forEach(gp->{
            System.out.println(i.incrementAndGet()+" "+gp.getFilename()+":: "
                    +GooglePhoto.DATE_FORMATTER.format(gp.getCreationDateTime())
                    +": "+gp.getDescription());
        });



        //          // Create a new Album with at title
        //          Album createdAlbum = photosLibraryClient.createAlbum("My Album");
        //
        //          // Get some properties from the album, such as its ID and product URL
        //          String id = createdAlbum.getId();
        //          String url = createdAlbum.getProductUrl();

    }

    public static void main(String[] args) throws IOException {
        //        new AlbumDemo().doStuff(args);

        Optional<String> credentialsFile = Optional.empty();

        if (args.length > 0) {
            credentialsFile = Optional.of(args[0]);
        }

        credentialsFile.ifPresent(c->{
            try {
                final Injector injector = Guice.createInjector(new SSCFSEBattlesModule());
                injector.getInstance(PhotosLibraryClientProvider.class).init(c);
                injector.getInstance(Main.class).demoSimple();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }

}
