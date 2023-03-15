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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.commands.projects.CreateProjectCommand.CreateProjectCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateProjectCommandTest {

    private final static String twoParamString = "%s %s";
    private final String createCommand = "gerrit create-project";
    private final String projectName = "reused";
    private final String parentProjectName = "parentProject";
    private final String projectOwner = "project.Owner";
    private final String projectDescription = "a reused project description";
    private final String projectBranch = "not/master";
    private CreateProjectCommand basicCommand;
    private CreateProjectCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new CreateProjectCommand(projectName);
        commandBuilder = new CreateProjectCommandBuilder(projectName);
    }

    @Test
    void testConstructor() {

        String projectName = "Project Name";

        CreateProjectCommand actualCreateProjectCommand = new CreateProjectCommand(projectName);

        assertEquals("gerrit create-project Project Name", actualCreateProjectCommand.getCommand());
        assertFalse(actualCreateProjectCommand.isTimedOut());
        assertFalse(actualCreateProjectCommand.isSuccessful());
        assertFalse(actualCreateProjectCommand.isComplete());
        assertTrue(actualCreateProjectCommand.getOutputList().isEmpty());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link CreateProjectCommand.CreateProjectCommandBuilder#build()}
     *   <li>{@link CreateProjectCommand.CreateProjectCommandBuilder#branch(String)}
     *   <li>{@link CreateProjectCommand.CreateProjectCommandBuilder#description(String)}
     *   <li>{@link CreateProjectCommand.CreateProjectCommandBuilder#maxObjectSizeLimit(String)}
     *   <li>{@link CreateProjectCommand.CreateProjectCommandBuilder#owner(String)}
     *   <li>{@link CreateProjectCommand.CreateProjectCommandBuilder#parent(String)}
     *   <li>{@link CreateProjectCommand.CreateProjectCommandBuilder#submitType(ProjectSubmitTypes)}
     * </ul>
     */
    @Test
    void testCreateProjectCommandBuilderBuild() {

        CreateProjectCommand.CreateProjectCommandBuilder submitTypeResult =
            (new CreateProjectCommand.CreateProjectCommandBuilder("Project Name")).branch("janedoe/featurebranch")
                .description("The characteristics of someone or something").maxObjectSizeLimit("Max Object Size Limit").owner("Owner")
                .parent("Parent Name").submitType(ProjectSubmitTypes.FAST_FORWARD_ONLY);

        CreateProjectCommand actualBuildResult = submitTypeResult.build();

        assertEquals("gerrit create-project --branch janedoe/featurebranch --description 'The characteristics of someone or"
            + " something' --max-object-size-limit Max Object Size Limit --owner Owner --parent Parent Name --submit-type"
            + " FAST_FORWARD_ONLY Project Name", actualBuildResult.getCommand());
        assertFalse(actualBuildResult.isTimedOut());
        assertFalse(actualBuildResult.isSuccessful());
        assertFalse(actualBuildResult.isComplete());
        assertNull(actualBuildResult.getTimeStarted());
        assertNull(actualBuildResult.getTimeEnded());
        assertTrue(actualBuildResult.getOutputList().isEmpty());
        assertNull(actualBuildResult.getOutput());
        assertEquals(0, actualBuildResult.getExitCode());
        assertNull(actualBuildResult.getDuration());
    }

    @Test
    void testCreateProjectCommandBuilderConstructor() {

        String projectName = "Project Name";

        CreateProjectCommand.CreateProjectCommandBuilder actualCreateProjectCommandBuilder =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName);

        CreateProjectCommand buildResult = actualCreateProjectCommandBuilder.build();
        assertEquals("gerrit create-project Project Name", buildResult.getCommand());
        assertFalse(buildResult.isTimedOut());
        assertFalse(buildResult.isSuccessful());
        assertFalse(buildResult.isComplete());
        assertTrue(buildResult.getOutputList().isEmpty());
    }

    @Test
    void testSetParentName() {

        CreateProjectCommand createProjectCommand = new CreateProjectCommand("Project Name");
        String parentName = "Parent Name";

        createProjectCommand.setParentName(parentName);

        assertEquals("gerrit create-project --parent Parent Name Project Name", createProjectCommand.getCommand());
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(String.format(twoParamString, createCommand, projectName), basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetEmptyCommit() {
        basicCommand.setEmptyCommit();
        assertGetMatchesCommand(CreateProjectOptions.EMPTY_COMMIT.getFlag());
        commandBuilder.emptyCommit();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetParent() {
        basicCommand.setParentName(parentProjectName);
        assertGetMatchesCommand(String.format(twoParamString, CreateProjectOptions.PARENT.getFlag(), parentProjectName));
        commandBuilder.parent(parentProjectName);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetPermissionsOnly() {
        basicCommand.setPermissionsOnly();
        assertGetMatchesCommand(CreateProjectOptions.PERMISSIONS_ONLY.getFlag());
        commandBuilder.permissionsOnly();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetSubmitType() {
        for (ProjectSubmitTypes submitType : ProjectSubmitTypes.values()) {
            basicCommand = new CreateProjectCommand(projectName);
            basicCommand.setSubmitType(submitType);
            commandBuilder = new CreateProjectCommandBuilder(projectName);
            commandBuilder.submitType(submitType);
            assertGetMatchesCommand(String.format(twoParamString, CreateProjectOptions.SUBMIT_TYPE.getFlag(), submitType));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetOwner() {
        basicCommand.setOwner(projectOwner);
        assertGetMatchesCommand(String.format(twoParamString, CreateProjectOptions.OWNER.getFlag(), projectOwner));
        commandBuilder.owner(projectOwner);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetCreateChangeForAllNotTarget() {
        basicCommand.setCreateChangeForAllNotTarget();
        assertGetMatchesCommand(CreateProjectOptions.CREATE_NEW_CHANGE_FOR_ALL_NOT_IN_TARGET.getFlag());
        commandBuilder.createNewChangeForAllNotInTarget();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetRequireChangeId() {
        basicCommand.setRequireChangeId();
        assertGetMatchesCommand(CreateProjectOptions.REQUIRE_CHANGE_ID.getFlag());
        commandBuilder.requireChangeId();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetMaxObjectSizeLimit() {
        basicCommand.setMaxObjectSizeLimit("100m");
        assertGetMatchesCommand(String.format(twoParamString, CreateProjectOptions.MAX_OBJECT_SIZE_LIMIT.getFlag(), "100m"));
        commandBuilder.maxObjectSizeLimit("100m");
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetUseContentMerge() {
        basicCommand.setUseContentMerge();
        assertGetMatchesCommand(CreateProjectOptions.USE_CONTENT_MERGE.getFlag());
        commandBuilder.useContentMerge();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetUseSignedOffBy() {
        basicCommand.setUseSignedOffBy();
        assertGetMatchesCommand(CreateProjectOptions.USE_SIGNED_OFF_BY.getFlag());
        commandBuilder.useSignedOffBy();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetUseContributorAgreements() {
        basicCommand.setUseContributorAgreements();
        assertGetMatchesCommand(CreateProjectOptions.USE_CONTRIBUTOR_AGREEMENTS.getFlag());
        commandBuilder.useContributorAgreement();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetDescription() {
        basicCommand.setDescription(projectDescription);
        assertGetMatchesCommand(String.format("%s '%s'", CreateProjectOptions.DESCRIPTION.getFlag(), projectDescription));
        commandBuilder.description(projectDescription);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetBranch() {
        basicCommand.setBranch(projectBranch);
        assertGetMatchesCommand(String.format(twoParamString, CreateProjectOptions.BRANCH.getFlag(), projectBranch));
        commandBuilder.branch(projectBranch);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testMultipleFlags() {
        basicCommand.setUseContentMerge();
        basicCommand.setEmptyCommit();
        basicCommand.setParentName(parentProjectName);
        assertGetMatchesCommand(
            String.format("%s %s %s %s", CreateProjectOptions.USE_CONTENT_MERGE.getFlag(), CreateProjectOptions.EMPTY_COMMIT.getFlag(),
                CreateProjectOptions.PARENT.getFlag(), parentProjectName));
        commandBuilder.useContentMerge();
        commandBuilder.emptyCommit();
        commandBuilder.parent(parentProjectName);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testRepeatedGetCommand() {
        String startingCommand = basicCommand.getCommand();
        for (int i = 0; i < 10; i++) {
            assertEquals(startingCommand, basicCommand.getCommand());
        }
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s %s", createCommand, flags, projectName), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}

