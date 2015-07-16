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

import com.google.common.base.Strings;
import jline.console.ConsoleReader;
import jline.console.UserInterruptException;

import java.io.IOException;

/**
 * The jeojs Read Eval Print loop console.
 */
public abstract class Repl {

    ConsoleReader console;
    State state;

    public Repl(ConsoleReader console) {
        this.console = console;
        state = new State();
    }

    public void start() throws IOException {
        while(true) {
            try {
                String line = console.readLine(state.prompt());
                state.feed(line);

                if (state.balanced()) {
                    handle(state.input(true));
                }
            }
            catch(UserInterruptException stop) {
                if (!Strings.isNullOrEmpty(stop.getPartialLine())) {
                    // cancel the current input
                    console.killLine();
                    continue;
                }

                if (state.stopping()) {
                    System.exit(0);
                }
                else {
                    console.println();
                    console.println("(^C again to quit)");
                    state.stop();
                }
            }
        }
    }

    protected abstract void handle(String input);

    class State {

        int balance = 0;
        StringBuilder buffer = new StringBuilder();
        boolean stop = false;

        String prompt() {
            return balance == 0 ? "> " : "... ";
        }

        State feed(String input) {
            stop = false;

            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                switch(c) {
                    case '(':
                    case '{':
                    case '[':
                        balance++;
                        break;
                    case ')':
                    case ']':
                    case '}':
                        balance--;
                }
            }

            buffer.append(input);
            return this;
        }

        State stop() {
            stop = true;
            return this;
        }

        boolean balanced() {
            return balance == 0;
        }

        boolean stopping() {
            return stop;
        }

        String input(boolean clear) {
            String input = buffer.toString();
            if (clear) {
                clear();
            }
            return input;
        }

        State clear() {
            buffer.setLength(0);
            return this;
        }
    }
}
