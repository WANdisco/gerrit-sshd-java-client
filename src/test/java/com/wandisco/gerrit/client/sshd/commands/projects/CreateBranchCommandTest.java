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
import com.wandisco.gerrit.client.sshd.commands.projects.CreateBranchCommand.CreateBranchCommandBuilder;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class CreateBranchCommandTest {

    @Test
    void testConstructor() {

        String project = "Project";
        String branchName = "janedoe/featurebranch";
        String revision = "Revision";

        CreateBranchCommand actualCreateBranchCommand = new CreateBranchCommand(project, branchName, revision);
        String branchName1 = "janedoe/featurebranch";
        actualCreateBranchCommand.setBranchName(branchName1);
        String project1 = "Project";
        actualCreateBranchCommand.setProject(project1);
        String revision1 = "Revision";
        actualCreateBranchCommand.setRevision(revision1);

        assertEquals("janedoe/featurebranch", actualCreateBranchCommand.getBranchName());
        assertEquals("Project", actualCreateBranchCommand.getProject());
        assertEquals("Revision", actualCreateBranchCommand.getRevision());
    }

    @Test
    void testConstructor2() {

        String project = "Project";
        String branchName = "janedoe/featurebranch";
        String revision = "Revision";

        CreateBranchCommand actualCreateBranchCommand = new CreateBranchCommand(project, branchName, revision);

        assertEquals("janedoe/featurebranch", actualCreateBranchCommand.getBranchName());
        assertFalse(actualCreateBranchCommand.isTimedOut());
        assertFalse(actualCreateBranchCommand.isSuccessful());
        assertFalse(actualCreateBranchCommand.isComplete());
        assertEquals("Revision", actualCreateBranchCommand.getRevision());
        assertEquals("Project", actualCreateBranchCommand.getProject());
        assertTrue(actualCreateBranchCommand.getOutputList().isEmpty());
        assertEquals("gerrit create-branch Project janedoe/featurebranch Revision", actualCreateBranchCommand.getCommand());
    }

    @Test
    void testCreateBranchCommandBuilderConstructor() {
        GerritCommandException thrown = assertThrows(GerritCommandException.class, () -> new CreateBranchCommandBuilder().build());
        assertEquals("Required Param absent:- ProjectSet = false, BranchSet = false, RevisionSet = false", thrown.getMessage());
    }

    @Test
    void testCreateBranchCommandBuilderConstructorOnlyProject() {
        GerritCommandException thrown =
            assertThrows(GerritCommandException.class, () -> new CreateBranchCommandBuilder().project("Project").build());
        assertEquals("Required Param absent:- ProjectSet = true, BranchSet = false, RevisionSet = false", thrown.getMessage());
    }

    @Test
    void testCreateBranchCommandBuilderConstructorOnlyBranch() {
        GerritCommandException thrown = assertThrows(GerritCommandException.class, () -> {
            new CreateBranchCommandBuilder().branch("janedoe/featurebranch").build();
        });
        assertEquals("Required Param absent:- ProjectSet = false, BranchSet = true, RevisionSet = false", thrown.getMessage());
    }

    @Test
    void testCreateBranchCommandBuilderConstructorOnlyRevision() {
        GerritCommandException thrown =
            assertThrows(GerritCommandException.class, () -> new CreateBranchCommandBuilder().revision("Revision").build());
        assertEquals("Required Param absent:- ProjectSet = false, BranchSet = false, RevisionSet = true", thrown.getMessage());
    }

    @Test
    void testCreateBranchCommandBuilderConstructorNoProject() {
        GerritCommandException thrown = assertThrows(GerritCommandException.class,
            () -> new CreateBranchCommandBuilder().branch("janedoe/featurebranch").revision("Revision").build());
        assertEquals("Required Param absent:- ProjectSet = false, BranchSet = true, RevisionSet = true", thrown.getMessage());
    }

    @Test
    void testCreateBranchCommandBuilderAll() {
        CreateBranchCommand actualCommand = assertDoesNotThrow(
            () -> new CreateBranchCommandBuilder().project("Project").branch("janedoe/featurebranch").revision("Revision").build());
        assertEquals("gerrit create-branch Project janedoe/featurebranch Revision", actualCommand.getCommand());
    }

    @Test
    void testGetCommand() {

        CreateBranchCommand createBranchCommand = new CreateBranchCommand("Project", "janedoe/featurebranch", "Revision");
        String actualCommand = createBranchCommand.getCommand();
        assertEquals("gerrit create-branch Project janedoe/featurebranch Revision", actualCommand);
    }

    @Test
    void assertBuilderEqualsCommand() {
        String projectName = "project1";
        String branchName = "master2";
        String revision = "Revision";
        CreateBranchCommand basicCommand = new CreateBranchCommand(projectName, branchName, revision);
        AtomicReference<CreateBranchCommand> builderCommand = new AtomicReference<>();
        assertDoesNotThrow(
            () -> builderCommand.set(new CreateBranchCommandBuilder().project(projectName).branch(branchName).revision(revision).build()));
        builderCommand.get();
        assertEquals(basicCommand.getCommand(), builderCommand.get().getCommand(), "Builder command did not match constructor command");
    }
}

