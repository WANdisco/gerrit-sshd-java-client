package integration.utils;

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

import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogger implements TestWatcher {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        logger.info("Test skipped: {} because of {}" , context.getDisplayName(), reason);

    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logger.info("Test Successful: {}" , context.getDisplayName());

    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        logger.info("Test Aborted: {}" , context.getDisplayName(),cause);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        logger.info("Test Failed: {}" , context.getDisplayName(),cause);
    }
}
