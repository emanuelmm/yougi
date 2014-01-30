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
package org.cejug.yougi.knowledge.business;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;

/**
 * Business logic dealing with articles from a web source.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class ArticleBean extends AbstractBean<Article> {

    private static final Logger LOGGER = Logger.getLogger(ArticleBean.class.getName());

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
            return em.createQuery("select a from Article a where a.published = :published order by a.publication desc")
                     .setParameter("published", Boolean.TRUE)
                     .getResultList();
    }

    public List<Article> findPublishedArticles(WebSource webSource) {
            return em.createQuery("select a from Article a where a.webSource = :webSource order by a.title asc")
                                 .setParameter("webSource", webSource)
                                 .getResultList();
    }

    public void publish(Article article) {
        article.setPublished(Boolean.TRUE);
        save(article);
    }

    public void unpublish(Article article) {
        remove(article.getId());
    }

    /**
     * @param webSource a web source that contains details about a user website.
     * @return a list of articles found in the informed web source.
     * */
    public WebSource loadWebSource(WebSource webSource) {
        List<Article> unpublishedArticles;

        String feedUrl = findWebsiteFeedURL(webSource.getProvider().getWebsite());

        try {
            URL url  = new URL(feedUrl);
            XmlReader reader = new XmlReader(url);
            SyndFeed feed = new SyndFeedInput().build(reader);
            webSource.setTitle(feed.getTitle());
            webSource.setFeed(feedUrl);
            unpublishedArticles = new ArrayList<>();
            Article article;
            for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
                SyndEntry entry = (SyndEntry) i.next();

                article = new Article();
                article.setTitle(entry.getTitle());
                article.setPermanentLink(entry.getLink());
                article.setAuthor(entry.getAuthor());
                article.setPublication(entry.getPublishedDate());
                article.setWebSource(webSource);
                if(entry.getDescription() != null) {
                    article.setSummary(entry.getDescription().getValue());
                }
                SyndContent syndContent;
                StringBuilder content = new StringBuilder();
                for(int j = 0;j < entry.getContents().size();j++) {
                    syndContent = (SyndContent) entry.getContents().get(j);
                    content.append(syndContent.getValue());
                }
                article.setContent(content.toString());

                unpublishedArticles.add(article);
            }

            // Remove from the list of unpublished articles the ones that are already published.
            List<Article> publishedArticles = findPublishedArticles(webSource);
            for(Article publishedArticle: publishedArticles) {
                unpublishedArticles.remove(publishedArticle);
            }

            webSource.setArticles(unpublishedArticles);

        } catch (IllegalArgumentException | FeedException | IOException iae) {
            LOGGER.log(Level.SEVERE, iae.getMessage(), iae);
        }

        return webSource;
    }

    /**
     * @param urlWebsite url used to find the web content where there is probably a feed to be consumed.
     * @return if a feed url is found in the web content, it is returned. Otherwise, the method returns null.
     * */
    public String findWebsiteFeedURL(String urlWebsite) {
        String feedUrl = null;
        String websiteContent = retrieveWebsiteContent(urlWebsite);

        if(websiteContent == null) {
            return null;
        }

        Pattern urlPattern = Pattern.compile("https?://(www)?(\\.?\\w+)+(/\\w+)*");
        Matcher matcher = urlPattern.matcher(websiteContent);

        while (matcher.find()) {
            feedUrl = matcher.group();
            if(isFeedURL(feedUrl)) {
                break;
            }
        }

        return feedUrl;
    }

    /**
     * @param url candidate to be a feed url.
     * @return true if the informed url is actually a feed, false otherwise.
     * */
    private boolean isFeedURL(String url) {
        String lowerCaseUrl = url.toLowerCase();
        if(lowerCaseUrl.contains("feed") || lowerCaseUrl.contains("rss") || lowerCaseUrl.contains("atom")) {
            return true;
        }
        return false;
    }

    /**
     * @param urlWebsite url used to find the web content where there is probably a feed to be consumed.
     * @return the entire content, which or without url feed.
     * */
    private String retrieveWebsiteContent(String url) {
        StringBuilder content = null;
        String fullUrl = url;
        if(!url.contains("http")) {
            fullUrl = "http://" + url;
        }

        if(fullUrl != null) {
            try {
                URL theUrl = new URL(fullUrl);
                BufferedReader br = new BufferedReader(new InputStreamReader(theUrl.openStream()));
                String line = "";
                content = new StringBuilder();
                while(null != (line = br.readLine())) {
                    content.append(line);
                }
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return content != null ? content.toString() : null;
    }
}