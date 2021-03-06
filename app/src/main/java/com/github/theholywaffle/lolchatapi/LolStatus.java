/*******************************************************************************
 * Copyright (c) 2014 Bert De Geyter (https://github.com/TheHolyWaffle).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Bert De Geyter (https://github.com/TheHolyWaffle)
 ******************************************************************************/
package com.github.theholywaffle.lolchatapi;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;
public class LolStatus {
    public enum Division {
        NONE,
        I,
        II,
        III,
        IV,
        V;
    }
    public enum GameStatus {
        TEAM_SELECT("teamSelect", "In Team Select", 2),
        HOSTING_NORMAL_GAME("hostingNormalGame", "Hosting Normal Game", 1),
        HOSTING_PRACTICE_GAME("hostingPracticeGame", "Hosting Practice Game", 1),
        HOSTING_RANKED_GAME("hostingRankedGame", "Hosting Ranked Game", 1),
        HOSTING_COOP_VS_AI_GAME("hostingCoopVsAIGame", "Hosting Co-op Vs A.I. Game", 1),
        IN_QUEUE("inQueue", "In Queue", 1),
        SPECTATING("spectating", "Spectating", 1),
        OUT_OF_GAME("outOfGame", "Online", 0),
        CHAMPION_SELECT("championSelect", "In Champ Select", 2),
        IN_GAME("inGame", "In Game", 3),
        IN_TEAMBUILDER("inTeamBuilder", "In Team Builder", 1),
        TUTORIAL("tutorial", "In...Tutorial?", 4),
        AWAY("away", "Away", 4),
        GROUP("gg", "gg", -1);

        private String internal;
        private String realTalk;
        private int order;
        public String realTalk() {
            return realTalk;
        }

        public int order() {
            return order;
        }
        GameStatus(String internal, String realTalk, int order) {
            this.internal = internal;
            this.realTalk = realTalk;
            this.order = order;
        }
        public String internal() {
            return internal;
        }
    }
    public enum Queue {
        NONE("None"),
        NORMAL("Normal"),
        NORMAL_3x3("3v3"),
        ODIN_UNRANKED("Dominion"),
        ARAM_UNRANKED_5x5("ARAM"),
        BOT("Bot"),
        BOT_3x3("Bot(3v3)"),
        RANKED_SOLO_5x5("Ranked"),
        RANKED_TEAM_3x3("Ranked(3v3)"),
        RANKED_TEAM_5x5("Ranked5's"),
        ONEFORALL_5x5("One For All"),
        FIRSTBLOOD_1x1("FirstBlood"),
        FIRSTBLOOD_2x2("FirstBlood(2v2)"),
        SR_6x6("Hexakill"),
        CAP_5x5("TeamBuilder"),
        URF("URF"),
        URF_BOT("URFvsAI"),
        NIGHTMARE_BOT("Nightmare Bots");
        private String desc;
        Queue(String desc) {
            this.desc = desc;
        }
        public String desc() {
            return desc;
        }
    }
    public enum Tier {
        UNRANKED,
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM,
        DIAMOND,
        MASTER,
        CHALLENGER;
    }
    private enum XMLProperty {
        level,
        rankedLeagueDivision,
        rankedLosses,
        rankedRating,
        leaves,
        gameQueueType,
        skinname,
        profileIcon,
        rankedLeagueQueue,
        tier,
        rankedLeagueName,
        queueType,
        timeStamp,
        rankedWins,
        odinLeaves,
        dropInSpectateGameId,
        statusMsg,
        rankedLeagueTier,
        featuredGameData,
        odinWins,
        wins,
        gameStatus,
        isObservable,
        mobile,
        championMasteryScore,
        rankedSoloRestricted;
        @Override
        public String toString() {
            return name();
        }
    }
    private static final XMLOutputter outputter = new XMLOutputter();
    private final Document doc;

