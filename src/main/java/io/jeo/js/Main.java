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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import jline.console.ConsoleReader;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Runnable class for executing a script or starting the repl.
 */
public class Main {

    @Parameter(description = "Script to execute")
    List<String> script = new ArrayList<>();

    @Parameter(names =  {"-nr", "--no-require"}, description = "Forgoes loading of require function")
    boolean noRequire = false;

    @Parameter(names =  {"-h", "--help"}, description = "Display command help", help = true)
    boolean help = false;

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        new JCommander(main, args);

        main.run();
    }

    public void run() throws Exception {
        if (help) {
            usage();
            System.exit(0);
        }

        JeoJS rt = new JeoJS().require(!noRequire);

        Optional<String> file = script.stream().findFirst();
        if (file.isPresent()) {
            exec(file.get(), rt);
        }
        else {
            // start in interactive mode
            repl(rt);
        }
    }

    void repl(final JeoJS rt) throws Exception {
        final ConsoleReader console = new ConsoleReader(System.in, System.out);
        console.setHandleUserInterrupt(true);

        rt.start(console.getOutput());

        new Repl(console) {
            @Override
            protected void handle(String input) {
                Object ret = null;
                try {
                    ret = rt.eval(input);
                } catch (ScriptException e) {
                    e.printStackTrace(new PrintWriter(console.getOutput()));
                }

                if (ret != null) {
                    try {
                        console.getOutput().write(ret.toString() + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();
    }

    void exec(String script, JeoJS rt) throws Exception {
        rt.start(new OutputStreamWriter(System.out));

        try (Reader in = Files.newBufferedReader(Paths.get(script))) {
            rt.eval(in);
        }
    }

    void usage() {
        new JCommander(new Main()).usage();
    }
}
