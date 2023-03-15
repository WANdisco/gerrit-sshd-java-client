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

import com.wandisco.gerrit.client.sshd.model.task.CommandOption;

public enum ReviewOptions implements CommandOption {

    PROJECT("--project"),
    BRANCH("--branch"),
    MESSAGE("--message"),
    NOTIFY("--notify"),
    SUBMIT("--submit"),
    ABANDON("--abandon"),
    RESTORE("--restore"),
    REBASE("--rebase"),
    MOVE("--move"),
    JSON("--json"),
    VERIFIED("--verified"),
    CODE_REVIEW("--code-review"),
    LABEL("--label"),
    TAG("--tag");

    private final String flag;

    ReviewOptions(String flag) {
        this.flag = flag;
    }

    @Override
    public String getFlag() {
        return flag;
    }
}
