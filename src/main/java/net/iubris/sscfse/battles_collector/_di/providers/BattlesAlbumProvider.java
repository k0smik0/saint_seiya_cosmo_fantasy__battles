/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.iubris.sscfse.battles_collector.Config;
import net.iubris.sscfse.battles_collector.dao.Queries;
import net.iubris.sscfse.battles_collector.model.Album;

/**
 * @author massimiliano.leone@iubris.net
 *
 * 20 May 2019
 */
@Singleton
public class BattlesAlbumProvider extends AlbumProvider {

	@Inject
	public BattlesAlbumProvider(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
    protected Query createQuery() {
        Query q = entityManager.createQuery(Queries.ALBUM_BY_NAME, Album.class);
        q.setParameter("name", Config.SSCFSE_BATTLES_ALBUM_TITLE);
        return q;
    }

}
