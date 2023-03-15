package com.wandisco.gerrit.client.sshd.model.exception;

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

class SSHAuthenticationExceptionTest {

    @Test
    void testConstructor() {
        SSHAuthenticationException actualSshAuthenticationException = new SSHAuthenticationException();

        assertNull(actualSshAuthenticationException.getCause());
        assertEquals(0, actualSshAuthenticationException.getSuppressed().length);
        assertNull(actualSshAuthenticationException.getMessage());
        assertNull(actualSshAuthenticationException.getLocalizedMessage());
    }

    @Test
    void testConstructor2() {

        String message = "An error occurred";

        SSHAuthenticationException actualSshAuthenticationException = new SSHAuthenticationException(message);

        assertNull(actualSshAuthenticationException.getCause());
        assertEquals(0, actualSshAuthenticationException.getSuppressed().length);
        assertEquals("An error occurred", actualSshAuthenticationException.getMessage());
        assertEquals("An error occurred", actualSshAuthenticationException.getLocalizedMessage());
    }

    @Test
    void testConstructor3() {

        String message = "An error occurred";
        Throwable throwable = new Throwable();

        SSHAuthenticationException actualSshAuthenticationException = new SSHAuthenticationException(message, throwable);

        Throwable cause = actualSshAuthenticationException.getCause();
        assertSame(throwable, cause);
        Throwable[] suppressed = actualSshAuthenticationException.getSuppressed();
        assertEquals(0, suppressed.length);
        assertEquals("An error occurred", actualSshAuthenticationException.getLocalizedMessage());
        assertEquals("An error occurred", actualSshAuthenticationException.getMessage());
        assertNull(cause.getLocalizedMessage());
        assertNull(cause.getCause());
        assertNull(cause.getMessage());
        assertSame(suppressed, cause.getSuppressed());
    }

    @Test
    void testConstructor4() {

        Throwable throwable = new Throwable();

        SSHAuthenticationException actualSshAuthenticationException = new SSHAuthenticationException(throwable);

        Throwable cause = actualSshAuthenticationException.getCause();
        assertSame(throwable, cause);
        Throwable[] suppressed = actualSshAuthenticationException.getSuppressed();
        assertEquals(0, suppressed.length);
        assertEquals("java.lang.Throwable", actualSshAuthenticationException.getLocalizedMessage());
        assertEquals("java.lang.Throwable", actualSshAuthenticationException.getMessage());
        assertNull(cause.getLocalizedMessage());
        assertNull(cause.getCause());
        assertNull(cause.getMessage());
        assertSame(suppressed, cause.getSuppressed());
    }
}

