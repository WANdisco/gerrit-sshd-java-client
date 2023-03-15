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

public final class IndexChangesCommand extends GerritCommand {

    private final List<String> changes;

    public IndexChangesCommand(List<String> changes) {
        super("index changes");
        this.changes = changes;
    }

    public IndexChangesCommand(String... changes) {
        super("index changes");
        this.changes = Arrays.asList(changes);
    }

    public String getCommand() {
        if (commandBuilder.toString().trim().endsWith(getCommandMethod())) {
            changes.forEach(s -> commandBuilder.append(String.format("%s ", s)));
        }
        return commandBuilder.toString().trim();
    }

    //BuilderClass
    public static class IndexChangesCommandBuilder {
        private final List<String> changes = new ArrayList<>();

        public IndexChangesCommandBuilder() {
        }

        public IndexChangesCommandBuilder change(String change) {
            changes.add(change);
            return this;
        }

        public IndexChangesCommandBuilder changes(List<String> changesToIndex) {
            changes.addAll(changesToIndex);
            return this;
        }

        public IndexChangesCommand build() {
            return new IndexChangesCommand(changes);
        }
    }
}
