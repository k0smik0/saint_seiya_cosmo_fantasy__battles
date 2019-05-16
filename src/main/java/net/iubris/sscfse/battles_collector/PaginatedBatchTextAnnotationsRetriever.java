/**
 *
 */
package net.iubris.sscfse.battles_collector;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.common.collect.Lists;

import net.iubris.sscfse.battles_collector.model.GooglePhoto;

/**
 * @author massimiliano.leone@iubris.net
 *
 *         May 14, 2019
 */
@Singleton
public class PaginatedBatchTextAnnotationsRetriever extends AbstractTextAnnotationsRetriever {

    private static final int QUOTA_MAX = 16;

    @Inject
    public PaginatedBatchTextAnnotationsRetriever(ImageAnnotatorClient imageAnnotatorClient) {
        super(imageAnnotatorClient);
    }

    public List<BatchAnnotateImagesRequest> buildImagesTextRecognitionBatchRequestsByPagination(List<GooglePhoto> googlePhotos) {
        AtomicInteger i = new AtomicInteger(0);
        List<BatchAnnotateImagesRequest> collect = Lists.partition(googlePhotos, QUOTA_MAX).stream().flatMap(l -> {
            List<AnnotateImageRequest> annotateImageRequests = l.stream().map(annotateImageRequestForGooglePhoto).collect(Collectors.toList());

            //            System.out.println(i.incrementAndGet() + ": adding " + annotateImageRequests.size() + " requests: "+ l.stream().map(GooglePhoto::getFilename).collect(Collectors.joining(",")));

            BatchAnnotateImagesRequest batchAnnotateImagesRequest = BatchAnnotateImagesRequest.newBuilder().addAllRequests(annotateImageRequests).build();
            BatchAnnotateImagesRequest[] bairs = new BatchAnnotateImagesRequest[] { batchAnnotateImagesRequest };
            return Arrays.stream(bairs);
        }).collect(Collectors.toList());
        // System.out.println("batch requests [flatMap]: "+collect.size());

        return collect;
    }

    public void retrieveTextAnnotationsViaBatch(List<BatchAnnotateImagesRequest> batchAnnotateImagesRequests) {

        batchAnnotateImagesRequests.forEach(bair -> {

            //            bair.getRequestsList().forEach(rl -> {
            //                System.out.println(rl.getImage().getSource().getImageUri());
            //            });

            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(bair);

            List<AnnotateImageResponse> responses = response.getResponsesList();



            for (AnnotateImageResponse air : responses) {
                if (air.hasError()) {
                    System.err.printf("Error: %s\n", air.getError().getMessage());
                    return;
                }

                //                String string = ReflectionToStringBuilder.toString(air, ToStringStyle.JSON_STYLE, true, true, Object.class);
                //                System.out.println(string);

                //                String uri = air.getContextOrBuilder().getUri();
                //                System.out.println("contextOrBuilder.uri: " + uri);
                //
                //                System.out.println("allFields:");
                //                air.getImagePropertiesAnnotation().getAllFields().entrySet().stream().forEach(e->{
                //                    System.out.println(e.getKey()+":"+e.getValue());
                //                });

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                // for (EntityAnnotation annotation : air.getTextAnnotationsList()) {
                // System.out.printf("Text: %s\n", annotation.getDescription());
                // System.out.printf("Position : %s\n", annotation.getBoundingPoly());
                // }
                //                System.out.println("annotations: " + air.getTextAnnotationsCount());
                //                System.out.println("");

//                System.out.println( ReflectionToStringBuilder.toString(air.getContext(), ToStringStyle.JSON_STYLE, true, true, Object.class) );

            }

        });
    }
}
