/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.links;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Aliaksei Boole
 */
public class LinksListFilter extends HtmlElement
{

    private final static By BASE_FILTER_ELEMENT = By.xpath("//ul[@class='filterLink']");
    private final static By TAGS_LINK = By.xpath("//ul[@class='filterLink']//span[@class='tag']/a");


    public enum FilterOption
    {
        ALL_LINKS(".//span[@class='all']/a"),
        MY_LINKS(".//span[@class='user']/a"),
        RECENTLY_ADDED(".//span[@class='recent']/a");

        FilterOption(String xpath)
        {
            this.by = By.xpath(xpath);
        }

        public final By by;
    }

    public LinksListFilter(WebDrone drone)
    {
        super(drone);
        setWebElement(drone.findAndWait(BASE_FILTER_ELEMENT));
    }

    /**
     * Mimic select filter type in upper left angle
     *
     * @param option FilterOption
     */
    public LinksPage select(FilterOption option)
    {
            checkNotNull(option);
            findAndWait(option.by).click();
            return new LinksPage(drone).waitUntilAlert().render();
    }

    /**
     * Return List with visible tags name
     *
     * @return List<String>
     */
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        List<WebElement> tagElements = findAllWithWait(TAGS_LINK);
        for (WebElement tagElement : tagElements)
        {
            tags.add(tagElement.getText());
        }
        return tags;
    }

    /**
     * Mimic select tag in left filter panel.
     *
     * @param tagName String
     */
    public LinksPage clickOnTag(String tagName)
    {
        checkNotNull(tagName);
        List<WebElement> tagElements = findAllWithWait(TAGS_LINK);
        for (WebElement tagElement : tagElements)
        {
            if (tagName.equals(tagElement.getText()))
            {
                tagElement.click();
                return new LinksPage(drone).waitUntilAlert().render();
            }
        }
        throw new PageException(String.format("Tag with name[%s] don't found.", tagName));
    }
}
