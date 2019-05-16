/**
 *
 */
package net.iubris.sscfse.battles_collector;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.common.collect.Table;

import net.iubris.sscfse.battles_collector.model.GooglePhoto;

/**
 * @author massimiliano.leone@iubris.net
 *
 *         May 15, 2019
 */
@Singleton
public class SequentiallyTextAnnotationsRetriever extends AbstractTextAnnotationsRetriever {

    @Inject
    public SequentiallyTextAnnotationsRetriever(ImageAnnotatorClient imageAnnotatorClient) {
        super(imageAnnotatorClient);
    }

    public Map<String, String> retrieveTextAnnotationsToFilenameToNoteMap(Table<String, String, GooglePhoto> urlOrFilenameToGooglePhotoTable) {
        AtomicInteger i = new AtomicInteger();

        System.out.println("sending request for: " + urlOrFilenameToGooglePhotoTable.values().size());

        ForkJoinPool fjp = new ForkJoinPool(16);

        Map<String, String> collect = null;
        try {
            collect = fjp.submit(() -> urlOrFilenameToGooglePhotoTable.cellSet().parallelStream()
                    .map(c -> {
                        GooglePhoto photo = c.getValue();
                        // System.out.println("rowKey:"+c.getRowKey()+", columnKey:"+c.getColumnKey()+",
                        // value:"+photo);
                        AnnotateImageRequest request = annotateImageRequestForGooglePhoto.apply(photo);
                        List<AnnotateImageRequest> asList = Arrays.asList(new AnnotateImageRequest[] { request });
                        System.out.println(i.incrementAndGet() + ": sending request for: " + photo.getFilename());
                        BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(asList);
                        //                    imageAnnotatorClient.asyncBatchAnnotateFilesAsync(AsyncBatchAnnotateFilesRequest.newBuilder(). );

                        String note = response.getResponsesList().parallelStream().flatMap(air -> {
                            Stream<String> stream = air.getTextAnnotationsList().parallelStream().map(ta -> {
                                String s =
                                        //					        "position:" + ta.getBoundingPoly().toString().replace("\n", " ").replace("y:", ",y:").replace("   ", "")
                                        //					        +", "+
                                        "\tdescription:" + ta.getDescription().replace("\n", " ");
                                return s;
                            });
                            return stream;
                        }).collect(Collectors.joining("\n"));
                        photo.setNote(note);

                        AnnotateImageResponse response0 = response.getResponses(0);
                        String textAnnotation0 = response0.getTextAnnotations(0).getDescription().replace("\n", " ");
//                        String labelAnnotation0 = response0.getLabelAnnotations(0).getDescription().replace("\n", " ");
//                        String logoAnnotation0 = response0.getLogoAnnotations(0).getDescription().replace("\n", " ");
                        String note2 = "\ttextAnnotation.description:"+textAnnotation0
//                        		+"\tlabelAnnotation.description:"+labelAnnotation0
//                        		+"\tlogoAnnotation.description:"+logoAnnotation0
                        		;
                        photo.setNote(note2);

                        System.out.println(i.get() + ": got response for: " + photo.getFilename());

                        return photo;
                    })
                    .collect(Collectors.toMap(GooglePhoto::getFilename, GooglePhoto::getNote))
                ).get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return collect;
    }
}
