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
import java.util.List;

public final class FlushCachesCommand extends GerritCommand {

    public FlushCachesCommand() {
        super("flush-caches");
    }

    @Override
    public String getCommand() {
        return commandBuilder.toString().trim();
    }

    public void setList() {
        addOption(FlushCachesOptions.LIST);
    }

    public void setAll() {
        addOption(FlushCachesOptions.ALL);
    }

    public void setCaches(List<String> caches) {
        for (String cache : caches) {
            addOption(FlushCachesOptions.CACHE, cache);
        }
    }

    public void setCache(String cache) {
        addOption(FlushCachesOptions.CACHE, cache);
    }

    //BuilderClass
    public static class FlushCachesCommandBuilder {
        private final FlushCachesCommand command;

        public FlushCachesCommandBuilder() {
            command = new FlushCachesCommand();
        }

        public FlushCachesCommandBuilder all() {
            command.setAll();
            return this;
        }

        public FlushCachesCommandBuilder list() {
            command.setList();
            return this;
        }

        public FlushCachesCommandBuilder cache(String cache) {
            command.setCache(cache);
            return this;
        }

        public FlushCachesCommandBuilder caches(List<String> cachesToFlush) {
            command.setCaches(cachesToFlush);
            return this;
        }

        public FlushCachesCommand build() {
            return command;
        }
    }
}
