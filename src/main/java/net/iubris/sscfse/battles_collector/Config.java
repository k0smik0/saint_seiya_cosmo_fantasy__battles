/**
 *
 */
package net.iubris.sscfse.battles_collector;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 */
public class Config {

    public static final List<String> REQUIRED_SCOPES =
            ImmutableList.of(
                    "https://www.googleapis.com/auth/photoslibrary.readonly",
                    "https://www.googleapis.com/auth/photoslibrary.appendonly");


    // project:
    // https://console.developers.google.com/apis/credentials?project=project-id-0637669633636693062

    public static final String SSCFSE_BATTLES_ALBUM_TITLE = "sscfse - win/lose battles";

    public static final String APPLICATION_NAME = "sscfse - battles populator";

}
