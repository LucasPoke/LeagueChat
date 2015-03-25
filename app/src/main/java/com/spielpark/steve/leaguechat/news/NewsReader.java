package com.spielpark.steve.leaguechat.news;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Steve on 1/30/2015.
 */
public class NewsReader {
    private static final String source = "http://www.reddit.com/r/leagueoflegends/.rss";
    Element feed;

    public NewsReader() {
        // TODO: read news from various languages.
        SAXBuilder jdomBuilder = new SAXBuilder();
        try {
            Document jdomDocument = jdomBuilder.build(source);
            feed = jdomDocument.getRootElement().getChild("channel");
        } catch(JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getTitlesAndLink(int num) {
        Map<String, String> titles = new LinkedHashMap<>();
        List<Element> threads = feed.getChildren("item");
        for (int i = 0; i < num; i++) {
            titles.put(threads.get(i).getChildText("title"), threads.get(i).getChildText("link"));
        }
        return titles;
    }


}
