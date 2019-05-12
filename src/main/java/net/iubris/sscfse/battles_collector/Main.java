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
import javax.inject.Named;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.photos.library.suppliers.SearchMediaItemSupplier;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.MediaItem;
import com.google.photos.library.v1.proto.MediaMetadata;
import com.google.protobuf.Timestamp;

import net.iubris.sscfse.battles_collector._di.PhotosLibraryClientProvider;
import net.iubris.sscfse.battles_collector._di.SSCFSEBattlesModule;
import net.iubris.sscfse.battles_collector.model.GooglePhoto;

public class Main {

	private final PhotosLibraryClient photosLibraryClient;
	private final Album battlesAlbum;
	private final SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier;

	@Inject
	public Main(PhotosLibraryClientProvider photosLibraryClientProvider, @Named(Config.SSCFSE_BATTLES_ALBUM_TITLE) Album battlesAlbum,
			@Named("BattlesAlbumSearchMediaItemSupplier") SearchMediaItemSupplier battlesAlbumSearchMediaItemSupplier) {
		this.battlesAlbum = battlesAlbum;
		this.battlesAlbumSearchMediaItemSupplier = battlesAlbumSearchMediaItemSupplier;
		photosLibraryClient = photosLibraryClientProvider.get();
	}

	public void demoSimple() throws IOException, GeneralSecurityException {
		//      Credentials.
		//        PhotosLibraryClient photosLibraryClient = PhotosLibraryClientFactory.createClient(credentialsPath, REQUIRED_SCOPES)

		System.out.println("Found "+Config.SSCFSE_BATTLES_ALBUM_TITLE+": "+battlesAlbum.getId());

		//		final SearchMediaItemsRequest request = SearchMediaItemsRequest.newBuilder().setAlbumId(battlesAlbum.getId()).build();
		//		final SearchMediaItemSupplier searchMediaItemSupplier = new SearchMediaItemSupplier(photosLibraryClient, request);

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

		credentialsFile.ifPresent(cf->{
			try {
				final Injector injector = Guice.createInjector(new SSCFSEBattlesModule());
				injector.getInstance(PhotosLibraryClientProvider.class).init(cf);
				injector.getInstance(Main.class).demoSimple();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}

}
