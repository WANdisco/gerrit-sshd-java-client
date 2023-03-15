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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wandisco.gerrit.client.sshd.commands.cache.ShowCachesCommand.ShowCachesCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShowCachesCommandTest {

    private final String createCommand = "gerrit show-caches";
    private final int width = 6;
    private ShowCachesCommand basicCommand;
    private ShowCachesCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new ShowCachesCommand();
        commandBuilder = new ShowCachesCommandBuilder();
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(createCommand, basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetGc() {
        basicCommand.setGc();
        assertGetMatchesCommand(ShowCachesOptions.GC.getFlag());
        commandBuilder.gc();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetShowJvm() {
        basicCommand.setShowJvm();
        assertGetMatchesCommand(ShowCachesOptions.SHOW_JVM.getFlag());
        commandBuilder.showJvm();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetShowThreads() {
        basicCommand.setShowThreads();
        assertGetMatchesCommand(ShowCachesOptions.SHOW_THREADS.getFlag());
        commandBuilder.showThreads();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetWidth() {
        basicCommand.setWidth(width);
        assertGetMatchesCommand(String.format("%s %d", ShowCachesOptions.WIDTH.getFlag(), width));
        commandBuilder.width(width);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testRepeatedGetCommand() {
        String startingCommand = basicCommand.getCommand();
        for (int i = 0; i < 10; i++) {
            assertEquals(startingCommand, basicCommand.getCommand());
        }
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s", createCommand, flags.trim()), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
