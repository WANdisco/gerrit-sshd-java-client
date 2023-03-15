package com.wandisco.gerrit.client.sshd.commands.projects;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.commands.exceptions.GerritCommandException;
import com.wandisco.gerrit.client.sshd.commands.projects.SetHeadCommand.SetHeadCommandBuilder;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class SetHeadCommandTest {

    @Test
    void testConstructor() {
        String project = "Project";
        String newHeadRef = "New Head Ref";
        SetHeadCommand actualSetHeadCommand = new SetHeadCommand(project, newHeadRef);
        assertEquals("New Head Ref", actualSetHeadCommand.getNewHeadRef());
        assertEquals("Project", actualSetHeadCommand.getProject());
    }

    @Test
    void testConstructor2() {

        String project = "Project";
        String newHeadRef = "New Head Ref";
        SetHeadCommand actualSetHeadCommand = new SetHeadCommand(project, newHeadRef);
        assertEquals("gerrit set-head Project --new-head New Head Ref", actualSetHeadCommand.getCommand());
        assertFalse(actualSetHeadCommand.isTimedOut());
        assertFalse(actualSetHeadCommand.isSuccessful());
        assertFalse(actualSetHeadCommand.isComplete());
        assertEquals("Project", actualSetHeadCommand.getProject());
        assertTrue(actualSetHeadCommand.getOutputList().isEmpty());
        assertEquals("New Head Ref", actualSetHeadCommand.getNewHeadRef());
    }

    @Test
    void testGetCommand() {
        SetHeadCommand setHeadCommand = new SetHeadCommand("Project", "New Head Ref");
        String actualCommand = setHeadCommand.getCommand();
        assertEquals("gerrit set-head Project --new-head New Head Ref", actualCommand);
    }

    @Test
    void testGetCommand2() {

        SetHeadCommand setHeadCommand = new SetHeadCommand("", "");

        String actualCommand = setHeadCommand.getCommand();

        assertEquals("gerrit set-head ", actualCommand);
    }

    @Test
    void testGetCommand3() {
        SetHeadCommand setHeadCommand = new SetHeadCommand("Project", "");
        String actualCommand = setHeadCommand.getCommand();
        assertEquals("gerrit set-head ", actualCommand);
    }

    @Test
    void testGetCommand4() {
        SetHeadCommand setHeadCommand = new SetHeadCommand("", "newHEAD");
        String actualCommand = setHeadCommand.getCommand();
        assertEquals("gerrit set-head ", actualCommand);
    }

    @Test
    void testGetCommand5() {
        SetHeadCommand setHeadCommand = new SetHeadCommand("", "newHEAD");
        setHeadCommand.setCommand("badEnding");
        String actualCommand = setHeadCommand.getCommand();
        assertEquals("gerrit set-head ", actualCommand);
    }

    @Test
    void testSetHeadCommandBuilderConstructor() {
        GerritCommandException thrown = assertThrows(GerritCommandException.class, () -> new SetHeadCommandBuilder().build());
        assertEquals("Required Param absent:- projectSet = false, newHeadRefSet = false", thrown.getMessage());
    }

    @Test
    void testSetHeadCommandBuilderConstructorProjectOnly() {
        GerritCommandException thrown =
            assertThrows(GerritCommandException.class, () -> new SetHeadCommandBuilder().project("Project").build());
        assertEquals("Required Param absent:- projectSet = true, newHeadRefSet = false", thrown.getMessage());
    }

    @Test
    void testSetHeadCommandBuilderConstructorHeadOnly() {
        GerritCommandException thrown =
            assertThrows(GerritCommandException.class, () -> new SetHeadCommandBuilder().newHeadRef("newRef").build());
        assertEquals("Required Param absent:- projectSet = false, newHeadRefSet = true", thrown.getMessage());
    }

    @Test
    void assertBuilderEqualsCommand() {
        String projectName = "project1";
        String newHead = "master2";
        SetHeadCommand basicCommand = new SetHeadCommand(projectName, newHead);
        AtomicReference<SetHeadCommand> builderCommand = new AtomicReference<>();
        assertDoesNotThrow(() -> builderCommand.set(new SetHeadCommandBuilder().project(projectName).newHeadRef(newHead).build()));
        builderCommand.get();
        assertEquals(basicCommand.getCommand(), builderCommand.get().getCommand(), "Builder command did not match constructor command");
    }
}

