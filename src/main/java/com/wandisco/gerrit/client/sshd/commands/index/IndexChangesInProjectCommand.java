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

import com.wandisco.gerrit.client.sshd.commands.GerritCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class IndexChangesInProjectCommand extends GerritCommand {

    private final List<String> projects;

    public IndexChangesInProjectCommand(List<String> projects) {
        super("index changes-in-project");
        this.projects = projects;
    }

    public IndexChangesInProjectCommand(String... projects) {
        super("index changes-in-project");
        this.projects = Arrays.asList(projects);
    }

    public String getCommand() {
        if (commandBuilder.toString().trim().endsWith(getCommandMethod())) {
            projects.forEach(s -> commandBuilder.append(String.format("%s ", s)));
        }
        return commandBuilder.toString().trim();
    }

    //BuilderClass
    public static class IndexChangesInProjectCommandBuilder {
        private final List<String> projects = new ArrayList<>();

        public IndexChangesInProjectCommandBuilder() {
        }

        public IndexChangesInProjectCommandBuilder project(String project) {
            projects.add(project);
            return this;
        }

        public IndexChangesInProjectCommandBuilder projects(List<String> projectsToIndex) {
            projects.addAll(projectsToIndex);
            return this;
        }

        public IndexChangesInProjectCommand build() {
            return new IndexChangesInProjectCommand(projects);
        }
    }
}
