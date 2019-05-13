/**
 *
 */
package net.iubris.sscfse.battles_collector._di.provider;

import javax.inject.Provider;

/**
 * @author massimiliano.leone@iubris.net
 *
 * 13 May 2019
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractInitableProvider<T,eICP extends AbstractInitableProvider> implements Provider<T> {
    protected abstract eICP init() throws Exception;
}
