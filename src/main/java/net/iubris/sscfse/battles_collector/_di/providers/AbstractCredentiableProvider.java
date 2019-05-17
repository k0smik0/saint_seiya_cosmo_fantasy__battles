/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

/**
 * @author massimiliano.leone - massimiliano.leone@iubris.net
 *
 * 13 May 2019
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractCredentiableProvider<T, eCP extends AbstractCredentiableProvider> extends AbstractInitableProvider<T, eCP> {

	protected String credentialsPath;

	@SuppressWarnings("unchecked")
	public eCP setCredentialsPath(String credentialsPath) {
		this.credentialsPath = credentialsPath;
		return (eCP) this;
	}
}
