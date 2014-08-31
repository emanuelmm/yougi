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
package org.yougi.util;

/**
 * This class groups a set of methods to deal with String
 * that are not already covered by the Java API.
 *
 * @author Daniel Cunha - danielsoro@gmail.com
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean isNullOrBlank(String property) {
        return property == null || property.trim().isEmpty();
    }

    /**
     * Receives a sentence and converts the first letter of each word to a
     * capital letter and the rest of each word to lowercase.
     */
    public static String capitalizeFirstCharWords(String sentence) {
        final StringBuilder result = new StringBuilder(sentence.length());
        String[] words = sentence.split("\\s");
        for (int i = 0, length = words.length; i < length; ++i) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(words[i].charAt(0)))
                    .append(words[i].substring(1).toLowerCase());
        }
        return result.toString();
    }
}