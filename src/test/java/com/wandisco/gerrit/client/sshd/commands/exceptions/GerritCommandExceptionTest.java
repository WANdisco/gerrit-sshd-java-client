package com.wandisco.gerrit.client.sshd.commands.exceptions;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class GerritCommandExceptionTest {

    @Test
    void testConstructor() {

        GerritCommandException actualGerritCommandException = new GerritCommandException();

        assertNull(actualGerritCommandException.getCause());
        assertEquals(0, actualGerritCommandException.getSuppressed().length);
        assertNull(actualGerritCommandException.getMessage());
        assertNull(actualGerritCommandException.getLocalizedMessage());
    }


    @Test
    void testConstructor2() {

        String message = "An error occurred";

        GerritCommandException actualGerritCommandException = new GerritCommandException(message);

        assertNull(actualGerritCommandException.getCause());
        assertEquals(0, actualGerritCommandException.getSuppressed().length);
        assertEquals("An error occurred", actualGerritCommandException.getMessage());
        assertEquals("An error occurred", actualGerritCommandException.getLocalizedMessage());
    }


    @Test
    void testConstructor3() {

        String message = "An error occurred";
        Throwable throwable = new Throwable();

        GerritCommandException actualGerritCommandException = new GerritCommandException(message, throwable);

        Throwable cause = actualGerritCommandException.getCause();
        assertSame(throwable, cause);
        Throwable[] suppressed = actualGerritCommandException.getSuppressed();
        assertEquals(0, suppressed.length);
        assertEquals("An error occurred", actualGerritCommandException.getLocalizedMessage());
        assertEquals("An error occurred", actualGerritCommandException.getMessage());
        assertNull(cause.getLocalizedMessage());
        assertNull(cause.getCause());
        assertNull(cause.getMessage());
        assertSame(suppressed, cause.getSuppressed());
    }

    @Test
    void testConstructor4() {

        Throwable throwable = new Throwable();

        GerritCommandException actualGerritCommandException = new GerritCommandException(throwable);

        Throwable cause = actualGerritCommandException.getCause();
        assertSame(throwable, cause);
        Throwable[] suppressed = actualGerritCommandException.getSuppressed();
        assertEquals(0, suppressed.length);
        assertEquals("java.lang.Throwable", actualGerritCommandException.getLocalizedMessage());
        assertEquals("java.lang.Throwable", actualGerritCommandException.getMessage());
        assertNull(cause.getLocalizedMessage());
        assertNull(cause.getCause());
        assertNull(cause.getMessage());
        assertSame(suppressed, cause.getSuppressed());
    }
}

