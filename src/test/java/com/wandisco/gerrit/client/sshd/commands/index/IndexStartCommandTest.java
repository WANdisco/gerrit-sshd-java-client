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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IndexStartCommandTest {

    private final String indexCommand = "gerrit index start";
    private IndexStartCommand command;

    @Test
    public void testNoForced() {
        command = new IndexStartCommand(IndexType.changes);
        assertGetMatchesCommand(IndexType.changes.name());
    }

    @Test
    public void testForced() {
        command = new IndexStartCommand(IndexType.changes, true);
        assertGetMatchesCommand(String.format("%s --force", IndexType.changes.name()));
    }

    @Test
    void testGetCommand() {

        IndexStartCommand indexStartCommand = new IndexStartCommand(IndexType.changes);

        String actualCommand = indexStartCommand.getCommand();

        assertEquals("gerrit index start changes", actualCommand);
    }

    @Test
    void testGetCommand2() {

        IndexStartCommand indexStartCommand = new IndexStartCommand(IndexType.changes, true);

        String actualCommand = indexStartCommand.getCommand();

        assertEquals("gerrit index start changes --force", actualCommand);
    }

    @Test
    void testGetCommand3() {

        IndexStartCommand indexStartCommand = new IndexStartCommand(IndexType.changes, false);

        String actualCommand = indexStartCommand.getCommand();

        assertEquals("gerrit index start changes", actualCommand);
    }

    @Test
    public void testAllIndex() {
        for (IndexType index : IndexType.values()) {
            command = new IndexStartCommand(index);
            assertGetMatchesCommand(index.name());
        }
    }

    @Test
    public void testForcedFalse() {
        command = new IndexStartCommand(IndexType.changes, false);
        assertGetMatchesCommand(IndexType.changes.name());
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s", indexCommand, flags.trim()), command.getCommand());
    }

}
