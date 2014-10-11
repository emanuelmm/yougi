/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.yougi.knowledge.business;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import org.yougi.business.AbstractBean;
import org.yougi.knowledge.entity.Article;
import org.yougi.knowledge.entity.QArticle;
import org.yougi.knowledge.entity.WebSource;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Business logic dealing with articles from a web source.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class ArticleBean extends AbstractBean<Article> {

    @PersistenceContext
    private EntityManager em;

    public ArticleBean() {
        super(Article.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Article> findPublishedArticles() {
        JPAQuery query = new JPAQuery(em);
        QArticle article = QArticle.article;
        return query.from(article)
                    .where(article.published.isTrue())
                    .orderBy(article.publication.desc()).list(article);
    }

    public List<Article> findPublishedArticles(WebSource webSource) {
        JPAQuery query = new JPAQuery(em);
        QArticle article = QArticle.article;

        return query.from(article)
                    .where(article.webSource.eq(webSource))
                    .orderBy(article.publication.desc())
                    .list(article);
    }

    public List<Article> findPublishedArticles(Article except) {
        JPAQuery query = new JPAQuery(em);
        QArticle qArticle = QArticle.article;
        query = query.from(qArticle);

        BooleanBuilder criteria = new BooleanBuilder();
        criteria.and(qArticle.id.ne(except.getId()));

        if(except.getWebSource() != null) {
            criteria.and(qArticle.webSource.id.eq(except.getWebSource().getId()));
        }

        return query.where(criteria).orderBy(qArticle.publication.desc()).list(qArticle);
    }

    public List<Article> findOtherPublishedArticles(WebSource except) {
        JPAQuery query = new JPAQuery(em);
        QArticle qArticle = QArticle.article;
        return query.from(qArticle).where(qArticle.webSource.ne(except)).orderBy(qArticle.publication.desc()).list(qArticle);
    }

    public void publish(Article article) {
        article.setPublished(Boolean.TRUE);
        save(article);
    }

    public void unpublish(Article article) {
        remove(article.getId());
    }
}