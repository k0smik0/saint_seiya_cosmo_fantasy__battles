package net.iubris.sscfse.battles_collector;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;
import com.google.photos.library.factories.PhotosLibraryClientFactory;
import com.google.photos.library.suppliers.ListAlbumsSupplier;
import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.ListAlbumsRequest;
import com.google.photos.library.v1.proto.MediaItem;
import com.google.photos.library.v1.proto.MediaMetadata;
import com.google.photos.library.v1.proto.SearchMediaItemsRequest;
import com.google.protobuf.Timestamp;

public class Main {
    
    private static final List<String> REQUIRED_SCOPES =
            ImmutableList.of(
                "https://www.googleapis.com/auth/photoslibrary.readonly",
                "https://www.googleapis.com/auth/photoslibrary.appendonly");
    
    
    // project:
    // https://console.developers.google.com/apis/credentials?project=project-id-0637669633636693062
    
    private static final String SSCFSE_BATTLES_ALBUM_TITLE = "sscf - win/lose battles";
    
    public void demoSimple(String credentialsPath, String albumName) throws IOException, GeneralSecurityException {
//      Credentials.
        try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClientFactory.createClient(credentialsPath, REQUIRED_SCOPES);) {
          
        ListAlbumsRequest listAlbumsRequest = ListAlbumsRequest.getDefaultInstance();
        final ListAlbumsSupplier listAlbumsSupplier = new ListAlbumsSupplier(photosLibraryClient, listAlbumsRequest);
        Album album = listAlbumsSupplier.get()
                .parallelStream()
                .filter(a -> a.getTitle().equalsIgnoreCase(albumName))
                .findFirst().get();
        
        System.out.println("Found "+SSCFSE_BATTLES_ALBUM_TITLE+": "+album.getId());

        SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(album.getId()).build();
        SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClient, request);
        
        Iterable<MediaItem> iterable = searchMediaItemSupplier.get();
        
        /*Iterator<MediaItem> iterator = iterable.iterator();
        int c = 0;
        while(iterator.hasNext()) {
            MediaItem next = iterator.next();
            c++;
        }
        System.out.println("found: "+c+" photos from iterator");
        */
//        request.
        
        List<GooglePhoto> googlePhotos = StreamSupport.stream(iterable.spliterator(), false)
            // MediaItem
            .map(mi -> {
                String description = mi.getDescription();
                String filename = mi.getFilename();
                String id = mi.getId();
                String baseUrl = mi.getBaseUrl();
    
                MediaMetadata mediaMetadata = mi.getMediaMetadata();
                Timestamp creationTime = mediaMetadata.getCreationTime();
                
                GooglePhoto googlePhoto = new GooglePhoto(id, filename, description, baseUrl, creationTime);
                
                return googlePhoto;
            })
            .collect(Collectors.toList());
        
        AtomicInteger i = new AtomicInteger(1);
        System.out.println("found: "+ googlePhotos.size()+ " photos");
        googlePhotos.stream().forEach(gp->{
            System.out.println(i.incrementAndGet()+" "+gp.getFilename()+":: "
                    +GooglePhoto.DATE_FORMATTER.format(gp.getCreationTime().getSeconds()*1000)
                    +": "+gp.getDescription());
        });
        
        }

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
                new Main().demoSimple(c, SSCFSE_BATTLES_ALBUM_TITLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}
