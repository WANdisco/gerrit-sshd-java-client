package com.wandisco.gerrit.client.sshd.commands.index;

/*-
 * #%L
 * gerrit-sshd-java-client
 * %%
 * Copyright (C) 2021 - 2023 WANdisco
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IndexActivateCommandTest {

    @Test
    void testConstructor() {

        IndexType indexType = IndexType.changes;

        IndexActivateCommand actualIndexActivateCommand = new IndexActivateCommand(indexType);

        assertEquals("gerrit index activate changes", actualIndexActivateCommand.getCommand());
        assertFalse(actualIndexActivateCommand.isTimedOut());
        assertFalse(actualIndexActivateCommand.isSuccessful());
        assertFalse(actualIndexActivateCommand.isComplete());
        assertTrue(actualIndexActivateCommand.getOutputList().isEmpty());
    }

    @Test
    void testConstructor2() {

        IndexType indexType = IndexType.accounts;

        IndexActivateCommand actualIndexActivateCommand = new IndexActivateCommand(indexType);

        assertEquals("gerrit index activate accounts", actualIndexActivateCommand.getCommand());
        assertFalse(actualIndexActivateCommand.isTimedOut());
        assertFalse(actualIndexActivateCommand.isSuccessful());
        assertFalse(actualIndexActivateCommand.isComplete());
        assertTrue(actualIndexActivateCommand.getOutputList().isEmpty());
    }

    @Test
    void testConstructor3() {

        IndexType indexType = IndexType.groups;

        IndexActivateCommand actualIndexActivateCommand = new IndexActivateCommand(indexType);

        assertEquals("gerrit index activate groups", actualIndexActivateCommand.getCommand());
        assertFalse(actualIndexActivateCommand.isTimedOut());
        assertFalse(actualIndexActivateCommand.isSuccessful());
        assertFalse(actualIndexActivateCommand.isComplete());
        assertTrue(actualIndexActivateCommand.getOutputList().isEmpty());
    }

    @Test
    void testConstructor4() {

        IndexType indexType = IndexType.projects;

        IndexActivateCommand actualIndexActivateCommand = new IndexActivateCommand(indexType);

        assertEquals("gerrit index activate projects", actualIndexActivateCommand.getCommand());
        assertFalse(actualIndexActivateCommand.isTimedOut());
        assertFalse(actualIndexActivateCommand.isSuccessful());
        assertFalse(actualIndexActivateCommand.isComplete());
        assertTrue(actualIndexActivateCommand.getOutputList().isEmpty());
    }
}

