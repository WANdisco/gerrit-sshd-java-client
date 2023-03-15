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

public final class IndexStartCommand extends GerritCommand {

    private static final String FORCED_OPTION = "--force";
    private final IndexType indexType;
    private boolean forced = false;

    public IndexStartCommand(IndexType indexType) {
        super("index start " + indexType);
        this.indexType = indexType;
    }

    public IndexStartCommand(IndexType indexType, boolean forced) {
        super("index start " + indexType);
        this.forced = forced;
        this.indexType = indexType;
    }

    @Override
    public String getCommand() {
        if (forced) {
            commandBuilder.append(FORCED_OPTION);
        }
        return commandBuilder.toString().trim();
    }

}
