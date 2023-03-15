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

import com.wandisco.gerrit.client.sshd.commands.index.IndexChangesCommand.IndexChangesCommandBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IndexChangesCommandTest {

    private final String indexCommand = "gerrit index changes";
    private final String change = "first";
    private final List<String> changes = Arrays.asList("first", "second", "third", "fourth", "fifth");
    private IndexChangesCommand basicCommand;
    private IndexChangesCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new IndexChangesCommand();
        commandBuilder = new IndexChangesCommandBuilder();
    }

    @Test
    public void testSetSingle() {
        basicCommand = new IndexChangesCommand(change);
        assertGetMatchesCommand(change);
        commandBuilder.change(change);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetList() {
        basicCommand = new IndexChangesCommand(changes);
        StringBuilder sb = new StringBuilder();
        for (String string : changes) {
            sb.append(String.format("%s ", string));
            commandBuilder.change(string);
        }
        assertGetMatchesCommand(sb.toString().trim());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetList2() {
        basicCommand = new IndexChangesCommand(changes);
        StringBuilder sb = new StringBuilder();
        List<String> changeList = new ArrayList<>();
        for (String string : changes) {
            sb.append(String.format("%s ", string));
            changeList.add(string);
        }
        commandBuilder.changes(changeList);
        assertGetMatchesCommand(sb.toString().trim());
        assertBuilderEqualsCommand();
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s", indexCommand, flags), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
