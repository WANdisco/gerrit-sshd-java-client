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

class SCPDownloadExceptionTest {

    @Test
    void testConstructor() {

        SCPDownloadException actualScpDownloadException = new SCPDownloadException();

        assertNull(actualScpDownloadException.getCause());
        assertEquals(0, actualScpDownloadException.getSuppressed().length);
        assertNull(actualScpDownloadException.getMessage());
        assertNull(actualScpDownloadException.getLocalizedMessage());
    }

    @Test
    void testConstructor2() {

        String message = "An error occurred";

        SCPDownloadException actualScpDownloadException = new SCPDownloadException(message);

        assertNull(actualScpDownloadException.getCause());
        assertEquals(0, actualScpDownloadException.getSuppressed().length);
        assertEquals("An error occurred", actualScpDownloadException.getMessage());
        assertEquals("An error occurred", actualScpDownloadException.getLocalizedMessage());
    }

    @Test
    void testConstructor3() {

        String message = "An error occurred";
        Throwable throwable = new Throwable();

        SCPDownloadException actualScpDownloadException = new SCPDownloadException(message, throwable);

        Throwable cause = actualScpDownloadException.getCause();
        assertSame(throwable, cause);
        Throwable[] suppressed = actualScpDownloadException.getSuppressed();
        assertEquals(0, suppressed.length);
        assertEquals("An error occurred", actualScpDownloadException.getLocalizedMessage());
        assertEquals("An error occurred", actualScpDownloadException.getMessage());
        assertNull(cause.getLocalizedMessage());
        assertNull(cause.getCause());
        assertNull(cause.getMessage());
        assertSame(suppressed, cause.getSuppressed());
    }

    @Test
    void testConstructor4() {

        Throwable throwable = new Throwable();

        SCPDownloadException actualScpDownloadException = new SCPDownloadException(throwable);

        Throwable cause = actualScpDownloadException.getCause();
        assertSame(throwable, cause);
        Throwable[] suppressed = actualScpDownloadException.getSuppressed();
        assertEquals(0, suppressed.length);
        assertEquals("java.lang.Throwable", actualScpDownloadException.getLocalizedMessage());
        assertEquals("java.lang.Throwable", actualScpDownloadException.getMessage());
        assertNull(cause.getLocalizedMessage());
        assertNull(cause.getCause());
        assertNull(cause.getMessage());
        assertSame(suppressed, cause.getSuppressed());
    }
}

