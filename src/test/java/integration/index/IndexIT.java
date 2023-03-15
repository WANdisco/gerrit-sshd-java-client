package integration.index;

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

import com.wandisco.gerrit.client.sshd.commands.index.IndexStartCommand;
import com.wandisco.gerrit.client.sshd.commands.index.IndexType;
import integration.utils.TestBase;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class IndexIT extends TestBase {

    @Test
    public void testIndexStartCommand() throws IOException {
        IndexStartCommand indexStartCommand = new IndexStartCommand(IndexType.accounts, true);
        indexStartCommand = (IndexStartCommand) getAdminGerritSSHClient().executeCommand(indexStartCommand);
        assertEquals(0, indexStartCommand.getExitCode());
    }
}
