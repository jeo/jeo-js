/* Copyright 2015 The jeo project. All rights reserved.
 *
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
 */
package io.jeo.js;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import io.jeo.Jeo;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * The Jeo + JS runtime.
 */
public class JeoJS {

    Path libRoot;
    ScriptEngine engine;

    boolean require = true;

    public JeoJS() throws IOException {
        this(Jeo.home().resolve("js"));
    }

    public JeoJS(Path libRoot) throws IOException {
        this.libRoot = libRoot;
        initLibs();
    }

    /**
     * Enabled/disables the registration of the require function.
     *
     * @param require Whether to setup require.
     */
    public JeoJS require(boolean require) {
        this.require = require;
        return this;
    }

    static final List<String> SCRIPTS;
    static {
        try {
            SCRIPTS = CharStreams.readLines(
                new InputStreamReader(JeoJS.class.getResourceAsStream("scripts.txt"), Charsets.UTF_8));
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    void initLibs() throws IOException {
        libRoot.toFile().mkdirs();
        for (String script : SCRIPTS) {
            Path path = libRoot.resolve(script);
            File file = path.toFile();
            if (file.exists()) {
                // compare checksums
                HashCode h1 = Hashing.md5().hashBytes(ByteStreams.toByteArray(loadScript(script)));
                HashCode h2 = Hashing.md5().hashBytes(Files.readAllBytes(path));

                if (!h1.equals(h2)) {
                    // overwrite
                    copyScript(script);
                }
            }
            else {
                copyScript(script);
            }
        }
    }

    InputStream loadScript(String script) {
        return getClass().getResourceAsStream(script);
    }

    void copyScript(String script) throws IOException {
        Path dest = libRoot.resolve(script);
        File parent = dest.getParent().toFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        Files.copy(loadScript(script), dest, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Starts the runtime performing the initial bootstrap and creation of the script engine.
     *
     * @param output Writer to direct output of the script engine to.
     */
    public void start(Writer output) throws ScriptException, IOException {
        ScriptEngineManager scriptMgr = new ScriptEngineManager();

        engine = scriptMgr.getEngineByExtension("js");
        engine.getContext().setErrorWriter(output);

        if (require) {
            try (Reader r = new InputStreamReader(loadScript("jvm-npm.js"), Charsets.UTF_8)) {
                engine.eval(r);
            }
        }
    }

    /**
     * Runs code in the script engine.
     *
     * @param input The code to run.
     */
    public Object eval(Reader input) throws ScriptException {
        return engine.eval(input);
    }

    /**
     * Runs code in the script engine.
     *
     * @param input The code to run.
     */
    public Object eval(String input) throws ScriptException {
        return engine.eval(input);
    }
}
