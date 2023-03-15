package com.wandisco.gerrit.client.sshd.commands.cache;

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

public final class ShowCachesCommand extends GerritCommand {

    public ShowCachesCommand() {
        super("show-caches");
    }

    @Override
    public String getCommand() {
        return commandBuilder.toString().trim();
    }

    public void setGc() {
        addOption(ShowCachesOptions.GC);
    }

    public void setShowJvm() {
        addOption(ShowCachesOptions.SHOW_JVM);
    }

    public void setShowThreads() {
        addOption(ShowCachesOptions.SHOW_THREADS);
    }

    public void setWidth(int width) {
        addOption(ShowCachesOptions.WIDTH, String.valueOf(width));
    }

    //BuilderClass
    public static class ShowCachesCommandBuilder {
        private final ShowCachesCommand command;

        public ShowCachesCommandBuilder() {
            command = new ShowCachesCommand();
        }

        public ShowCachesCommandBuilder gc() {
            command.setGc();
            return this;
        }

        public ShowCachesCommandBuilder showJvm() {
            command.setShowJvm();
            return this;
        }

        public ShowCachesCommandBuilder showThreads() {
            command.setShowThreads();
            return this;
        }

        public ShowCachesCommandBuilder width(int width) {
            command.setWidth(width);
            return this;
        }

        public ShowCachesCommand build() {
            return command;
        }
    }
}
