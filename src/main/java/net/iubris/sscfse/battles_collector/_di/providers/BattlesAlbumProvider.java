/**
 *
 */
package net.iubris.sscfse.battles_collector._di.providers;

import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.iubris.sscfse.battles_collector.Config;
import net.iubris.sscfse.battles_collector.model.Album;

/**
 * @author massimiliano.leone@iubris.net
 *
 * 20 May 2019
 */
public class BattlesAlbumProvider implements Provider<Album> {

    private EntityManager entityManager;

    private BattlesAlbumProvider(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Query createQuery() {
        Query q = entityManager.createQuery("SELECT a FROM Album a WHERE a.name = :threshold", Album.class);
        q.setParameter("name", Config.SSCFSE_BATTLES_ALBUM_TITLE);
        return q;
    }

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
