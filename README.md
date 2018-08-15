# $ profit $
Micro-library to help debug Clojure programs.

Profit allows you to print intermediate results of Clojure expressions. It's similar to other libraries such as [Spyscope](https://github.com/dgrnbrg/spyscope) and [clojure.tools.trace](https://github.com/clojure/tools.trace), but more minimalist (less featureful) in order to be more convenient to use.

## Installation
Profit is meant to be used exclusively to aid development. Therefore it is supposed to be installed in your local profile as opposed to part of the project. This has two main advantages: (1) it is impossible to accidentally deploy your program in a production environment with undesired prints; (2) it is always available in all projects on your machine.

### Leiningen
Edit your `~/.lein/profiles.clj` to include the following items:

```
{:user
 {:dependencies [[profit "0.1.0-SNAPSHOT"]]
  :injections   [(require '$)
                 (#'clojure.core/load-data-readers)
                 (set! *data-readers* (.getRawRoot #'*data-readers*))
                 ($/set-generic-tags!)]}}
```
This makes Profit load automatically in every Leiningen project. Without the second and third injections, the data readers are not loaded automatically in a REPL without an associated project. (read [here](https://github.com/boot-clj/boot/issues/47), [here](https://github.com/rkneufeld/lein-try/issues/11) and [here](https://github.com/rkneufeld/lein-try/issues/13) for similar issues in other projects).
The last line is not necessary but highly useful. See **Usage** below for details.

### Boot
Insert the following lines in your `~/.boot/profile.boot` file:

```
(set-env! :dependencies #(conj % '[profit "0.1.0-SNAPSHOT"]))
(require '$)
(boot.core/load-data-readers!)
```

The command `($/set-generic-tags!)` throws a java.lang.IllegalStateException if inserted in the boot profile, not sure why. The solution for now is to run this last command manually after launching the REPL.

## Usage

### Basic print command: `#$`
If you have a Clojure expression such as:
```clojure
my-project.core> (reduce + (map (fn [x] (* x x)) (range 10)))
285
```
and you are interested in an intermediate result (say, after calculating the squares but before summing), insert the special `#$` symbol right before the expression you want to see, then evaluate (notice the symbol inserted here):
```clojure
my-project.core> (reduce + #$ (map (fn [x] (* x x)) (range 10)))
285
```
And the following will be printed to standard output:
```
my-project.core (map (fn [x] (* x x)) (range 10))
(0 1 4 9 16 25 36 49 64 81) <=
```
You will see three extra things printed out:
 1. The namespace of the form inspected;
 1. The form itself;
 1. The result of the form evaluation.
All of this without interfering with the original program results.
The prints are colored to help identify the debugging instructions in the output of your program.

The symbol `#$` was chosen because:
 1. Being a tagged-literal, it avoids unnecessary wrapping of the inspected form for a function/macro call (and subsequent unwrapping after debugging);
 1. it is the shortest tagged-literal possible (and most symbols are disallowed);
 1. `#` and `$` are side by side (above numbers 3 and 4) in many keyboard layouts, making it easy to type;
 
### Arbitrary names
The symbol `#$` is convenient and I expect it to be used most of the time. However, in some situations it may lead to a lot of noise given that it is relatively verbose. In other situations it may not be clear enough, in case of multiple identical forms. Or you may simply prefer to give your own names to each inspected form.
Thus the library supports arbitrary, undefined tagged-literals. In the example above, you could have done:

```clojure
my-project.core> (reduce + #squares (map (fn [x] (* x x)) #range (range 10)))
285
```
Output:
```
range (0 1 2 3 4 5 6 7 8 9) <=
squares (0 1 4 9 16 25 36 49 64 81) <=
```

### Threading macros

If you have a threading macro (for example, `->` or `->>`) and want to inspect intermediate results, the tagged-literals won't work, since they necessarily evaluate the form following them, even before macro-expansion. To get around this limitation, you can use `$/$`, which works in all threading macros; or, if you want to give a name to the form, use `$/->` or `$/->>`:

```clojure
(->> (range 10)
     $/$
     (map (fn [x] (* x x)))
     ($/->> squares)
     (reduce +))
```
Output:
```
$ (0 1 2 3 4 5 6 7 8 9) <=
squares (0 1 4 9 16 25 36 49 64 81) <=
```

## License

Copyright © 2018 Mauro Lopes

Distributed under the Eclipse Public License version 2.0, almost the same as Clojure, which uses Eclipse Public License version 1.0.
