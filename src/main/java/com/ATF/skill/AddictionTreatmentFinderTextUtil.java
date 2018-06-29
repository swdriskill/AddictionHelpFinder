/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package main.java.com.ATF.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Util containing various text related utils.
 */
public final class AddictionTreatmentFinderTextUtil {
    private static final Logger log = LoggerFactory.getLogger(AddictionTreatmentFinderTextUtil.class);

    private AddictionTreatmentFinderTextUtil () {
    }

    /**
     * List of player names blacklisted for this app.
     */
    private static final List<String> NAME_BLACKLIST = Arrays.asList("player", "players");


    /**
    /**
     * Cleans up the player name, and sanitizes it against the blacklist.
     *
     * @param recognizedUserName
     * @return
     */
    public static String getUserName (String recognizedUserName) {
        log.debug("Complete name provided is : " + recognizedUserName);
        if (recognizedUserName == null || recognizedUserName.isEmpty()) {
            return null;
        }

        String cleanedName;
        if (recognizedUserName.contains(" ")) {
            // the name should only contain a first name, so ignore the second part if any
            cleanedName = recognizedUserName.substring(recognizedUserName.indexOf(" "));
        } else {
            cleanedName = recognizedUserName;
        }
        log.debug("Cleaned name sent back by the util is : " + cleanedName);

        // if the name is on our blacklist, it must be mis-recognition
        if (NAME_BLACKLIST.contains(cleanedName)) {
            return null;
        }

        return cleanedName;
    }
}
