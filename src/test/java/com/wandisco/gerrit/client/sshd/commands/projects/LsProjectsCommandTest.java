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

import com.wandisco.gerrit.client.sshd.commands.projects.LsProjectsCommand.LSProjectsCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LsProjectsCommandTest {

    private final String lsProjectsCommand = "gerrit ls-projects";
    private LsProjectsCommand basicCommand;
    private LSProjectsCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new LsProjectsCommand();
        commandBuilder = new LSProjectsCommandBuilder();
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#all()}.
     * <p>
     * Method under test: {@link LsProjectsCommand.LSProjectsCommandBuilder#all()}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder all()")
    void testLSProjectsCommandBuilderAll() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualAllResult = lsProjectsCommandBuilder.all();

        // Assert
        assertSame(lsProjectsCommandBuilder, actualAllResult);
    }

    /**
     * Test LSProjectsCommandBuilder
     * {@link LSProjectsCommandBuilder#branch(String)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#branch(String)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder branch(String)")
    void testLSProjectsCommandBuilderBranch() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        String branch = "janedoe/featurebranch";

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualBranchResult = lsProjectsCommandBuilder.branch(branch);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualBranchResult);
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#build()}.
     * <p>
     * Method under test: {@link LsProjectsCommand.LSProjectsCommandBuilder#build()}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder build()")
    void testLSProjectsCommandBuilderBuild() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder typeResult = (new LsProjectsCommand.LSProjectsCommandBuilder())
                .branch("janedoe/featurebranch")
                .format(LsProjectsOptions.Format.TEXT)
                .limit("Limit")
                .match("Match")
                .prefix("Prefix")
                .regex(".*")
                .start("Start")
                .state(SetProjectCommand.ProjectState.ACTIVE)
                .type(LsProjectsOptions.Type.CODE);

        // Act
        LsProjectsCommand actualBuildResult = typeResult.build();

        // Assert
        assertEquals("gerrit ls-projects --show-branch janedoe/featurebranch --format text --limit Limit --match Match"
                + " --prefix Prefix -r .* --start Start --state ACTIVE --type code", actualBuildResult.getCommand());
        assertNull(actualBuildResult.getOutput());
        assertNull(actualBuildResult.getDuration());
        assertNull(actualBuildResult.getTimeEnded());
        assertNull(actualBuildResult.getTimeStarted());
        assertEquals(0, actualBuildResult.getExitCode());
        assertEquals(180000000000L, actualBuildResult.getTimeout().toNanos());
        assertFalse(actualBuildResult.isComplete());
        assertFalse(actualBuildResult.isSuccessful());
        assertFalse(actualBuildResult.isTimedOut());
        assertTrue(actualBuildResult.getOutputList().isEmpty());
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#description()}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#description()}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder description()")
    void testLSProjectsCommandBuilderDescription() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualDescriptionResult = lsProjectsCommandBuilder.description();

        // Assert
        assertSame(lsProjectsCommandBuilder, actualDescriptionResult);
    }

    /**
     * Test LSProjectsCommandBuilder
     * {@link LSProjectsCommandBuilder#format(Format)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#format(LsProjectsOptions.Format)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder format(Format)")
    void testLSProjectsCommandBuilderFormat() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        LsProjectsOptions.Format format = LsProjectsOptions.Format.TEXT;

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualFormatResult = lsProjectsCommandBuilder.format(format);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualFormatResult);
    }

    /**
     * Test LSProjectsCommandBuilder
     * {@link LSProjectsCommandBuilder#hasACLFor(String)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#hasACLFor(String)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder hasACLFor(String)")
    void testLSProjectsCommandBuilderHasACLFor() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        String hasACLForGroup = "Has ACLFor Group";

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualHasACLForResult = lsProjectsCommandBuilder
                .hasACLFor(hasACLForGroup);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualHasACLForResult);
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#limit(String)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#limit(String)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder limit(String)")
    void testLSProjectsCommandBuilderLimit() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        String limit = "Limit";

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualLimitResult = lsProjectsCommandBuilder.limit(limit);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualLimitResult);
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#match(String)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#match(String)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder match(String)")
    void testLSProjectsCommandBuilderMatch() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        String match = "Match";

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualMatchResult = lsProjectsCommandBuilder.match(match);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualMatchResult);
    }

    /**
     * Test LSProjectsCommandBuilder new {@link LSProjectsCommandBuilder} (default
     * constructor).
     * <p>
     * Method under test: default or parameterless constructor of
     * {@link LsProjectsCommand.LSProjectsCommandBuilder}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder new LSProjectsCommandBuilder (default constructor)")
    void testLSProjectsCommandBuilderNewLSProjectsCommandBuilder() {
        // Arrange and Act
        LsProjectsCommand.LSProjectsCommandBuilder actualLsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();

        // Assert
        LsProjectsCommand buildResult = actualLsProjectsCommandBuilder.build();
        assertEquals("gerrit ls-projects", buildResult.getCommand());
        assertNull(buildResult.getOutput());
        assertNull(buildResult.getDuration());
        assertNull(buildResult.getTimeEnded());
        assertNull(buildResult.getTimeStarted());
        assertEquals(0, buildResult.getExitCode());
        assertEquals(180000000000L, buildResult.getTimeout().toNanos());
        assertFalse(buildResult.isComplete());
        assertFalse(buildResult.isSuccessful());
        assertFalse(buildResult.isTimedOut());
        assertTrue(buildResult.getOutputList().isEmpty());
    }

    /**
     * Test LSProjectsCommandBuilder
     * {@link LSProjectsCommandBuilder#prefix(String)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#prefix(String)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder prefix(String)")
    void testLSProjectsCommandBuilderPrefix() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        String prefix = "Prefix";

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualPrefixResult = lsProjectsCommandBuilder.prefix(prefix);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualPrefixResult);
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#regex(String)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#regex(String)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder regex(String)")
    void testLSProjectsCommandBuilderRegex() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        String regex = ".*";

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualRegexResult = lsProjectsCommandBuilder.regex(regex);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualRegexResult);
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#start(String)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#start(String)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder start(String)")
    void testLSProjectsCommandBuilderStart() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        String start = "Start";

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualStartResult = lsProjectsCommandBuilder.start(start);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualStartResult);
    }

    /**
     * Test LSProjectsCommandBuilder
     * {@link LSProjectsCommandBuilder#state(ProjectState)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#state(SetProjectCommand.ProjectState)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder state(ProjectState)")
    void testLSProjectsCommandBuilderState() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        SetProjectCommand.ProjectState projectState = SetProjectCommand.ProjectState.ACTIVE;

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualStateResult = lsProjectsCommandBuilder.state(projectState);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualStateResult);
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#tree()}.
     * <p>
     * Method under test: {@link LsProjectsCommand.LSProjectsCommandBuilder#tree()}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder tree()")
    void testLSProjectsCommandBuilderTree() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualTreeResult = lsProjectsCommandBuilder.tree();

        // Assert
        assertSame(lsProjectsCommandBuilder, actualTreeResult);
    }

    /**
     * Test LSProjectsCommandBuilder {@link LSProjectsCommandBuilder#type(Type)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand.LSProjectsCommandBuilder#type(LsProjectsOptions.Type)}
     */
    @Test
    @DisplayName("Test LSProjectsCommandBuilder type(Type)")
    void testLSProjectsCommandBuilderType() {
        // Arrange
        LsProjectsCommand.LSProjectsCommandBuilder lsProjectsCommandBuilder = new LsProjectsCommand.LSProjectsCommandBuilder();
        LsProjectsOptions.Type type = LsProjectsOptions.Type.CODE;

        // Act
        LsProjectsCommand.LSProjectsCommandBuilder actualTypeResult = lsProjectsCommandBuilder.type(type);

        // Assert
        assertSame(lsProjectsCommandBuilder, actualTypeResult);
    }

    /**
     * Test {@link LsProjectsCommand#setBranch(String)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setBranch(String)}
     */
    @Test
    @DisplayName("Test setBranch(String)")
    void testSetBranch() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        String branch = "janedoe/featurebranch";

        // Act
        lsProjectsCommand.setBranch(branch);

        // Assert
        assertEquals("gerrit ls-projects --show-branch janedoe/featurebranch", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setDescription()}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setDescription()}
     */
    @Test
    @DisplayName("Test setDescription()")
    void testSetDescription() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();

        // Act
        lsProjectsCommand.setDescription();

        // Assert
        assertEquals("gerrit ls-projects --description", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setTree()}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setTree()}
     */
    @Test
    @DisplayName("Test setTree()")
    void testSetTree() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();

        // Act
        lsProjectsCommand.setTree();

        // Assert
        assertEquals("gerrit ls-projects --tree", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setMatch(String)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setMatch(String)}
     */
    @Test
    @DisplayName("Test setMatch(String)")
    void testSetMatch() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        String matchProject = "Match Project";

        // Act
        lsProjectsCommand.setMatch(matchProject);

        // Assert
        assertEquals("gerrit ls-projects --match Match Project", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setRegex(String)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setRegex(String)}
     */
    @Test
    @DisplayName("Test setRegex(String)")
    void testSetRegex() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        String matchProjectRegex = ".*";

        // Act
        lsProjectsCommand.setRegex(matchProjectRegex);

        // Assert
        assertEquals("gerrit ls-projects -r .*", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setStart(String)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setStart(String)}
     */
    @Test
    @DisplayName("Test setStart(String)")
    @Disabled("TODO: Complete this test")
    void testSetStart() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        String numberOfProjectsToSkip = "127.0.0.1";

        // Act
        lsProjectsCommand.setStart(numberOfProjectsToSkip);
    }

    /**
     * Test {@link LsProjectsCommand#setState(ProjectState)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand#setState(SetProjectCommand.ProjectState)}
     */
    @Test
    @DisplayName("Test setState(ProjectState)")
    void testSetState() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        SetProjectCommand.ProjectState projectState = SetProjectCommand.ProjectState.ACTIVE;

        // Act
        lsProjectsCommand.setState(projectState);

        // Assert
        assertEquals("gerrit ls-projects --state ACTIVE", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setType(Type)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setType(LsProjectsOptions.Type)}
     */
    @Test
    @DisplayName("Test setType(Type)")
    void testSetType() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        LsProjectsOptions.Type type = LsProjectsOptions.Type.CODE;

        // Act
        lsProjectsCommand.setType(type);

        // Assert
        assertEquals("gerrit ls-projects --type code", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setFormat(Format)}.
     * <p>
     * Method under test:
     * {@link LsProjectsCommand#setFormat(LsProjectsOptions.Format)}
     */
    @Test
    @DisplayName("Test setFormat(Format)")
    void testSetFormat() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        LsProjectsOptions.Format format = LsProjectsOptions.Format.TEXT;

        // Act
        lsProjectsCommand.setFormat(format);

        // Assert
        assertEquals("gerrit ls-projects --format text", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setAll()}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setAll()}
     */
    @Test
    @DisplayName("Test setAll()")
    void testSetAll() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();

        // Act
        lsProjectsCommand.setAll();

        // Assert
        assertEquals("gerrit ls-projects --all", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setLimit(String)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setLimit(String)}
     */
    @Test
    @DisplayName("Test setLimit(String)")
    void testSetLimit() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        String limit = "Limit";

        // Act
        lsProjectsCommand.setLimit(limit);

        // Assert
        assertEquals("gerrit ls-projects --limit Limit", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setPrefix(String)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setPrefix(String)}
     */
    @Test
    @DisplayName("Test setPrefix(String)")
    void testSetPrefix() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        String prefix = "Prefix";

        // Act
        lsProjectsCommand.setPrefix(prefix);

        // Assert
        assertEquals("gerrit ls-projects --prefix Prefix", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#setHasACLFor(String)}.
     * <p>
     * Method under test: {@link LsProjectsCommand#setHasACLFor(String)}
     */
    @Test
    @DisplayName("Test setHasACLFor(String)")
    void testSetHasACLFor() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();
        String groupACL = "Group ACL";

        // Act
        lsProjectsCommand.setHasACLFor(groupACL);

        // Assert
        assertEquals("gerrit ls-projects --has-acl-for Group ACL", lsProjectsCommand.getCommand());
    }

    /**
     * Test {@link LsProjectsCommand#getCommand()}.
     * <p>
     * Method under test: {@link LsProjectsCommand#getCommand()}
     */
    @Test
    @DisplayName("Test getCommand()")
    void testGetCommand() {
        // Arrange
        LsProjectsCommand lsProjectsCommand = new LsProjectsCommand();

        // Act
        String actualCommand = lsProjectsCommand.getCommand();

        // Assert
        assertEquals("gerrit ls-projects", actualCommand);
    }

    /**
     * Test new {@link LsProjectsCommand} (default constructor).
     * <p>
     * Method under test: default or parameterless constructor of
     * {@link LsProjectsCommand}
     */
    @Test
    @DisplayName("Test new LsProjectsCommand (default constructor)")
    void testNewLsProjectsCommand() {
        // Arrange and Act
        LsProjectsCommand actualLsProjectsCommand = new LsProjectsCommand();

        // Assert
        assertEquals("gerrit ls-projects", actualLsProjectsCommand.getCommand());
        assertNull(actualLsProjectsCommand.getOutput());
        assertNull(actualLsProjectsCommand.getDuration());
        assertNull(actualLsProjectsCommand.getTimeEnded());
        assertNull(actualLsProjectsCommand.getTimeStarted());
        assertEquals(0, actualLsProjectsCommand.getExitCode());
        assertEquals(180000000000L, actualLsProjectsCommand.getTimeout().toNanos());
        assertFalse(actualLsProjectsCommand.isComplete());
        assertFalse(actualLsProjectsCommand.isSuccessful());
        assertFalse(actualLsProjectsCommand.isTimedOut());
        assertTrue(actualLsProjectsCommand.getOutputList().isEmpty());
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s", lsProjectsCommand, flags.trim()), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
