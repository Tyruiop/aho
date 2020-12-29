# aho

A simple implementation of the [Aho Corasick](https://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_algorithm) algorithm capable of working on any sequence.

[![Clojars Project](https://img.shields.io/clojars/v/aho.svg)](https://clojars.org/aho)

## Usage

```clojure
(:require [aho.core :refer [build-automaton search]])

(def automaton (build-automaton [[:word1 "foo"] [:word2 "bar"]]))
(search automaton "ofoo and bar")
;; ({:index 11, :pattern :word2} {:index 3, :pattern :word1})
;; the index is the last position of the match in the string.
```

## License

Copyright © 2020

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
