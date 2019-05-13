/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 13 May 2019
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class CredentiableProvider<T, eCP extends CredentiableProvider> extends InitableProvider<T, eCP> {

    protected String credentialsPath;

    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }
}