    public LolStatus() {
        outputter
                .setFormat(outputter.getFormat().setExpandEmptyElements(false));
        doc = new Document(new Element("body"));
        for (final XMLProperty p : XMLProperty.values()) {
            doc.getRootElement().addContent(new Element(p.toString()));
        }
    }
    /**
     * This constructor is not intended for usage.
     *
     * @param xml
     * An XML string
     * @throws JDOMException
     * Is thrown when the xml string is invalid
     * @throws IOException
     * Is thrown when the xml string is invalid
     */
    public LolStatus(String xml) throws JDOMException, IOException {
        outputter
                .setFormat(outputter.getFormat().setExpandEmptyElements(false));
        final SAXBuilder saxBuilder = new SAXBuilder();
        doc = saxBuilder.build(new StringReader(xml));
        for (final Element e : doc.getRootElement().getChildren()) {
            boolean found = false;
            for (final XMLProperty p : XMLProperty.values()) {
                if (p.name().equals(e.getName())) {
                    found = true;
                }
            }
            if (!found) {
                System.err.println("XMLProperty \"" + e.getName()
                        + "\" value: \"" + e.getValue()
                        + "\" not implemented yet!");
            }
        }
    }
    private String get(XMLProperty p) {
        final Element child = getElement(p);
        if (child == null) {
            return "";
        }
        return child.getValue();
    }
    public int getDominionLeaves() {
        return getInt(XMLProperty.odinLeaves);
    }
    public int getDominionWins() {
        return getInt(XMLProperty.odinWins);
    }
    private Element getElement(XMLProperty p) {
        return doc.getRootElement().getChild(p.toString());
    }
    public String getFeaturedGameData() {
        return get(XMLProperty.featuredGameData);
    }
    public Queue getGameQueueType() {
        final String type = get(XMLProperty.gameQueueType);
        if (!type.isEmpty()) {
            for (final Queue s : Queue.values()) {
                if (s.name().equals(type)) {
                    return s;
                }
            }
            System.err
                    .println("GameStatus " + type + " not implemented yet!");
        }
        return Queue.NONE;
    }
    public GameStatus getGameStatus() {
        final String status = get(XMLProperty.gameStatus);
        if (!status.isEmpty()) {
            for (final GameStatus s : GameStatus.values()) {
                if (s.internal.equals(status)) {
                    return s;
                }
            }
            System.err
                    .println("GameStatus " + status + " not implemented yet!");
        }
        return null;
    }
    private int getInt(XMLProperty p) {
        final String value = get(p);
        if (value.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(value);
    }
    public int getLevel() {
        return getInt(XMLProperty.level);
    }
    private long getLong(XMLProperty p) {
        final String value = get(p);
        if (value.isEmpty()) {
            return -1L;
        }
        return Long.parseLong(value);
    }

    /**
     * @return the current time a friend has been ingame.
     */
    public long getGameTime() {
        Date gameTime = getTimestamp();
        if (gameTime != null) {
            return (System.currentTimeMillis() - gameTime.getTime());
        } else {
            return 0;
        }
    }
    public int getMasteryScore () { return getInt(XMLProperty.championMasteryScore);}
    public int getNormalLeaves() {
        return getInt(XMLProperty.leaves);
    }
    public int getNormalWins() {
        return getInt(XMLProperty.wins);
    }
    public int getProfileIconId() {
        return getInt(XMLProperty.profileIcon);
    }
    /**
     * Seems like an unused variable of Riot
     *
     * @return Empty string
     */
    @Deprecated
    public String getQueueType() {
        return get(XMLProperty.queueType);
    }
    public Division getRankedLeagueDivision() {
        final String div = get(XMLProperty.rankedLeagueDivision);
        if (!div.isEmpty()) {
            return Division.valueOf(div);
        }
        return Division.NONE;
    }
    public String getRankedLeagueName() {
        return get(XMLProperty.rankedLeagueName);
    }
    public String getRankedLeagueQueue() {
        return get(XMLProperty.rankedLeagueQueue);
    }
    public Tier getRankedLeagueTier() {
        final String tier = get(XMLProperty.rankedLeagueTier);
        if (!tier.isEmpty()) {
            return Tier.valueOf(tier);
        }
        return Tier.UNRANKED;
    }
    /**
     * Seems like an unused variable of Riot.
     *
     * @return 0
     */
    @Deprecated
    public int getRankedLosses() {
        return getInt(XMLProperty.rankedLosses);
    }
    /**
     * Seems like an unused variable of Riot.
     *
     * @return 0
     */
    @Deprecated
    public int getRankedRating() {
        return getInt(XMLProperty.rankedRating);
    }
    public int getRankedWins() {
        return getInt(XMLProperty.rankedWins);
    }
    public String getSkin() {
        return get(XMLProperty.skinname);
    }
    public String getSpectatedGameId() {
        return get(XMLProperty.dropInSpectateGameId);
    }
    public String getStatusMessage() {
        return get(XMLProperty.statusMsg);
    }
    public Tier getTier() {
        final String tier = get(XMLProperty.tier);
        if (!tier.isEmpty()) {
            return Tier.valueOf(tier);
        }
        return Tier.UNRANKED;
    }
    public Date getTimestamp() {
        final long l = getLong(XMLProperty.timeStamp);
        if (l > 0) {
            return new Date(l);
        }
        return null;
    }
    public boolean isObservable() {
        return get(XMLProperty.isObservable).equals("ALL");
    }
    public LolStatus setDominionLeaves(int leaves) {
        setElement(XMLProperty.odinLeaves, leaves);
        return this;
    }
    public LolStatus setDominionWins(int wins) {
        setElement(XMLProperty.odinWins, wins);
        return this;
    }
    private void setElement(XMLProperty p, int value) {
        setElement(p, String.valueOf(value));
    }
    private void setElement(XMLProperty p, long value) {
        setElement(p, String.valueOf(value));
    }
    private void setElement(XMLProperty p, String value) {
        getElement(p).setText(value);
    }
    private void setElement(XMLProperty p, boolean value) {
        setElement(p, String.valueOf(value));
    }
    public LolStatus setFeaturedGameData(String data) {
        setElement(XMLProperty.featuredGameData, data);
        return this;
    }
    public LolStatus setGameQueueType(Queue q) {
        return setGameQueueType(q.name());
    }
    public LolStatus setGameQueueType(String q) {
        setElement(XMLProperty.gameQueueType, q);
        return this;
    }
    public void setMasteryScore (int i) { setElement(XMLProperty.championMasteryScore, i);}
    public LolStatus setGameStatus(GameStatus s) {
        setElement(XMLProperty.gameStatus, s.internal);
        return this;
    }
    public LolStatus setLevel(int level) {
        setElement(XMLProperty.level, level);
        return this;
    }
    public LolStatus setNormalLeaves(int leaves) {
        setElement(XMLProperty.leaves, leaves);
        return this;
    }
    public LolStatus setNormalWins(int wins) {
        setElement(XMLProperty.wins, wins);
        return this;
    }
    public LolStatus setObservable() {
        setElement(XMLProperty.isObservable, "ALL");
        return this;
    }
    public LolStatus setProfileIconId(int id) {
        setElement(XMLProperty.profileIcon, id);
        return this;
    }
    @Deprecated
    public LolStatus setQueueType(Queue q) {
        setElement(XMLProperty.queueType, q.name());
        return this;
    }
    public LolStatus setRankedLeagueDivision(Division d) {
        setElement(XMLProperty.rankedLeagueDivision, d.name());
        return this;
    }
    public LolStatus setRankedLeagueName(String name) {
        setElement(XMLProperty.rankedLeagueName, name);
        return this;
    }
    public LolStatus setRankedLeagueQueue(Queue q) {
        setElement(XMLProperty.rankedLeagueQueue, q.name());
        return this;
    }
    public LolStatus setRankedLeagueTier(Tier t) {
        setElement(XMLProperty.rankedLeagueTier, t.name());
        return this;
    }
    @Deprecated
    public LolStatus setRankedLosses(int losses) {
        setElement(XMLProperty.rankedLosses, losses);
        return this;
    }
    @Deprecated
    public LolStatus setRankedRating(int rating) {
        setElement(XMLProperty.rankedRating, rating);
        return this;
    }
    public LolStatus setRankedWins(int wins) {
        setElement(XMLProperty.rankedWins, wins);
        return this;
    }
    public LolStatus setSkin(String name) {
        setElement(XMLProperty.skinname, name);
        return this;
    }
    public LolStatus setSpectatedGameId(String id) {
        setElement(XMLProperty.dropInSpectateGameId, id);
        return this;
    }
    public LolStatus setStatusMessage(String message) {
        setElement(XMLProperty.statusMsg, message);
        return this;
    }
    public LolStatus setTier(Tier t) {
        setElement(XMLProperty.tier, t.name());
        return this;
    }
    public LolStatus setTimestamp(Date date) {
        return setTimestamp(date.getTime());
    }
    public LolStatus setTimestamp(long date) {
        setElement(XMLProperty.timeStamp, date);
        return this;
    }
    public LolStatus setMobile(String mobile) {
        setElement(XMLProperty.mobile, mobile);
        return this;
    }
    public String getMobile() {
        return get(XMLProperty.mobile);
    }
    public LolStatus setRankedSoloRestricted(boolean rankedSoloRestricted) {
        setElement(XMLProperty.rankedSoloRestricted, rankedSoloRestricted);
        return this;
    }
    public boolean getRankedSoloRestricted() {
        return Boolean.valueOf(get(XMLProperty.rankedSoloRestricted));
    }
    @Override
    public String toString() {
        return outputter.outputString(doc.getRootElement());
    }
}