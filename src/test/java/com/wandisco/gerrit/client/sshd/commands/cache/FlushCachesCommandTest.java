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

import com.wandisco.gerrit.client.sshd.commands.cache.FlushCachesCommand.FlushCachesCommandBuilder;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FlushCachesCommandTest {

    private final String flushCommand = "gerrit flush-caches";
    private final String cache = "first";
    private final List<String> caches = Arrays.asList("first", "second", "third", "fourth", "fifth");
    private FlushCachesCommand basicCommand;
    private FlushCachesCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new FlushCachesCommand();
        commandBuilder = new FlushCachesCommandBuilder();
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(flushCommand, basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetAll() {
        basicCommand.setAll();
        assertGetMatchesCommand(FlushCachesOptions.ALL.getFlag());
        commandBuilder.all();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetList() {
        basicCommand.setList();
        assertGetMatchesCommand(FlushCachesOptions.LIST.getFlag());
        commandBuilder.list();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetSingleCache() {
        basicCommand.setCache(cache);
        assertGetMatchesCommand(String.format("%s %s", FlushCachesOptions.CACHE.getFlag(), cache));
        commandBuilder.cache(cache);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetListCache() {
        basicCommand.setCaches(caches);
        StringBuilder sb = new StringBuilder();
        for (String string : caches) {
            sb.append(String.format("%s %s ", FlushCachesOptions.CACHE.getFlag(), string));
        }
        assertGetMatchesCommand(sb.toString().trim());
        commandBuilder.caches(caches);
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
        assertEquals(String.format("%s %s", flushCommand, flags.trim()), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
