package com.wandisco.gerrit.client.sshd.commands.accounts;

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

import com.wandisco.gerrit.client.sshd.model.task.CommandOption;

public enum CreateAccountOptions implements CommandOption {

    GROUP("--group"),
    FULL_NAME("--full-name"),
    EMAIL("--email"),
    SSH_KEY("--ssh-key"),
    HTTP_PASSWORD("--http-password");

    private final String option;

    CreateAccountOptions(String option) {
        this.option = option;
    }

    @Override
    public String getFlag() {
        return option;
    }
}
