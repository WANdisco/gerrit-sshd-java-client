package com.wandisco.gerrit.client.sshd.commands.projects;

/*-
 * #%L
 * gerrit-sshd-java-client
 * %%
 * Copyright (C) 2021 - 2024 WANdisco
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.wandisco.gerrit.client.sshd.commands.projects.LsProjectsOptions.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LsProjectsOptionsTest {

    /**
     * Test {@link LsProjectsOptions#getFlag()}.
     * <p>
     * Method under test: {@link LsProjectsOptions#getFlag()}
     */
    @Test
    @DisplayName("Test getFlag()")
    void testGetFlag() {
        // Arrange
        LsProjectsOptions valueOfResult = LsProjectsOptions.valueOf("BRANCH");

        // Act
        String actualFlag = valueOfResult.getFlag();

        // Assert
        assertEquals("--show-branch", actualFlag);
    }

    /**
     * Test Type {@link Type#toString()}.
     * <p>
     * Method under test: {@link LsProjectsOptions.Type#toString()}
     */
    @Test
    @DisplayName("Test Type toString()")
    void testTypeToString() {
        // Arrange
        LsProjectsOptions.Type valueOfResult = LsProjectsOptions.Type.valueOf("CODE");

        // Act
        String actualToStringResult = valueOfResult.toString();

        // Assert
        assertEquals("code", actualToStringResult);
    }
}
