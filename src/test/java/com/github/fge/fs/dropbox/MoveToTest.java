
package com.github.fge.fs.dropbox;

import java.net.URI;
import java.nio.file.FileSystem;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import vavi.util.properties.annotation.PropsEntity;

import static vavi.nio.file.Base.testMoveFolder;


/**
 * dropbox move folder.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/21 umjammer initial version <br>
 */
@DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
class MoveToTest {

    @Test
    void test01() throws Exception {
        String email = System.getenv("TEST_ACCOUNT");

        URI uri = URI.create("dropbox:///?id=" + email);
        FileSystem fs = new DropBoxFileSystemProvider().newFileSystem(uri, Collections.emptyMap());

        testMoveFolder(fs);
    }
}
