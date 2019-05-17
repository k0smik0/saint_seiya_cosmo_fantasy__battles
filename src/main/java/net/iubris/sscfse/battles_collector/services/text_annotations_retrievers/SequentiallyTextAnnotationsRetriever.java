/**
 *
 */
package net.iubris.sscfse.battles_collector.services.text_annotations_retrievers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Vertex;
import com.google.common.collect.Table;

import net.iubris.sscfse.battles_collector.model.GooglePhoto;

/**
 * @author massimiliano.leone@iubris.net
 *
 *         May 15, 2019
 */
@Singleton
public class SequentiallyTextAnnotationsRetriever extends AbstractTextAnnotationsRetriever {

    private static final int PARALLEL_REQUESTS = 16;

    @Inject
    public SequentiallyTextAnnotationsRetriever(ImageAnnotatorClient imageAnnotatorClient) {
        super(imageAnnotatorClient);
    }

    public Map<String, String> retrieveTextAnnotationsToFilenameToNoteMap(Table<String, String, GooglePhoto> urlOrFilenameToGooglePhotoTable) {
        AtomicInteger i = new AtomicInteger();

        System.out.println("sending requests and waiting for responses for: " + urlOrFilenameToGooglePhotoTable.values().size()+" photos, using "+PARALLEL_REQUESTS+" parallel requests");
        System.out.print("processed: ");

        ForkJoinPool fjp = new ForkJoinPool(PARALLEL_REQUESTS);

        Map<String, String> collect = null;
        try {
            int total = urlOrFilenameToGooglePhotoTable.cellSet().size();
            AtomicInteger counter = new AtomicInteger(0);
            collect = fjp.submit(() -> urlOrFilenameToGooglePhotoTable.cellSet().parallelStream()
                    .map(c -> {
                        GooglePhoto photo = c.getValue();
//                        System.out.println("rowKey:"+c.getRowKey()+", columnKey:"+c.getColumnKey()+", value:"+photo);
                        AnnotateImageRequest request = annotateImageRequestForGooglePhoto.apply(photo);
                        List<AnnotateImageRequest> asList = Arrays.asList(new AnnotateImageRequest[] { request });
// System.out.println(i.incrementAndGet() + ": sending request for: " + photo.getFilename());
                        BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(asList);
//                        imageAnnotatorClient.asyncBatchAnnotateFilesAsync(AsyncBatchAnnotateFilesRequest.newBuilder(). );


                        AtomicInteger R = new AtomicInteger(0);
                        String note = response.getResponsesList().stream().flatMap(air->{
                            int Rn = R.incrementAndGet();
                            AtomicInteger A = new AtomicInteger(0);
                            Stream<String> stream = air.getTextAnnotationsList().stream().map(ta->{
                                int An = A.incrementAndGet();
                                String s = Rn+"."+An+": "+ "position:" + ta.getBoundingPoly().toString().replace("\n", " ") .replace("y:", ",y:") .replace("   ", "")
                                        +", "+ "\tdescription:" + ta.getDescription().replace("\n", " ");
                                return s;
                            });
                            return stream;
                        })
                        .collect(Collectors.joining("\n"));
                        photo.setNote(note);


                        Supplier<TreeSet<EntityAnnotation>> supplier = () -> {
                            return new TreeSet<>(comparatorEntityAnnotationByBoundingPoly);
                        };
                        AtomicInteger R2 = new AtomicInteger(0);
                        AtomicInteger A2 = new AtomicInteger(0);
                        String noteOrdered = response.getResponsesList().stream()
                                .flatMap(air->{
//                                    Set<EntityAnnotation> textAnnotationsOrderedByVertex = air.getTextAnnotationsList()
//                                            .stream()
//                                            .collect(Collectors.toCollection(supplier));
//                                    return textAnnotationsOrderedByVertex.stream();
                                    R2.incrementAndGet();
                                    List<EntityAnnotation> textAnnotationsList = new ArrayList<>();
                                    textAnnotationsList.addAll(air.getTextAnnotationsList());
                                    Collections.sort(textAnnotationsList, comparatorEntityAnnotationByBoundingPoly);
                                    return textAnnotationsList.stream();
                                })
//                                .map(ta->{return ta.getDescription().replace("\n", " ").toLowerCase())
                                .filter(ta->isAdmissibleDescription(ta.getDescription().replace("\n", " ") ))
                                .map(ta->{
                                    int R2n = R2.get();
                                    int A2n = A2.incrementAndGet();
                                    /*
                                     String s = "\t"+R2n+"."+A2n+": "+ "position:" + ta.getBoundingPoly().toString()
                                                .replace("\n", " ")
                                                .replace("x: ", "x:")
                                                .replace("y: ", ",y:")
                                                .replace(" }", "}")
                                                .replace("  ,", ",")
                                                .replace("   ", "")
                                            +", "+ "\tdescription:" + ta.getDescription().replace("\n", " ");
                                     */
                                    return "\tdescription:" + ta.getDescription().replace("\n", " ").toLowerCase();
//                                    return s;
                                })
                                .collect(Collectors.toSet())
                                .stream()
                                .collect(Collectors.joining("\n"));
                        photo.setNote(noteOrdered);





                        /*
                        AnnotateImageResponse response0 = response.getResponses(0);
                        String textAnnotation0 = "";
                        if (response0.getTextAnnotationsCount()>0) {
                            textAnnotation0 = response0.getTextAnnotations(0).getDescription().replace("\n", " ");
                        }
                        String note2 = "\ttextAnnotation.description:"+textAnnotation0+"\n";
                        photo.setNote(note2);
                        */

// System.out.println(i.get() + ": got response for: " + photo.getFilename());

                        int partial = counter.incrementAndGet();
                        float p = partial*1.0f/total*100;
                        if (p<10) {
                            System.out.print(" ");
                        }
                        System.out.printf("%.0f", p);
                        System.out.print("%\b\b\b");

                        return photo;
                    })
                    .collect(Collectors.toMap(GooglePhoto::getFilename, GooglePhoto::getNote))
                ).get();
            System.out.println("");
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return collect;
    }

    private static final Set<String> GOOD_WORDS_FOR_DESCRIPTION = new HashSet<>();
    private static final Set<String> NOT_GOOD_WORDS_FOR_DESCRIPTION = new HashSet<>();
    static {
        NOT_GOOD_WORDS_FOR_DESCRIPTION.addAll(Arrays.asList(
                new String[] {"History", "Chat", "WIN", "LOSE", "Battle", "Bottle", "Bsttle", "Replay", "(", ")",
                        "Team", "Phase", "Replay", "League", "Lv", "Party", "List", "World", "Ranking", "Rank",
                        "Season", "Wain", "I", "II", "ID", "Filter", "All", "Total", "Power",
                        "opponent", "your", "return", "effect", "phase", "SP", "Start", "eset",
                        "BR", "MR", "FR", "days", "ago"
                })
        );
        NOT_GOOD_WORDS_FOR_DESCRIPTION.addAll(Arrays.asList(
        		new String[] { "wam", "totolehero","totolohero", "katyney","alcoor1984","geminisaga","otowan","germangaso",
        				"pvmomc", "sa4ny", "archontes", "arclat","job", "ultragr", "antony75", "leone", "danyel1403"
        		})
		);
    }
    private boolean isAdmissibleDescription(String description) {

    	boolean descriptionContainsGoodWords = GOOD_WORDS_FOR_DESCRIPTION.parallelStream()
    			.anyMatch( s->description.equalsIgnoreCase(s) );
    	if (descriptionContainsGoodWords) {
    		return true;
    	}

        boolean descriptionContainsAnyNotGoodWordsOrViceversa = NOT_GOOD_WORDS_FOR_DESCRIPTION.parallelStream()
    			.anyMatch(s->
    				description.toUpperCase().contains(s.toUpperCase())
//    				||
//    				s.toUpperCase().contains(description.toUpperCase())
				);
        if (descriptionContainsAnyNotGoodWordsOrViceversa) {
            return false;
        }

        // start with number
        if (Character.isDigit(description.charAt(0))) { return false; }

        // not letter
        if (!Character.isLetter(description.charAt(0))) { return false; }

        // is not alphabetic ?
        if (!Character.isAlphabetic(description.charAt(0))) { return false; }

        return true;
    }



    private static final Comparator<BoundingPoly> comparatorBoundingPoly = (o1, o2) -> {
        Vertex o1v1 = o1.getVertices(0);
        Vertex o2v1 = o2.getVertices(0);

        int o1v1x = o1v1.getX();
        int o2v1x = o2v1.getX();
//        System.out.println("o1v1x:"+o1v1x+" "+"o2v1x:"+o2v1x);
        if (o1v1x < o2v1x) {
//            System.out.println("o1v1x:"+o1v1x+" < "+"o2v1x:"+o2v1x+" -> 1");
            return -1;
        }
        if (o1v1x > o2v1x) {
//            System.out.println("o1v1x:"+o1v1x+" > "+"o2v1x:"+o2v1x+" -> -1");
            return 1;
        }

        int o1v1y = o1v1.getY();
        int o2v1y = o2v1.getY();
        if (o1v1y < o2v1y) {
//            System.out.println("o1v1y:"+o1v1y+" < "+"o2v1y:"+o2v1y+" -> 1");
            return -1;
        }
        if (o1v1y > o2v1y) {
//            System.out.println("o1v1y:"+o1v1y+" > "+"o2v1y:"+o2v1y+" -> -1");
            return 1;
        }

//        System.out.println("o1v1x:"+o1v1x+" = "+"o2v1x:"+o2v1x+" -> 0");
        return 0;
    };
    private static final Comparator<EntityAnnotation> comparatorEntityAnnotationByBoundingPoly = (o1, o2) -> {
        BoundingPoly o1BoundingPoly = o1.getBoundingPoly();
        BoundingPoly o2BoundingPoly = o2.getBoundingPoly();
        return comparatorBoundingPoly.compare(o1BoundingPoly, o2BoundingPoly);
    };
}
