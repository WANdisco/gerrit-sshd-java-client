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

import com.wandisco.gerrit.client.sshd.commands.projects.SetProjectCommand.ProjectSettingOption;
import com.wandisco.gerrit.client.sshd.commands.projects.SetProjectCommand.ProjectState;
import com.wandisco.gerrit.client.sshd.commands.projects.SetProjectCommand.SetProjectCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SetProjectCommandTest {

    private final static String twoParamString = "%s %s";
    private final String setCommand = "gerrit set-project";
    private final String projectName = "reused";
    private final String projectDescription = "a reused project description";
    private SetProjectCommand basicCommand;
    private SetProjectCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new SetProjectCommand(projectName);
        commandBuilder = new SetProjectCommandBuilder(projectName);
    }

    @Test
    void testConstructor() {

        String projectName = "Project Name";

        SetProjectCommand actualSetProjectCommand = new SetProjectCommand(projectName);

        assertEquals("gerrit set-project Project Name", actualSetProjectCommand.getCommand());
        assertFalse(actualSetProjectCommand.isTimedOut());
        assertFalse(actualSetProjectCommand.isSuccessful());
        assertFalse(actualSetProjectCommand.isComplete());
        assertTrue(actualSetProjectCommand.getOutputList().isEmpty());
    }

    @Test
    void testSetProjectCommandBuilderBuild() {

        SetProjectCommand.SetProjectCommandBuilder submitTypeResult =
            (new SetProjectCommand.SetProjectCommandBuilder("Project Name")).changeId(SetProjectCommand.ProjectSettingOption.TRUE)
                .contentMerge(SetProjectCommand.ProjectSettingOption.TRUE).contributorAgreement(SetProjectCommand.ProjectSettingOption.TRUE)
                .description("The characteristics of someone or something").maxObjectSizeLimit("Max Object Size Limit")
                .signedOffBy(SetProjectCommand.ProjectSettingOption.TRUE).state(SetProjectCommand.ProjectState.ACTIVE)
                .submitType(ProjectSubmitTypes.FAST_FORWARD_ONLY);

        SetProjectCommand actualBuildResult = submitTypeResult.build();

        assertEquals("gerrit set-project --change-id TRUE --content-merge TRUE --contributor-agreements TRUE --description"
            + " 'The characteristics of someone or something' --max-object-size-limit Max Object Size Limit --signed-off-by"
            + " TRUE --project-state ACTIVE --submit-type FAST_FORWARD_ONLY Project Name", actualBuildResult.getCommand());
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
    void testSetProjectCommandBuilderConstructor() {

        String projectName = "Project Name";

        SetProjectCommand.SetProjectCommandBuilder actualSetProjectCommandBuilder =
            new SetProjectCommand.SetProjectCommandBuilder(projectName);

        SetProjectCommand buildResult = actualSetProjectCommandBuilder.build();
        assertEquals("gerrit set-project Project Name", buildResult.getCommand());
        assertFalse(buildResult.isTimedOut());
        assertFalse(buildResult.isSuccessful());
        assertFalse(buildResult.isComplete());
        assertTrue(buildResult.getOutputList().isEmpty());
    }

    @Test
    void testSetSubmitType2() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        ProjectSubmitTypes submitType = ProjectSubmitTypes.MERGE_IF_NECESSARY;

        setProjectCommand.setSubmitType(submitType);

        assertEquals("gerrit set-project --submit-type MERGE_IF_NECESSARY Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetSubmitType3() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        ProjectSubmitTypes submitType = ProjectSubmitTypes.REBASE_IF_NECESSARY;

        setProjectCommand.setSubmitType(submitType);

        assertEquals("gerrit set-project --submit-type REBASE_IF_NECESSARY Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetSubmitType4() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        ProjectSubmitTypes submitType = ProjectSubmitTypes.REBASE_ALWAYS;

        setProjectCommand.setSubmitType(submitType);

        assertEquals("gerrit set-project --submit-type REBASE_ALWAYS Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetSubmitType5() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        ProjectSubmitTypes submitType = ProjectSubmitTypes.MERGE_ALWAYS;

        setProjectCommand.setSubmitType(submitType);

        assertEquals("gerrit set-project --submit-type MERGE_ALWAYS Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetSubmitType6() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        ProjectSubmitTypes submitType = ProjectSubmitTypes.CHERRY_PICK;

        setProjectCommand.setSubmitType(submitType);

        assertEquals("gerrit set-project --submit-type CHERRY_PICK Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetProjectState() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectState state = SetProjectCommand.ProjectState.ACTIVE;

        setProjectCommand.setProjectState(state);

        assertEquals("gerrit set-project --project-state ACTIVE Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetProjectState2() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectState state = SetProjectCommand.ProjectState.READ_ONLY;

        setProjectCommand.setProjectState(state);

        assertEquals("gerrit set-project --project-state READ_ONLY Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetProjectState3() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectState state = SetProjectCommand.ProjectState.HIDDEN;

        setProjectCommand.setProjectState(state);

        assertEquals("gerrit set-project --project-state HIDDEN Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetRequireChangeId2() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.FALSE;

        setProjectCommand.setRequireChangeId(option);

        assertEquals("gerrit set-project --change-id FALSE Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetRequireChangeId3() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.INHERIT;

        setProjectCommand.setRequireChangeId(option);

        assertEquals("gerrit set-project --change-id INHERIT Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetContentMerge2() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.FALSE;

        setProjectCommand.setContentMerge(option);

        assertEquals("gerrit set-project --content-merge FALSE Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetContentMerge3() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.INHERIT;

        setProjectCommand.setContentMerge(option);

        assertEquals("gerrit set-project --content-merge INHERIT Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetSignedOffBy2() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.FALSE;

        setProjectCommand.setSignedOffBy(option);

        assertEquals("gerrit set-project --signed-off-by FALSE Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetSignedOffBy3() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.INHERIT;

        setProjectCommand.setSignedOffBy(option);

        assertEquals("gerrit set-project --signed-off-by INHERIT Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetContributorAgreements() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.TRUE;

        setProjectCommand.setContributorAgreements(option);

        assertEquals("gerrit set-project --contributor-agreements TRUE Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetContributorAgreements2() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.FALSE;

        setProjectCommand.setContributorAgreements(option);

        assertEquals("gerrit set-project --contributor-agreements FALSE Project Name", setProjectCommand.getCommand());
    }

    @Test
    void testSetContributorAgreements3() {

        SetProjectCommand setProjectCommand = new SetProjectCommand("Project Name");
        SetProjectCommand.ProjectSettingOption option = SetProjectCommand.ProjectSettingOption.INHERIT;

        setProjectCommand.setContributorAgreements(option);

        assertEquals("gerrit set-project --contributor-agreements INHERIT Project Name", setProjectCommand.getCommand());
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(String.format(twoParamString, setCommand, projectName), basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetDescription() {
        basicCommand.setDescription(projectDescription);
        assertGetMatchesCommand(String.format("%s '%s'", SetProjectOptions.DESCRIPTION.getFlag(), projectDescription));
        commandBuilder.description(projectDescription);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetSubmitType() {
        for (ProjectSubmitTypes submitType : ProjectSubmitTypes.values()) {
            basicCommand = new SetProjectCommand(projectName);
            basicCommand.setSubmitType(submitType);
            commandBuilder = new SetProjectCommandBuilder(projectName);
            commandBuilder.submitType(submitType);
            assertGetMatchesCommand(String.format(twoParamString, SetProjectOptions.SUBMIT_TYPE.getFlag(), submitType));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetContributorAggreements() {
        for (ProjectSettingOption setting : ProjectSettingOption.values()) {
            basicCommand = new SetProjectCommand(projectName);
            basicCommand.setContributorAgreements(setting);
            commandBuilder = new SetProjectCommandBuilder(projectName);
            commandBuilder.contributorAgreement(setting);
            assertGetMatchesCommand(String.format(twoParamString, SetProjectOptions.CONTRIBUTOR_AGREEMENTS.getFlag(), setting));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetSignedOffBy() {
        for (ProjectSettingOption setting : ProjectSettingOption.values()) {
            basicCommand = new SetProjectCommand(projectName);
            basicCommand.setSignedOffBy(setting);
            commandBuilder = new SetProjectCommandBuilder(projectName);
            commandBuilder.signedOffBy(setting);
            assertGetMatchesCommand(String.format(twoParamString, SetProjectOptions.SIGNED_OFF_BY.getFlag(), setting));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetContentMerge() {
        for (ProjectSettingOption setting : ProjectSettingOption.values()) {
            basicCommand = new SetProjectCommand(projectName);
            basicCommand.setContentMerge(setting);
            commandBuilder = new SetProjectCommandBuilder(projectName);
            commandBuilder.contentMerge(setting);
            assertGetMatchesCommand(String.format(twoParamString, SetProjectOptions.CONTENT_MERGE.getFlag(), setting));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetRequireChangeId() {
        for (ProjectSettingOption setting : ProjectSettingOption.values()) {
            basicCommand = new SetProjectCommand(projectName);
            basicCommand.setRequireChangeId(setting);
            commandBuilder = new SetProjectCommandBuilder(projectName);
            commandBuilder.changeId(setting);
            assertGetMatchesCommand(String.format(twoParamString, SetProjectOptions.CHANGE_ID.getFlag(), setting));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetMaxObjectSizeLimit() {
        basicCommand.setMaxObjectSizeLimit("100m");
        assertGetMatchesCommand(String.format(twoParamString, SetProjectOptions.MAX_OBJECT_SIZE_LIMIT.getFlag(), "100m"));
        commandBuilder.maxObjectSizeLimit("100m");
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetState() {
        for (ProjectState setting : ProjectState.values()) {
            basicCommand = new SetProjectCommand(projectName);
            basicCommand.setProjectState(setting);
            commandBuilder = new SetProjectCommandBuilder(projectName);
            commandBuilder.state(setting);
            assertGetMatchesCommand(String.format(twoParamString, SetProjectOptions.PROJECT_STATE.getFlag(), setting));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testRepeatedGetCommand() {
        String startingCommand = basicCommand.getCommand();
        for (int i = 0; i < 10; i++) {
            assertEquals(startingCommand, basicCommand.getCommand());
        }
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s %s", setCommand, flags, projectName), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}

