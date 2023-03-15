package integration;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.model.task.GerritBaseCommand;
import integration.utils.TestBase;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class GerritSSHClientIT extends TestBase {
    private static GerritBaseCommand goodCommand = new GerritBaseCommand("gerrit ls-projects");
    private static GerritBaseCommand badCommand = new GerritBaseCommand("gerrit badCommand");

    @BeforeAll
    public static void setup() throws IOException {
        goodCommand = getAdminGerritSSHClient().executeCommand(goodCommand);
        badCommand = getAdminGerritSSHClient().executeCommand(badCommand);
    }

    @Test
    public void testGoodStringReturn() {
        assertEquals("All-Projects\nAll-Users", goodCommand.getOutput());
    }

    @Test
    public void testGoodListReturn() {
        assertEquals(2, goodCommand.getOutputList().size());
    }

    @Test
    public void testBadStringReturn() {
        assertEquals("fatal: gerrit: badCommand: not found", badCommand.getOutput());
    }

    @Test
    public void testSuccessful() {
        assertTrue(goodCommand.isSuccessful());
    }

    @Test
    public void testUnsuccessful() {
        assertFalse(badCommand.isSuccessful());
    }

    @Test
    public void testStartTime() {
        assertNotNull(goodCommand.getTimeStarted());
    }

    @Test
    public void testEndTime() {
        assertNotNull(goodCommand.getTimeEnded());
    }

    @Test
    public void testGoodExit() {
        assertEquals(0, goodCommand.getExitCode());
    }

    @Test
    public void testBadExit() {
        assertEquals(1, badCommand.getExitCode());
    }

    @Test
    public void testGoodCommandTimeStarted() {
        assertTrue(goodCommand.getTimeStarted().isBefore(Instant.now()));
        assertTrue(goodCommand.getTimeStarted().isBefore(goodCommand.getTimeEnded()));
    }

    @Test
    public void testGoodCommandTimeEnded() {
        assertTrue(goodCommand.getTimeEnded().isBefore(Instant.now()));
        assertTrue(goodCommand.getTimeEnded().isAfter(goodCommand.getTimeStarted()));
    }

    @Test
    public void testGoodCommandDuration() {
        long startTime = goodCommand.getTimeStarted().toEpochMilli();
        long endTime = goodCommand.getTimeEnded().toEpochMilli();
        long duration = (endTime - startTime);
        assertEquals(duration, goodCommand.getDuration().toMillis());
    }
}
