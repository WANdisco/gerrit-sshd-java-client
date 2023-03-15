package integration.cache;

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

import com.wandisco.gerrit.client.sshd.GerritSSHClient;
import com.wandisco.gerrit.client.sshd.commands.GerritCommand;
import com.wandisco.gerrit.client.sshd.commands.cache.FlushCachesCommand;
import com.wandisco.gerrit.client.sshd.commands.cache.ShowCachesCommand;
import com.wandisco.gerrit.client.sshd.model.task.GerritBaseCommand;
import integration.utils.TestBase;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CacheIT extends TestBase {

    @Test
    public void testFlushCaches() throws IOException {
        FlushCachesCommand command = new FlushCachesCommand();
        GerritSSHClient client = getAdminGerritSSHClient();
        GerritCommand output = (GerritCommand) client.executeCommand(command);
        assertEquals(0, output.getExitCode());
    }

    @Test
    public void testFlushCachesAll() throws IOException {
        FlushCachesCommand command = new FlushCachesCommand();
        command.setAll();
        GerritSSHClient client = getAdminGerritSSHClient();
        GerritCommand output = (GerritCommand) client.executeCommand(command);
        assertEquals(0, output.getExitCode());
    }

    @Test
    public void testFlushCachesList() throws IOException {
        FlushCachesCommand command = new FlushCachesCommand();
        command.setList();
        GerritSSHClient client = getAdminGerritSSHClient();
        GerritBaseCommand output = client.executeCommand(command);
        assertEquals(0, output.getExitCode());
    }

    @Test
    public void testFlushCachesSingleCache() throws IOException {
        FlushCachesCommand command = new FlushCachesCommand();
        command.setCache("accounts");
        GerritSSHClient client = getAdminGerritSSHClient();
        GerritCommand output = (GerritCommand) client.executeCommand(command);
        assertEquals(0, output.getExitCode());
    }

    @Test
    public void testFlushCachesMultipleCaches() throws IOException {
        FlushCachesCommand command = new FlushCachesCommand();
        command.setCache("accounts");
        command.setCache("changes");
        command.setCache("sshkeys");
        GerritSSHClient client = getAdminGerritSSHClient();
        GerritCommand output = (GerritCommand) client.executeCommand(command);
        assertEquals(0, output.getExitCode());
    }

    @Test
    public void testFlushCachesCacheList() throws IOException {
        List<String> caches = Arrays.asList("accounts", "changes", "sshkeys");
        FlushCachesCommand command = new FlushCachesCommand();
        command.setCaches(caches);
        GerritSSHClient client = getAdminGerritSSHClient();
        GerritCommand output = (GerritCommand) client.executeCommand(command);
        assertEquals(0, output.getExitCode());
    }

    @Test
    public void testShowCaches() throws IOException {
        ShowCachesCommand command = new ShowCachesCommand();
        GerritSSHClient client = getAdminGerritSSHClient();
        GerritBaseCommand output = client.executeCommand(command);
        assertEquals(0, output.getExitCode());
    }
}
