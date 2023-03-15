package com.wandisco.gerrit.client.sshd.commands.changes;

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

import com.wandisco.gerrit.client.sshd.commands.changes.ReviewCommand.ReviewCommandBuilder;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReviewCommandTest {
    private final static String twoParamString = "%s %s";
    private final String reviewCommand = "gerrit review";
    private final String commit = "ThisIsACommitSha";
    private final String project = "Project1";
    private final String startingBranch = "branch1";
    private final String message = "This is a CodeReview";
    private final String newBranch = "newBranch1";
    private final String tag = "newTag";
    private final String customLabel = "good-Score-Label";
    private final List<String> scores = Arrays.asList("+2", "+1", "0", "-1", "-2");
    private ReviewCommand basicCommand;
    private ReviewCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new ReviewCommand(commit);
        commandBuilder = new ReviewCommandBuilder(commit);
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(String.format(twoParamString, reviewCommand, commit), basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetProject() {
        basicCommand.setProject(project);
        assertGetMatchesCommand(String.format(twoParamString, ReviewOptions.PROJECT.getFlag(), project));
        commandBuilder.project(project);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetBranch() {
        basicCommand.setBranch(startingBranch);
        assertGetMatchesCommand(String.format(twoParamString, ReviewOptions.BRANCH.getFlag(), startingBranch));
        commandBuilder.branch(startingBranch);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetMessage() {
        basicCommand.setMessage(message);
        assertGetMatchesCommand(String.format("%s '%s'", ReviewOptions.MESSAGE.getFlag(), message));
        commandBuilder.message(message);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetNotify() {
        for (ReviewNotifyTypes notifyType : ReviewNotifyTypes.values()) {
            basicCommand = new ReviewCommand(commit);
            basicCommand.setNotify(notifyType);
            commandBuilder = new ReviewCommandBuilder(commit);
            commandBuilder.notifyHandler(notifyType);
            assertGetMatchesCommand(String.format(twoParamString, ReviewOptions.NOTIFY.getFlag(), notifyType.name()));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetSubmit() {
        basicCommand.setSubmit();
        assertGetMatchesCommand(ReviewOptions.SUBMIT.getFlag());
        commandBuilder.submit();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetAbandon() {
        basicCommand.setAbandon();
        assertGetMatchesCommand(ReviewOptions.ABANDON.getFlag());
        commandBuilder.abandon();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetRestore() {
        basicCommand.setRestore();
        assertGetMatchesCommand(ReviewOptions.RESTORE.getFlag());
        commandBuilder.restore();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetRebase() {
        basicCommand.setRebase();
        assertGetMatchesCommand(ReviewOptions.REBASE.getFlag());
        commandBuilder.rebase();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetMove() {
        basicCommand.setMove(newBranch);
        assertGetMatchesCommand(String.format(twoParamString, ReviewOptions.MOVE.getFlag(), newBranch));
        commandBuilder.move(newBranch);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetJson() {
        basicCommand.setJson("some string");
        assertGetMatchesCommand(String.format("%s '%s'", ReviewOptions.JSON.getFlag(), "some string"));
        commandBuilder.json("some string");
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetVerified() {
        for (String score : scores) {
            basicCommand = new ReviewCommand(commit);
            basicCommand.setVerified(score);
            commandBuilder = new ReviewCommandBuilder(commit);
            commandBuilder.verify(score);
            assertGetMatchesCommand(String.format(twoParamString, ReviewOptions.VERIFIED.getFlag(), score));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetCodeReview() {
        for (String score : scores) {
            basicCommand = new ReviewCommand(commit);
            basicCommand.setCodeReview(score);
            commandBuilder = new ReviewCommandBuilder(commit);
            commandBuilder.codeReview(score);
            assertGetMatchesCommand(String.format(twoParamString, ReviewOptions.CODE_REVIEW.getFlag(), score));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetCustomLabel() {
        for (String score : scores) {
            basicCommand = new ReviewCommand(commit);
            basicCommand.setCustomLabel(customLabel, score);
            commandBuilder = new ReviewCommandBuilder(commit);
            commandBuilder.label(customLabel, score);
            assertGetMatchesCommand(String.format("%s %s=%s", ReviewOptions.LABEL.getFlag(), customLabel, score));
            assertBuilderEqualsCommand();
        }
    }

    @Test
    public void testSetTag() {
        basicCommand.setTag(tag);
        assertGetMatchesCommand(String.format(twoParamString, ReviewOptions.TAG.getFlag(), tag));
        commandBuilder.tag(tag);
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
        assertEquals(String.format("%s %s %s", reviewCommand, flags, commit), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
