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

import com.wandisco.gerrit.client.sshd.model.task.CommandOption;

public enum LsProjectsOptions implements CommandOption {
    BRANCH("--show-branch"),

    DESCRIPTION("--description"),

    TREE("--tree"),

    TYPE("--type"), // See Type enum below

    FORMAT("--format"), // see format enum below

    ALL("--all"),

    LIMIT("--limit"),

    PREFIX("--prefix"),

    HAS_ACL_FOR("--has-acl-for"),

    MATCH("--match"),

    REGEX("-r"),

    START("--start"),

    STATE("--state");

    private final String option;

    private LsProjectsOptions(String option) {
        this.option = option;
    }

    public String getFlag() {
        return this.option;
    }

    public enum Type {
        CODE,
        PERMISSIONS,
        ALL;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum Format {
        TEXT,
        JSON,
        JSON_COMPACT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
