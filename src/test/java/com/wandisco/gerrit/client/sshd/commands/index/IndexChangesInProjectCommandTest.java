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

import com.wandisco.gerrit.client.sshd.commands.index.IndexChangesInProjectCommand.IndexChangesInProjectCommandBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IndexChangesInProjectCommandTest {

    private final String indexCommand = "gerrit index changes-in-project";
    private final String project = "first";
    private final List<String> projects = Arrays.asList("first", "second", "third", "fourth", "fifth");
    private IndexChangesInProjectCommand basicCommand;
    private IndexChangesInProjectCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new IndexChangesInProjectCommand();
        commandBuilder = new IndexChangesInProjectCommandBuilder();
    }

    @Test
    public void testSetSingle() {
        basicCommand = new IndexChangesInProjectCommand(project);
        assertGetMatchesCommand(project);
        commandBuilder.project(project);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetList() {
        basicCommand = new IndexChangesInProjectCommand(projects);
        StringBuilder sb = new StringBuilder();
        for (String string : projects) {
            sb.append(String.format("%s ", string));
            commandBuilder.project(string);
        }
        assertGetMatchesCommand(sb.toString().trim());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetList2() {
        basicCommand = new IndexChangesInProjectCommand(projects);
        StringBuilder sb = new StringBuilder();
        List<String> projectList = new ArrayList<>();
        for (String string : projects) {
            sb.append(String.format("%s ", string));
            projectList.add(string);
        }
        commandBuilder.projects(projectList);
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
