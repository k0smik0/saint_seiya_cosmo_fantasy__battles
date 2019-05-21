/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.iubris.sscfse.battles_collector.model.Album;

/**
 * @author massimiliano.leone@iubris.net
 *
 * May 20, 2019
 */
public abstract class AlbumProvider implements Provider<Album> {

	protected EntityManager entityManager;

    protected AlbumProvider(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected abstract Query createQuery();

	@Override
	public Album get() {
		@SuppressWarnings("unchecked")
        List<Album> resultList = createQuery().getResultList();
        if (resultList!=null && resultList.size()>0) {
            return resultList.get(0);
        }
        return null;
	}

}
