/**
 *
 */
package net.iubris.sscfse.battles_collector.services.text_annotations_retrievers;

import java.util.function.Function;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;

import net.iubris.sscfse.battles_collector.model.GooglePhoto;

/**
 * @author massimiliano.leone@iubris.net
 *
 *         May 14, 2019
 */
public class AbstractTextAnnotationsRetriever {

	protected final ImageAnnotatorClient imageAnnotatorClient;

	public AbstractTextAnnotationsRetriever(ImageAnnotatorClient imageAnnotatorClient) {
		this.imageAnnotatorClient = imageAnnotatorClient;
	}

	protected final static Function<GooglePhoto, AnnotateImageRequest> annotateImageRequestForGooglePhoto = gp -> {
		String gpUrl = gp.getBaseUrl();
		ImageSource imgSource = ImageSource.newBuilder().setImageUri(gpUrl).build();
		Image img = Image.newBuilder().setSource(imgSource).build();
		Feature feat = Feature.newBuilder()
		        .setType(Type.TEXT_DETECTION)
//		        .setType(Type.LABEL_DETECTION)
//		        .setType(Type.LOGO_DETECTION)
		        .build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		return request;
	};
}
