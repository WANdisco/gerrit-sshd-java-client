package com.wandisco.gerrit.client.sshd.commands.groups;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LsGroupsCommandTest {
    private final static String twoParamString = "%s %s";
    private final String lsCommand = "gerrit ls-groups";
    private final String user = "reused.user";
    private final String project = "reused.project";
    private LsGroupsCommand basicCommand;
    private LsGroupsCommand.LSGroupSCommandBuilder commandBuilder;

    @Test
    void testConstructor() {

        LsGroupsCommand actualLsGroupsCommand = new LsGroupsCommand();

        assertEquals("gerrit ls-groups", actualLsGroupsCommand.getCommand());
        assertFalse(actualLsGroupsCommand.isTimedOut());
        assertFalse(actualLsGroupsCommand.isSuccessful());
        assertFalse(actualLsGroupsCommand.isComplete());
        assertTrue(actualLsGroupsCommand.getOutputList().isEmpty());
    }

    @Test
    void testGetCommand() {

        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();

        String actualCommand = lsGroupsCommand.getCommand();

        assertEquals("gerrit ls-groups", actualCommand);
    }

    @BeforeEach
    public void createCommand() {
        basicCommand = new LsGroupsCommand();
        commandBuilder = new LsGroupsCommand.LSGroupSCommandBuilder();
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(lsCommand, basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link LsGroupsCommand.LSGroupSCommandBuilder#build()}
     *   <li>{@link LsGroupsCommand.LSGroupSCommandBuilder#project(String)}
     *   <li>{@link LsGroupsCommand.LSGroupSCommandBuilder#user(String)}
     * </ul>
     */
    @Test
    void testLSGroupSCommandBuilderBuild() {

        LsGroupsCommand.LSGroupSCommandBuilder userResult =
            (new LsGroupsCommand.LSGroupSCommandBuilder()).project("Project").user("janedoe");

        LsGroupsCommand actualBuildResult = userResult.build();

        assertEquals("gerrit ls-groups --project Project --user janedoe", actualBuildResult.getCommand());
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
    void testLSGroupSCommandBuilderConstructor() {

        LsGroupsCommand.LSGroupSCommandBuilder actualLsGroupSCommandBuilder = new LsGroupsCommand.LSGroupSCommandBuilder();

        LsGroupsCommand buildResult = actualLsGroupSCommandBuilder.build();
        assertEquals("gerrit ls-groups", buildResult.getCommand());
        assertFalse(buildResult.isTimedOut());
        assertFalse(buildResult.isSuccessful());
        assertFalse(buildResult.isComplete());
        assertTrue(buildResult.getOutputList().isEmpty());
    }

    @Test
    public void testSetProject() {
        basicCommand.setProject(project);
        assertGetMatchesCommand(String.format(twoParamString, LsGroupsOptions.PROJECT.getFlag(), project));
        commandBuilder.project(project);
        assertBuilderEqualsCommand();
    }

    @Test
    void testSetProject2() {

        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();
        String project = "Project";

        lsGroupsCommand.setProject(project);

        assertEquals("gerrit ls-groups --project Project", lsGroupsCommand.getCommand());
    }

    @Test
    public void testSetUser() {
        basicCommand.setUser(user);
        assertGetMatchesCommand(String.format(twoParamString, LsGroupsOptions.USER.getFlag(), user));
        commandBuilder.user(user);
        assertBuilderEqualsCommand();
    }

    @Test
    void testSetUser2() {

        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();
        String username = "janedoe";

        lsGroupsCommand.setUser(username);

        assertEquals("gerrit ls-groups --user janedoe", lsGroupsCommand.getCommand());
    }

    @Test
    public void testSetOwned() {
        basicCommand.setOwned();
        assertGetMatchesCommand(LsGroupsOptions.OWNED.getFlag());
        commandBuilder.owned();
        assertBuilderEqualsCommand();
    }

    @Test
    void testSetOwned2() {

        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();

        lsGroupsCommand.setOwned();

        assertEquals("gerrit ls-groups --owned", lsGroupsCommand.getCommand());
    }

    @Test
    void testSetQueryFlag() {

        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();

        lsGroupsCommand.setQueryFlag();

        assertEquals("gerrit ls-groups -q", lsGroupsCommand.getCommand());
    }

    @Test
    void testSetVisibleToAll() {

        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();

        lsGroupsCommand.setVisibleToAll();

        assertEquals("gerrit ls-groups --visible-to-all", lsGroupsCommand.getCommand());
    }

    @Test
    void testSetVerbose() {

        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();

        lsGroupsCommand.setVerbose();

        assertEquals("gerrit ls-groups --verbose", lsGroupsCommand.getCommand());
    }

    @Test
    public void testVerbose() {
        basicCommand.setVerbose();
        assertGetMatchesCommand(LsGroupsOptions.VERBOSE.getFlag());
        commandBuilder.verbose();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testVisibleToAll() {
        basicCommand.setVisibleToAll();
        assertGetMatchesCommand(LsGroupsOptions.VISIBLE_TO_ALL.getFlag());
        commandBuilder.visibleToAll();
        assertBuilderEqualsCommand();
    }

    @Test
    public void setQuery() {
        basicCommand.setQueryFlag();
        assertGetMatchesCommand(LsGroupsOptions.QUERY.getFlag());
        commandBuilder.addQuery();
        assertBuilderEqualsCommand();
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s", lsCommand, flags.trim()), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
