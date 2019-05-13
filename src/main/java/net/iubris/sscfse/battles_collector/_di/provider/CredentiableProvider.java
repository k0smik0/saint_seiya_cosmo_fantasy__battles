/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import javax.inject.Provider;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 13 May 2019
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class CredentiableProvider<T, eP extends CredentiableProvider> implements Provider<T> {

    public abstract eP init(String credentialsPath) throws Exception;

}
