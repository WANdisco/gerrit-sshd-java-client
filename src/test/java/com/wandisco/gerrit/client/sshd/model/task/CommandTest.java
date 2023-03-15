package com.wandisco.gerrit.client.sshd.model.task;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CommandTest {

    private static final GerritBaseCommand goodCommand = new GerritBaseCommand("Good Command");
    private static final GerritBaseCommand badCommand = new GerritBaseCommand("Bad Command");
    private static final GerritBaseCommand emptyCommand = new GerritBaseCommand();

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        goodCommand.start();
        TimeUnit.SECONDS.sleep(1);
        goodCommand.end();
        goodCommand.setOutputLines(Arrays.asList("line1", "line2"));
        goodCommand.setSuccessful(true);

        badCommand.start();
        badCommand.setTimedOut(true);
        TimeUnit.SECONDS.sleep(1);
        badCommand.end();
        badCommand.setOutput("Error string \n failed");
        badCommand.setSuccessful(false);
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
        Duration duration = Duration.between(goodCommand.getTimeStarted(), goodCommand.getTimeEnded());
        assertEquals(duration, goodCommand.getDuration());
    }

    @Test
    public void testCommandIsSuccessful() {
        assertTrue(goodCommand.isSuccessful());
    }

    @Test
    public void testCommandUnsuccessful() {
        assertFalse(badCommand.isSuccessful());
    }

    @Test
    public void testCommandTimedOut() {
        assertTrue(badCommand.isTimedOut());
    }

    @Test
    public void testCommandNotTimedOut() {
        assertFalse(goodCommand.isTimedOut());
    }

    @Test
    public void testCommandGetCommand() {
        assertEquals("Good Command", goodCommand.getCommand());
        assertEquals("Bad Command", badCommand.getCommand());
    }

    @Test
    public void testGetOutputList() {
        assertEquals(Arrays.asList("line1", "line2"), goodCommand.getOutputList());
    }

    @Test
    public void testOutPutListToString() {
        assertEquals("line1\nline2", goodCommand.getOutput());
    }

    @Test
    public void testOutputString() {
        assertEquals("Error string \n failed", badCommand.getOutput());
    }

    @Test
    public void testOutputStringToList() {
        assertEquals(Arrays.asList("Error string ", " failed"), badCommand.getOutputList());
    }

    @Test
    public void testEmptyCommandOutput() {
        assertEquals("", emptyCommand.getCommand());
    }

}
